package ru.msu.cmc.alumnihub.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Editable fields of an alumni profile. Photo is uploaded separately.
 */
public record UpdateProfileRequest(
        @NotBlank @Size(max = 255) String fullName,
        @Min(1950) @Max(2100) Integer graduationYear,
        @Size(max = 255) String department,
        @Size(max = 255) String currentPosition,
        @Size(max = 255) String company,
        @Size(max = 255) String city,
        @Size(max = 255) String country,
        @Size(max = 5000) String careerDescription,
        @Size(max = 5000) String interestsDescription,
        Set<String> tagSlugs) {
}
