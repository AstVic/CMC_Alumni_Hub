package ru.msu.cmc.alumnihub.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Alumni's answer to an approved question.
 */
public record AnswerQuestionRequest(
        @NotBlank @Size(min = 1, max = 4000) String answerText) {
}
