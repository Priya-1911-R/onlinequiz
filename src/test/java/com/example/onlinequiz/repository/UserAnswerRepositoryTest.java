package com.example.onlinequiz.repository;

import com.example.onlinequiz.model.UserAnswer;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.model.Role; // Add this import
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserAnswerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Test
    void findByQuizAttempt_AttemptExists_ReturnsUserAnswers() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persist(user);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        entityManager.persist(attempt);

        Question question1 = new Question();
        question1.setText("Question 1");
        entityManager.persist(question1);

        Question question2 = new Question();
        question2.setText("Question 2");
        entityManager.persist(question2);

        UserAnswer answer1 = new UserAnswer(attempt, question1, 0);
        UserAnswer answer2 = new UserAnswer(attempt, question2, 1);
        
        entityManager.persist(answer1);
        entityManager.persist(answer2);
        entityManager.flush();

        // Act
        List<UserAnswer> foundAnswers = userAnswerRepository.findByQuizAttempt(attempt);

        // Assert
        assertNotNull(foundAnswers);
        assertEquals(2, foundAnswers.size());
        assertTrue(foundAnswers.stream().anyMatch(a -> a.getSelectedOptionIndex() == 0));
        assertTrue(foundAnswers.stream().anyMatch(a -> a.getSelectedOptionIndex() == 1));
    }

    @Test
    void findByQuizAttempt_NoAnswers_ReturnsEmptyList() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persist(user);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        entityManager.persist(attempt);
        entityManager.flush();

        // Act
        List<UserAnswer> foundAnswers = userAnswerRepository.findByQuizAttempt(attempt);

        // Assert
        assertNotNull(foundAnswers);
        assertTrue(foundAnswers.isEmpty());
    }

    @Test
    void findByQuizAttemptAndQuestion_Exists_ReturnsUserAnswer() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persist(user);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        entityManager.persist(attempt);

        Question question = new Question();
        question.setText("Test Question");
        entityManager.persist(question);

        UserAnswer userAnswer = new UserAnswer(attempt, question, 2);
        entityManager.persist(userAnswer);
        entityManager.flush();

        // Act - Now handles Optional return type
        Optional<UserAnswer> foundAnswerOpt = userAnswerRepository.findByQuizAttemptAndQuestion(attempt, question);

        // Assert
        assertTrue(foundAnswerOpt.isPresent());
        UserAnswer foundAnswer = foundAnswerOpt.get();
        assertEquals(2, foundAnswer.getSelectedOptionIndex());
        assertEquals(attempt.getId(), foundAnswer.getQuizAttempt().getId());
        assertEquals(question.getId(), foundAnswer.getQuestion().getId());
    }

    @Test
    void findByQuizAttemptAndQuestion_NotFound_ReturnsEmptyOptional() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persist(user);

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        entityManager.persist(attempt);

        Question question = new Question();
        question.setText("Test Question");
        entityManager.persist(question);
        entityManager.flush();

        // Act - No UserAnswer persisted for this combination
        Optional<UserAnswer> foundAnswerOpt = userAnswerRepository.findByQuizAttemptAndQuestion(attempt, question);

        // Assert
        assertFalse(foundAnswerOpt.isPresent());
    }

    // Additional test for edge cases
    @Test
    void findByQuizAttempt_MultipleAttempts_ReturnsCorrectAnswers() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persist(user);

        QuizAttempt attempt1 = new QuizAttempt();
        attempt1.setUser(user);
        entityManager.persist(attempt1);

        QuizAttempt attempt2 = new QuizAttempt();
        attempt2.setUser(user);
        entityManager.persist(attempt2);

        Question question = new Question();
        question.setText("Test Question");
        entityManager.persist(question);

        UserAnswer answer1 = new UserAnswer(attempt1, question, 1);
        UserAnswer answer2 = new UserAnswer(attempt2, question, 2);
        
        entityManager.persist(answer1);
        entityManager.persist(answer2);
        entityManager.flush();

        // Act
        List<UserAnswer> attempt1Answers = userAnswerRepository.findByQuizAttempt(attempt1);
        List<UserAnswer> attempt2Answers = userAnswerRepository.findByQuizAttempt(attempt2);

        // Assert
        assertEquals(1, attempt1Answers.size());
        assertEquals(1, attempt1Answers.get(0).getSelectedOptionIndex());
        
        assertEquals(1, attempt2Answers.size());
        assertEquals(2, attempt2Answers.get(0).getSelectedOptionIndex());
    }
}