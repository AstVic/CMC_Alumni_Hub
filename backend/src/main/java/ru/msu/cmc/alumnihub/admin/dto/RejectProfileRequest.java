package ru.msu.cmc.alumnihub.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectProfileRequest(
        @NotBlank @Size(max = 1000) String comment) {
}
