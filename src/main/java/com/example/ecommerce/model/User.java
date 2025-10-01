package com.example.ecommerce.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final String id;
    private final String email;
    private final String name;
    private final String passwordHash;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private boolean active;
    
    public User(String email, String name, String password) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.name = name;
        this.passwordHash = hashPassword(password);
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }
    
    private String hashPassword(String password) {
        // Simple hash for demo purposes
        return "hash_" + password.hashCode();
    }
    
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public boolean isActive() { return active; }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{id='" + id + "', email='" + email + "', name='" + name + "'}";
    }
}
