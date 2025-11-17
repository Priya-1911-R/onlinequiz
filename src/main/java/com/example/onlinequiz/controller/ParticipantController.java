package com.example.onlinequiz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizAttemptService;

@Controller
@RequestMapping("/participant")
public class ParticipantController {
    
    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @GetMapping("/results")
    public String myResults(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(user);
            model.addAttribute("attempts", attempts);
            model.addAttribute("user", user);
            
            // Calculate statistics for results page
            if (!attempts.isEmpty()) {
                long totalAttempts = attempts.size();
                double averageScore = attempts.stream()
                    .mapToDouble(attempt -> {
                        if (attempt.getQuiz().getQuestions() != null && !attempt.getQuiz().getQuestions().isEmpty()) {
                            return (attempt.getScore() * 100.0) / attempt.getQuiz().getQuestions().size();
                        }
                        return 0.0;
                    })
                    .average()
                    .orElse(0.0);
                double bestScore = attempts.stream()
                    .mapToDouble(attempt -> {
                        if (attempt.getQuiz().getQuestions() != null && !attempt.getQuiz().getQuestions().isEmpty()) {
                            return (attempt.getScore() * 100.0) / attempt.getQuiz().getQuestions().size();
                        }
                        return 0.0;
                    })
                    .max()
                    .orElse(0.0);
                long quizzesTaken = attempts.stream()
                    .map(attempt -> attempt.getQuiz().getId())
                    .distinct()
                    .count();
                
                model.addAttribute("totalAttempts", totalAttempts);
                model.addAttribute("averageScore", Math.round(averageScore));
                model.addAttribute("bestScore", Math.round(bestScore));
                model.addAttribute("quizzesTaken", quizzesTaken);
            } else {
                // Default values when no attempts
                model.addAttribute("totalAttempts", 0);
                model.addAttribute("averageScore", 0);
                model.addAttribute("bestScore", 0);
                model.addAttribute("quizzesTaken", 0);
            }
            
            return "participant/results";
        } catch (Exception e) {
            model.addAttribute("attempts", List.of());
            model.addAttribute("user", user);
            model.addAttribute("totalAttempts", 0);
            model.addAttribute("averageScore", 0);
            model.addAttribute("bestScore", 0);
            model.addAttribute("quizzesTaken", 0);
            model.addAttribute("error", "Unable to load results");
            return "participant/results";
        }
    }
    @GetMapping("/dashboard")
public String dashboard(@AuthenticationPrincipal User user, Model model) {
    if (user == null) {
        return "redirect:/login";
    }
    
    try {
        // Add your dashboard logic here
        List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(user);
        model.addAttribute("attempts", attempts);
        model.addAttribute("user", user);
        return "participant/dashboard";
    } catch (Exception e) {
        model.addAttribute("attempts", List.of());
        model.addAttribute("user", user);
        model.addAttribute("error", "Unable to load dashboard");
        return "participant/dashboard";
    }
}
}