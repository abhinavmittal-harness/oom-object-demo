package com.example.ecommerce.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Notification {
    private final String id;
    private final String userId;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final LocalDateTime createdAt;
    private boolean sent;
    private LocalDateTime sentAt;
    private int retryCount;
    
    public enum NotificationType {
        WELCOME, ORDER_CONFIRMATION, SHIPPING_UPDATE, SUPPORT_TICKET, PROMOTIONAL, SYSTEM_ALERT
    }
    
    public Notification(String userId, String title, String message, NotificationType type) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.sent = false;
        this.retryCount = 0;
    }
    
    public void markAsSent() {
        this.sent = true;
        this.sentAt = LocalDateTime.now();
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    public boolean shouldRetry() {
        return !sent && retryCount < 3;
    }
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isSent() { return sent; }
    public LocalDateTime getSentAt() { return sentAt; }
    public int getRetryCount() { return retryCount; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Notification{id='" + id + "', userId='" + userId + "', type=" + type + 
               ", sent=" + sent + ", retryCount=" + retryCount + "}";
    }
}
