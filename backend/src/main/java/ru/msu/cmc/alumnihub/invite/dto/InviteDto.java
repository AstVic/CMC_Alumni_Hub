package ru.msu.cmc.alumnihub.invite.dto;

import ru.msu.cmc.alumnihub.invite.entity.AlumniInvite;
import ru.msu.cmc.alumnihub.invite.entity.InviteStatus;
import ru.msu.cmc.alumnihub.user.entity.Role;

import java.time.Instant;

/**
 * Admin-facing view of an invitation. Never exposes the token hash.
 */
public record InviteDto(
        Long id,
        String email,
        Role role,
        InviteStatus status,
        Instant createdAt,
        Instant expiresAt,
        Instant usedAt,
        Instant revokedAt,
        String note) {

    public static InviteDto from(AlumniInvite invite) {
        return new InviteDto(
                invite.getId(),
                invite.getEmail(),
                invite.getRole(),
                invite.getStatus(),
                invite.getCreatedAt(),
                invite.getExpiresAt(),
                invite.getUsedAt(),
                invite.getRevokedAt(),
                invite.getNote());
    }
}
