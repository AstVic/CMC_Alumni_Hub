package ru.msu.cmc.alumnihub.tag.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.tag.dto.TagDto;
import ru.msu.cmc.alumnihub.tag.service.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/public/tags")
public class PublicTagController {

    private final TagService tagService;

    public PublicTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<TagDto> list() {
        return tagService.listAll();
    }
}
