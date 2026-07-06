package ru.msu.cmc.alumnihub.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.msu.cmc.alumnihub.common.exception.ForbiddenException;
import ru.msu.cmc.alumnihub.user.entity.User;

/**
 * Convenience accessor for the currently authenticated user.
 */
@Component
public class CurrentUserService {

    public User requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new ForbiddenException("Требуется аутентификация");
        }
        return securityUser.getDomainUser();
    }

    public Long requireCurrentUserId() {
        return requireCurrentUser().getId();
    }
}
