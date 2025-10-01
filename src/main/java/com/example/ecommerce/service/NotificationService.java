package com.example.ecommerce.service;

import com.example.ecommerce.model.Notification;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;

public class NotificationService {
    private final BlockingQueue<Notification> notificationQueue;
    private final Logger logger;
    private final Random random;
    
    public NotificationService(Logger logger) {
        this.notificationQueue = new LinkedBlockingQueue<>();
        this.logger = logger;
        this.random = new Random();
    }
    
    public void sendWelcomeNotification(User user) {
        Notification notification = new Notification(
            user.getId(),
            "Welcome to Our Store!",
            "Thank you for joining us, " + user.getName() + "! Enjoy shopping with us.",
            Notification.NotificationType.WELCOME
        );
        
        notificationQueue.offer(notification);
        logger.debug("Queued welcome notification for user: " + user.getEmail());
    }
    
    public void sendOrderConfirmation(User user, Order order) {
        Notification notification = new Notification(
            user.getId(),
            "Order Confirmation",
            "Your order " + order.getId() + " has been confirmed. Total: $" + 
            String.format("%.2f", order.getTotalAmount()),
            Notification.NotificationType.ORDER_CONFIRMATION
        );
        
        notificationQueue.offer(notification);
        logger.debug("Queued order confirmation for user: " + user.getEmail());
    }
    
    public void sendShippingNotification(User user, Order order) {
        Notification notification = new Notification(
            user.getId(),
            "Order Shipped",
            "Your order " + order.getId() + " has been shipped and is on its way!",
            Notification.NotificationType.SHIPPING_UPDATE
        );
        
        notificationQueue.offer(notification);
        logger.debug("Queued shipping notification for user: " + user.getEmail());
    }
    
    public void sendSupportTicket(User user, String issue) {
        Notification notification = new Notification(
            user.getId(),
            "Support Ticket Created",
            "We've received your support request: " + issue + ". We'll get back to you soon!",
            Notification.NotificationType.SUPPORT_TICKET
        );
        
        notificationQueue.offer(notification);
        logger.debug("Queued support ticket notification for user: " + user.getEmail());
    }
    
    public void sendPromotionalNotification(User user, String promotion) {
        Notification notification = new Notification(
            user.getId(),
            "Special Offer!",
            promotion,
            Notification.NotificationType.PROMOTIONAL
        );
        
        notificationQueue.offer(notification);
        logger.debug("Queued promotional notification for user: " + user.getEmail());
    }
    
    public void processNotificationQueue() {
        // Process up to 10 notifications per batch
        int processed = 0;
        while (processed < 10 && !notificationQueue.isEmpty()) {
            Notification notification = notificationQueue.poll();
            if (notification != null) {
                sendNotification(notification);
                processed++;
            }
        }
        
        if (processed > 0) {
            logger.debug("Processed " + processed + " notifications");
        }
    }
    
    private void sendNotification(Notification notification) {
        // Simulate sending notification (email, SMS, push, etc.)
        // Sometimes notifications fail and need retry
        if (random.nextDouble() < 0.9) { // 90% success rate
            notification.markAsSent();
            logger.debug("Sent notification: " + notification.getId() + " to user: " + notification.getUserId());
        } else {
            // Failed to send, increment retry count
            notification.incrementRetryCount();
            if (notification.shouldRetry()) {
                // Re-queue for retry
                notificationQueue.offer(notification);
                logger.debug("Failed to send notification: " + notification.getId() + ", queued for retry");
            } else {
                logger.warn("Failed to send notification after max retries: " + notification.getId());
            }
        }
    }
    
    public int getPendingNotificationCount() {
        return notificationQueue.size();
    }
    
    public void clearNotificationQueue() {
        int cleared = notificationQueue.size();
        notificationQueue.clear();
        if (cleared > 0) {
            logger.info("Cleared " + cleared + " pending notifications");
        }
    }
}
