package com.example.onlinequiz.service;

import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendQuizResultsEmail_ValidParameters_NoException() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        // You might need to set other required fields on attempt

        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> 
            emailService.sendQuizResultsEmail(attempt)
        );
    }

    @Test
    void sendRegistrationEmail_ValidUser_NoException() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);

        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> 
            emailService.sendRegistrationEmail(user)
        );
    }

    @Test
    void sendPasswordResetEmail_ValidParameters_NoException() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        String resetToken = "test-reset-token";

        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> 
            emailService.sendPasswordResetEmail(user, resetToken)
        );
    }

    @Test
    void sendQuizResultsEmail_NullAttempt_NoException() {
        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> 
            emailService.sendQuizResultsEmail(null)
        );
    }

    @Test
    void sendRegistrationEmail_NullUser_NoException() {
        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> 
            emailService.sendRegistrationEmail(null)
        );
    }
}