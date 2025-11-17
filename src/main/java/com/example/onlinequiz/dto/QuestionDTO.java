// QuestionDTO.java
package com.example.onlinequiz.dto;

import java.util.List;

public class QuestionDTO {
    private Long id;
    private String text;
    private List<String> options;
    private Integer correctAnswer;
    
    // Constructors
    public QuestionDTO() {}

     public QuestionDTO(String text, List<String> options, Integer correctAnswer) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(Integer correctAnswer) { this.correctAnswer = correctAnswer; }

}