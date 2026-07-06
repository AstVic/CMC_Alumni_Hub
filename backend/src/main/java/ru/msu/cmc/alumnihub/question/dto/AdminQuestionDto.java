package ru.msu.cmc.alumnihub.question.dto;

import ru.msu.cmc.alumnihub.question.entity.AiModerationStatus;
import ru.msu.cmc.alumnihub.question.entity.Question;
import ru.msu.cmc.alumnihub.question.entity.QuestionStatus;

import java.time.Instant;

/**
 * Full admin view of a question, including whom it was asked and AI verdict.
 */
public record AdminQuestionDto(
        Long id,
        Long alumniProfileId,
        String alumniName,
        String senderName,
        String senderEmail,
        String questionText,
        QuestionStatus status,
        AiModerationStatus aiModerationStatus,
        String aiModerationReason,
        String adminModerationComment,
        boolean readByAlumni,
        Instant createdAt) {

    public static AdminQuestionDto from(Question q, String alumniName) {
        return new AdminQuestionDto(
                q.getId(),
                q.getAlumniProfileId(),
                alumniName,
                q.getSenderName(),
                q.getSenderEmail(),
                q.getQuestionText(),
                q.getStatus(),
                q.getAiModerationStatus(),
                q.getAiModerationReason(),
                q.getAdminModerationComment(),
                q.isReadByAlumni(),
                q.getCreatedAt());
    }
}
