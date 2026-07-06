package ru.msu.cmc.alumnihub.profile.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.profile.dto.AlumniProfileDto;
import ru.msu.cmc.alumnihub.profile.dto.UpdateProfileRequest;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;
import ru.msu.cmc.alumnihub.storage.StorageService;
import ru.msu.cmc.alumnihub.tag.entity.Tag;
import ru.msu.cmc.alumnihub.tag.repository.TagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Self-service management of an alumni's own profile card.
 *
 * <p>Editing moves the card back to DRAFT (it leaves the public catalog until
 * re-approved); the alumni then explicitly submits it for moderation.
 */
@Service
public class AlumniProfileService {

    private final AlumniProfileRepository profileRepository;
    private final TagRepository tagRepository;
    private final StorageService storageService;

    public AlumniProfileService(AlumniProfileRepository profileRepository,
                                TagRepository tagRepository,
                                StorageService storageService) {
        this.profileRepository = profileRepository;
        this.tagRepository = tagRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public AlumniProfileDto getMyProfile(Long userId) {
        return AlumniProfileDto.from(requireProfile(userId));
    }

    @Transactional
    public AlumniProfileDto updateMyProfile(Long userId, UpdateProfileRequest request) {
        AlumniProfile profile = requireProfile(userId);

        profile.setFullName(request.fullName().trim());
        profile.setGraduationYear(request.graduationYear());
        profile.setDepartment(request.department());
        profile.setCurrentPosition(request.currentPosition());
        profile.setCompany(request.company());
        profile.setCity(request.city());
        profile.setCountry(request.country());
        profile.setCareerDescription(request.careerDescription());
        profile.setInterestsDescription(request.interestsDescription());
        profile.setTags(resolveTags(request.tagSlugs()));

        // Any edit returns the card to DRAFT: it must be re-moderated to publish.
        profile.setStatus(ProfileStatus.DRAFT);
        profile.setModerationComment(null);
        return AlumniProfileDto.from(profile);
    }

    @Transactional
    public AlumniProfileDto submitForModeration(Long userId) {
        AlumniProfile profile = requireProfile(userId);
        if (profile.getFullName() == null || profile.getFullName().isBlank()) {
            throw new BadRequestException("Заполните хотя бы ФИО перед отправкой на модерацию");
        }
        if (profile.getStatus() == ProfileStatus.PENDING_MODERATION) {
            throw new BadRequestException("Карточка уже на модерации");
        }
        profile.setStatus(ProfileStatus.PENDING_MODERATION);
        profile.setModerationComment(null);
        return AlumniProfileDto.from(profile);
    }

    @Transactional
    public AlumniProfileDto updatePhoto(Long userId, MultipartFile file) {
        AlumniProfile profile = requireProfile(userId);
        String url = storageService.storeImage(file);
        profile.setPhotoUrl(url);
        return AlumniProfileDto.from(profile);
    }

    private Set<Tag> resolveTags(Set<String> slugs) {
        if (slugs == null || slugs.isEmpty()) {
            return new HashSet<>();
        }
        List<Tag> found = tagRepository.findBySlugIn(slugs);
        if (found.size() != slugs.size()) {
            throw new BadRequestException("Некоторые теги не найдены");
        }
        return new HashSet<>(found);
    }

    private AlumniProfile requireProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Профиль не найден"));
    }
}
