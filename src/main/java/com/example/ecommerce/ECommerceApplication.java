package com.example.ecommerce;

import com.example.ecommerce.model.*;
import com.example.ecommerce.service.*;
import com.example.ecommerce.util.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * E-Commerce Application Simulation
 * 
 * This is a realistic e-commerce application simulation that processes orders,
 * manages inventory, handles user sessions, and performs analytics.
 * 
 * This application simulates a realistic e-commerce system.
 * 
 * The application simulates:
 * - User registration and login
 * - Product catalog management
 * - Order processing
 * - Inventory management
 * - Analytics and reporting
 * - Session management
 * - Notification system
 */
public class ECommerceApplication {
    
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final SessionManager sessionManager;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;
    private final Logger logger;
    
    private final ScheduledExecutorService scheduler;
    private final Random random;
    
    private volatile boolean running = true;
    
    public ECommerceApplication() {
        this.logger = new Logger("ECommerceApp");
        this.userService = new UserService(logger);
        this.productService = new ProductService(logger);
        this.orderService = new OrderService(logger);
        this.inventoryService = new InventoryService(logger);
        this.sessionManager = new SessionManager(logger);
        this.notificationService = new NotificationService(logger);
        this.analyticsService = new AnalyticsService(logger);
        
        this.scheduler = Executors.newScheduledThreadPool(8);
        this.random = new Random();
        
        logger.info("E-Commerce Application initialized");
    }
    
    public static void main(String[] args) {
        System.out.println("=== E-Commerce Application ===");
        System.out.println("This application simulates a realistic e-commerce system.");
        System.out.println();
        System.out.println("Run with: -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heap-dumps/");
        System.out.println();
        
        ECommerceApplication app = new ECommerceApplication();
        
        // Setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(app::shutdown));
        
        try {
            app.start();
        } catch (Exception e) {
            app.logger.error("Application failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void start() {
        logger.info("Starting E-Commerce Application...");
        
        // Initialize sample data
        initializeSampleData();
        
        // Start background services
        startBackgroundServices();
        
        // Start main simulation loop
        runSimulation();
    }
    
    private void initializeSampleData() {
        logger.info("Initializing sample data...");
        
        // Create sample products
        for (int i = 1; i <= 1000; i++) {
            Product product = new Product(
                "PROD-" + i,
                "Product " + i,
                "Description for product " + i,
                19.99 + (i % 100),
                "Category " + (i % 10)
            );
            productService.addProduct(product);
            inventoryService.addStock(product.getId(), 100 + random.nextInt(500));
        }
        
        // Create sample users
        for (int i = 1; i <= 500; i++) {
            User user = new User(
                "user" + i + "@example.com",
                "User " + i,
                "password123"
            );
            userService.registerUser(user);
        }
        
        logger.info("Sample data initialized: 1000 products, 500 users");
    }
    
    private void startBackgroundServices() {
        logger.info("Starting background services...");
        
        // User activity simulation
        scheduler.scheduleAtFixedRate(this::simulateUserActivity, 0, 50, TimeUnit.MILLISECONDS);
        
        // Order processing
        scheduler.scheduleAtFixedRate(this::processOrders, 0, 200, TimeUnit.MILLISECONDS);
        
        // Inventory management
        scheduler.scheduleAtFixedRate(this::manageInventory, 0, 500, TimeUnit.MILLISECONDS);
        
        // Analytics processing
        scheduler.scheduleAtFixedRate(this::processAnalytics, 0, 1000, TimeUnit.MILLISECONDS);
        
        // Session cleanup
        scheduler.scheduleAtFixedRate(this::cleanupSessions, 0, 500, TimeUnit.MILLISECONDS);
        
        // Notification processing
        scheduler.scheduleAtFixedRate(this::processNotifications, 0, 300, TimeUnit.MILLISECONDS);
        
        // Memory monitoring
        scheduler.scheduleAtFixedRate(this::printMemoryStats, 5, 5, TimeUnit.SECONDS);
        
        logger.info("Background services started");
    }
    
    private void runSimulation() {
        logger.info("Starting main simulation...");
        
        while (running) {
            try {
                // Simulate various application activities
                simulateUserRegistration();
                simulateProductBrowsing();
                simulateOrderPlacement();
                simulateCustomerSupport();
                
                Thread.sleep(50); // Small delay to prevent excessive CPU usage
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in simulation: " + e.getMessage());
            }
        }
    }
    
    private void simulateUserActivity() {
        try {
            // Simulate users logging in and out
            if (random.nextDouble() < 0.8) {
                String email = "user" + (1 + random.nextInt(500)) + "@example.com";
                User user = userService.getUserByEmail(email);
                if (user != null) {
                    String sessionId = sessionManager.createSession(user);
                    
                    // Simulate some activity during the session
                    simulateSessionActivity(sessionId, user);
                }
            }
        } catch (Exception e) {
            logger.error("Error in user activity simulation: " + e.getMessage());
        }
    }
    
    private void simulateSessionActivity(String sessionId, User user) {
        // Simulate user browsing products
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            String productId = "PROD-" + (1 + random.nextInt(1000));
            Product product = productService.getProduct(productId);
            if (product != null) {
                analyticsService.trackProductView(user.getId(), product.getId());
            }
        }
        
        // Sometimes add items to cart
        if (random.nextDouble() < 0.4) {
            String productId = "PROD-" + (1 + random.nextInt(1000));
            sessionManager.addToCart(sessionId, productId, 1 + random.nextInt(3));
        }
        
        // Sometimes place an order
        if (random.nextDouble() < 0.2) {
            orderService.createOrderFromCart(user, sessionManager.getCart(sessionId));
        }
        
        // Session might expire naturally or user might logout
        if (random.nextDouble() < 0.1) {
            sessionManager.invalidateSession(sessionId);
        }
    }
    
    private void simulateUserRegistration() {
        if (random.nextDouble() < 0.05) { // 5% chance
            int userId = 1000 + random.nextInt(10000);
            User newUser = new User(
                "newuser" + userId + "@example.com",
                "New User " + userId,
                "password123"
            );
            userService.registerUser(newUser);
            
            // Send welcome notification
            notificationService.sendWelcomeNotification(newUser);
        }
    }
    
    private void simulateProductBrowsing() {
        // Simulate product searches and views
        if (random.nextDouble() < 0.8) { // 80% chance
            String category = "Category " + random.nextInt(10);
            var products = productService.getProductsByCategory(category);
            
            // Track analytics for product views
            if (!products.isEmpty() && random.nextDouble() < 0.6) {
                Product product = products.get(random.nextInt(products.size()));
                String userId = "user" + (1 + random.nextInt(500)) + "@example.com";
                analyticsService.trackProductView(userId, product.getId());
            }
        }
    }
    
    private void simulateOrderPlacement() {
        if (random.nextDouble() < 0.1) { // 10% chance
            String email = "user" + (1 + random.nextInt(500)) + "@example.com";
            User user = userService.getUserByEmail(email);
            if (user != null) {
                // Create a random order
                Order order = new Order(user.getId());
                
                // Add random products to order
                int itemCount = 1 + random.nextInt(5);
                for (int i = 0; i < itemCount; i++) {
                    String productId = "PROD-" + (1 + random.nextInt(1000));
                    Product product = productService.getProduct(productId);
                    if (product != null && inventoryService.isInStock(productId)) {
                        int quantity = 1 + random.nextInt(3);
                        order.addItem(new OrderItem(productId, quantity, product.getPrice()));
                        inventoryService.reserveStock(productId, quantity);
                    }
                }
                
                if (!order.getItems().isEmpty()) {
                    orderService.processOrder(order);
                    notificationService.sendOrderConfirmation(user, order);
                    analyticsService.trackPurchase(user.getId(), order.getTotalAmount());
                }
            }
        }
    }
    
    private void simulateCustomerSupport() {
        if (random.nextDouble() < 0.02) { // 2% chance
            String email = "user" + (1 + random.nextInt(500)) + "@example.com";
            User user = userService.getUserByEmail(email);
            if (user != null) {
                String issue = "Support issue from " + user.getName();
                notificationService.sendSupportTicket(user, issue);
            }
        }
    }
    
    private void processOrders() {
        try {
            // Process pending orders
            var pendingOrders = orderService.getPendingOrders();
            for (Order order : pendingOrders) {
                if (random.nextDouble() < 0.3) { // 30% chance to process
                    orderService.fulfillOrder(order.getId());
                    
                    // Send notification
                    User user = userService.getUserById(order.getUserId());
                    if (user != null) {
                        notificationService.sendShippingNotification(user, order);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error processing orders: " + e.getMessage());
        }
    }
    
    private void manageInventory() {
        try {
            // Restock low inventory items
            var lowStockProducts = inventoryService.getLowStockProducts();
            for (String productId : lowStockProducts) {
                int restockAmount = 50 + random.nextInt(200);
                inventoryService.addStock(productId, restockAmount);
                logger.debug("Restocked product " + productId + " with " + restockAmount + " units");
            }
        } catch (Exception e) {
            logger.error("Error managing inventory: " + e.getMessage());
        }
    }
    
    private void processAnalytics() {
        try {
            // Generate analytics reports
            analyticsService.generateHourlyReport();
            analyticsService.updatePopularProducts();
            analyticsService.calculateUserEngagement();
        } catch (Exception e) {
            logger.error("Error processing analytics: " + e.getMessage());
        }
    }
    
    private void cleanupSessions() {
        try {
            int cleanedUp = sessionManager.cleanupExpiredSessions();
            if (cleanedUp > 0) {
                logger.debug("Cleaned up " + cleanedUp + " expired sessions");
            }
        } catch (Exception e) {
            logger.error("Error cleaning up sessions: " + e.getMessage());
        }
    }
    
    private void processNotifications() {
        try {
            notificationService.processNotificationQueue();
        } catch (Exception e) {
            logger.error("Error processing notifications: " + e.getMessage());
        }
    }
    
    private void printMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        logger.info(String.format(
            "Memory: Used=%dMB, Free=%dMB, Total=%dMB, Max=%dMB | " +
            "Users=%d, Products=%d, Orders=%d, Sessions=%d, Notifications=%d",
            usedMemory / (1024 * 1024),
            freeMemory / (1024 * 1024),
            totalMemory / (1024 * 1024),
            maxMemory / (1024 * 1024),
            userService.getUserCount(),
            productService.getProductCount(),
            orderService.getOrderCount(),
            sessionManager.getActiveSessionCount(),
            notificationService.getPendingNotificationCount()
        ));
    }
    
    public void shutdown() {
        logger.info("Shutting down E-Commerce Application...");
        running = false;
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("Application shutdown complete");
    }
}
