package ru.msu.cmc.alumnihub.profile.dto;

import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.tag.dto.TagDto;

import java.util.Comparator;
import java.util.List;

/**
 * Compact public view for the catalog (board and list modes).
 */
public record ProfileCardDto(
        Long id,
        String fullName,
        Integer graduationYear,
        String department,
        String currentPosition,
        String company,
        String city,
        String country,
        String shortDescription,
        String photoUrl,
        int questionCount,
        List<TagDto> tags) {

    private static final int SHORT_LEN = 180;

    public static ProfileCardDto from(AlumniProfile p) {
        List<TagDto> tags = p.getTags().stream()
                .map(TagDto::from)
                .sorted(Comparator.comparing(TagDto::name))
                .toList();
        return new ProfileCardDto(
                p.getId(),
                p.getFullName(),
                p.getGraduationYear(),
                p.getDepartment(),
                p.getCurrentPosition(),
                p.getCompany(),
                p.getCity(),
                p.getCountry(),
                shorten(p.getCareerDescription()),
                p.getPhotoUrl(),
                p.getQuestionCount(),
                tags);
    }

    private static String shorten(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.strip();
        if (trimmed.length() <= SHORT_LEN) {
            return trimmed;
        }
        return trimmed.substring(0, SHORT_LEN).stripTrailing() + "…";
    }
}
