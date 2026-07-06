package ru.msu.cmc.alumnihub.question.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Visitor question submission. {@code website} is a hidden honeypot field that
 * must stay empty; bots that fill it are silently rejected.
 */
public record CreateQuestionRequest(
        @Size(max = 255) String senderName,
        @Email @Size(max = 255) String senderEmail,
        @NotBlank @Size(min = 5, max = 2000) String questionText,
        @AssertTrue(message = "Необходимо принять правила сайта") boolean acceptedRules,
        String website) {

    public boolean isHoneypotTriggered() {
        return website != null && !website.isBlank();
    }
}
