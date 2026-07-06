package ru.msu.cmc.alumnihub.profile.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.common.dto.PageResponse;
import ru.msu.cmc.alumnihub.profile.dto.ProfileCardDto;
import ru.msu.cmc.alumnihub.profile.dto.ProfileDetailDto;
import ru.msu.cmc.alumnihub.profile.service.ProfileCatalogService;

import java.util.Set;

/**
 * Public catalog of published alumni profiles.
 */
@RestController
@RequestMapping("/api/public/profiles")
public class PublicProfileController {

    private final ProfileCatalogService catalogService;

    public PublicProfileController(ProfileCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public PageResponse<ProfileCardDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Integer graduationYear,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return catalogService.search(search, tags, graduationYear, company, sort, page, size);
    }

    @GetMapping("/{id}")
    public ProfileDetailDto detail(@PathVariable Long id) {
        return catalogService.getPublishedById(id);
    }
}
