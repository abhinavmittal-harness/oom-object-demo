package com.example.oom;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Memory Leak Demo Application
 * 
 * This application demonstrates various types of memory leaks that can cause OutOfMemoryError:
 * 1. Collection Memory Leak - Objects accumulating in collections
 * 2. Static Collection Leak - Static collections that never get cleared
 * 3. Listener Leak - Event listeners not being removed
 * 4. Thread Leak - Threads holding references to objects
 * 5. Cache Leak - Unbounded cache growth
 */
public class MemoryLeakDemo {
    
    // Static collections that will cause memory leaks
    private static final List<LargeObject> STATIC_LEAK_LIST = new ArrayList<>();
    private static final Map<String, LargeObject> STATIC_CACHE = new ConcurrentHashMap<>();
    
    // Instance collections for demonstration
    private final List<LargeObject> instanceList = new ArrayList<>();
    private final Set<EventListener> listeners = new HashSet<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    
    public static void main(String[] args) {
        System.out.println("=== Memory Leak Demo Application ===");
        System.out.println("This application will demonstrate various memory leak scenarios.");
        System.out.println("Run with JVM options: -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./");
        System.out.println();
        
        MemoryLeakDemo demo = new MemoryLeakDemo();
        
        if (args.length > 0) {
            String scenario = args[0].toLowerCase();
            switch (scenario) {
                case "collection":
                    demo.demonstrateCollectionLeak();
                    break;
                case "static":
                    demo.demonstrateStaticLeak();
                    break;
                case "listener":
                    demo.demonstrateListenerLeak();
                    break;
                case "thread":
                    demo.demonstrateThreadLeak();
                    break;
                case "cache":
                    demo.demonstrateCacheLeak();
                    break;
                case "all":
                default:
                    demo.demonstrateAllLeaks();
                    break;
            }
        } else {
            demo.demonstrateAllLeaks();
        }
    }
    
    /**
     * Demonstrates all memory leak scenarios simultaneously
     */
    public void demonstrateAllLeaks() {
        System.out.println("Starting all memory leak scenarios...");
        
        // Start background threads for different leak types
        executor.scheduleAtFixedRate(this::addToStaticCollection, 0, 100, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::addToInstanceCollection, 0, 50, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::addToCache, 0, 75, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::createListeners, 0, 200, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::createThreads, 0, 500, TimeUnit.MILLISECONDS);
        
        // Keep the main thread alive and print memory stats
        printMemoryStats();
    }
    
    /**
     * Scenario 1: Collection Memory Leak
     * Objects keep getting added to collections but never removed
     */
    public void demonstrateCollectionLeak() {
        System.out.println("Demonstrating Collection Memory Leak...");
        
        while (true) {
            // Add large objects to instance collection
            for (int i = 0; i < 1000; i++) {
                instanceList.add(new LargeObject("CollectionLeak_" + System.currentTimeMillis() + "_" + i));
            }
            
            System.out.println("Instance list size: " + instanceList.size() + 
                             ", Memory used: " + getUsedMemoryMB() + " MB");
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Scenario 2: Static Collection Leak
     * Static collections accumulate objects and are never cleared
     */
    public void demonstrateStaticLeak() {
        System.out.println("Demonstrating Static Collection Memory Leak...");
        
        while (true) {
            addToStaticCollection();
            
            if (STATIC_LEAK_LIST.size() % 1000 == 0) {
                System.out.println("Static list size: " + STATIC_LEAK_LIST.size() + 
                                 ", Memory used: " + getUsedMemoryMB() + " MB");
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Scenario 3: Event Listener Leak
     * Event listeners are added but never removed
     */
    public void demonstrateListenerLeak() {
        System.out.println("Demonstrating Event Listener Memory Leak...");
        
        while (true) {
            createListeners();
            
            if (listeners.size() % 100 == 0) {
                System.out.println("Listeners count: " + listeners.size() + 
                                 ", Memory used: " + getUsedMemoryMB() + " MB");
            }
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Scenario 4: Thread Leak
     * Threads are created and hold references to large objects
     */
    public void demonstrateThreadLeak() {
        System.out.println("Demonstrating Thread Memory Leak...");
        
        while (true) {
            createThreads();
            
            System.out.println("Active threads: " + Thread.activeCount() + 
                             ", Memory used: " + getUsedMemoryMB() + " MB");
            
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Scenario 5: Cache Leak
     * Unbounded cache that grows indefinitely
     */
    public void demonstrateCacheLeak() {
        System.out.println("Demonstrating Cache Memory Leak...");
        
        while (true) {
            addToCache();
            
            if (STATIC_CACHE.size() % 1000 == 0) {
                System.out.println("Cache size: " + STATIC_CACHE.size() + 
                                 ", Memory used: " + getUsedMemoryMB() + " MB");
            }
            
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    // Helper methods for creating memory leaks
    
    private void addToStaticCollection() {
        STATIC_LEAK_LIST.add(new LargeObject("StaticLeak_" + System.currentTimeMillis()));
    }
    
    private void addToInstanceCollection() {
        instanceList.add(new LargeObject("InstanceLeak_" + System.currentTimeMillis()));
    }
    
    private void addToCache() {
        String key = "cache_key_" + System.currentTimeMillis() + "_" + Math.random();
        STATIC_CACHE.put(key, new LargeObject("CacheLeak_" + key));
    }
    
    private void createListeners() {
        // Create listeners that hold references to large objects
        EventListener listener = new EventListener(new LargeObject("ListenerLeak_" + System.currentTimeMillis()));
        listeners.add(listener);
        
        // Simulate registering listener with some event source
        // In real scenarios, these listeners would be registered with UI components,
        // event buses, etc., but never unregistered
    }
    
    private void createThreads() {
        // Create threads that hold references to large objects
        Thread thread = new Thread(new LeakyRunnable(new LargeObject("ThreadLeak_" + System.currentTimeMillis())));
        thread.setDaemon(true);
        thread.start();
    }
    
    private void printMemoryStats() {
        executor.scheduleAtFixedRate(() -> {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            System.out.printf("Memory Stats - Used: %d MB, Free: %d MB, Total: %d MB, Max: %d MB%n",
                    usedMemory / (1024 * 1024),
                    freeMemory / (1024 * 1024),
                    totalMemory / (1024 * 1024),
                    maxMemory / (1024 * 1024));
            
            System.out.printf("Collection Sizes - Static List: %d, Instance List: %d, Cache: %d, Listeners: %d%n",
                    STATIC_LEAK_LIST.size(), instanceList.size(), STATIC_CACHE.size(), listeners.size());
            
            System.out.println("Active Threads: " + Thread.activeCount());
            System.out.println("---");
            
        }, 2, 2, TimeUnit.SECONDS);
        
        // Keep main thread alive
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private long getUsedMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }
    
    /**
     * Large object that consumes significant memory
     */
    static class LargeObject {
        private final String id;
        private final byte[] data;
        private final List<String> metadata;
        private final Map<String, Object> properties;
        
        public LargeObject(String id) {
            this.id = id;
            // Allocate ~1KB of data per object
            this.data = new byte[1024];
            Arrays.fill(data, (byte) 1);
            
            // Add some metadata
            this.metadata = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                metadata.add("metadata_" + id + "_" + i + "_" + System.nanoTime());
            }
            
            // Add properties map
            this.properties = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                properties.put("prop_" + i, "value_" + id + "_" + i + "_" + System.currentTimeMillis());
            }
        }
        
        public String getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return "LargeObject{id='" + id + "', dataSize=" + data.length + "}";
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LargeObject that = (LargeObject) o;
            return Objects.equals(id, that.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
    
    /**
     * Event listener that holds reference to large objects
     */
    static class EventListener {
        private final LargeObject data;
        private final String listenerId;
        
        public EventListener(LargeObject data) {
            this.data = data;
            this.listenerId = "listener_" + System.currentTimeMillis();
        }
        
        public void onEvent(String event) {
            // Simulate event handling that uses the large object
            System.out.println("Listener " + listenerId + " handling event: " + event + 
                             " with data: " + data.getId());
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EventListener that = (EventListener) o;
            return Objects.equals(listenerId, that.listenerId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(listenerId);
        }
    }
    
    /**
     * Runnable that holds reference to large objects and runs indefinitely
     */
    static class LeakyRunnable implements Runnable {
        private final LargeObject data;
        private final String threadId;
        
        public LeakyRunnable(LargeObject data) {
            this.data = data;
            this.threadId = "thread_" + System.currentTimeMillis();
        }
        
        @Override
        public void run() {
            try {
                // Keep the thread alive and hold reference to large object
                while (!Thread.currentThread().isInterrupted()) {
                    // Simulate some work with the large object
                    String result = processData();
                    
                    // Sleep to prevent excessive CPU usage
                    Thread.sleep(5000 + (int)(Math.random() * 5000));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private String processData() {
            // Simulate processing that keeps reference to data alive
            return "Processed: " + data.getId() + " by " + threadId;
        }
    }
}
