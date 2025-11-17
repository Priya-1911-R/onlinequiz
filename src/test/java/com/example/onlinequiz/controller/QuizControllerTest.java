package com.example.onlinequiz.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizAttemptService;
import com.example.onlinequiz.service.QuizService;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    @Mock
    private QuizService quizService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private Model model;

    @Mock
    private User user;

    @InjectMocks
    private QuizController quizController;

    @Test
    void browseQuizzes_ReturnsBrowsePageWithQuizzes() {
        // Arrange
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Java Quiz");
        quiz1.setActive(true);

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Spring Quiz");
        quiz2.setActive(true);

        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);
        when(quizService.getAllActiveQuizzes()).thenReturn(quizzes);

        // Act
        String viewName = quizController.browseQuizzes(user, model); // Added user parameter

        // Assert
        assertEquals("quiz/browse", viewName);
        verify(model, times(1)).addAttribute("quizzes", quizzes);
        verify(quizService, times(1)).getAllActiveQuizzes();
    }

    @Test
    void takeQuiz_QuizExists_ReturnsTakeQuizPage() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(100L);
        
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(quizAttemptService.getInProgressAttempt(user, quiz)).thenReturn(null);
        when(quizAttemptService.createNewAttempt(user, quiz)).thenReturn(attempt);

        // Act
        String viewName = quizController.takeQuiz(1L, user, model);

        // Assert
        assertEquals("quiz/take", viewName);
        verify(model, times(1)).addAttribute("quiz", quiz);
        verify(model, times(1)).addAttribute("attempt", attempt);
        verify(quizService, times(1)).getQuizById(1L);
        verify(quizAttemptService, times(1)).getInProgressAttempt(user, quiz);
        verify(quizAttemptService, times(1)).createNewAttempt(user, quiz);
    }

    @Test
    void takeQuiz_QuizNotFound_ThrowsException() {
        // Arrange
        when(quizService.getQuizById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> quizController.takeQuiz(999L, user, model));
    }

    @Test
    void takeQuiz_ExistingAttemptFound_ReturnsTakeQuizPage() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        
        QuizAttempt existingAttempt = new QuizAttempt();
        existingAttempt.setId(50L);
        
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(quizAttemptService.getInProgressAttempt(user, quiz)).thenReturn(existingAttempt);

        // Act
        String viewName = quizController.takeQuiz(1L, user, model);

        // Assert
        assertEquals("quiz/take", viewName);
        verify(model, times(1)).addAttribute("quiz", quiz);
        verify(model, times(1)).addAttribute("attempt", existingAttempt);
        verify(quizAttemptService, times(1)).getInProgressAttempt(user, quiz);
        verify(quizAttemptService, never()).createNewAttempt(user, quiz);
    }

    @Test
    void browseQuizzes_ServiceThrowsException_ReturnsBrowsePageWithEmptyList() {
        // Arrange
        when(quizService.getAllActiveQuizzes()).thenThrow(new RuntimeException("Database error"));

        // Act
        String viewName = quizController.browseQuizzes(user, model); // Added user parameter

        // Assert
        assertEquals("quiz/browse", viewName);
        verify(model, times(1)).addAttribute(eq("quizzes"), any(List.class));
        verify(model, times(1)).addAttribute("error", "Unable to load quizzes. Please try again later.");
        verify(quizService, times(1)).getAllActiveQuizzes();
    }

    // Remove or fix the testBrowse method since it doesn't match any controller method
    // @Test
    // void testBrowse_ReturnsBrowsePageWithEmptyList() {
    //     // Act
    //     String viewName = quizController.testBrowse(model);
    //
    //     // Assert
    //     assertEquals("quiz/browse", viewName);
    //     verify(model, times(1)).addAttribute("quizzes", List.of());
    // }

    // Test for AutoSaveRequest DTO
    @Test
    void autoSaveRequest_SettersAndGetters_WorkCorrectly() {
        // Arrange
        QuizController.AutoSaveRequest request = new QuizController.AutoSaveRequest();
        
        // Act
        request.setAttemptId(100L);
        request.setQuestionId(200L);
        request.setAnswerIndex(2);

        // Assert
        assertEquals(100L, request.getAttemptId());
        assertEquals(200L, request.getQuestionId());
        assertEquals(2, request.getAnswerIndex());
    }
}