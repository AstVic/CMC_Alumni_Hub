package ru.msu.cmc.alumnihub.invite.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.invite.dto.CreateInviteRequest;
import ru.msu.cmc.alumnihub.invite.dto.InviteDto;
import ru.msu.cmc.alumnihub.invite.service.InviteService;
import ru.msu.cmc.alumnihub.security.CurrentUserService;
import ru.msu.cmc.alumnihub.user.entity.Role;

import java.util.List;

/**
 * Admin management of alumni invitations.
 */
@RestController
@RequestMapping("/api/admin/invites")
public class AdminInviteController {

    private final InviteService inviteService;
    private final CurrentUserService currentUserService;

    public AdminInviteController(InviteService inviteService, CurrentUserService currentUserService) {
        this.inviteService = inviteService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InviteDto create(@Valid @RequestBody CreateInviteRequest request) {
        return inviteService.createInvite(
                request, currentUserService.requireCurrentUserId(), Role.ALUMNI);
    }

    @GetMapping
    public List<InviteDto> list() {
        return inviteService.listInvites(Role.ALUMNI);
    }

    @PostMapping("/{id}/resend")
    public InviteDto resend(@PathVariable Long id) {
        return inviteService.resend(id, Role.ALUMNI);
    }

    @PatchMapping("/{id}/revoke")
    public InviteDto revoke(@PathVariable Long id) {
        return inviteService.revoke(id, Role.ALUMNI);
    }
}
