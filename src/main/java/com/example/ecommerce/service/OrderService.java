package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.User;
import com.example.ecommerce.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OrderService {
    private final Map<String, Order> orders;
    private final Logger logger;
    
    public OrderService(Logger logger) {
        this.orders = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    public Order createOrder(String userId) {
        Order order = new Order(userId);
        orders.put(order.getId(), order);
        logger.debug("Created order: " + order.getId());
        return order;
    }
    
    public Order createOrderFromCart(User user, Map<String, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            return null;
        }
        
        Order order = new Order(user.getId());
        
        // Convert cart items to order items
        // In a real app, we'd fetch product details and validate prices
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            double price = 19.99 + (Integer.parseInt(productId.replace("PROD-", "")) % 100);
            
            OrderItem item = new OrderItem(productId, quantity, price);
            order.addItem(item);
        }
        
        orders.put(order.getId(), order);
        logger.info("Created order from cart: " + order.getId() + " for user: " + user.getEmail());
        return order;
    }
    
    public void processOrder(Order order) {
        order.setStatus(Order.OrderStatus.CONFIRMED);
        logger.info("Processing order: " + order.getId());
    }
    
    public void fulfillOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(Order.OrderStatus.SHIPPED);
            logger.info("Fulfilled order: " + orderId);
        }
    }
    
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
    
    public List<Order> getOrdersByUser(String userId) {
        return orders.values().stream()
                    .filter(order -> order.getUserId().equals(userId))
                    .collect(Collectors.toList());
    }
    
    public List<Order> getPendingOrders() {
        return orders.values().stream()
                    .filter(order -> order.getStatus() == Order.OrderStatus.PENDING ||
                                   order.getStatus() == Order.OrderStatus.CONFIRMED)
                    .collect(Collectors.toList());
    }
    
    public void cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            logger.info("Cancelled order: " + orderId);
        }
    }
    
    public int getOrderCount() {
        return orders.size();
    }
}
