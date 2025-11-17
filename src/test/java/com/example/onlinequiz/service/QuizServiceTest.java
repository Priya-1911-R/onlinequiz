package com.example.onlinequiz.service;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void getAllActiveQuizzes_ReturnsActiveQuizzes() {
        // Arrange
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);
        quiz1.setTitle("Java Basics");
        quiz1.setActive(true);

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Spring Framework");
        quiz2.setActive(true);

        List<Quiz> activeQuizzes = Arrays.asList(quiz1, quiz2);
        when(quizRepository.findByActiveTrue()).thenReturn(activeQuizzes);

        // Act
        List<Quiz> result = quizService.getAllActiveQuizzes();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Quiz::isActive));
        verify(quizRepository, times(1)).findByActiveTrue();
    }

    @Test
    void getQuizById_Exists_ReturnsQuiz() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        // Act
        Optional<Quiz> result = quizService.getQuizById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Quiz", result.get().getTitle());
        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void getQuizById_NotFound_ReturnsEmpty() {
        // Arrange
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Quiz> result = quizService.getQuizById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(quizRepository, times(1)).findById(999L);
    }

    @Test
    void createQuiz_SavesQuiz() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("New Quiz");
        quiz.setDescription("Test Description");

        when(quizRepository.save(quiz)).thenReturn(quiz);

        // Act
        Quiz result = quizService.createQuiz(quiz);

        // Assert
        assertNotNull(result);
        assertEquals("New Quiz", result.getTitle());
        verify(quizRepository, times(1)).save(quiz);
    }
}