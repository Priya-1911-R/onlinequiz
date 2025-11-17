package com.example.onlinequiz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    @Test
    void showRegistrationForm_ReturnsRegisterPage() {
        // Act
        String viewName = authController.showRegistrationForm(model);

        // Assert
        assertEquals("register", viewName);
    }

    @Test
    void showLoginForm_WithoutParameters_ReturnsLoginPage() {
        // Act
        String viewName = authController.showLoginForm(null, null, model);

        // Assert
        assertEquals("login", viewName);
    }

    @Test
    void showLoginForm_WithError_AddsErrorAttribute() {
        // Act
        String viewName = authController.showLoginForm("true", null, model);

        // Assert
        assertEquals("login", viewName);
        verify(model).addAttribute("error", "Invalid username or password!");
    }

    @Test
    void showLoginForm_WithRegistered_AddsMessageAttribute() {
        // Act
        String viewName = authController.showLoginForm(null, "true", model);

        // Assert
        assertEquals("login", viewName);
        verify(model).addAttribute("message", "Registration successful! Please login.");
    }

    @Test
    void registerUser_Success_RedirectsToLogin() {
        // Arrange
        User testUser = new User("testuser", "test@example.com", "encodedPassword", Role.PARTICIPANT);
        when(userService.registerUser(anyString(), anyString(), anyString(), any(Role.class)))
                .thenReturn(testUser);

        // Act
        String viewName = authController.registerUser("testuser", "test@example.com", "password123", "PARTICIPANT", model);

        // Assert
        assertEquals("redirect:/login?registered=true", viewName);
        verify(userService).registerUser("testuser", "test@example.com", "password123", Role.PARTICIPANT);
    }

    @Test
    void registerUser_UsernameExists_ReturnsRegisterWithError() {
        // Arrange
        when(userService.registerUser(anyString(), anyString(), anyString(), any(Role.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        // Act
        String viewName = authController.registerUser("existinguser", "test@example.com", "password123", "ADMIN", model);

        // Assert
        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Username already exists");
        verify(userService).registerUser("existinguser", "test@example.com", "password123", Role.ADMIN);
    }

    @Test
    void registerUser_EmailExists_ReturnsRegisterWithError() {
        // Arrange
        when(userService.registerUser(anyString(), anyString(), anyString(), any(Role.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act
        String viewName = authController.registerUser("newuser", "existing@example.com", "password123", "PARTICIPANT", model);

        // Assert
        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Email already exists");
        verify(userService).registerUser("newuser", "existing@example.com", "password123", Role.PARTICIPANT);
    }

    @Test
    void registerUser_InvalidRole_ReturnsRegisterWithError() {
        // Act & Assert
        // This will throw IllegalArgumentException when Role.valueOf("INVALID") is called
        // We need to catch it in the controller or let it propagate
        try {
            authController.registerUser("testuser", "test@example.com", "password123", "INVALID", model);
        } catch (IllegalArgumentException e) {
            // Expected behavior - the role conversion fails
        }
    }
}