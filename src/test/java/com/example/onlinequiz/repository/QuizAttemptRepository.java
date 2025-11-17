package com.example.onlinequiz.repository;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    Optional<QuizAttempt> findByUserAndQuizAndSubmittedAtIsNull(User user, Quiz quiz);
    
    // Add this method to find all attempts by user
    List<QuizAttempt> findByUserOrderByStartedAtDesc(User user);
    
    // Add this method for completed attempts only
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user AND qa.submittedAt IS NOT NULL ORDER BY qa.submittedAt DESC")
    List<QuizAttempt> findCompletedAttemptsByUser(@Param("user") User user);
    
    // Add these methods for the tests
    List<QuizAttempt> findByUserId(Long userId);
    
    List<QuizAttempt> findByQuizIdOrderBySubmittedAtDesc(Long quizId);
}