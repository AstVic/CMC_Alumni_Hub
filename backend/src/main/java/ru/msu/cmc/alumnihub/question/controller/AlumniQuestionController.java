package ru.msu.cmc.alumnihub.question.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.question.dto.QuestionDto;
import ru.msu.cmc.alumnihub.question.service.QuestionService;
import ru.msu.cmc.alumnihub.security.CurrentUserService;

import java.util.List;

/**
 * Alumni access to questions addressed to them (approved and visible only).
 */
@RestController
@RequestMapping("/api/alumni/questions")
public class AlumniQuestionController {

    private final QuestionService questionService;
    private final CurrentUserService currentUserService;

    public AlumniQuestionController(QuestionService questionService,
                                    CurrentUserService currentUserService) {
        this.questionService = questionService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<QuestionDto> list(@RequestParam(required = false) String filter) {
        return questionService.listForAlumni(currentUserService.requireCurrentUserId(), filter);
    }

    @PatchMapping("/{id}/read")
    public QuestionDto markRead(@PathVariable Long id) {
        return questionService.markRead(currentUserService.requireCurrentUserId(), id);
    }
}
