package com.hoidap.hoidapdemo.infrastructure.adapter.web.controller.admin;

import com.hoidap.hoidapdemo.application.service.QuestionServiceImpl;
import com.hoidap.hoidapdemo.infrastructure.adapter.data.entity.question.QuestionJpaEntity;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.common.PageResponse;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question.QuestionFilter;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question.QuestionRequest;
import com.hoidap.hoidapdemo.infrastructure.adapter.web.dto.question.QuestionResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/questions")
public class QuestionAdminController {
    private final QuestionServiceImpl questionService;

    public QuestionAdminController(QuestionServiceImpl questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public String listQuestions(
            @ModelAttribute QuestionFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        PageResponse<QuestionResponse> pageData = questionService.getAllQuestions(filter, page, size);

        model.addAttribute("listQuestions", pageData.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("totalItems", pageData.getTotalElements());

        model.addAttribute("filter", filter);

        return "admin/question/list";
    }

    @GetMapping("/detail/{id}")
    public String detailQuestion(@PathVariable Long id, Model model) {
        Map<String, Object> details = questionService.getQuestionDetailForAdmin(id);

        model.addAttribute("question", details.get("question"));
        model.addAttribute("answer", details.get("answer"));
        model.addAttribute("history", details.get("history"));

        return "admin/question/detail";
    }

    @GetMapping("/edit/{id}")
    public String editQuestion(@PathVariable Long id, Model model) {
        QuestionJpaEntity q = questionService.getQuestionEntityById(id);

        QuestionRequest dto = new QuestionRequest();
        dto.setTieuDe(q.getTieuDe());
        dto.setNoiDung(q.getNoiDung());

        model.addAttribute("questionRequest", dto);
        model.addAttribute("questionId", id);

        return "admin/question/form";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable Long id, @ModelAttribute QuestionRequest request) {
        questionService.adminUpdateQuestion(id, request);
        return "redirect:/admin/questions";
    }

    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/admin/questions";
    }
}
