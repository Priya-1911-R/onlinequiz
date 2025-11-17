package com.example.onlinequiz.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    private Integer score = 0;
    
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;
    
    @Column(name = "completed", nullable = false)
    private Boolean completed = false;
    
    @Column(columnDefinition = "TEXT")
    private String answersJson; // Store answers as JSON string
    
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    private Integer timeTaken; // in seconds
    
    // Constructors
    public QuizAttempt() {
        this.startedAt = LocalDateTime.now();
        this.completed = false;
    }
    
    public QuizAttempt(User user, Quiz quiz) {
        this();
        this.user = user;
        this.quiz = quiz;
        if (quiz != null && quiz.getQuestions() != null) {
            this.totalQuestions = quiz.getQuestions().size();
        }
    }
    
    // Helper methods
    public void markAsCompleted() {
        this.completed = true;
        this.submittedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted(Integer score) {
        this.completed = true;
        this.score = score;
        this.submittedAt = LocalDateTime.now();
    }
    
    public boolean isInProgress() {
        return !completed && submittedAt == null;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public Integer getPercentageScore() {
        if (totalQuestions == null || totalQuestions == 0) return 0;
        return (score * 100) / totalQuestions;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        if (quiz != null && quiz.getQuestions() != null) {
            this.totalQuestions = quiz.getQuestions().size();
        }
    }

    

    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
        // Auto-set completed when submittedAt is set
        if (submittedAt != null) {
            this.completed = true;
        }
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
        if (completed && this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }
    
    public String getAnswersJson() {
        return answersJson;
    }
    
    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }
    
    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }
    
    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
    
    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }
    
    public void addUserAnswer(UserAnswer userAnswer) {
        userAnswers.add(userAnswer);
        userAnswer.setQuizAttempt(this);
    }
    
    public void removeUserAnswer(UserAnswer userAnswer) {
        userAnswers.remove(userAnswer);
        userAnswer.setQuizAttempt(null);
    }
    
    @Override
    public String toString() {
        return "QuizAttempt{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : "null") +
                ", quiz=" + (quiz != null ? quiz.getId() : "null") +
                ", startedAt=" + startedAt +
                ", submittedAt=" + submittedAt +
                ", score=" + score +
                ", totalQuestions=" + totalQuestions +
                ", completed=" + completed +
                '}';
    }
}