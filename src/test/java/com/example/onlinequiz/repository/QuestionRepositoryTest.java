package com.example.onlinequiz.repository;

import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void findByQuizId_QuizExists_ReturnsQuestions() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");
        entityManager.persist(quiz);

        Question question1 = new Question();
        question1.setText("What is Java?");
        question1.setQuiz(quiz);
        entityManager.persist(question1);

        Question question2 = new Question();
        question2.setText("What is JVM?");
        question2.setQuiz(quiz);
        entityManager.persist(question2);

        entityManager.flush();

        // Act - This will now work with the updated repository
        List<Question> foundQuestions = questionRepository.findByQuizId(quiz.getId());

        // Assert
        assertNotNull(foundQuestions);
        assertEquals(2, foundQuestions.size());
        assertTrue(foundQuestions.stream().allMatch(q -> q.getQuiz().getId().equals(quiz.getId())));
    }

    @Test
    void findByQuizId_NoQuestions_ReturnsEmptyList() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("Empty Quiz");
        entityManager.persist(quiz);
        entityManager.flush();

        // Act
        List<Question> foundQuestions = questionRepository.findByQuizId(quiz.getId());

        // Assert
        assertNotNull(foundQuestions);
        assertTrue(foundQuestions.isEmpty());
    }

    @Test
    void findByQuizId_NonExistentQuiz_ReturnsEmptyList() {
        // Act
        List<Question> foundQuestions = questionRepository.findByQuizId(999L);

        // Assert
        assertNotNull(foundQuestions);
        assertTrue(foundQuestions.isEmpty());
    }

    @Test
    void deleteByQuizId_QuizExists_DeletesQuestions() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("Quiz to Delete");
        entityManager.persist(quiz);

        Question question1 = new Question();
        question1.setText("Question 1");
        question1.setQuiz(quiz);
        entityManager.persist(question1);

        Question question2 = new Question();
        question2.setText("Question 2");
        question2.setQuiz(quiz);
        entityManager.persist(question2);

        entityManager.flush();

        // Verify questions exist before deletion
        List<Question> questionsBefore = questionRepository.findByQuizId(quiz.getId());
        assertEquals(2, questionsBefore.size());

        // Act
        questionRepository.deleteByQuizId(quiz.getId());
        entityManager.flush(); // Important: flush after delete

        // Assert
        List<Question> questionsAfter = questionRepository.findByQuizId(quiz.getId());
        assertTrue(questionsAfter.isEmpty());
    }

    @Test
    void countByQuizId_QuizExists_ReturnsCount() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");
        entityManager.persist(quiz);

        Question question1 = new Question();
        question1.setText("What is Java?");
        question1.setQuiz(quiz);
        entityManager.persist(question1);

        Question question2 = new Question();
        question2.setText("What is JVM?");
        question2.setQuiz(quiz);
        entityManager.persist(question2);

        entityManager.flush();

        // Act
        long count = questionRepository.countByQuizId(quiz.getId());

        // Assert
        assertEquals(2, count);
    }
}