package ru.msu.cmc.alumnihub.admin.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.admin.dto.RejectProfileRequest;
import ru.msu.cmc.alumnihub.admin.service.ProfileModerationService;
import ru.msu.cmc.alumnihub.profile.dto.AlumniProfileDto;

import java.util.List;

/**
 * Admin moderation of alumni profile cards. Path {id} is the profile id.
 */
@RestController
@RequestMapping("/api/admin/profiles")
public class AdminProfileController {

    private final ProfileModerationService moderationService;

    public AdminProfileController(ProfileModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping
    public List<AlumniProfileDto> all() {
        return moderationService.allProfiles();
    }

    @GetMapping("/moderation")
    public List<AlumniProfileDto> moderationQueue() {
        return moderationService.moderationQueue();
    }

    @PatchMapping("/{id}/approve")
    public AlumniProfileDto approve(@PathVariable Long id) {
        return moderationService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public AlumniProfileDto reject(@PathVariable Long id,
                                   @Valid @RequestBody RejectProfileRequest request) {
        return moderationService.reject(id, request.comment());
    }
}
