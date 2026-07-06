package ru.msu.cmc.alumnihub.moderation;

/**
 * Outcome of an automatic moderation pass.
 */
public record ModerationResult(Decision decision, String reason) {

    public enum Decision {
        APPROVED,
        NEEDS_REVIEW,
        REJECTED
    }

    public static ModerationResult approved() {
        return new ModerationResult(Decision.APPROVED, null);
    }

    public static ModerationResult needsReview(String reason) {
        return new ModerationResult(Decision.NEEDS_REVIEW, reason);
    }

    public static ModerationResult rejected(String reason) {
        return new ModerationResult(Decision.REJECTED, reason);
    }
}
