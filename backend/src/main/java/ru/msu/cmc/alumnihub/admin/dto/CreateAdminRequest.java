package ru.msu.cmc.alumnihub.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Owner creates a new administrator with an initial password.
 */
public record CreateAdminRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password) {
}
