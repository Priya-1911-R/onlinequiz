package com.example.onlinequiz.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.repository.QuizAttemptRepository;
import com.example.onlinequiz.repository.QuizRepository;
import com.example.onlinequiz.repository.UserRepository;

@RestController
@RequestMapping("/api/take")
public class TakeQuizController {
    
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;
    
    public TakeQuizController(QuizRepository quizRepository, 
                            QuizAttemptRepository attemptRepository,
                            UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{quizId}")
    public ResponseEntity<?> submit(@PathVariable Long quizId, @RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(String.valueOf(payload.get("userId")));
            @SuppressWarnings("unchecked")
            Map<String, Integer> answers = (Map<String, Integer>) payload.get("answers");

            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            int score = calculateScore(quiz, answers);
            
            QuizAttempt attempt = new QuizAttempt(user, quiz);
            attempt.setScore(score);
            attempt.setSubmittedAt(LocalDateTime.now());
            
            QuizAttempt savedAttempt = attemptRepository.save(attempt);

            return ResponseEntity.ok(createResponse(score, quiz.getQuestions().size(), savedAttempt.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "An unexpected error occurred"));
        }
    }

    private int calculateScore(Quiz quiz, Map<String, Integer> answers) {
        int score = 0;
        for (Question q : quiz.getQuestions()) {
            Integer selected = answers.get(String.valueOf(q.getId()));
            if (selected != null && selected.equals(q.getCorrectOptionIndex())) {
                score++;
            }
        }
        return score;
    }

    private Map<String, Object> createResponse(int score, int totalQuestions, Long attemptId) {
        return Map.of(
            "score", score, 
            "total", totalQuestions,
            "attemptId", attemptId,
            "percentage", Math.round((score * 100.0) / totalQuestions)
        );
    }
}