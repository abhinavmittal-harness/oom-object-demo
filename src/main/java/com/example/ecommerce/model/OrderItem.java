package com.example.ecommerce.model;

import java.util.Objects;

public class OrderItem {
    private final String productId;
    private final int quantity;
    private final double price;
    
    public OrderItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    
    public double getTotalPrice() {
        return price * quantity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity &&
               Double.compare(orderItem.price, price) == 0 &&
               Objects.equals(productId, orderItem.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, price);
    }
    
    @Override
    public String toString() {
        return "OrderItem{productId='" + productId + "', quantity=" + quantity + ", price=" + price + "}";
    }
}
