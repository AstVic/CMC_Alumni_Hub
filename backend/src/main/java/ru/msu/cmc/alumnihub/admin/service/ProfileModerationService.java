package ru.msu.cmc.alumnihub.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.moderation.ModerationLogService;
import ru.msu.cmc.alumnihub.moderation.entity.ModerationEntityType;
import ru.msu.cmc.alumnihub.moderation.entity.ModeratorType;
import ru.msu.cmc.alumnihub.profile.dto.AlumniProfileDto;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;

import java.time.Instant;
import java.util.List;

/**
 * Admin moderation of alumni profile cards.
 */
@Service
public class ProfileModerationService {

    private static final Logger log = LoggerFactory.getLogger(ProfileModerationService.class);

    private final AlumniProfileRepository profileRepository;
    private final ModerationLogService moderationLogService;

    public ProfileModerationService(AlumniProfileRepository profileRepository,
                                    ModerationLogService moderationLogService) {
        this.profileRepository = profileRepository;
        this.moderationLogService = moderationLogService;
    }

    @Transactional(readOnly = true)
    public List<AlumniProfileDto> moderationQueue() {
        return profileRepository.findByStatusOrderByUpdatedAtDesc(ProfileStatus.PENDING_MODERATION)
                .stream().map(AlumniProfileDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AlumniProfileDto> allProfiles() {
        return profileRepository.findAllByOrderByUpdatedAtDesc()
                .stream().map(AlumniProfileDto::from).toList();
    }

    @Transactional
    public AlumniProfileDto approve(Long profileId) {
        AlumniProfile profile = getProfile(profileId);
        if (profile.getStatus() != ProfileStatus.PENDING_MODERATION) {
            throw new BadRequestException("Одобрять можно только карточки на модерации");
        }
        profile.setStatus(ProfileStatus.PUBLISHED);
        profile.setModerationComment(null);
        profile.setPublishedAt(Instant.now());
        moderationLogService.log(ModerationEntityType.PROFILE, profile.getId(),
                ModeratorType.ADMIN, "APPROVED", null);
        log.info("Profile moderation id={} decision=APPROVED", profile.getId());
        return AlumniProfileDto.from(profile);
    }

    @Transactional
    public AlumniProfileDto reject(Long profileId, String comment) {
        AlumniProfile profile = getProfile(profileId);
        if (profile.getStatus() != ProfileStatus.PENDING_MODERATION) {
            throw new BadRequestException("Отклонять можно только карточки на модерации");
        }
        profile.setStatus(ProfileStatus.REJECTED);
        profile.setModerationComment(comment);
        moderationLogService.log(ModerationEntityType.PROFILE, profile.getId(),
                ModeratorType.ADMIN, "REJECTED", comment);
        log.info("Profile moderation id={} decision=REJECTED", profile.getId());
        return AlumniProfileDto.from(profile);
    }

    private AlumniProfile getProfile(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карточка не найдена"));
    }
}
