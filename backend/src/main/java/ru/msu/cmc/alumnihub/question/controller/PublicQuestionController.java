package ru.msu.cmc.alumnihub.question.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.question.dto.CreateQuestionRequest;
import ru.msu.cmc.alumnihub.question.dto.QuestionSubmissionResponse;
import ru.msu.cmc.alumnihub.question.service.QuestionService;

/**
 * Public submission of questions to a specific alumni profile.
 */
@RestController
@RequestMapping("/api/public/profiles/{profileId}/questions")
public class PublicQuestionController {

    private final QuestionService questionService;

    public PublicQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public QuestionSubmissionResponse submit(@PathVariable Long profileId,
                                             @Valid @RequestBody CreateQuestionRequest request,
                                             HttpServletRequest servletRequest) {
        return questionService.submit(profileId, request, clientIp(servletRequest));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
