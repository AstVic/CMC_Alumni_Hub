package ru.msu.cmc.alumnihub.moderation;

/**
 * Automatic content moderation. Implemented for MVP by simple rules/heuristics
 * ({@code rule-based}); prepared to be swapped for a real AI API ({@code ai})
 * via the {@code app.moderation.provider} property.
 */
public interface ModerationProvider {

    ModerationResult moderateQuestion(String text);
}
