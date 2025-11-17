package com.example.onlinequiz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;
import com.example.onlinequiz.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // FIXED: Changed parameter from String role to Role role
    public User registerUser(String username, String email, String password, Role role) {
        // Check if user exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User(username, email, passwordEncoder.encode(password), role);
        return userRepository.save(user);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Debug logging
        logger.info("=== AUTHENTICATION DEBUG ===");
        logger.info("User found: {}", user.getUsername());
        logger.info("User role: {}", user.getRole());
        logger.info("User password (encoded): {}", user.getPassword());
        logger.info("Authorities: {}", user.getAuthorities());
        
        // Return the User entity itself since it implements UserDetails
        return user;
    }
}