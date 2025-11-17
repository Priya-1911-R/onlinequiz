// QuizAttemptDTO.java
package com.example.onlinequiz.dto;

import java.util.Map;

public class QuizAttemptDTO {
    private Long quizId;
    private Map<Long, Integer> answers; // questionId -> selectedOptionIndex
    
    // Constructors
    public QuizAttemptDTO() {}
    
    // Getters and Setters
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
    
    public Map<Long, Integer> getAnswers() { return answers; }
    public void setAnswers(Map<Long, Integer> answers) { this.answers = answers; }
}