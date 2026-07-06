package ru.msu.cmc.alumnihub.question.entity;

/**
 * Outcome of the automatic (AI/rule-based) moderation pass.
 */
public enum AiModerationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    NEEDS_REVIEW
}
