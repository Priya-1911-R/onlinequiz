package com.example.onlinequiz.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @InjectMocks
    private CustomErrorController customErrorController;

    @Test
    void handleError_404Error_Returns404Page() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.error.status_code", 404);
        Model model = mock(Model.class);

        // Act
        String viewName = customErrorController.handleError(request, model);

        // Assert
        assertEquals("error/404", viewName);
        verify(model, times(1)).addAttribute("errorMessage", "Page not found");
    }

    @Test
    void handleError_403Error_Returns403Page() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.error.status_code", 403);
        Model model = mock(Model.class);

        // Act
        String viewName = customErrorController.handleError(request, model);

        // Assert
        assertEquals("error/403", viewName);
        verify(model, times(1)).addAttribute("errorMessage", "Access denied");
    }

    @Test
    void handleError_500Error_Returns500Page() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.error.status_code", 500);
        Model model = mock(Model.class);

        // Act
        String viewName = customErrorController.handleError(request, model);

        // Assert
        assertEquals("error/500", viewName);
        verify(model, times(1)).addAttribute("errorMessage", "Internal server error");
    }

    @Test
    void handleError_UnknownError_ReturnsGenericErrorPage() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.error.status_code", 418); // Unknown status
        Model model = mock(Model.class);

        // Act
        String viewName = customErrorController.handleError(request, model);

        // Assert
        assertEquals("error/generic", viewName);
        verify(model, times(1)).addAttribute("errorMessage", "Something went wrong");
    }
}