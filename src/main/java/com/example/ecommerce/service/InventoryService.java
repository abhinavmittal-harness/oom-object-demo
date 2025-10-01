package com.example.ecommerce.service;

import com.example.ecommerce.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> inventory;
    private final Map<String, Integer> reservedStock;
    private final Logger logger;
    
    private static final int LOW_STOCK_THRESHOLD = 10;
    
    public InventoryService(Logger logger) {
        this.inventory = new ConcurrentHashMap<>();
        this.reservedStock = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    public void addStock(String productId, int quantity) {
        inventory.merge(productId, quantity, Integer::sum);
        logger.debug("Added stock for product " + productId + ": " + quantity);
    }
    
    public boolean isInStock(String productId) {
        int available = getAvailableStock(productId);
        return available > 0;
    }
    
    public int getAvailableStock(String productId) {
        int total = inventory.getOrDefault(productId, 0);
        int reserved = reservedStock.getOrDefault(productId, 0);
        return Math.max(0, total - reserved);
    }
    
    public boolean reserveStock(String productId, int quantity) {
        int available = getAvailableStock(productId);
        if (available >= quantity) {
            reservedStock.merge(productId, quantity, Integer::sum);
            logger.debug("Reserved stock for product " + productId + ": " + quantity);
            return true;
        }
        return false;
    }
    
    public void releaseReservedStock(String productId, int quantity) {
        int currentReserved = reservedStock.getOrDefault(productId, 0);
        int newReserved = Math.max(0, currentReserved - quantity);
        
        if (newReserved == 0) {
            reservedStock.remove(productId);
        } else {
            reservedStock.put(productId, newReserved);
        }
        
        logger.debug("Released reserved stock for product " + productId + ": " + quantity);
    }
    
    public void fulfillReservedStock(String productId, int quantity) {
        // Remove from both inventory and reserved stock
        int currentInventory = inventory.getOrDefault(productId, 0);
        int currentReserved = reservedStock.getOrDefault(productId, 0);
        
        int actualFulfilled = Math.min(quantity, Math.min(currentInventory, currentReserved));
        
        if (actualFulfilled > 0) {
            inventory.put(productId, currentInventory - actualFulfilled);
            
            int newReserved = currentReserved - actualFulfilled;
            if (newReserved == 0) {
                reservedStock.remove(productId);
            } else {
                reservedStock.put(productId, newReserved);
            }
            
            logger.debug("Fulfilled stock for product " + productId + ": " + actualFulfilled);
        }
    }
    
    public List<String> getLowStockProducts() {
        List<String> lowStockProducts = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String productId = entry.getKey();
            int availableStock = getAvailableStock(productId);
            
            if (availableStock <= LOW_STOCK_THRESHOLD) {
                lowStockProducts.add(productId);
            }
        }
        
        return lowStockProducts;
    }
    
    public Map<String, Integer> getInventorySnapshot() {
        Map<String, Integer> snapshot = new ConcurrentHashMap<>();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            String productId = entry.getKey();
            snapshot.put(productId, getAvailableStock(productId));
        }
        return snapshot;
    }
}
