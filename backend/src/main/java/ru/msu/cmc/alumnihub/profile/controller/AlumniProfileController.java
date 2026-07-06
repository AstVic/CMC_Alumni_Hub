package ru.msu.cmc.alumnihub.profile.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.msu.cmc.alumnihub.profile.dto.AlumniProfileDto;
import ru.msu.cmc.alumnihub.profile.dto.UpdateProfileRequest;
import ru.msu.cmc.alumnihub.profile.service.AlumniProfileService;
import ru.msu.cmc.alumnihub.security.CurrentUserService;

/**
 * Alumni self-management of their own profile card.
 */
@RestController
@RequestMapping("/api/alumni/profile")
public class AlumniProfileController {

    private final AlumniProfileService profileService;
    private final CurrentUserService currentUserService;

    public AlumniProfileController(AlumniProfileService profileService,
                                   CurrentUserService currentUserService) {
        this.profileService = profileService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public AlumniProfileDto getMyProfile() {
        return profileService.getMyProfile(currentUserService.requireCurrentUserId());
    }

    @PutMapping
    public AlumniProfileDto update(@Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateMyProfile(currentUserService.requireCurrentUserId(), request);
    }

    @PostMapping("/submit-for-moderation")
    public AlumniProfileDto submit() {
        return profileService.submitForModeration(currentUserService.requireCurrentUserId());
    }

    @PostMapping("/photo")
    public AlumniProfileDto uploadPhoto(@RequestParam("file") MultipartFile file) {
        return profileService.updatePhoto(currentUserService.requireCurrentUserId(), file);
    }
}
