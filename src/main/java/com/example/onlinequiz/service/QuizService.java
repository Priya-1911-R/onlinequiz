package com.example.onlinequiz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.QuizResult;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.model.Option;
import com.example.onlinequiz.repository.QuestionRepository;
import com.example.onlinequiz.repository.QuizAttemptRepository;
import com.example.onlinequiz.repository.QuizRepository;
import com.example.onlinequiz.repository.QuizResultRepository;
import com.example.onlinequiz.repository.OptionRepository;

@Service
@Transactional
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private OptionRepository optionRepository;
    
    @Transactional
    public Quiz createQuiz(Quiz quiz) {
       Quiz savedQuiz = quizRepository.save(quiz);
        
        // Then save questions and options
        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(savedQuiz);
                Question savedQuestion = questionRepository.save(question);
                
                if (question.getOptions() != null) {
                    for (Option option : question.getOptions()) {
                        option.setQuestion(savedQuestion);
                        optionRepository.save(option);
                    }
                }
            }
        }
        
        return savedQuiz;
    }
    
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByActiveTrue();
    }
    
    public List<Quiz> getQuizzesByUser(User user) {
        return quizRepository.findByCreatedBy(user);
    }
    
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }
    
    public Question addQuestionToQuiz(Question question) {
        return questionRepository.save(question);
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
        
        // For now, we'll store in answersJson - you might want to implement proper JSON handling
        // This is a simplified version
        attempt.setAnswersJson("{\"" + questionId + "\":" + answerIndex + "}");
        quizAttemptRepository.save(attempt);
    }
    
    public void saveProgress(Long attemptId, Map<String, String> answers, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        // Simple JSON storage - implement proper JSON handling in real application
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
    
    public AttemptProgress getAttemptProgress(Long attemptId, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        return new AttemptProgress(attempt);
    }
    
    public QuizResult submitAttempt(Long attemptId, Map<String, String> answers, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
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
    
    private int calculateScore(QuizAttempt attempt, Map<String, String> answers) {
        int score = 0;
        Quiz quiz = attempt.getQuiz();
        
        for (Question question : quiz.getQuestions()) {
            String answerKey = "q_" + question.getId();
            if (answers.containsKey(answerKey)) {
                Integer userAnswer = Integer.valueOf(answers.get(answerKey));
                // Use correctOptionIndex instead of correctAnswerIndex
                if (userAnswer.equals(question.getCorrectOptionIndex())) {
                    score += 1; // Simple scoring - 1 point per correct answer
                }
            }
        }
        
        return score;
    }
    
    public QuizResult getQuizResult(Long attemptId, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        // Fixed: Use explicit type casting to resolve the generic type issue
        Optional<QuizResult> resultOptional = quizResultRepository.findByQuizAttempt(attempt);
        return resultOptional.orElseThrow(() -> new RuntimeException("Result not found"));
    }
    
    public QuizAttempt getAttemptById(Long attemptId, User user) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        // Verify user owns this attempt
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to attempt");
        }
        
        return attempt;
    }
    
    // Progress DTO
    public static class AttemptProgress {
        private final List<AnswerProgress> answers;
        private final List<Integer> markedQuestions;
        
        public AttemptProgress(QuizAttempt attempt) {
            this.answers = new ArrayList<>();
            this.markedQuestions = new ArrayList<>();
            
            // TODO: Implement JSON parsing from answersJson
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