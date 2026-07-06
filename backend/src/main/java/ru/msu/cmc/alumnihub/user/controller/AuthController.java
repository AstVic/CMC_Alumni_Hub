package ru.msu.cmc.alumnihub.user.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.security.CurrentUserService;
import ru.msu.cmc.alumnihub.user.dto.LoginRequest;
import ru.msu.cmc.alumnihub.user.dto.RefreshRequest;
import ru.msu.cmc.alumnihub.user.dto.TokenResponse;
import ru.msu.cmc.alumnihub.user.dto.UserDto;
import ru.msu.cmc.alumnihub.user.service.AuthService;

/**
 * Authentication endpoints shared by admins and alumni.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @GetMapping("/me")
    public UserDto me() {
        return UserDto.from(currentUserService.requireCurrentUser());
    }
}
