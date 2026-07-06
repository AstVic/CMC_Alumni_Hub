package ru.msu.cmc.alumnihub.invite.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Alumni self-registration payload. The email is NOT accepted here — it is taken
 * from the invitation to prevent tampering.
 */
public record RegisterByInviteRequest(
        @NotBlank String token,
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank String passwordConfirm,
        @AssertTrue(message = "Необходимо принять правила сайта") boolean acceptedRules) {

    @AssertTrue(message = "Пароли не совпадают")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}
