package ru.msu.cmc.alumnihub.question.dto;

import ru.msu.cmc.alumnihub.question.entity.Question;

import java.time.Instant;

/**
 * Alumni-facing view of an approved question addressed to them.
 */
public record QuestionDto(
        Long id,
        String senderName,
        String senderEmail,
        String questionText,
        boolean read,
        String answerText,
        Instant answeredAt,
        Instant createdAt) {

    public static QuestionDto from(Question q) {
        return new QuestionDto(
                q.getId(),
                q.getSenderName(),
                q.getSenderEmail(),
                q.getQuestionText(),
                q.isReadByAlumni(),
                q.getAnswerText(),
                q.getAnsweredAt(),
                q.getCreatedAt());
    }
}
