package ru.msu.cmc.alumnihub.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.admin.dto.DashboardDto;
import ru.msu.cmc.alumnihub.invite.entity.InviteStatus;
import ru.msu.cmc.alumnihub.invite.repository.AlumniInviteRepository;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;
import ru.msu.cmc.alumnihub.question.entity.QuestionStatus;
import ru.msu.cmc.alumnihub.question.repository.QuestionRepository;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.util.Set;

/**
 * Computes admin dashboard statistics.
 */
@Service
public class DashboardService {

    private static final Set<QuestionStatus> ON_MODERATION = Set.of(
            QuestionStatus.PENDING_MODERATION, QuestionStatus.AI_APPROVED,
            QuestionStatus.PENDING_ADMIN_REVIEW);
    private static final Set<QuestionStatus> REJECTED = Set.of(
            QuestionStatus.AI_REJECTED, QuestionStatus.REJECTED_BY_ADMIN);

    private final UserRepository userRepository;
    private final AlumniProfileRepository profileRepository;
    private final QuestionRepository questionRepository;
    private final AlumniInviteRepository inviteRepository;

    public DashboardService(UserRepository userRepository,
                            AlumniProfileRepository profileRepository,
                            QuestionRepository questionRepository,
                            AlumniInviteRepository inviteRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.questionRepository = questionRepository;
        this.inviteRepository = inviteRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto getStats() {
        return new DashboardDto(
                userRepository.countByRole(Role.ALUMNI),
                profileRepository.countByStatus(ProfileStatus.PUBLISHED),
                profileRepository.countByStatus(ProfileStatus.PENDING_MODERATION),
                questionRepository.count(),
                questionRepository.countByStatusIn(ON_MODERATION),
                questionRepository.countByStatusIn(REJECTED),
                inviteRepository.countByStatus(InviteStatus.USED),
                inviteRepository.countByStatus(InviteStatus.SENT)
                        + inviteRepository.countByStatus(InviteStatus.CREATED));
    }
}
