package com.example.onlinequiz.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.onlinequiz.model.Option;
import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.repository.QuizAttemptRepository;
import com.example.onlinequiz.repository.QuizRepository;

@ExtendWith(MockitoExtension.class)
class QuizTakingServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @InjectMocks
    private QuizTakingService quizTakingService;

    private Quiz testQuiz;
    private User testUser;
    private Question question1;
    private Question question2;

    @BeforeEach
    void setUp() {
        // FIXED: Use Role enum instead of String
        testUser = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        testUser.setId(1L);

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Java Quiz");

        // Create questions with Option objects instead of strings
        question1 = createQuestionWithOptions(1L, "What is Java?", 0);
        question2 = createQuestionWithOptions(2L, "What is Spring?", 1);

        testQuiz.setQuestions(Arrays.asList(question1, question2));
    }

    @Test
    void getQuizForTaking_QuizExists_ReturnsQuiz() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act
        Quiz result = quizTakingService.getQuizForTaking(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Java Quiz", result.getTitle());
        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void getQuizForTaking_QuizNotFound_ThrowsException() {
        // Arrange
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> quizTakingService.getQuizForTaking(999L));
        assertEquals("Quiz not found", exception.getMessage());
    }

    @Test
    void submitQuiz_AllCorrectAnswers_ReturnsPerfectScore() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0); // Correct - first option for question 1
        answers.put(2L, 1); // Correct - second option for question 2

        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            attempt.setId(1L);
            return attempt;
        });

        // Act
        QuizAttempt result = quizTakingService.submitQuiz(1L, testUser, answers);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getScore()); // Perfect score
        assertEquals(testUser, result.getUser());
        assertEquals(testQuiz, result.getQuiz());
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    void submitQuiz_SomeWrongAnswers_ReturnsPartialScore() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0); // Correct
        answers.put(2L, 0); // Wrong (should be 1)

        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            attempt.setId(1L);
            return attempt;
        });

        // Act
        QuizAttempt result = quizTakingService.submitQuiz(1L, testUser, answers);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getScore()); // One correct, one wrong
    }

    @Test
    void submitQuiz_NoAnswers_ReturnsZeroScore() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>(); // Empty answers

        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            attempt.setId(1L);
            return attempt;
        });

        // Act
        QuizAttempt result = quizTakingService.submitQuiz(1L, testUser, answers);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getScore()); // No correct answers
    }

    @Test
    void submitQuiz_WithInvalidQuestionId_IgnoresInvalidAnswer() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0); // Valid question, correct answer
        answers.put(999L, 0); // Invalid question ID

        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            attempt.setId(1L);
            return attempt;
        });

        // Act
        QuizAttempt result = quizTakingService.submitQuiz(1L, testUser, answers);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getScore()); // Only one valid correct answer
    }

    @Test
    void submitQuiz_WithInvalidOptionIndex_CountsAsWrong() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0); // Correct
        answers.put(2L, 999); // Invalid option index (out of bounds)

        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            attempt.setId(1L);
            return attempt;
        });

        // Act
        QuizAttempt result = quizTakingService.submitQuiz(1L, testUser, answers);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getScore()); // One correct, one invalid (counts as wrong)
    }

    // Helper method to create a question with Option objects
    private Question createQuestionWithOptions(Long questionId, String questionText, int correctOptionIndex) {
        Question question = new Question();
        question.setId(questionId);
        question.setText(questionText);
        
        // Create Option objects
        Option option1 = new Option();
        option1.setId(questionId * 10 + 1L);
        option1.setText("Option A");
        option1.setCorrect(correctOptionIndex == 0);
        option1.setQuestion(question);
        
        Option option2 = new Option();
        option2.setId(questionId * 10 + 2L);
        option2.setText("Option B");
        option2.setCorrect(correctOptionIndex == 1);
        option2.setQuestion(question);
        
        Option option3 = new Option();
        option3.setId(questionId * 10 + 3L);
        option3.setText("Option C");
        option3.setCorrect(correctOptionIndex == 2);
        option3.setQuestion(question);
        
        Option option4 = new Option();
        option4.setId(questionId * 10 + 4L);
        option4.setText("Option D");
        option4.setCorrect(correctOptionIndex == 3);
        option4.setQuestion(question);
        
        question.setOptions(Arrays.asList(option1, option2, option3, option4));
        return question;
    }
}