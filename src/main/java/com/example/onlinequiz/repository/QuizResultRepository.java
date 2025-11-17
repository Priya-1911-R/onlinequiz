package com.example.onlinequiz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.QuizResult;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    Optional<QuizResult> findByQuizAttempt(QuizAttempt quizAttempt);
}
