package com.example.onlinequiz.security;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals; // Add this import
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext(); // Clear security context before each test
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange - FIXED: Use Role enum instead of String
        String validToken = "valid.jwt.token";
        String username = "testuser";
        
        User user = new User(username, "test@example.com", "password", Role.PARTICIPANT);
        UserDetails userDetails = user;
        
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).validateToken(validToken);
        verify(jwtUtil, times(1)).getUsernameFromToken(validToken);
        verify(userService, times(1)).loadUserByUsername(username);
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Check that authentication was set in security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_InvalidToken_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + invalidToken);
        
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).validateToken(invalidToken);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Authentication should not be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_NoToken_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange - No Authorization header

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(userService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Authentication should not be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_MalformedAuthHeader_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "MalformedHeaderWithoutBearer");

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ExpiredToken_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange
        String expiredToken = "expired.jwt.token";
        request.addHeader("Authorization", "Bearer " + expiredToken);
        
        when(jwtUtil.validateToken(expiredToken)).thenReturn(false);

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).validateToken(expiredToken);
        verify(jwtUtil, never()).getUsernameFromToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidTokenButUserNotFound_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        String username = "nonexistent";
        
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(null);

        // Act
        jwtFilter.doFilter(request, response, filterChain);

        // Assert
        verify(jwtUtil, times(1)).validateToken(validToken);
        verify(jwtUtil, times(1)).getUsernameFromToken(validToken);
        verify(userService, times(1)).loadUserByUsername(username);
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Authentication should not be set because user was not found
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}