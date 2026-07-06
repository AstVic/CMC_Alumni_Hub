package ru.msu.cmc.alumnihub.invite.dto;

/**
 * Result of validating an invite token. On VALID, the email (from the invite)
 * is returned so the registration form can show it read-only.
 */
public record InviteValidationResponse(Result result, String email) {

    public enum Result {
        VALID,
        INVALID,
        EXPIRED,
        USED,
        REVOKED
    }

    public static InviteValidationResponse valid(String email) {
        return new InviteValidationResponse(Result.VALID, email);
    }

    public static InviteValidationResponse of(Result result) {
        return new InviteValidationResponse(result, null);
    }
}
