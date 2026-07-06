package ru.msu.cmc.alumnihub.invite.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.invite.dto.InviteValidationResponse;
import ru.msu.cmc.alumnihub.invite.dto.RegisterByInviteRequest;
import ru.msu.cmc.alumnihub.invite.service.InviteService;
import ru.msu.cmc.alumnihub.user.dto.TokenResponse;

/**
 * Public endpoints for invite validation and alumni self-registration.
 */
@RestController
public class PublicInviteController {

    private final InviteService inviteService;

    public PublicInviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @GetMapping("/api/public/invites/validate")
    public InviteValidationResponse validate(@RequestParam String token) {
        return inviteService.validate(token);
    }

    @PostMapping("/api/public/auth/register-by-invite")
    public TokenResponse registerByInvite(@Valid @RequestBody RegisterByInviteRequest request) {
        return inviteService.registerByInvite(request);
    }
}
