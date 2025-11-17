package com.example.onlinequiz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.onlinequiz.model.Option;
import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.EmailService;
import com.example.onlinequiz.service.QuizAttemptService;
import com.example.onlinequiz.service.QuizService;

@Controller
@RequestMapping("/quiz")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @Autowired
    private EmailService emailService;
    
    // Add this inner class to hold quiz results
    public static class QuizResult {
        private int score;
        private int totalQuestions;
        private double percentage;
        private Quiz quiz;
        private Map<Long, String> userAnswers;
        private Map<Long, String> correctAnswers;
        
        public QuizResult() {}
        
        public QuizResult(int score, int totalQuestions, Quiz quiz) {
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0;
            this.quiz = quiz;
        }
        
        // Getters and setters
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        
        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
        
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
        
        public Quiz getQuiz() { return quiz; }
        public void setQuiz(Quiz quiz) { this.quiz = quiz; }
        
        public Map<Long, String> getUserAnswers() { return userAnswers; }
        public void setUserAnswers(Map<Long, String> userAnswers) { this.userAnswers = userAnswers; }
        
        public Map<Long, String> getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(Map<Long, String> correctAnswers) { this.correctAnswers = correctAnswers; }
    }
    
    @GetMapping("/browse")
    public String browseQuizzes(@AuthenticationPrincipal User user, Model model) {
        try {
            System.out.println("=== BROWSE QUIZZES CALLED ===");
            System.out.println("User: " + (user != null ? user.getUsername() : "null"));
            
            List<Quiz> quizzes = quizService.getAllActiveQuizzes();
            
            // If no quizzes exist, create sample quizzes
            if (quizzes == null || quizzes.isEmpty()) {
                System.out.println("No quizzes found in database, creating sample quizzes");
                quizzes = createSampleQuizzes();
            }
            
            // Create a simple structure for the template
            List<Map<String, Object>> quizData = new ArrayList<>();
            for (Quiz quiz : quizzes) {
                Map<String, Object> quizMap = new HashMap<>();
                quizMap.put("id", quiz.getId());
                quizMap.put("title", quiz.getTitle());
                quizMap.put("description", quiz.getDescription());
                quizMap.put("category", quiz.getCategory());
                quizMap.put("difficulty", quiz.getDifficulty());
                quizMap.put("timeLimit", quiz.getTimeLimit());
                quizMap.put("questions", quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
                quizMap.put("attemptCount", 0); // Default value
                quizMap.put("userAttempted", false); // Default value
                
                quizData.add(quizMap);
            }
            
            model.addAttribute("quizzes", quizData);
            model.addAttribute("totalQuizzes", quizData.size());
            model.addAttribute("userAttemptsCount", 0);
            model.addAttribute("averageScore", 0);
            model.addAttribute("completedQuizzes", 0);
            model.addAttribute("debug", true); // Enable debug info
            
            System.out.println("=== SUCCESS - RENDERING BROWSE PAGE ===");
            System.out.println("Quizzes loaded: " + quizData.size());
            
            return "quiz/browse";
        } catch (Exception e) {
            System.err.println("=== ERROR in browseQuizzes ===");
            e.printStackTrace();
            // Return sample data even on error
            model.addAttribute("quizzes", createSampleQuizData());
            model.addAttribute("error", "Unable to load quizzes. Please try again later.");
            return "quiz/browse";
        }
    }
    
    // Create sample quiz data for the template
    private List<Map<String, Object>> createSampleQuizData() {
        List<Map<String, Object>> quizData = new ArrayList<>();
        
        // HTML/CSS Quiz
        Map<String, Object> htmlQuiz = new HashMap<>();
        htmlQuiz.put("id", 1L);
        htmlQuiz.put("title", "HTML & CSS Fundamentals");
        htmlQuiz.put("description", "Test your knowledge of HTML tags, CSS styling, and responsive design principles.");
        htmlQuiz.put("category", "Web Development");
        htmlQuiz.put("difficulty", "Easy");
        htmlQuiz.put("timeLimit", 20);
        htmlQuiz.put("questions", 3);
        htmlQuiz.put("attemptCount", 1200);
        htmlQuiz.put("userAttempted", false);
        quizData.add(htmlQuiz);
        
        // JavaScript Quiz
        Map<String, Object> jsQuiz = new HashMap<>();
        jsQuiz.put("id", 2L);
        jsQuiz.put("title", "JavaScript Advanced Concepts");
        jsQuiz.put("description", "Challenge yourself with advanced JavaScript concepts including closures, promises, and async/await.");
        jsQuiz.put("category", "Programming");
        jsQuiz.put("difficulty", "Hard");
        jsQuiz.put("timeLimit", 30);
        jsQuiz.put("questions", 3);
        jsQuiz.put("attemptCount", 856);
        jsQuiz.put("userAttempted", false);
        quizData.add(jsQuiz);
        
        return quizData;
    }
    
    // Simplified takeQuiz method for testing
    @GetMapping("/take/{id}")
    public String takeQuiz(@PathVariable Long id, 
                          @AuthenticationPrincipal User user,
                          Model model) {
        try {
            System.out.println("=== TAKE QUIZ CALLED ===");
            System.out.println("Quiz ID: " + id);
            System.out.println("User: " + (user != null ? user.getUsername() : "null"));
            
            // Always use sample quizzes for now to avoid service issues
            List<Quiz> sampleQuizzes = createSampleQuizzes();
            System.out.println("Sample quizzes created: " + sampleQuizzes.size());
            
            Quiz quiz = sampleQuizzes.stream()
                    .filter(q -> q.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
            
            System.out.println("Found quiz: " + quiz.getTitle());
            System.out.println("Questions in quiz: " + (quiz.getQuestions() != null ? quiz.getQuestions().size() : "null"));
            
            if (quiz.getQuestions() != null) {
                for (int i = 0; i < quiz.getQuestions().size(); i++) {
                    Question q = quiz.getQuestions().get(i);
                    System.out.println("Question " + (i+1) + ": " + q.getText());
                    System.out.println("  Options: " + (q.getOptions() != null ? q.getOptions().size() : "null"));
                    if (q.getOptions() != null) {
                        for (int j = 0; j < q.getOptions().size(); j++) {
                            Option opt = q.getOptions().get(j);
                            System.out.println("    Option " + (j+1) + ": " + opt.getText() + " (Correct: " + opt.getCorrect() + ")");
                        }
                    }
                }
            }
            
            // Create a simple attempt object without using the service
            QuizAttempt attempt = new QuizAttempt();
            attempt.setId(System.currentTimeMillis()); // Temporary ID
            
            // Try different setter methods for the timestamp
            try {
                attempt.setStartedAt(java.time.LocalDateTime.now());
            } catch (Exception e) {
                System.out.println("setStartTime failed, trying setStartedAt");
                try {
                    attempt.setStartedAt(java.time.LocalDateTime.now());
                } catch (Exception e2) {
                    System.out.println("Both timestamp setters failed, continuing without timestamp");
                }
            }
            
            attempt.setUser(user);
            attempt.setQuiz(quiz);
            
            model.addAttribute("quiz", quiz);
            model.addAttribute("attempt", attempt);
            model.addAttribute("debug", true); // Enable debug info
            
            System.out.println("=== SUCCESS - RENDERING QUIZ TAKE PAGE ===");
            return "quiz/take";
            
        } catch (Exception e) {
            System.err.println("=== ERROR in takeQuiz ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Unable to start quiz: " + e.getMessage());
            return "redirect:/quiz/browse";
        }
    }
    
    // Simple test endpoint without authentication
    @GetMapping("/test/{id}")
    public String testQuiz(@PathVariable Long id, Model model) {
        try {
            System.out.println("=== TEST QUIZ CALLED ===");
            System.out.println("Quiz ID: " + id);
            
            List<Quiz> sampleQuizzes = createSampleQuizzes();
            Quiz quiz = sampleQuizzes.stream()
                    .filter(q -> q.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
            
            QuizAttempt attempt = new QuizAttempt();
            attempt.setId(System.currentTimeMillis());
            
            model.addAttribute("quiz", quiz);
            model.addAttribute("attempt", attempt);
            model.addAttribute("debug", true);
            
            System.out.println("=== SUCCESS - RENDERING TEST QUIZ PAGE ===");
            return "quiz/take";
            
        } catch (Exception e) {
            System.err.println("=== ERROR in testQuiz ===");
            e.printStackTrace();
            model.addAttribute("error", "Test failed: " + e.getMessage());
            return "redirect:/quiz/browse";
        }
    }
    
    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Long quizId,
                           @RequestParam Long attemptId,
                           @RequestParam Map<String, String> answers,
                           @AuthenticationPrincipal User user,
                           Model model) {
        try {
            System.out.println("=== QUIZ SUBMISSION ===");
            System.out.println("Quiz ID: " + quizId);
            System.out.println("Attempt ID: " + attemptId);
            System.out.println("Answers received: " + answers);
            
            // Get the quiz from sample quizzes
            List<Quiz> sampleQuizzes = createSampleQuizzes();
            Quiz quiz = sampleQuizzes.stream()
                    .filter(q -> q.getId().equals(quizId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
            
            // Calculate score
            int score = 0;
            int totalQuestions = quiz.getQuestions().size();
            Map<Long, String> userAnswers = new HashMap<>();
            Map<Long, String> correctAnswers = new HashMap<>();
            
            for (Question question : quiz.getQuestions()) {
                String answerKey = "question_" + question.getId();
                String userAnswerId = answers.get(answerKey);
                
                if (userAnswerId != null) {
                    // Store user's answer
                    Long selectedOptionId = Long.parseLong(userAnswerId);
                    Option selectedOption = question.getOptions().stream()
                            .filter(opt -> opt.getId().equals(selectedOptionId))
                            .findFirst()
                            .orElse(null);
                    
                    if (selectedOption != null) {
                        userAnswers.put(question.getId(), selectedOption.getText());
                        
                        // Find correct option
                        Option correctOption = question.getOptions().stream()
                                .filter(Option::getCorrect)
                                .findFirst()
                                .orElse(null);
                        
                        if (correctOption != null) {
                            correctAnswers.put(question.getId(), correctOption.getText());
                            
                            // Check if answer is correct
                            if (selectedOption.getCorrect()) {
                                score++;
                            }
                        }
                    }
                }
            }
            
            // Create quiz result
            QuizResult result = new QuizResult(score, totalQuestions, quiz);
            result.setUserAnswers(userAnswers);
            result.setCorrectAnswers(correctAnswers);
            
            System.out.println("Quiz scored: " + score + "/" + totalQuestions + " (" + result.getPercentage() + "%)");
            
            // Add result to model
            model.addAttribute("result", result);
            model.addAttribute("quiz", quiz);
            
            System.out.println("=== SUCCESS - QUIZ SUBMITTED AND SCORED ===");
            return "quiz/results";
            
        } catch (Exception e) {
            System.err.println("=== ERROR in submitQuiz ===");
            e.printStackTrace();
            model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
            return "quiz/results";
        }
    }
    
    // Alternative submit method with path variable
    @PostMapping("/submit/{attemptId}")
    public String submitQuizWithAttemptId(@PathVariable Long attemptId,
                                        @RequestParam Map<String, String> answers,
                                        @AuthenticationPrincipal User user,
                                        Model model) {
        try {
            System.out.println("=== QUIZ SUBMISSION (with path variable) ===");
            System.out.println("Attempt ID: " + attemptId);
            System.out.println("Answers: " + answers);
            
            // For simplicity, we'll use quiz ID 1 for this method
            Long quizId = 1L;
            List<Quiz> sampleQuizzes = createSampleQuizzes();
            Quiz quiz = sampleQuizzes.stream()
                    .filter(q -> q.getId().equals(quizId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
            
            // Calculate score (same logic as above)
            int score = 0;
            int totalQuestions = quiz.getQuestions().size();
            Map<Long, String> userAnswers = new HashMap<>();
            Map<Long, String> correctAnswers = new HashMap<>();
            
            for (Question question : quiz.getQuestions()) {
                String answerKey = "question_" + question.getId();
                String userAnswerId = answers.get(answerKey);
                
                if (userAnswerId != null) {
                    Long selectedOptionId = Long.parseLong(userAnswerId);
                    Option selectedOption = question.getOptions().stream()
                            .filter(opt -> opt.getId().equals(selectedOptionId))
                            .findFirst()
                            .orElse(null);
                    
                    if (selectedOption != null) {
                        userAnswers.put(question.getId(), selectedOption.getText());
                        
                        Option correctOption = question.getOptions().stream()
                                .filter(Option::getCorrect)
                                .findFirst()
                                .orElse(null);
                        
                        if (correctOption != null) {
                            correctAnswers.put(question.getId(), correctOption.getText());
                            
                            if (selectedOption.getCorrect()) {
                                score++;
                            }
                        }
                    }
                }
            }
            
            QuizResult result = new QuizResult(score, totalQuestions, quiz);
            result.setUserAnswers(userAnswers);
            result.setCorrectAnswers(correctAnswers);
            
            System.out.println("Quiz scored: " + score + "/" + totalQuestions + " (" + result.getPercentage() + "%)");
            
            model.addAttribute("result", result);
            model.addAttribute("quiz", quiz);
            
            System.out.println("Quiz submitted successfully with results");
            return "quiz/results";
            
        } catch (Exception e) {
            System.err.println("=== ERROR in submitQuizWithAttemptId ===");
            e.printStackTrace();
            model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
            return "quiz/results";
        }
    }

    // Create sample quizzes for demonstration
    private List<Quiz> createSampleQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        
        // HTML/CSS Quiz
        Quiz htmlQuiz = new Quiz();
        htmlQuiz.setId(1L);
        htmlQuiz.setTitle("HTML & CSS Fundamentals");
        htmlQuiz.setDescription("Test your knowledge of HTML tags, CSS styling, and responsive design principles.");
        htmlQuiz.setCategory("Web Development");
        htmlQuiz.setDifficulty("Easy");
        htmlQuiz.setTimeLimit(20);
        htmlQuiz.setActive(true);
        htmlQuiz.setPublicQuiz(true);
        
        List<Question> htmlQuestions = new ArrayList<>();
        
        // Question 1
        Question q1 = new Question();
        q1.setText("What does HTML stand for?");
        q1.setId(1L);
        List<Option> options1 = Arrays.asList(
            createOption("Hyper Text Markup Language", true, 1L),
            createOption("High Tech Modern Language", false, 2L),
            createOption("Hyper Transfer Markup Language", false, 3L),
            createOption("Home Tool Markup Language", false, 4L)
        );
        q1.setOptions(options1);
        htmlQuestions.add(q1);
        
        // Question 2
        Question q2 = new Question();
        q2.setText("Which CSS property is used to change the text color?");
        q2.setId(2L);
        List<Option> options2 = Arrays.asList(
            createOption("text-color", false, 5L),
            createOption("color", true, 6L),
            createOption("font-color", false, 7L),
            createOption("text-style", false, 8L)
        );
        q2.setOptions(options2);
        htmlQuestions.add(q2);
        
        // Question 3
        Question q3 = new Question();
        q3.setText("What does CSS stand for?");
        q3.setId(3L);
        List<Option> options3 = Arrays.asList(
            createOption("Computer Style Sheets", false, 9L),
            createOption("Creative Style System", false, 10L),
            createOption("Cascading Style Sheets", true, 11L),
            createOption("Colorful Style Sheets", false, 12L)
        );
        q3.setOptions(options3);
        htmlQuestions.add(q3);
        
        htmlQuiz.setQuestions(htmlQuestions);
        quizzes.add(htmlQuiz);

        // JavaScript Quiz
        Quiz jsQuiz = new Quiz();
        jsQuiz.setId(2L);
        jsQuiz.setTitle("JavaScript Advanced Concepts");
        jsQuiz.setDescription("Challenge yourself with advanced JavaScript concepts including closures, promises, and async/await.");
        jsQuiz.setCategory("Programming");
        jsQuiz.setDifficulty("Hard");
        jsQuiz.setTimeLimit(30);
        jsQuiz.setActive(true);
        jsQuiz.setPublicQuiz(true);
        
        List<Question> jsQuestions = new ArrayList<>();
        
        // Question 1
        Question jsq1 = new Question();
        jsq1.setText("What is a closure in JavaScript?");
        jsq1.setId(4L);
        List<Option> jsOptions1 = Arrays.asList(
            createOption("A function that has access to variables in its outer scope", true, 13L),
            createOption("A way to close a web page", false, 14L),
            createOption("A method to hide HTML elements", false, 15L),
            createOption("A type of JavaScript loop", false, 16L)
        );
        jsq1.setOptions(jsOptions1);
        jsQuestions.add(jsq1);
        
        // Question 2
        Question jsq2 = new Question();
        jsq2.setText("What is the purpose of the 'async' keyword?");
        jsq2.setId(5L);
        List<Option> jsOptions2 = Arrays.asList(
            createOption("To make a function synchronous", false, 17L),
            createOption("To define an asynchronous function", true, 18L),
            createOption("To create a new thread", false, 19L),
            createOption("To optimize function performance", false, 20L)
        );
        jsq2.setOptions(jsOptions2);
        jsQuestions.add(jsq2);
        
        // Question 3
        Question jsq3 = new Question();
        jsq3.setText("What does 'this' keyword refer to in JavaScript?");
        jsq3.setId(6L);
        List<Option> jsOptions3 = Arrays.asList(
            createOption("The current function", false, 21L),
            createOption("The global window object", false, 22L),
            createOption("The object that the function is a property of", true, 23L),
            createOption("The parent function", false, 24L)
        );
        jsq3.setOptions(jsOptions3);
        jsQuestions.add(jsq3);
        
        jsQuiz.setQuestions(jsQuestions);
        quizzes.add(jsQuiz);

        return quizzes;
    }

    private Option createOption(String text, boolean isCorrect, Long id) {
        Option option = new Option();
        option.setId(id);
        option.setText(text);
        option.setCorrect(isCorrect);
        return option;
    }

    @GetMapping("/test-browse")
    public String testBrowse(Model model) {
        model.addAttribute("quizzes", createSampleQuizData());
        model.addAttribute("debug", true);
        return "quiz/browse";
    }
    
    // DTO for auto-save requests
    public static class AutoSaveRequest {
        private Long attemptId;
        private Long questionId;
        private Integer answerIndex;
        
        // Getters and setters
        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        
        public Integer getAnswerIndex() { return answerIndex; }
        public void setAnswerIndex(Integer answerIndex) { this.answerIndex = answerIndex; }
    }
}