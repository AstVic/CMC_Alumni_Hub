package ru.msu.cmc.alumnihub.profile.dto;

import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.tag.dto.TagDto;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Full profile view for the owning alumni (includes status and moderation info).
 */
public record AlumniProfileDto(
        Long id,
        String fullName,
        Integer graduationYear,
        String department,
        String currentPosition,
        String company,
        String city,
        String country,
        String careerDescription,
        String interestsDescription,
        String photoUrl,
        ProfileStatus status,
        String moderationComment,
        int questionCount,
        List<TagDto> tags,
        Instant publishedAt,
        Instant updatedAt) {

    public static AlumniProfileDto from(AlumniProfile p) {
        List<TagDto> tags = p.getTags().stream()
                .map(TagDto::from)
                .sorted(Comparator.comparing(TagDto::name))
                .toList();
        return new AlumniProfileDto(
                p.getId(),
                p.getFullName(),
                p.getGraduationYear(),
                p.getDepartment(),
                p.getCurrentPosition(),
                p.getCompany(),
                p.getCity(),
                p.getCountry(),
                p.getCareerDescription(),
                p.getInterestsDescription(),
                p.getPhotoUrl(),
                p.getStatus(),
                p.getModerationComment(),
                p.getQuestionCount(),
                tags,
                p.getPublishedAt(),
                p.getUpdatedAt());
    }
}
