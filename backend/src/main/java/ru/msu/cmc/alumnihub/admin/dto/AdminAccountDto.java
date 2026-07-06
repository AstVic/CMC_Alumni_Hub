package ru.msu.cmc.alumnihub.admin.dto;

import ru.msu.cmc.alumnihub.user.entity.User;

import java.time.Instant;

/**
 * Admin account as seen by the owner in the admin-management section.
 */
public record AdminAccountDto(
        Long id,
        String email,
        boolean enabled,
        boolean owner,
        Instant createdAt) {

    public static AdminAccountDto from(User user) {
        return new AdminAccountDto(
                user.getId(), user.getEmail(), user.isEnabled(), user.isOwner(), user.getCreatedAt());
    }
}
