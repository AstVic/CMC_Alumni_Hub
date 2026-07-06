package ru.msu.cmc.alumnihub.question.dto;

/**
 * Neutral acknowledgement returned to the visitor. Never leaks moderation
 * internals (e.g. whether the AI rejected the message).
 */
public record QuestionSubmissionResponse(boolean accepted, String message) {

    public static QuestionSubmissionResponse received() {
        return new QuestionSubmissionResponse(true,
                "Спасибо! Ваш вопрос отправлен на модерацию и появится после проверки.");
    }
}
