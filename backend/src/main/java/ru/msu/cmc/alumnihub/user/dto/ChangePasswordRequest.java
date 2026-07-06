package ru.msu.cmc.alumnihub.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Change-own-password payload for any authenticated user.
 */
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, max = 100) String newPassword,
        @NotBlank String newPasswordConfirm) {

    @AssertTrue(message = "Новые пароли не совпадают")
    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(newPasswordConfirm);
    }
}
