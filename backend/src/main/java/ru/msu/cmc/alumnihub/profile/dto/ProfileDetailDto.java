package ru.msu.cmc.alumnihub.profile.dto;

import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.tag.dto.TagDto;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Full public detail view of a published profile. Excludes moderation fields.
 */
public record ProfileDetailDto(
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
        int questionCount,
        List<TagDto> tags,
        Instant publishedAt) {

    public static ProfileDetailDto from(AlumniProfile p) {
        List<TagDto> tags = p.getTags().stream()
                .map(TagDto::from)
                .sorted(Comparator.comparing(TagDto::name))
                .toList();
        return new ProfileDetailDto(
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
                p.getQuestionCount(),
                tags,
                p.getPublishedAt());
    }
}
