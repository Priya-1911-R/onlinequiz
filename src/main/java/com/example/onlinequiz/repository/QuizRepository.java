
    package com.example.onlinequiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlinequiz.model.Quiz;
import com.example.onlinequiz.model.User;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByActiveTrue();
    List<Quiz> findByCreatedBy(User createdBy);
    

    @Query("SELECT q FROM Quiz q WHERE q.active = true AND (q.publicQuiz = true OR q.createdBy = :user)")
    List<Quiz> findAvailableQuizzes(@Param("user") User user);
}
