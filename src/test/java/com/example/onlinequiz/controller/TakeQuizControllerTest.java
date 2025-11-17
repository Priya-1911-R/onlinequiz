package com.example.onlinequiz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.onlinequiz.model.Option;
import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.repository.QuizAttemptRepository;
import com.example.onlinequiz.repository.QuizRepository;
import com.example.onlinequiz.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TakeQuizControllerTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TakeQuizController takeQuizController;

    @Test
    void submitQuiz_Success_ReturnsScore() {
        // Arrange
        Long quizId = 1L;
        Long userId = 1L;
        
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        
        // Create questions with options
        Question question1 = createQuestionWithOptions(1L, 0); // First option is correct
        Question question2 = createQuestionWithOptions(2L, 1); // Second option is correct
        
        quiz.setQuestions(Arrays.asList(question1, question2));
        
        User user = new User();
        user.setId(userId);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        Map<String, Integer> answers = new HashMap<>();
        answers.put("1", 0); // Correct - selected first option for question 1
        answers.put("2", 1); // Correct - selected second option for question 2
        payload.put("answers", answers);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<?> response = takeQuizController.submit(quizId, payload);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, responseBody.get("score")); // Both answers correct
        assertEquals(2, responseBody.get("total"));
        assertEquals(100, responseBody.get("percentage"));
        
        verify(quizRepository, times(1)).findById(quizId);
        verify(userRepository, times(1)).findById(userId);
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    void submitQuiz_WithSomeWrongAnswers_ReturnsPartialScore() {
        // Arrange
        Long quizId = 1L;
        Long userId = 1L;
        
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        
        Question question1 = createQuestionWithOptions(1L, 0); // First option correct
        Question question2 = createQuestionWithOptions(2L, 1); // Second option correct
        Question question3 = createQuestionWithOptions(3L, 2); // Third option correct
        
        quiz.setQuestions(Arrays.asList(question1, question2, question3));
        
        User user = new User();
        user.setId(userId);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        Map<String, Integer> answers = new HashMap<>();
        answers.put("1", 0); // Correct
        answers.put("2", 0); // Wrong (should be 1)
        answers.put("3", 2); // Correct
        payload.put("answers", answers);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<?> response = takeQuizController.submit(quizId, payload);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(2, responseBody.get("score")); // 2 out of 3 correct
        assertEquals(3, responseBody.get("total"));
        // Note: Percentage might be 67 or 66 depending on rounding
        assertTrue((Integer) responseBody.get("percentage") >= 66 && (Integer) responseBody.get("percentage") <= 67);
    }

    @Test
    void submitQuiz_QuizNotFound_ReturnsError() {
        // Arrange
        Long quizId = 999L;
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("answers", new HashMap<>());

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = takeQuizController.submit(quizId, payload);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Quiz not found", responseBody.get("error"));
    }

    @Test
    void submitQuiz_UserNotFound_ReturnsError() {
        // Arrange
        Long quizId = 1L;
        Long userId = 999L;
        
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("answers", new HashMap<>());

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = takeQuizController.submit(quizId, payload);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not found", responseBody.get("error"));
    }

    @Test
    void submitQuiz_WithMissingAnswers_ReturnsZeroScore() {
        // Arrange
        Long quizId = 1L;
        Long userId = 1L;
        
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        
        Question question1 = createQuestionWithOptions(1L, 0);
        Question question2 = createQuestionWithOptions(2L, 1);
        
        quiz.setQuestions(Arrays.asList(question1, question2));
        
        User user = new User();
        user.setId(userId);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("answers", new HashMap<>()); // No answers provided

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<?> response = takeQuizController.submit(quizId, payload);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(0, responseBody.get("score")); // No correct answers
        assertEquals(2, responseBody.get("total"));
        assertEquals(0, responseBody.get("percentage"));
    }

    // Helper method to create a question with options
    private Question createQuestionWithOptions(Long questionId, int correctOptionIndex) {
        Question question = new Question();
        question.setId(questionId);
        question.setText("Question " + questionId);
        
        List<Option> options = new ArrayList<>();
        String[] optionTexts = {"Option A", "Option B", "Option C", "Option D"};
        
        for (int i = 0; i < optionTexts.length; i++) {
            Option option = new Option();
            option.setId((long) i + 1);
            option.setText(optionTexts[i]);
            option.setCorrect(i == correctOptionIndex);
            option.setQuestion(question);
            options.add(option);
        }
        
        question.setOptions(options);
        return question;
    }
}