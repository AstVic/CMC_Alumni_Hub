package ru.msu.cmc.alumnihub.question.entity;

/**
 * Full moderation lifecycle of a visitor's question.
 */
public enum QuestionStatus {
    PENDING_MODERATION,
    AI_APPROVED,
    AI_REJECTED,
    PENDING_ADMIN_REVIEW,
    APPROVED_BY_ADMIN,
    REJECTED_BY_ADMIN,
    VISIBLE_TO_ALUMNI,
    ARCHIVED
}
