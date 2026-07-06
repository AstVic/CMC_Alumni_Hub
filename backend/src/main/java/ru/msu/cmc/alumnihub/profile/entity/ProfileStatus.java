package ru.msu.cmc.alumnihub.profile.entity;

/**
 * Moderation lifecycle of an alumni profile card.
 */
public enum ProfileStatus {
    DRAFT,
    PENDING_MODERATION,
    PUBLISHED,
    REJECTED
}
