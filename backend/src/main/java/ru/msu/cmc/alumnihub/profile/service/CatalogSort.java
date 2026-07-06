package ru.msu.cmc.alumnihub.profile.service;

import org.springframework.data.domain.Sort;

/**
 * Maps the public {@code sort} query value to a JPA {@link Sort}.
 */
public enum CatalogSort {

    NEWEST("newest", Sort.by(Sort.Direction.DESC, "publishedAt", "id")),
    OLDEST("oldest", Sort.by(Sort.Direction.ASC, "publishedAt", "id")),
    NAME_ASC("name_asc", Sort.by(Sort.Direction.ASC, "fullName")),
    GRADUATION_YEAR_DESC("graduation_year_desc", Sort.by(Sort.Direction.DESC, "graduationYear")),
    GRADUATION_YEAR_ASC("graduation_year_asc", Sort.by(Sort.Direction.ASC, "graduationYear")),
    QUESTIONS_DESC("questions_desc", Sort.by(Sort.Direction.DESC, "questionCount")),
    QUESTIONS_ASC("questions_asc", Sort.by(Sort.Direction.ASC, "questionCount"));

    private final String value;
    private final Sort sort;

    CatalogSort(String value, Sort sort) {
        this.value = value;
        this.sort = sort;
    }

    public Sort toSort() {
        // Stable tiebreaker so pagination is deterministic across pages.
        return sort.and(Sort.by(Sort.Direction.DESC, "id"));
    }

    public static CatalogSort fromValue(String value) {
        if (value == null || value.isBlank()) {
            return NEWEST;
        }
        for (CatalogSort option : values()) {
            if (option.value.equalsIgnoreCase(value)) {
                return option;
            }
        }
        return NEWEST;
    }
}
