package com.example.onlinequiz.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.repository.QuizAttemptRepository;
import com.example.onlinequiz.repository.QuizRepository;

@Service
public class QuizTakingService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    public Quiz getQuizForTaking(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }
    
    public QuizAttempt submitQuiz(Long quizId, User user, Map<Long, Integer> answers) {
        Quiz quiz = getQuizForTaking(quizId);
        
        int score = calculateScore(quiz, answers);
        
        QuizAttempt attempt = new QuizAttempt(user, quiz);
        attempt.setScore(score);
        attempt.setSubmittedAt(LocalDateTime.now());
        
        return quizAttemptRepository.save(attempt);
    }
    
    private int calculateScore(Quiz quiz, Map<Long, Integer> answers) {
        int score = 0;
        for (Question question : quiz.getQuestions()) {
            Integer selectedAnswer = answers.get(question.getId());
            if (selectedAnswer != null && selectedAnswer.equals(question.getCorrectOptionIndex())) {
                score++;
            }
        }
        return score;
    }
}