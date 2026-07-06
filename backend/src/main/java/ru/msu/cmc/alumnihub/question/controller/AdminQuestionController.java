package ru.msu.cmc.alumnihub.question.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.question.dto.AdminQuestionDto;
import ru.msu.cmc.alumnihub.question.dto.RejectQuestionRequest;
import ru.msu.cmc.alumnihub.question.entity.QuestionStatus;
import ru.msu.cmc.alumnihub.question.service.QuestionService;

import java.util.List;

/**
 * Admin moderation and overview of all questions.
 */
@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<AdminQuestionDto> list(@RequestParam(required = false) QuestionStatus status) {
        return questionService.adminList(status);
    }

    @GetMapping("/moderation")
    public List<AdminQuestionDto> moderationQueue() {
        return questionService.adminModerationQueue();
    }

    @GetMapping("/rejected")
    public List<AdminQuestionDto> rejectedQueue() {
        return questionService.adminRejectedQueue();
    }

    @PatchMapping("/{id}/approve")
    public AdminQuestionDto approve(@PathVariable Long id) {
        return questionService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public AdminQuestionDto reject(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) RejectQuestionRequest request) {
        String comment = request == null ? null : request.comment();
        return questionService.reject(id, comment);
    }
}
