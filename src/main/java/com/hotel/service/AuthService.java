package com.hotel.service;

import com.hotel.model.User;
import com.hotel.model.enums.UserRole;
import com.hotel.repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String fullName, String email, String phone, String rawPassword, UserRole role) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            throw new RuntimeException("Email already registered: " + email);
        }

        String salt = generateSalt();
        String hash = hashPassword(rawPassword, salt);

        User user = new User(fullName, email, phone, hash, salt, role);
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        String hashAttempt = hashPassword(rawPassword, user.getSalt());

        if (!hashAttempt.equals(user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    public void requireRole(User user, UserRole requiredRole) {
        if (user.getRole() != requiredRole) {
            throw new RuntimeException("Access denied: requires role " + requiredRole);
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private String hashPassword(String rawPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hashedBytes = digest.digest(rawPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available: " + e.getMessage(), e);
        }
    }
}