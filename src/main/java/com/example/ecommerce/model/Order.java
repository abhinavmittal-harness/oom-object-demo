package com.example.ecommerce.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final String id;
    private final String userId;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    public Order(String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        this.updatedAt = LocalDateTime.now();
    }
    
    public double getTotalAmount() {
        return items.stream()
                   .mapToDouble(item -> item.getPrice() * item.getQuantity())
                   .sum();
    }
    
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Order{id='" + id + "', userId='" + userId + "', status=" + status + 
               ", items=" + items.size() + ", total=" + getTotalAmount() + "}";
    }
}
