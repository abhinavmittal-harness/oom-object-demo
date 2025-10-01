package com.example.ecommerce.service;

import com.example.ecommerce.model.User;
import com.example.ecommerce.model.UserSession;
import com.example.ecommerce.util.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session Manager - Handles user sessions and shopping carts
 */
public class SessionManager {
    private final Map<String, UserSession> activeSessions;
    private final Map<String, UserSession> expiredSessions;
    private final Logger logger;
    
    public SessionManager(Logger logger) {
        this.activeSessions = new ConcurrentHashMap<>();
        this.expiredSessions = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    public String createSession(User user) {
        UserSession session = new UserSession(user.getId());
        activeSessions.put(session.getSessionId(), session);
        
        logger.debug("Created session for user: " + user.getEmail() + " (Session: " + session.getSessionId() + ")");
        return session.getSessionId();
    }
    
    public UserSession getSession(String sessionId) {
        UserSession session = activeSessions.get(sessionId);
        if (session != null && session.isValid()) {
            session.touch();
            return session;
        }
        return null;
    }
    
    public void invalidateSession(String sessionId) {
        UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            session.invalidate();
            logger.debug("Invalidated session: " + sessionId);
        }
    }
    
    public void addToCart(String sessionId, String productId, int quantity) {
        UserSession session = getSession(sessionId);
        if (session != null) {
            session.addToCart(productId, quantity);
            logger.debug("Added to cart - Session: " + sessionId + ", Product: " + productId + ", Qty: " + quantity);
        }
    }
    
    public void removeFromCart(String sessionId, String productId) {
        UserSession session = getSession(sessionId);
        if (session != null) {
            session.removeFromCart(productId);
            logger.debug("Removed from cart - Session: " + sessionId + ", Product: " + productId);
        }
    }
    
    public Map<String, Integer> getCart(String sessionId) {
        UserSession session = getSession(sessionId);
        return session != null ? session.getShoppingCart() : new ConcurrentHashMap<>();
    }
    
    public void clearCart(String sessionId) {
        UserSession session = getSession(sessionId);
        if (session != null) {
            session.clearCart();
            logger.debug("Cleared cart for session: " + sessionId);
        }
    }
    
    /**
     * Clean up expired sessions
     */
    public int cleanupExpiredSessions() {
        List<String> expiredSessionIds = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Find expired sessions
        for (Map.Entry<String, UserSession> entry : activeSessions.entrySet()) {
            UserSession session = entry.getValue();
            if (session.isExpired()) {
                expiredSessionIds.add(entry.getKey());
            }
        }
        
        // Move expired sessions to "expired" collection
        for (String sessionId : expiredSessionIds) {
            UserSession expiredSession = activeSessions.remove(sessionId);
            if (expiredSession != null) {
                expiredSession.invalidate();
                expiredSessions.put(sessionId, expiredSession);
            }
        }
        
        if (!expiredSessionIds.isEmpty()) {
            logger.debug("Moved " + expiredSessionIds.size() + " expired sessions to expired collection");
        }
        
        return expiredSessionIds.size();
    }
    
    public int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    public int getExpiredSessionCount() {
        return expiredSessions.size();
    }
    
    public int getTotalSessionCount() {
        return activeSessions.size() + expiredSessions.size();
    }
    
    /**
     * Purge old expired sessions
     */
    public void purgeOldExpiredSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<String> toPurge = new ArrayList<>();
        
        for (Map.Entry<String, UserSession> entry : expiredSessions.entrySet()) {
            if (entry.getValue().getCreatedAt().isBefore(cutoff)) {
                toPurge.add(entry.getKey());
            }
        }
        
        for (String sessionId : toPurge) {
            expiredSessions.remove(sessionId);
        }
        
        if (!toPurge.isEmpty()) {
            logger.info("Purged " + toPurge.size() + " old expired sessions");
        }
    }
    
    /**
     * Debug method to show session statistics
     */
    public void printSessionStats() {
        logger.info("Session Stats - Active: " + activeSessions.size() + 
                   ", Expired: " + expiredSessions.size() + 
                   ", Total: " + getTotalSessionCount());
    }
}
