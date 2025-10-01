package com.example.ecommerce.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String component;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public Logger(String component) {
        this.component = component;
    }
    
    public void debug(String message) {
        log("DEBUG", message);
    }
    
    public void info(String message) {
        log("INFO", message);
    }
    
    public void warn(String message) {
        log("WARN", message);
    }
    
    public void error(String message) {
        log("ERROR", message);
    }
    
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String threadName = Thread.currentThread().getName();
        
        System.out.printf("[%s] [%s] [%s] [%s] %s%n", 
                         timestamp, level, threadName, component, message);
    }
}
