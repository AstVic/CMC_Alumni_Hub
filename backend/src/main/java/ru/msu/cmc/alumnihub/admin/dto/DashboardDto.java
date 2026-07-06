package ru.msu.cmc.alumnihub.admin.dto;

/**
 * Aggregated statistics for the admin dashboard.
 */
public record DashboardDto(
        long totalAlumni,
        long publishedProfiles,
        long profilesOnModeration,
        long totalQuestions,
        long questionsOnModeration,
        long rejectedQuestions,
        long usedInvites,
        long pendingInvites) {
}
