package ru.msu.cmc.alumnihub.user.dto;

import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;

/**
 * Public view of a user. Never exposes the password hash.
 */
public record UserDto(Long id, String email, Role role, boolean enabled, boolean owner) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getRole(),
                user.isEnabled(), user.isOwner());
    }
}
