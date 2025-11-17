package com.example.onlinequiz.repository;

import com.example.onlinequiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Add this method to find questions by quiz ID
    List<Question> findByQuizId(Long quizId);
    
    // Add this method to delete questions by quiz ID
    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Long quizId);
    
    // Optional: Count questions by quiz ID
    long countByQuizId(Long quizId);
}