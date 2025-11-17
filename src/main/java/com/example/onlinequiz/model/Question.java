package com.example.onlinequiz.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String text;
    
    private String type = "MULTIPLE_CHOICE";
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<UserAnswer> userAnswers = new ArrayList<>();
    
    public Question() {}
    
    public Question(String text, Quiz quiz) {
        this.text = text;
        this.quiz = quiz;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    
    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }
    
    public List<UserAnswer> getUserAnswers() { return userAnswers; }
    public void setUserAnswers(List<UserAnswer> userAnswers) { this.userAnswers = userAnswers; }
    
    // Helper method to add option
    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }
    
    // Helper method to remove option
    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }
    
    // Helper method to get correct option index
    public Integer getCorrectOptionIndex() {
        if (options == null || options.isEmpty()) {
            return null;
        }
        
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getCorrect()) {
                return i;
            }
        }
        
        return null;
    }
}  