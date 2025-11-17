package com.example.onlinequiz.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue; // Add this import
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;

@DataJpaTest
class QuizRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuizRepository quizRepository;

    @Test
    void findByActiveTrue_ReturnsOnlyActiveQuizzes() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("creator", "creator@test.com", "password", Role.ADMIN);
        entityManager.persist(user);

        Quiz activeQuiz = new Quiz();
        activeQuiz.setTitle("Active Quiz");
        activeQuiz.setActive(true);
        activeQuiz.setCreatedBy(user);
        entityManager.persist(activeQuiz);

        Quiz inactiveQuiz = new Quiz();
        inactiveQuiz.setTitle("Inactive Quiz");
        inactiveQuiz.setActive(false);
        inactiveQuiz.setCreatedBy(user);
        entityManager.persist(inactiveQuiz);

        entityManager.flush();

        // Act
        List<Quiz> activeQuizzes = quizRepository.findByActiveTrue();

        // Assert
        assertEquals(1, activeQuizzes.size());
        assertEquals("Active Quiz", activeQuizzes.get(0).getTitle());
        assertTrue(activeQuizzes.get(0).isActive());
    }

    @Test
    void findByCreatedBy_ReturnsUserQuizzes() {
        // Arrange - FIXED: Use Role enum instead of String
        User user1 = new User("user1", "user1@test.com", "password", Role.ADMIN);
        entityManager.persist(user1);

        User user2 = new User("user2", "user2@test.com", "password", Role.ADMIN);
        entityManager.persist(user2);

        Quiz quiz1 = new Quiz();
        quiz1.setTitle("User1 Quiz");
        quiz1.setCreatedBy(user1);
        entityManager.persist(quiz1);

        Quiz quiz2 = new Quiz();
        quiz2.setTitle("User2 Quiz");
        quiz2.setCreatedBy(user2);
        entityManager.persist(quiz2);

        entityManager.flush();

        // Act
        List<Quiz> user1Quizzes = quizRepository.findByCreatedBy(user1);

        // Assert
        assertEquals(1, user1Quizzes.size());
        assertEquals("User1 Quiz", user1Quizzes.get(0).getTitle());
        assertEquals(user1.getId(), user1Quizzes.get(0).getCreatedBy().getId());
    }
}