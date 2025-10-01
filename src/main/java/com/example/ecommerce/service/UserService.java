package com.example.ecommerce.service;

import com.example.ecommerce.model.User;
import com.example.ecommerce.util.Logger;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class UserService {
    private final Map<String, User> usersByEmail;
    private final Map<String, User> usersById;
    private final Logger logger;
    
    public UserService(Logger logger) {
        this.usersByEmail = new ConcurrentHashMap<>();
        this.usersById = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    public User registerUser(User user) {
        if (usersByEmail.containsKey(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        usersByEmail.put(user.getEmail(), user);
        usersById.put(user.getId(), user);
        
        logger.debug("Registered new user: " + user.getEmail());
        return user;
    }
    
    public User getUserByEmail(String email) {
        User user = usersByEmail.get(email);
        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
        }
        return user;
    }
    
    public User getUserById(String id) {
        return usersById.get(id);
    }
    
    public boolean authenticateUser(String email, String password) {
        User user = usersByEmail.get(email);
        if (user != null && user.isActive()) {
            // Simple password check for demo
            String expectedHash = "hash_" + password.hashCode();
            if (expectedHash.equals(user.getPasswordHash())) {
                user.setLastLoginAt(LocalDateTime.now());
                return true;
            }
        }
        return false;
    }
    
    public void deactivateUser(String email) {
        User user = usersByEmail.get(email);
        if (user != null) {
            user.setActive(false);
            logger.info("Deactivated user: " + email);
        }
    }
    
    public int getUserCount() {
        return usersByEmail.size();
    }
}
