package ru.msu.cmc.alumnihub.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.auth.dto.ForgotPasswordRequest;
import ru.msu.cmc.alumnihub.auth.dto.ResetPasswordRequest;
import ru.msu.cmc.alumnihub.auth.service.PasswordResetService;

import java.util.Map;

/**
 * Public "forgot password" endpoints.
 */
@RestController
@RequestMapping("/api/public/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request);
        // Always the same response, whether or not the email is registered.
        return Map.of("message",
                "Если аккаунт с этим email существует, мы отправили письмо со ссылкой для восстановления пароля.");
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
    }
}
