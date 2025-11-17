package com.example.onlinequiz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlinequiz.model.Question;
import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.UserAnswer;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    // Find all answers for a specific quiz attempt
    List<UserAnswer> findByQuizAttempt(QuizAttempt quizAttempt);
    
    // Find all answers for a quiz attempt ID
    List<UserAnswer> findByQuizAttemptId(Long quizAttemptId);
    
    // Find specific answer for a question in an attempt
    Optional<UserAnswer> findByQuizAttemptAndQuestion(QuizAttempt quizAttempt, Question question);
    
    // Find answers for a specific question across all attempts
    List<UserAnswer> findByQuestion(Question question);
    
    // Find answers for a specific question ID
    List<UserAnswer> findByQuestionId(Long questionId);
    
    // Find correct answers for a quiz attempt
    List<UserAnswer> findByQuizAttemptAndCorrectTrue(QuizAttempt quizAttempt);
    
    // Find incorrect answers for a quiz attempt
    List<UserAnswer> findByQuizAttemptAndCorrectFalse(QuizAttempt quizAttempt);
    
    // Count correct answers for a quiz attempt
    Long countByQuizAttemptAndCorrectTrue(QuizAttempt quizAttempt);
    
    // Count incorrect answers for a quiz attempt
    Long countByQuizAttemptAndCorrectFalse(QuizAttempt quizAttempt);
    
    // Calculate accuracy for a specific question
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.question.id = :questionId AND ua.correct = true")
    Long countCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
    
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.question.id = :questionId")
    Long countTotalAnswersByQuestionId(@Param("questionId") Long questionId);
    
    // Get question difficulty statistics
    @Query("SELECT ua.question.id, COUNT(ua), SUM(CASE WHEN ua.correct = true THEN 1 ELSE 0 END) " +
           "FROM UserAnswer ua WHERE ua.question.id IN :questionIds GROUP BY ua.question.id")
    List<Object[]> findQuestionStatistics(@Param("questionIds") List<Long> questionIds);
    
    // Find most frequently missed questions
    @Query("SELECT ua.question.id, COUNT(ua) as missCount FROM UserAnswer ua " +
           "WHERE ua.correct = false GROUP BY ua.question.id ORDER BY missCount DESC")
    List<Object[]> findMostMissedQuestions();
    
    // Find user's answer pattern for a question
    @Query("SELECT ua.selectedOptionIndex, COUNT(ua) FROM UserAnswer ua " +
           "WHERE ua.question.id = :questionId GROUP BY ua.selectedOptionIndex")
    List<Object[]> findAnswerDistributionByQuestion(@Param("questionId") Long questionId);
    
    // Check if user has answered a specific question in an attempt
    boolean existsByQuizAttemptAndQuestion(QuizAttempt quizAttempt, Question question);
    
    // Find answers by selected option index
    List<UserAnswer> findBySelectedOptionIndex(Integer selectedOptionIndex);
    
    // Find answers for multiple attempts
    List<UserAnswer> findByQuizAttemptIn(List<QuizAttempt> quizAttempts);
    
    // Delete all answers for a quiz attempt
    void deleteByQuizAttempt(QuizAttempt quizAttempt);
    
    // Delete answers for multiple attempts
    void deleteByQuizAttemptIn(List<QuizAttempt> quizAttempts);
    
    // Calculate average time spent per question (if you track time)
    @Query("SELECT AVG(ua.timeSpent) FROM UserAnswer ua WHERE ua.question.id = :questionId")
    Double findAverageTimeSpentByQuestionId(@Param("questionId") Long questionId);
}