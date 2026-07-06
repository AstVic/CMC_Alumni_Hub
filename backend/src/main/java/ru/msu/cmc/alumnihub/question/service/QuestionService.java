package ru.msu.cmc.alumnihub.question.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.ForbiddenException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.common.exception.TooManyRequestsException;
import ru.msu.cmc.alumnihub.common.ratelimit.RateLimiterService;
import ru.msu.cmc.alumnihub.moderation.ModerationLogService;
import ru.msu.cmc.alumnihub.moderation.ModerationProvider;
import ru.msu.cmc.alumnihub.moderation.ModerationResult;
import ru.msu.cmc.alumnihub.moderation.entity.ModerationEntityType;
import ru.msu.cmc.alumnihub.moderation.entity.ModeratorType;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;
import ru.msu.cmc.alumnihub.question.dto.AdminQuestionDto;
import ru.msu.cmc.alumnihub.question.dto.CreateQuestionRequest;
import ru.msu.cmc.alumnihub.question.dto.QuestionDto;
import ru.msu.cmc.alumnihub.question.dto.QuestionSubmissionResponse;
import ru.msu.cmc.alumnihub.question.entity.AiModerationStatus;
import ru.msu.cmc.alumnihub.question.entity.Question;
import ru.msu.cmc.alumnihub.question.entity.QuestionStatus;
import ru.msu.cmc.alumnihub.question.repository.QuestionRepository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Question submission, two-stage moderation and per-role querying.
 */
@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private static final int RATE_LIMIT = 5;
    private static final Duration RATE_WINDOW = Duration.ofMinutes(10);

    private static final Set<QuestionStatus> MODERATION_QUEUE =
            Set.of(QuestionStatus.PENDING_MODERATION, QuestionStatus.AI_APPROVED,
                    QuestionStatus.PENDING_ADMIN_REVIEW);
    private static final Set<QuestionStatus> REJECTED_QUEUE =
            Set.of(QuestionStatus.AI_REJECTED, QuestionStatus.REJECTED_BY_ADMIN);

    private final QuestionRepository questionRepository;
    private final AlumniProfileRepository profileRepository;
    private final ModerationProvider moderationProvider;
    private final ModerationLogService moderationLogService;
    private final RateLimiterService rateLimiterService;

    public QuestionService(QuestionRepository questionRepository,
                           AlumniProfileRepository profileRepository,
                           ModerationProvider moderationProvider,
                           ModerationLogService moderationLogService,
                           RateLimiterService rateLimiterService) {
        this.questionRepository = questionRepository;
        this.profileRepository = profileRepository;
        this.moderationProvider = moderationProvider;
        this.moderationLogService = moderationLogService;
        this.rateLimiterService = rateLimiterService;
    }

    // ---- Visitor ----

    @Transactional
    public QuestionSubmissionResponse submit(Long profileId, CreateQuestionRequest request,
                                             String clientIp) {
        if (!rateLimiterService.tryConsume("q:" + clientIp, RATE_LIMIT, RATE_WINDOW)) {
            throw new TooManyRequestsException("Слишком много вопросов. Попробуйте позже.");
        }
        // Silently accept honeypot hits without persisting — bots get no signal.
        if (request.isHoneypotTriggered()) {
            log.info("Honeypot triggered for profile {} from {}", profileId, clientIp);
            return QuestionSubmissionResponse.received();
        }

        AlumniProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Карточка не найдена"));
        if (profile.getStatus() != ProfileStatus.PUBLISHED) {
            throw new BadRequestException("Нельзя задать вопрос неопубликованной карточке");
        }

        Question question = new Question();
        question.setAlumniProfileId(profileId);
        question.setSenderName(blankToNull(request.senderName()));
        question.setSenderEmail(blankToNull(request.senderEmail()));
        question.setQuestionText(request.questionText().trim());
        question.setStatus(QuestionStatus.PENDING_MODERATION);
        question.setAiModerationStatus(AiModerationStatus.PENDING);
        questionRepository.save(question);

        applyAutomaticModeration(question);
        log.info("Question submitted id={} profileId={} moderationStatus={}",
                question.getId(), profileId, question.getAiModerationStatus());
        return QuestionSubmissionResponse.received();
    }

    private void applyAutomaticModeration(Question question) {
        ModerationResult result = moderationProvider.moderateQuestion(question.getQuestionText());
        switch (result.decision()) {
            case APPROVED -> {
                question.setAiModerationStatus(AiModerationStatus.APPROVED);
                question.setStatus(QuestionStatus.AI_APPROVED);
            }
            case NEEDS_REVIEW -> {
                question.setAiModerationStatus(AiModerationStatus.NEEDS_REVIEW);
                question.setStatus(QuestionStatus.PENDING_ADMIN_REVIEW);
            }
            case REJECTED -> {
                question.setAiModerationStatus(AiModerationStatus.REJECTED);
                question.setStatus(QuestionStatus.AI_REJECTED);
            }
        }
        question.setAiModerationReason(result.reason());
        moderationLogService.log(ModerationEntityType.QUESTION, question.getId(),
                ModeratorType.AI, result.decision().name(), result.reason());
    }

    // ---- Alumni ----

    @Transactional(readOnly = true)
    public List<QuestionDto> listForAlumni(Long userId, String filter) {
        Long profileId = requireProfileId(userId);
        List<Question> questions = switch (filter == null ? "new" : filter.toLowerCase()) {
            case "read" -> questionRepository
                    .findByAlumniProfileIdAndStatusAndReadByAlumniOrderByCreatedAtDesc(
                            profileId, QuestionStatus.VISIBLE_TO_ALUMNI, true);
            case "archived" -> questionRepository
                    .findByAlumniProfileIdAndStatusOrderByCreatedAtDesc(
                            profileId, QuestionStatus.ARCHIVED);
            default -> questionRepository
                    .findByAlumniProfileIdAndStatusAndReadByAlumniOrderByCreatedAtDesc(
                            profileId, QuestionStatus.VISIBLE_TO_ALUMNI, false);
        };
        return questions.stream().map(QuestionDto::from).toList();
    }

    @Transactional
    public QuestionDto markRead(Long userId, Long questionId) {
        Long profileId = requireProfileId(userId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Вопрос не найден"));
        if (!question.getAlumniProfileId().equals(profileId)) {
            throw new ForbiddenException("Это не ваш вопрос");
        }
        if (question.getStatus() != QuestionStatus.VISIBLE_TO_ALUMNI) {
            throw new BadRequestException("Вопрос недоступен");
        }
        question.setReadByAlumni(true);
        return QuestionDto.from(question);
    }

    // ---- Admin ----

    @Transactional(readOnly = true)
    public List<AdminQuestionDto> adminList(QuestionStatus status) {
        List<Question> questions = (status == null)
                ? questionRepository.findAllByOrderByCreatedAtDesc()
                : questionRepository.findByStatusInOrderByCreatedAtDesc(Set.of(status));
        return toAdminDtos(questions);
    }

    @Transactional(readOnly = true)
    public List<AdminQuestionDto> adminModerationQueue() {
        return toAdminDtos(questionRepository.findByStatusInOrderByCreatedAtDesc(MODERATION_QUEUE));
    }

    @Transactional(readOnly = true)
    public List<AdminQuestionDto> adminRejectedQueue() {
        return toAdminDtos(questionRepository.findByStatusInOrderByCreatedAtDesc(REJECTED_QUEUE));
    }

    @Transactional
    public AdminQuestionDto approve(Long questionId) {
        Question question = getQuestion(questionId);
        if (question.getStatus() == QuestionStatus.VISIBLE_TO_ALUMNI) {
            throw new BadRequestException("Вопрос уже одобрен");
        }
        question.setStatus(QuestionStatus.VISIBLE_TO_ALUMNI);
        question.setAdminModerationComment(null);

        AlumniProfile profile = profileRepository.findById(question.getAlumniProfileId())
                .orElseThrow(() -> new NotFoundException("Карточка не найдена"));
        profile.setQuestionCount(profile.getQuestionCount() + 1);

        moderationLogService.log(ModerationEntityType.QUESTION, question.getId(),
                ModeratorType.ADMIN, "APPROVED", null);
        log.info("Question moderation id={} decision=APPROVED", question.getId());
        return AdminQuestionDto.from(question, profile.getFullName());
    }

    @Transactional
    public AdminQuestionDto reject(Long questionId, String comment) {
        Question question = getQuestion(questionId);
        boolean wasVisible = question.getStatus() == QuestionStatus.VISIBLE_TO_ALUMNI;
        question.setStatus(QuestionStatus.REJECTED_BY_ADMIN);
        question.setAdminModerationComment(comment);

        String alumniName = null;
        AlumniProfile profile = profileRepository.findById(question.getAlumniProfileId()).orElse(null);
        if (profile != null) {
            alumniName = profile.getFullName();
            // If it had been made visible, decrement the denormalised count.
            if (wasVisible && profile.getQuestionCount() > 0) {
                profile.setQuestionCount(profile.getQuestionCount() - 1);
            }
        }
        moderationLogService.log(ModerationEntityType.QUESTION, question.getId(),
                ModeratorType.ADMIN, "REJECTED", comment);
        log.info("Question moderation id={} decision=REJECTED", question.getId());
        return AdminQuestionDto.from(question, alumniName);
    }

    // ---- helpers ----

    private List<AdminQuestionDto> toAdminDtos(List<Question> questions) {
        Set<Long> profileIds = questions.stream()
                .map(Question::getAlumniProfileId)
                .collect(Collectors.toSet());
        Map<Long, String> names = profileRepository.findAllById(profileIds).stream()
                .collect(Collectors.toMap(AlumniProfile::getId, AlumniProfile::getFullName));
        return questions.stream()
                .map(q -> AdminQuestionDto.from(q, names.get(q.getAlumniProfileId())))
                .toList();
    }

    private Long requireProfileId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(AlumniProfile::getId)
                .orElseThrow(() -> new NotFoundException("Профиль не найден"));
    }

    private Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вопрос не найден"));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
