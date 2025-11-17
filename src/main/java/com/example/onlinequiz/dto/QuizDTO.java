// QuizDTO.java
package com.example.onlinequiz.dto;

import java.util.List;

public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private Boolean active = true;
    private Boolean publicQuiz = false;
    private List<QuestionDTO> questions;
    
    // Constructors
    public QuizDTO() {}
    public QuizDTO(String title, String description, Integer timeLimit) {
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public Boolean getPublicQuiz() { return publicQuiz; }
    public void setPublicQuiz(Boolean publicQuiz) { this.publicQuiz = publicQuiz; }
    
    public List<QuestionDTO> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; }
}