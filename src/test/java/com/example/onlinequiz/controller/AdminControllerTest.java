package com.example.onlinequiz.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.QuizService;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private QuizService quizService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @Test
    void adminDashboard_ReturnsDashboardWithQuizzes() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");

        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Quiz 1");

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Quiz 2");

        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);

        when(quizService.getQuizzesByUser(any(User.class))).thenReturn(quizzes);

        // Act
        String viewName = adminController.adminDashboard(user, model);

        // Assert
        assertEquals("admin/dashboard", viewName);
        verify(quizService, times(1)).getQuizzesByUser(user);
        verify(model, times(1)).addAttribute(eq("quizzes"), eq(quizzes));
        verify(model, times(1)).addAttribute(eq("recentQuizzes"), eq(quizzes));
        verify(model, times(1)).addAttribute(eq("recentAttempts"), any(List.class));
        verify(model, times(1)).addAttribute(eq("adminStats"), any(AdminController.AdminStats.class));
    }

    @Test
    void adminDashboard_WhenUserIsNull_RedirectsToLogin() {
        // Arrange
        User user = null;

        // Act
        String viewName = adminController.adminDashboard(user, model);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void adminDashboard_WhenExceptionOccurs_ReturnsDashboardWithError() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(quizService.getQuizzesByUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        String viewName = adminController.adminDashboard(user, model);

        // Assert
        assertEquals("admin/dashboard", viewName);
        verify(model, times(1)).addAttribute(eq("error"), eq("Unable to load dashboard"));
        verify(model, times(1)).addAttribute(eq("quizzes"), any(List.class));
        verify(model, times(1)).addAttribute(eq("recentQuizzes"), any(List.class));
        verify(model, times(1)).addAttribute(eq("recentAttempts"), any(List.class));
        verify(model, times(1)).addAttribute(eq("adminStats"), any(AdminController.AdminStats.class));
    }

    @Test
    void manageQuizzes_ReturnsManageQuizzesPage() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Quiz 1");

        List<Quiz> quizzes = Arrays.asList(quiz1);

        when(quizService.getQuizzesByUser(any(User.class))).thenReturn(quizzes);

        // Act
        String viewName = adminController.manageQuizzes(user, model);

        // Assert
        assertEquals("admin/manage-quizzes", viewName);
        verify(model, times(1)).addAttribute(eq("quizzes"), eq(quizzes));
        verify(quizService, times(1)).getQuizzesByUser(user);
    }

    @Test
    void manageQuizzes_WhenUserIsNull_RedirectsToLogin() {
        // Arrange
        User user = null;

        // Act
        String viewName = adminController.manageQuizzes(user, model);

        // Assert
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void manageQuizzes_WhenExceptionOccurs_ReturnsManageQuizzesWithError() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(quizService.getQuizzesByUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        String viewName = adminController.manageQuizzes(user, model);

        // Assert
        assertEquals("admin/manage-quizzes", viewName);
        verify(model, times(1)).addAttribute(eq("error"), eq("Unable to load quizzes"));
        verify(model, times(1)).addAttribute(eq("quizzes"), any(List.class));
    }

    @Test
    void adminStats_CreatesCorrectStats() {
        // Arrange
        AdminController.AdminStats stats = new AdminController.AdminStats(5, 10, 15, 85);

        // Assert
        assertEquals(5, stats.getTotalQuizzes());
        assertEquals(10, stats.getTotalUsers());
        assertEquals(15, stats.getTotalAttempts());
        assertEquals(85, stats.getAverageScore());
    }
}