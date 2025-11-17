package com.example.onlinequiz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.User;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    // Find all attempts by a user, ordered by submission time (most recent first)
    List<QuizAttempt> findByUserOrderBySubmittedAtDesc(User user);
    
    // Find all attempts for a quiz, ordered by submission time
    List<QuizAttempt> findByQuizIdOrderBySubmittedAtDesc(Long quizId);
    
    // Find in-progress attempt (not submitted) for a user and quiz
    Optional<QuizAttempt> findByUserAndQuizAndSubmittedAtIsNull(User user, Quiz quiz);
    
    // Find completed attempts by a user (submitted is not null)
    List<QuizAttempt> findByUserAndSubmittedAtIsNotNull(User user);
    
    // Find completed attempts for a quiz (submitted is not null)
    List<QuizAttempt> findByQuizAndSubmittedAtIsNotNull(Quiz quiz);
    
    // Find all attempts by a user, ordered by start time (most recent first)
    List<QuizAttempt> findByUserOrderByStartedAtDesc(User user);
    
    // Custom query to find user attempts for a specific quiz, ordered by start time
    @Query("SELECT a FROM QuizAttempt a WHERE a.user = :user AND a.quiz = :quiz ORDER BY a.startedAt DESC")
    List<QuizAttempt> findUserAttemptsForQuiz(@Param("user") User user, @Param("quiz") Quiz quiz);
}