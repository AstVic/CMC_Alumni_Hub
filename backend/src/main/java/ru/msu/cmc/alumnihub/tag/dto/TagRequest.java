package ru.msu.cmc.alumnihub.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Create/update payload for a tag. Slug must be a URL-safe lowercase string.
 */
public record TagRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100)
        @Pattern(regexp = "[a-z0-9-]+", message = "slug: только строчные латинские буквы, цифры и дефис")
        String slug,
        @Size(max = 50) String category) {
}
