package com.example.ecommerce.service;

import com.example.ecommerce.util.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyticsService {
    private final Map<String, Integer> productViews;
    private final Map<String, Double> userPurchases;
    private final Map<String, List<String>> userProductViews;
    private final Map<String, Integer> hourlyStats;
    private final Logger logger;
    
    public AnalyticsService(Logger logger) {
        this.productViews = new ConcurrentHashMap<>();
        this.userPurchases = new ConcurrentHashMap<>();
        this.userProductViews = new ConcurrentHashMap<>();
        this.hourlyStats = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    public void trackProductView(String userId, String productId) {
        // Track product view count
        productViews.merge(productId, 1, Integer::sum);
        
        // Track user's product views
        userProductViews.computeIfAbsent(userId, k -> new ArrayList<>()).add(productId);
        
        // Track hourly stats
        String hour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
        hourlyStats.merge(hour, 1, Integer::sum);
        
        logger.debug("Tracked product view - User: " + userId + ", Product: " + productId);
    }
    
    public void trackPurchase(String userId, double amount) {
        userPurchases.merge(userId, amount, Double::sum);
        logger.debug("Tracked purchase - User: " + userId + ", Amount: $" + String.format("%.2f", amount));
    }
    
    public Map<String, Integer> getTopProducts(int limit) {
        return productViews.entrySet().stream()
                          .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                          .limit(limit)
                          .collect(LinkedHashMap::new,
                                  (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                                  LinkedHashMap::putAll);
    }
    
    public Map<String, Double> getTopSpenders(int limit) {
        return userPurchases.entrySet().stream()
                           .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                           .limit(limit)
                           .collect(LinkedHashMap::new,
                                   (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                                   LinkedHashMap::putAll);
    }
    
    public void generateHourlyReport() {
        String currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
        int views = hourlyStats.getOrDefault(currentHour, 0);
        
        if (views > 0) {
            logger.info("Hourly Report [" + currentHour + "]: " + views + " product views");
        }
    }
    
    public void updatePopularProducts() {
        Map<String, Integer> topProducts = getTopProducts(10);
        logger.debug("Top 10 products updated: " + topProducts.size() + " products");
    }
    
    public void calculateUserEngagement() {
        int totalUsers = userProductViews.size();
        int totalViews = productViews.values().stream().mapToInt(Integer::intValue).sum();
        double avgViewsPerUser = totalUsers > 0 ? (double) totalViews / totalUsers : 0;
        
        logger.debug("User engagement - Total Users: " + totalUsers + 
                    ", Total Views: " + totalViews + 
                    ", Avg Views/User: " + String.format("%.2f", avgViewsPerUser));
    }
    
    public int getTotalProductViews() {
        return productViews.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public int getUniqueViewers() {
        return userProductViews.size();
    }
    
    public double getTotalRevenue() {
        return userPurchases.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    public void clearOldData() {
        // Clear data older than 24 hours
        String cutoff = LocalDateTime.now().minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
        
        List<String> toRemove = new ArrayList<>();
        for (String hour : hourlyStats.keySet()) {
            if (hour.compareTo(cutoff) < 0) {
                toRemove.add(hour);
            }
        }
        
        for (String hour : toRemove) {
            hourlyStats.remove(hour);
        }
        
        if (!toRemove.isEmpty()) {
            logger.info("Cleared analytics data for " + toRemove.size() + " hours");
        }
    }
}
