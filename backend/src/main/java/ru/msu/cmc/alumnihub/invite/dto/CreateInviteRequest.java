package ru.msu.cmc.alumnihub.invite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInviteRequest(
        @NotBlank @Email String email,
        @Size(max = 500) String note) {
}
