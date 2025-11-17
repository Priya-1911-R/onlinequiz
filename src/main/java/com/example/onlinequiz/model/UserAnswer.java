package com.example.onlinequiz.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_answers")
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id")
    private QuizAttempt quizAttempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
    
    @Column(name = "selected_option_index")
    private Integer selectedOptionIndex;
    
    @Column(name = "correct")
    private Boolean correct;
    
    @Column(name = "time_spent")
    private Long timeSpent; // in seconds
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    // constructors
    public UserAnswer() {}
    
    public UserAnswer(QuizAttempt quizAttempt, Question question, Integer selectedOptionIndex) {
        this.quizAttempt = quizAttempt;
        this.question = question;
        this.selectedOptionIndex = selectedOptionIndex;
        // Automatically determine if the answer is correct
        this.correct = selectedOptionIndex != null && 
                      selectedOptionIndex.equals(question.getCorrectOptionIndex());
        this.answeredAt = LocalDateTime.now();
    }
    
    public UserAnswer(QuizAttempt quizAttempt, Question question, Integer selectedOptionIndex, Long timeSpent) {
        this(quizAttempt, question, selectedOptionIndex);
        this.timeSpent = timeSpent;
    }
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public QuizAttempt getQuizAttempt() { return quizAttempt; }
    public void setQuizAttempt(QuizAttempt quizAttempt) { this.quizAttempt = quizAttempt; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { 
        this.question = question;
        // Recalculate correctness when question changes
        if (this.question != null && this.selectedOptionIndex != null) {
            this.correct = this.selectedOptionIndex.equals(this.question.getCorrectOptionIndex());
        }
    }
    
    public Integer getSelectedOptionIndex() { return selectedOptionIndex; }
    public void setSelectedOptionIndex(Integer selectedOptionIndex) { 
        this.selectedOptionIndex = selectedOptionIndex;
        // Update correctness when option changes
        if (this.question != null && selectedOptionIndex != null) {
            this.correct = selectedOptionIndex.equals(this.question.getCorrectOptionIndex());
        }
    }
    
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
    
    // Add the isCorrect() method - this is what's missing
    public boolean isCorrect() {
        return Boolean.TRUE.equals(correct);
    }
    
    public Long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Long timeSpent) { this.timeSpent = timeSpent; }
    
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
    
    // Utility method to check if answer is correct based on question
    public boolean checkCorrectness() {
        if (question == null || selectedOptionIndex == null) {
            this.correct = false;
            return false;
        }
        this.correct = selectedOptionIndex.equals(question.getCorrectOptionIndex());
        return this.correct;
    }
    
    @Override
    public String toString() {
        return "UserAnswer{" +
                "id=" + id +
                ", questionId=" + (question != null ? question.getId() : "null") +
                ", selectedOptionIndex=" + selectedOptionIndex +
                ", correct=" + correct +
                ", timeSpent=" + timeSpent +
                '}';
    }
}