package com.example.onlinequiz.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals; // Add this import
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.onlinequiz.model.Role;
import com.example.onlinequiz.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_UserExists_ReturnsUser() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persistAndFlush(user);

        // Act - Now handles Optional return type
        Optional<User> foundOpt = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(foundOpt.isPresent());
        User found = foundOpt.get();
        assertEquals("testuser", found.getUsername());
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void findByUsername_UserNotFound_ReturnsEmptyOptional() {
        // Act
        Optional<User> foundOpt = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(foundOpt.isPresent());
    }

    @Test
    void findByEmail_UserExists_ReturnsUser() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persistAndFlush(user);

        // Act - Now handles Optional return type
        Optional<User> foundOpt = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(foundOpt.isPresent());
        User found = foundOpt.get();
        assertEquals("test@example.com", found.getEmail());
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void findByEmail_UserNotFound_ReturnsEmptyOptional() {
        // Act
        Optional<User> foundOpt = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundOpt.isPresent());
    }

    @Test
    void existsByUsername_UserExists_ReturnsTrue() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persistAndFlush(user);

        // Act
        boolean exists = userRepository.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_UserNotExists_ReturnsFalse() {
        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByEmail_UserExists_ReturnsTrue() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persistAndFlush(user);

        // Act
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_UserNotExists_ReturnsFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    // Additional test for saving and retrieving user
    @Test
    void save_NewUser_PersistsUser() {
        // Arrange - FIXED: Use Role enum instead of String
        User newUser = new User("newuser", "new@example.com", "password", Role.PARTICIPANT);

        // Act
        User savedUser = userRepository.save(newUser);
        entityManager.flush();

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
    }

    @Test
    void findById_UserExists_ReturnsUser() {
        // Arrange - FIXED: Use Role enum instead of String
        User user = new User("testuser", "test@example.com", "password", Role.PARTICIPANT);
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> foundOpt = userRepository.findById(user.getId());

        // Assert
        assertTrue(foundOpt.isPresent());
        assertEquals("testuser", foundOpt.get().getUsername());
    }
}