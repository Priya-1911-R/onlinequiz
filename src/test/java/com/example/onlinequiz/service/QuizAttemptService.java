package com.example.onlinequiz.service;

import com.example.onlinequiz.model.*;
import com.example.onlinequiz.repository.QuizAttemptRepository;
import com.example.onlinequiz.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QuizAttemptService {
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private QuizResultRepository quizResultRepository;
    
    // Add this method to get user attempts
    public List<QuizAttempt> getUserAttempts(User user) {
        return quizAttemptRepository.findByUserOrderByStartedAtDesc(user);
    }
    
    // Add missing methods for tests
    public List<QuizAttempt> getQuizAttempts(Long quizId) {
        return quizAttemptRepository.findByQuizIdOrderBySubmittedAtDesc(quizId);
    }
    
    public QuizAttempt saveAttempt(QuizAttempt attempt) {
        return quizAttemptRepository.save(attempt);
    }
    
    public void deleteAttempt(Long attemptId) {
        quizAttemptRepository.deleteById(attemptId);
    }
    
    public QuizAttempt createNewAttempt(User user, Quiz quiz) {
        QuizAttempt attempt = new QuizAttempt(user, quiz);
        return quizAttemptRepository.save(attempt);
    }
    
    public QuizAttempt getInProgressAttempt(User user, Quiz quiz) {
        return quizAttemptRepository.findByUserAndQuizAndSubmittedAtIsNull(user, quiz)
                .orElse(null);
    }
    
    public void saveAnswer(Long attemptId, Long questionId, Integer answerIndex, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        // Simple JSON storage implementation
        String currentAnswers = attempt.getAnswersJson();
        if (currentAnswers == null || currentAnswers.isEmpty()) {
            currentAnswers = "{}";
        }
        
        // Very basic JSON manipulation - in production, use a proper JSON library
        String newAnswers = currentAnswers.replaceAll("\\{", "")
                                         .replaceAll("\\}", "")
                                         .trim();
        if (!newAnswers.isEmpty()) {
            newAnswers += ",";
        }
        newAnswers += "\"" + questionId + "\":" + answerIndex;
        newAnswers = "{" + newAnswers + "}";
        
        attempt.setAnswersJson(newAnswers);
        quizAttemptRepository.save(attempt);
    }
    
    public void saveProgress(Long attemptId, Map<String, String> answers, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        // Build JSON string from answers
        StringBuilder jsonBuilder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("q_")) {
                Long questionId = Long.valueOf(key.substring(2));
                Integer answerIndex = Integer.valueOf(entry.getValue());
                if (!first) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\"").append(questionId).append("\":").append(answerIndex);
                first = false;
            }
        }
        jsonBuilder.append("}");
        
        attempt.setAnswersJson(jsonBuilder.toString());
        quizAttemptRepository.save(attempt);
    }
    
    public QuizAttempt getAttemptById(Long attemptId) {
        return quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
    }
    
    public QuizAttempt getAttemptById(Long attemptId, User user) {
        QuizAttempt attempt = getAttemptById(attemptId);
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        return attempt;
    }
    
    public QuizResult submitAttempt(Long attemptId, Map<String, String> answers, User user) {
        QuizAttempt attempt = getAttemptById(attemptId, user);
        
        // Save final answers
        saveProgress(attemptId, answers, user);
        
        // Calculate score
        int score = calculateScore(attempt, answers);
        int totalQuestions = attempt.getQuiz().getQuestions().size();
        
        // Update attempt
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setScore(score);
        attempt.setTotalQuestions(totalQuestions);
        
        // Create quiz result
        QuizResult result = new QuizResult();
        result.setQuizAttempt(attempt);
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);
        result.setPercentage((double) score / totalQuestions * 100);
        result.setSubmittedAt(LocalDateTime.now());
        
        quizAttemptRepository.save(attempt);
        return quizResultRepository.save(result);
    }
    
    public QuizResult getQuizResult(Long attemptId, User user) {
        QuizAttempt attempt = getAttemptById(attemptId, user);
        
        // Fixed: Use explicit type casting to resolve the generic type issue
        java.util.Optional<QuizResult> resultOptional = quizResultRepository.findByQuizAttempt(attempt);
        return resultOptional.orElseThrow(() -> new RuntimeException("Result not found"));
    }
    
    public AttemptProgress getAttemptProgress(Long attemptId, User user) {
        QuizAttempt attempt = getAttemptById(attemptId, user);
        return new AttemptProgress(attempt);
    }
    
    private int calculateScore(QuizAttempt attempt, Map<String, String> answers) {
        int score = 0;
        Quiz quiz = attempt.getQuiz();
        
        for (Question question : quiz.getQuestions()) {
            String answerKey = "q_" + question.getId();
            if (answers.containsKey(answerKey)) {
                try {
                    Integer userAnswer = Integer.valueOf(answers.get(answerKey));
                    if (userAnswer.equals(question.getCorrectOptionIndex())) {
                        score += 1; // Simple scoring - 1 point per correct answer
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid answers
                }
            }
        }
        
        return score;
    }
    
    // Progress DTO
    public static class AttemptProgress {
        private final List<AnswerProgress> answers;
        private final List<Integer> markedQuestions;
        
        public AttemptProgress(QuizAttempt attempt) {
            this.answers = new java.util.ArrayList<>();
            this.markedQuestions = new java.util.ArrayList<>();
            
            // TODO: Implement proper JSON parsing from answersJson
            // For now, return empty progress
        }
        
        // Getters
        public List<AnswerProgress> getAnswers() { return answers; }
        public List<Integer> getMarkedQuestions() { return markedQuestions; }
    }
    
    public static class AnswerProgress {
        private final Long questionId;
        private final Integer answerIndex;
        
        public AnswerProgress(Long questionId, Integer answerIndex) {
            this.questionId = questionId;
            this.answerIndex = answerIndex;
        }
        
        // Getters
        public Long getQuestionId() { return questionId; }
        public Integer getAnswerIndex() { return answerIndex; }
    }
}