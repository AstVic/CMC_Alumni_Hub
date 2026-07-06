package ru.msu.cmc.alumnihub.invite.dto;

import ru.msu.cmc.alumnihub.user.entity.Role;

/**
 * Result of validating an invite token. On VALID, the email and target role
 * (from the invite) are returned so the registration form can adapt.
 */
public record InviteValidationResponse(Result result, String email, Role role) {

    public enum Result {
        VALID,
        INVALID,
        EXPIRED,
        USED,
        REVOKED
    }

    public static InviteValidationResponse valid(String email, Role role) {
        return new InviteValidationResponse(Result.VALID, email, role);
    }

    public static InviteValidationResponse of(Result result) {
        return new InviteValidationResponse(result, null, null);
    }
}
