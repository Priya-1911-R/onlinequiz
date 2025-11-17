package com.example.onlinequiz.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.onlinequiz.dto.QuestionDTO;
import com.example.onlinequiz.dto.QuizDTO;
import com.example.onlinequiz.model.Option;
import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizService;

@Controller
@RequestMapping("/admin") 
public class AdminController {
    
    @Autowired
    private QuizService quizService;
    
    // Dashboard - http://localhost:8081/admin/dashboard
    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal User user, Model model) {
        try {
            if (user == null) {
                return "redirect:/login";
            }
            
            List<Quiz> quizzes = quizService.getQuizzesByUser(user);
            model.addAttribute("quizzes", quizzes);
            model.addAttribute("recentQuizzes", quizzes);
            model.addAttribute("recentAttempts", List.of());
            model.addAttribute("adminStats", new AdminStats(quizzes.size(), 0, 0, 0));
            
            return "admin/dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("quizzes", List.of());
            model.addAttribute("recentQuizzes", List.of());
            model.addAttribute("recentAttempts", List.of());
            model.addAttribute("adminStats", new AdminStats(0, 0, 0, 0));
            model.addAttribute("error", "Unable to load dashboard");
            return "admin/dashboard";
        }
    }
    
    // Manage Quizzes - http://localhost:8081/admin/quizzes
    @GetMapping("/quizzes")
    public String manageQuizzes(@AuthenticationPrincipal User user, Model model) {
        try {
            if (user == null) {
                return "redirect:/login";
            }
            
            List<Quiz> quizzes = quizService.getQuizzesByUser(user);
            model.addAttribute("quizzes", quizzes);
            return "admin/manage-quizzes";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("quizzes", List.of());
            model.addAttribute("error", "Unable to load quizzes");
            return "admin/manage-quizzes";
        }
    }

    // Show create quiz form
    @GetMapping("/quizzes/create")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("quizDTO", new QuizDTO());
            return "admin/create-quiz";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/quizzes?error=Unable to load create form";
        }
    }

    // Create new quiz - UPDATED VERSION
    @PostMapping("/quizzes/create")
    public String createQuiz(@ModelAttribute QuizDTO quizDTO,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CREATING QUIZ ===");
            System.out.println("Title: " + quizDTO.getTitle());
            System.out.println("Time Limit: " + quizDTO.getTimeLimit());
            System.out.println("Active: " + quizDTO.getActive());
            System.out.println("Public Quiz: " + quizDTO.getPublicQuiz());
            
            // Debug the incoming questions data
            if (quizDTO.getQuestions() != null) {
                System.out.println("Number of questions: " + quizDTO.getQuestions().size());
                for (int i = 0; i < quizDTO.getQuestions().size(); i++) {
                    QuestionDTO q = quizDTO.getQuestions().get(i);
                    System.out.println("Question " + i + ": " + q.getText());
                    System.out.println("  Correct Answer: " + q.getCorrectAnswer());
                    if (q.getOptions() != null) {
                        System.out.println("  Options: " + q.getOptions().size());
                        for (int j = 0; j < q.getOptions().size(); j++) {
                            System.out.println("    Option " + j + ": " + q.getOptions().get(j));
                        }
                    } else {
                        System.out.println("  Options: NULL");
                    }
                }
            } else {
                System.out.println("Questions: NULL");
            }

            // Validate basic quiz info
            if (quizDTO.getTitle() == null || quizDTO.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Quiz title is required");
                return "redirect:/admin/quizzes/create";
            }
            
            if (quizDTO.getTimeLimit() == null || quizDTO.getTimeLimit() < 1) {
                redirectAttributes.addFlashAttribute("error", "Time limit must be at least 1 minute");
                return "redirect:/admin/quizzes/create";
            }

            // Validate questions
            if (quizDTO.getQuestions() == null || quizDTO.getQuestions().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "At least one question is required");
                return "redirect:/admin/quizzes/create";
            }

            // Convert DTO to Entity
            Quiz quiz = new Quiz();
            quiz.setTitle(quizDTO.getTitle().trim());
            quiz.setDescription(quizDTO.getDescription() != null ? quizDTO.getDescription().trim() : "");
            quiz.setTimeLimit(quizDTO.getTimeLimit());
            quiz.setActive(quizDTO.getActive() != null ? quizDTO.getActive() : true);
            quiz.setPublicQuiz(quizDTO.getPublicQuiz() != null ? quizDTO.getPublicQuiz() : false);
            quiz.setCreatedBy(user);

            // Convert questions
            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < quizDTO.getQuestions().size(); i++) {
                QuestionDTO questionDTO = quizDTO.getQuestions().get(i);
                
                // Skip empty questions
                if (questionDTO.getText() == null || questionDTO.getText().trim().isEmpty()) {
                    continue;
                }
                
                Question question = new Question();
                question.setText(questionDTO.getText().trim());
                question.setQuiz(quiz);
                
                // Create options
                List<Option> options = new ArrayList<>();
                if (questionDTO.getOptions() != null) {
                    for (int j = 0; j < questionDTO.getOptions().size(); j++) {
                        String optionText = questionDTO.getOptions().get(j);
                        if (optionText != null && !optionText.trim().isEmpty()) {
                            Option option = new Option();
                            option.setText(optionText.trim());
                            // Set correct option based on correctAnswer index
                            option.setCorrect(j == questionDTO.getCorrectAnswer());
                            option.setQuestion(question);
                            options.add(option);
                        }
                    }
                }
                
                // Validate we have at least 2 options and a correct answer
                if (options.size() < 2) {
                    redirectAttributes.addFlashAttribute("error", "Each question must have at least 2 options");
                    return "redirect:/admin/quizzes/create";
                }
                
                // Check if correct answer is within bounds
                if (questionDTO.getCorrectAnswer() == null || 
                    questionDTO.getCorrectAnswer() < 0 || 
                    questionDTO.getCorrectAnswer() >= options.size()) {
                    redirectAttributes.addFlashAttribute("error", "Please select a correct answer for all questions");
                    return "redirect:/admin/quizzes/create";
                }
                
                question.setOptions(options);
                questions.add(question);
            }
            
            if (questions.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "At least one valid question is required");
                return "redirect:/admin/quizzes/create";
            }
            
            quiz.setQuestions(questions);
            
            // Save the quiz
            Quiz savedQuiz = quizService.createQuiz(quiz);
            
            System.out.println("✅ Quiz created successfully with ID: " + savedQuiz.getId());
            redirectAttributes.addFlashAttribute("success", "Quiz '" + savedQuiz.getTitle() + "' created successfully!");
            return "redirect:/admin/quizzes?success=Quiz+created+successfully";
            
        } catch (Exception e) {
            System.err.println("❌ Error creating quiz: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to create quiz: " + e.getMessage());
            return "redirect:/admin/quizzes/create";
        }
    }
    
    // Simple DTO for admin statistics
    public static class AdminStats {
        private final int totalQuizzes;
        private final int totalUsers;
        private final int totalAttempts;
        private final int averageScore;
        
        public AdminStats(int totalQuizzes, int totalUsers, int totalAttempts, int averageScore) {
            this.totalQuizzes = totalQuizzes;
            this.totalUsers = totalUsers;
            this.totalAttempts = totalAttempts;
            this.averageScore = averageScore;
        }
        
        public int getTotalQuizzes() { return totalQuizzes; }
        public int getTotalUsers() { return totalUsers; }
        public int getTotalAttempts() { return totalAttempts; }
        public int getAverageScore() { return averageScore; }
    }
}