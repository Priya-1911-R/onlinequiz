package com.example.onlinequiz.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
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
import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizAttemptService;
import com.example.onlinequiz.service.QuizService;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    @Mock
    private QuizService quizService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private Model model;

    @InjectMocks
    private ParticipantController participantController;

    private User testUser;
    private Quiz testQuiz;
    private QuizAttempt testAttempt;

    @BeforeEach
    void setUp() {
        testUser = new User("participant", "participant@test.com", "password", Role.PARTICIPANT);
        testUser.setId(1L);

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Java Quiz");
        testQuiz.setActive(true);

        testAttempt = new QuizAttempt();
        testAttempt.setId(1L);
        testAttempt.setUser(testUser);
        testAttempt.setQuiz(testQuiz);
        testAttempt.setScore(8);
        // Removed setCompleted() since it doesn't exist in your QuizAttempt class
    }

    @Test
    void myResults_UserAuthenticated_ReturnsResultsWithData() {
        // Arrange
        List<QuizAttempt> attempts = Arrays.asList(testAttempt);

        when(quizAttemptService.getUserAttempts(testUser)).thenReturn(attempts);

        // Act - Using the correct method name "myResults"
        String viewName = participantController.myResults(testUser, model);

        // Assert
        assertEquals("participant/results", viewName);
        verify(model, times(1)).addAttribute("attempts", attempts);
        verify(model, times(1)).addAttribute("user", testUser);
        verify(quizAttemptService, times(1)).getUserAttempts(testUser);
    }

    @Test
    void myResults_UserNull_RedirectsToLogin() {
        // Act
        String viewName = participantController.myResults(null, model);

        // Assert
        assertEquals("redirect:/login", viewName);
        verify(quizAttemptService, never()).getUserAttempts(any());
    }

    @Test
    void myResults_ServiceException_ReturnsResultsWithEmptyData() {
        // Arrange
        when(quizAttemptService.getUserAttempts(testUser)).thenThrow(new RuntimeException("Service error"));

        // Act
        String viewName = participantController.myResults(testUser, model);

        // Assert
        assertEquals("participant/results", viewName);
        verify(model, times(1)).addAttribute(eq("attempts"), any(List.class));
        verify(model, times(1)).addAttribute("user", testUser);
        verify(model, times(1)).addAttribute("error", "Unable to load results");
    }

    @Test
    void myResults_WithAttempts_CalculatesStatisticsCorrectly() {
        // Arrange
        List<QuizAttempt> attempts = Arrays.asList(testAttempt);

        when(quizAttemptService.getUserAttempts(testUser)).thenReturn(attempts);

        // Act
        String viewName = participantController.myResults(testUser, model);

        // Assert
        assertEquals("participant/results", viewName);
        verify(model, times(1)).addAttribute("attempts", attempts);
        verify(model, times(1)).addAttribute("user", testUser);
        // Verify that statistics are calculated and added to the model
        verify(model, times(1)).addAttribute(eq("totalAttempts"), any(Long.class));
        verify(model, times(1)).addAttribute(eq("averageScore"), any(Long.class));
        verify(model, times(1)).addAttribute(eq("bestScore"), any(Long.class));
        verify(model, times(1)).addAttribute(eq("quizzesTaken"), any(Long.class));
    }

    @Test
    void myResults_NoAttempts_AddsDefaultStatistics() {
        // Arrange
        when(quizAttemptService.getUserAttempts(testUser)).thenReturn(Arrays.asList());

        // Act
        String viewName = participantController.myResults(testUser, model);

        // Assert
        assertEquals("participant/results", viewName);
        verify(model, times(1)).addAttribute(eq("attempts"), any(List.class));
        verify(model, times(1)).addAttribute("user", testUser);
        verify(model, times(1)).addAttribute("totalAttempts", 0);
        verify(model, times(1)).addAttribute("averageScore", 0);
        verify(model, times(1)).addAttribute("bestScore", 0);
        verify(model, times(1)).addAttribute("quizzesTaken", 0);
    }

    @Test
    void controller_ShouldBeCreated() {
        // Simple test to verify the controller can be created
        assertNotNull(participantController);
    }
}