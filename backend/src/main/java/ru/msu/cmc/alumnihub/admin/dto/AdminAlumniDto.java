package ru.msu.cmc.alumnihub.admin.dto;

import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.user.entity.User;

import java.time.Instant;

/**
 * Admin summary of an alumni account (user + profile).
 */
public record AdminAlumniDto(
        Long userId,
        String email,
        boolean enabled,
        Long profileId,
        String fullName,
        ProfileStatus profileStatus,
        String company,
        Integer graduationYear,
        int questionCount,
        Instant registeredAt) {

    public static AdminAlumniDto of(User user, AlumniProfile profile) {
        return new AdminAlumniDto(
                user.getId(),
                user.getEmail(),
                user.isEnabled(),
                profile != null ? profile.getId() : null,
                profile != null ? profile.getFullName() : null,
                profile != null ? profile.getStatus() : null,
                profile != null ? profile.getCompany() : null,
                profile != null ? profile.getGraduationYear() : null,
                profile != null ? profile.getQuestionCount() : 0,
                user.getCreatedAt());
    }
}
