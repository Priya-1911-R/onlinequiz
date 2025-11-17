package com.example.onlinequiz.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_results")
public class QuizResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "attempt_id")
    private QuizAttempt quizAttempt;
    
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private LocalDateTime submittedAt;
    private Integer correctAnswers; 
    
    // Constructors
    public QuizResult() {}
    
    public QuizResult(QuizAttempt quizAttempt, Integer score, Integer totalQuestions, LocalDateTime submittedAt) {
        this.quizAttempt = quizAttempt;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = totalQuestions > 0 ? (double) score / totalQuestions * 100 : 0.0;
        this.submittedAt = submittedAt;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public QuizAttempt getQuizAttempt() { return quizAttempt; }
    public void setQuizAttempt(QuizAttempt quizAttempt) { this.quizAttempt = quizAttempt; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { 
        this.score = score; 
        // Recalculate percentage when score is set
        if (this.totalQuestions != null && this.totalQuestions > 0) {
            this.percentage = (double) score / totalQuestions * 100;
        }
    }
    
    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { 
        this.totalQuestions = totalQuestions; 
        // Recalculate percentage when total questions is set
        if (this.score != null && totalQuestions != null && totalQuestions > 0) {
            this.percentage = (double) score / totalQuestions * 100;
        }
    }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    
    
    // Helper methods
    public String getFormattedPercentage() {
        if (percentage != null) {
            return String.format("%.1f%%", percentage);
        }
        return "0%";
    }
    
    public boolean isPassing(double passingThreshold) {
        return percentage != null && percentage >= passingThreshold;
    }
    
    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", score=" + score +
                ", totalQuestions=" + totalQuestions +
                ", percentage=" + percentage +
                ", submittedAt=" + submittedAt +
                '}';
    }
}