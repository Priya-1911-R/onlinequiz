package com.example.onlinequiz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create test admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@quiz.com");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("=== CREATED TEST USER ===");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("Role: ADMIN");
        }

        // Create test participant user
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@quiz.com");
            user.setRole(Role.PARTICIPANT);
            userRepository.save(user);
            System.out.println("=== CREATED TEST USER ===");
            System.out.println("Username: user");
            System.out.println("Password: user123");
            System.out.println("Role: PARTICIPANT");
        }
    }
}