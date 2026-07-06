package ru.msu.cmc.alumnihub.question.dto;

import jakarta.validation.constraints.Size;

public record RejectQuestionRequest(@Size(max = 1000) String comment) {
}
