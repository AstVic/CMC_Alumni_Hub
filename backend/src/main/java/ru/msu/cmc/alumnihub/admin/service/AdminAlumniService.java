package ru.msu.cmc.alumnihub.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.admin.dto.AdminAlumniDto;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.profile.dto.AlumniProfileDto;
import ru.msu.cmc.alumnihub.profile.dto.UpdateProfileRequest;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;
import ru.msu.cmc.alumnihub.tag.entity.Tag;
import ru.msu.cmc.alumnihub.tag.repository.TagRepository;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Admin management of alumni accounts (listing, detail, blocking).
 */
@Service
public class AdminAlumniService {

    private static final Logger log = LoggerFactory.getLogger(AdminAlumniService.class);

    private final UserRepository userRepository;
    private final AlumniProfileRepository profileRepository;
    private final TagRepository tagRepository;

    public AdminAlumniService(UserRepository userRepository,
                              AlumniProfileRepository profileRepository,
                              TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminAlumniDto> listAlumni() {
        List<User> alumni = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ALUMNI)
                .toList();
        Map<Long, AlumniProfile> byUser = profileRepository.findAll().stream()
                .collect(Collectors.toMap(AlumniProfile::getUserId, p -> p, (a, b) -> a));
        return alumni.stream()
                .map(u -> AdminAlumniDto.of(u, byUser.get(u.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public AlumniProfileDto getAlumniProfile(Long userId) {
        return AlumniProfileDto.from(requireAlumniProfile(userId));
    }

    @Transactional
    public AlumniProfileDto updateAlumniProfile(Long userId, UpdateProfileRequest request) {
        AlumniProfile profile = requireAlumniProfile(userId);
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
        // Admin edits do not change moderation status.
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

    @Transactional
    public AdminAlumniDto setBlocked(Long userId, boolean blocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getRole() != Role.ALUMNI) {
            throw new BadRequestException("Управлять можно только аккаунтами выпускников");
        }
        user.setEnabled(!blocked);
        log.info("Admin set alumni userId={} blocked={}", userId, blocked);
        AlumniProfile profile = profileRepository.findByUserId(userId).orElse(null);
        return AdminAlumniDto.of(user, profile);
    }

    private AlumniProfile requireAlumniProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Профиль выпускника не найден"));
    }
}
