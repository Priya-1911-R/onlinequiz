package com.example.onlinequiz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.onlinequiz.model.QuizAttempt;
import com.example.onlinequiz.model.QuizResult;
import com.example.onlinequiz.model.User; 

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendQuizResultsEmail(QuizAttempt attempt) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(attempt.getUser().getEmail());
        message.setSubject("Your Quiz Results");
        message.setText("Your quiz attempt has been completed. Score: " + attempt.getScore());
        mailSender.send(message);
    }

    public void sendRegistrationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Online Quiz!");
        message.setText("Thank you for registering, " + user.getUsername() + "!");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("Your password reset token is: " + resetToken);
        mailSender.send(message);
    }
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendQuizResultEmail(User user, QuizResult result) {
        try {
            // Mock implementation - replace with actual email sending logic
            logger.info("=== QUIZ RESULT EMAIL ===");
            logger.info("To: {}", user.getEmail());
            logger.info("Subject: Quiz Results - {}", result.getQuizAttempt().getQuiz().getTitle());
            logger.info("Score: {}%", result.getScore());
            logger.info("Correct: {}/{}", result.getCorrectAnswers(), result.getTotalQuestions());
            logger.info("=== EMAIL WOULD BE SENT ===");
            
            // TODO: Implement actual email sending when JavaMailSender is configured
            // For now, this just logs the email content
        } catch (Exception e) {
            logger.error("Failed to send quiz result email", e);
        }
    }
    

}