package com.example.onlinequiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.onlinequiz.dto.QuizDTO;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizService;

@Controller
@RequestMapping("/admin/quiz") // Changed from "/admin/quizzes" to "/admin/quiz"
public class QuizAdminController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("quizDTO", new QuizDTO());
        return "admin/create-quiz";
    }

    @PostMapping("/create")
    public String createQuiz(@ModelAttribute QuizDTO quizDTO,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes) {
        try {
            // ... (existing code)

            // Redirect to the admin quizzes list (which is in AdminController)
            redirectAttributes.addFlashAttribute("success", "Quiz created successfully!");
            return "redirect:/admin/quizzes"; // This redirect goes to AdminController's manageQuizzes method
            
        } catch (Exception e) {
            // ... (existing code)
            return "redirect:/admin/quiz/create"; // Now using the new base path
        }
    }

    // Since we are using AdminController for listing, we can remove the listQuizzes method from this controller.
    // But if we want to keep it, we can change the mapping to avoid conflict.
    // However, note that the redirect in createQuiz goes to /admin/quizzes, which is handled by AdminController.

    // Let's remove the listQuizzes method from this controller to avoid ambiguity.
    // Alternatively, if we want to keep it, we can change the mapping to something else, but then we have two list methods.

    // Since the requirement is to fix the ambiguous mapping, and we already have a method in AdminController for listing,
    // I will remove the listQuizzes method from QuizAdminController.

    // If you remove this method, then the QuizAdminController will only have create functionality.
    // If you want to keep the listQuizzes method, then change its mapping to something else, e.g., "/list" and then the full path would be "/admin/quiz/list".

    // I will comment out the listQuizzes method for now.

    // @GetMapping
    // public String listQuizzes(Model model) {
    //     List<Quiz> quizzes = quizService.getAllQuizzes();
    //     model.addAttribute("quizzes", quizzes);
    //     return "admin/quizzes";
    // }
}