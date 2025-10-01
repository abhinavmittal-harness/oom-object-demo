package com.example.ecommerce.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserSession {
    private final String sessionId;
    private final String userId;
    private final LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private final Map<String, Integer> shoppingCart;
    private final Map<String, Object> attributes;
    private boolean valid;
    
    // Session timeout in minutes
    private static final int SESSION_TIMEOUT_MINUTES = 1; // Reduced from 30 to 1 minute
    
    public UserSession(String userId) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.shoppingCart = new HashMap<>();
        this.attributes = new HashMap<>();
        this.valid = true;
    }
    
    public void touch() {
        this.lastAccessedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return lastAccessedAt.isBefore(LocalDateTime.now().minusMinutes(SESSION_TIMEOUT_MINUTES));
    }
    
    public void addToCart(String productId, int quantity) {
        touch();
        shoppingCart.merge(productId, quantity, Integer::sum);
    }
    
    public void removeFromCart(String productId) {
        touch();
        shoppingCart.remove(productId);
    }
    
    public void clearCart() {
        touch();
        shoppingCart.clear();
    }
    
    public void setAttribute(String key, Object value) {
        touch();
        attributes.put(key, value);
    }
    
    public Object getAttribute(String key) {
        touch();
        return attributes.get(key);
    }
    
    public void invalidate() {
        this.valid = false;
        shoppingCart.clear();
        attributes.clear();
    }
    
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public Map<String, Integer> getShoppingCart() { return new HashMap<>(shoppingCart); }
    public boolean isValid() { return valid && !isExpired(); }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return Objects.equals(sessionId, that.sessionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
    
    @Override
    public String toString() {
        return "UserSession{sessionId='" + sessionId + "', userId='" + userId + 
               "', valid=" + valid + ", cartItems=" + shoppingCart.size() + 
               ", expired=" + isExpired() + "}";
    }
}
