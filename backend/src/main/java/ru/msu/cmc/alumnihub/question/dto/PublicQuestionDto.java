package ru.msu.cmc.alumnihub.question.dto;

import ru.msu.cmc.alumnihub.question.entity.Question;

import java.time.Instant;

/**
 * Public view of an approved question shown on a profile card, with the alumni's
 * answer if one exists. Never exposes the sender's email.
 */
public record PublicQuestionDto(
        Long id,
        String senderName,
        String questionText,
        String answerText,
        Instant createdAt,
        Instant answeredAt) {

    public static PublicQuestionDto from(Question q) {
        return new PublicQuestionDto(
                q.getId(),
                q.getSenderName(),
                q.getQuestionText(),
                q.getAnswerText(),
                q.getCreatedAt(),
                q.getAnsweredAt());
    }
}
