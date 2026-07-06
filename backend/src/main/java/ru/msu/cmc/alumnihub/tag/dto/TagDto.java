package ru.msu.cmc.alumnihub.tag.dto;

import ru.msu.cmc.alumnihub.tag.entity.Tag;

public record TagDto(Long id, String name, String slug, String category) {

    public static TagDto from(Tag tag) {
        return new TagDto(tag.getId(), tag.getName(), tag.getSlug(), tag.getCategory());
    }
}
