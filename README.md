# OOM Memory Leak Demo

A comprehensive Java application designed to demonstrate various types of memory leaks that cause OutOfMemoryError, perfect for learning heap dump analysis and memory leak detection techniques.

## 🎯 Purpose

This project is designed for educational sessions on:
- Understanding different types of memory leaks in Java applications
- Generating and analyzing heap dumps
- Using memory profiling tools
- Identifying memory leak patterns in real applications

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- At least 1GB of available RAM

### Running the Demo

1. **Clone and navigate to the project:**
   ```bash
   git clone <repository-url>
   cd oom-object-demo
   ```

2. **Run all memory leak scenarios:**
   ```bash
   ./run-demo.sh
   ```

3. **Run specific scenarios:**
   ```bash
   ./run-demo.sh collection    # Collection memory leak
   ./run-demo.sh static        # Static collection leak
   ./run-demo.sh listener      # Event listener leak
   ./run-demo.sh thread        # Thread memory leak
   ./run-demo.sh cache         # Cache memory leak
   ```

4. **Customize heap size:**
   ```bash
   ./run-demo.sh all 256m      # Run with 256MB heap
   ./run-demo.sh static 1g     # Run static leak with 1GB heap
   ```

## 🔍 Memory Leak Scenarios

### 1. Collection Memory Leak
**Pattern:** Objects continuously added to collections but never removed
- **Location:** `instanceList` in `MemoryLeakDemo`
- **Cause:** Unbounded growth of ArrayList
- **Real-world example:** Caching without eviction policy

### 2. Static Collection Leak
**Pattern:** Static collections that accumulate objects
- **Location:** `STATIC_LEAK_LIST` and `STATIC_CACHE`
- **Cause:** Static references prevent garbage collection
- **Real-world example:** Global registries, static caches

### 3. Event Listener Leak
**Pattern:** Event listeners registered but never unregistered
- **Location:** `listeners` Set in `MemoryLeakDemo`
- **Cause:** Strong references from event sources to listeners
- **Real-world example:** UI components, observer patterns

### 4. Thread Memory Leak
**Pattern:** Threads holding references to large objects
- **Location:** `LeakyRunnable` class
- **Cause:** Long-running threads with object references
- **Real-world example:** Background tasks, thread pools

### 5. Cache Memory Leak
**Pattern:** Unbounded cache growth
- **Location:** `STATIC_CACHE` ConcurrentHashMap
- **Cause:** No cache eviction or size limits
- **Real-world example:** Application-level caching

## 📊 Heap Dump Analysis

### Generating Heap Dumps

The application automatically generates heap dumps when OOM occurs:
- **Location:** `./heap-dumps/` directory
- **Format:** `.hprof` files
- **Trigger:** OutOfMemoryError

### Manual Heap Dump Generation

```bash
# Find Java process ID
jps

# Generate heap dump manually
jmap -dump:format=b,file=manual-heap.hprof <pid>
```

### Analysis Tools

#### 1. Eclipse MAT (Memory Analyzer Tool)
**Best for:** Comprehensive analysis and automatic leak detection

```bash
# Download from: https://eclipse.dev/mat/
# Open MAT -> File -> Open Heap Dump -> Select .hprof file
```

**Key Features:**
- Leak Suspects Report (automatic analysis)
- Dominator Tree view
- Histogram view
- Path to GC Roots
- OQL (Object Query Language)

#### 2. jhat (Built-in with JDK)
**Best for:** Quick web-based analysis

```bash
./analyze-heap.sh jhat
# Or manually:
jhat -port 7000 heap-dumps/java_pid12345.hprof
# Open: http://localhost:7000
```

#### 3. VisualVM
**Best for:** Real-time monitoring and heap analysis

```bash
# Start VisualVM
visualvm
# File -> Load -> Select .hprof file
```

#### 4. Command Line Analysis

```bash
# Heap histogram
jmap -histo <pid>

# GC analysis
jstat -gc <pid> 1s

# Thread analysis
jstack <pid>
```

## 🔧 Analysis Helper Script

Use the provided analysis helper:

```bash
./analyze-heap.sh guide    # Show analysis guide
./analyze-heap.sh list     # List available heap dumps
./analyze-heap.sh jhat     # Start jhat analysis
./analyze-heap.sh report   # Generate report template
```

## 🕵️ What to Look For

### In Eclipse MAT

1. **Run Leak Suspects Report**
   - Automatic identification of potential leaks
   - Shows suspicious objects and their retained heap

2. **Examine Histogram**
   - Look for `LargeObject` instances
   - Check `ArrayList`, `HashMap` sizes
   - Find `EventListener` accumulation

3. **Dominator Tree Analysis**
   - Identify objects preventing garbage collection
   - Find memory-consuming object hierarchies

4. **Path to GC Roots**
   - Trace why objects aren't being collected
   - Identify static references and thread locals

### Key Patterns in This Demo

```sql
-- OQL Queries for Eclipse MAT

-- Find all LargeObject instances
SELECT * FROM com.example.oom.MemoryLeakDemo$LargeObject

-- Find large collections
SELECT * FROM java.util.ArrayList WHERE this.@retainedHeapSize > 1000000

-- Find EventListener instances
SELECT * FROM com.example.oom.MemoryLeakDemo$EventListener

-- Find objects in static fields
SELECT * FROM OBJECTS 0 WHERE @GCRootInfo.@type = "static field"
```

### Memory Metrics to Monitor

- **Retained Heap:** Memory freed if object is garbage collected
- **Shallow Heap:** Memory consumed by object itself
- **Dominator Tree:** Objects preventing others from being GC'd
- **GC Roots:** Objects that anchor others in memory

## 📈 Expected Behavior

### Memory Growth Patterns

1. **Rapid Growth:** Collection and cache leaks
2. **Steady Growth:** Static collection accumulation
3. **Stepped Growth:** Listener and thread leaks
4. **Mixed Pattern:** All scenarios combined

### OOM Timeline

- **256MB heap:** ~2-5 minutes
- **512MB heap:** ~5-10 minutes
- **1GB heap:** ~10-20 minutes

*Times vary based on system performance and scenario*

## 🛠️ Troubleshooting

### Common Issues

1. **Maven not found:**
   ```bash
   # Install Maven
   brew install maven  # macOS
   apt-get install maven  # Ubuntu
   ```

2. **Java version issues:**
   ```bash
   # Check Java version
   java -version
   # Should be Java 11 or higher
   ```

3. **Permission denied:**
   ```bash
   chmod +x run-demo.sh
   chmod +x analyze-heap.sh
   ```

4. **Heap dump not generated:**
   - Check `heap-dumps/` directory
   - Verify JVM options in `run-demo.sh`
   - Ensure sufficient disk space

### Performance Tuning

```bash
# Faster OOM for demos
./run-demo.sh all 128m

# Slower progression for detailed analysis
./run-demo.sh static 2g

# Enable additional GC logging
export MAVEN_OPTS="-XX:+PrintGCApplicationStoppedTime -XX:+PrintStringDeduplicationStatistics"
```

## 📚 Learning Objectives

After completing this demo, you should understand:

1. **Memory Leak Types:**
   - Collection leaks
   - Static reference leaks
   - Listener leaks
   - Thread-related leaks
   - Cache leaks

2. **Heap Dump Analysis:**
   - Generating heap dumps
   - Using analysis tools
   - Reading memory metrics
   - Identifying leak patterns

3. **Prevention Strategies:**
   - Proper resource cleanup
   - Weak references usage
   - Cache size limits
   - Listener management
   - Thread lifecycle management

## 🎓 Session Guide

### For Instructors

1. **Setup (5 minutes):**
   - Verify prerequisites
   - Run quick demo to ensure setup

2. **Theory (10 minutes):**
   - Explain memory leak types
   - Discuss heap structure
   - Overview of analysis tools

3. **Hands-on Demo (30 minutes):**
   - Run different scenarios
   - Generate heap dumps
   - Analyze with MAT/jhat

4. **Analysis Practice (15 minutes):**
   - Students analyze provided heap dumps
   - Identify leak patterns
   - Discuss findings

### For Students

1. **Pre-session:**
   - Install Java 11+, Maven, Eclipse MAT
   - Clone and test the demo

2. **During session:**
   - Follow along with scenarios
   - Take notes on analysis techniques
   - Ask questions about patterns

3. **Post-session:**
   - Practice with different heap sizes
   - Try various analysis tools
   - Create analysis reports

## 📝 Sample Analysis Report

```
HEAP DUMP ANALYSIS REPORT
=========================
Date: 2024-01-15
Application: OOM Memory Leak Demo
Heap Dump File: java_pid12345.hprof
Total Heap Size: 512 MB

TOP MEMORY CONSUMERS:
1. LargeObject[] - Instances: 45,231 - Total Size: 387 MB
2. ArrayList - Instances: 3 - Total Size: 89 MB
3. ConcurrentHashMap - Instances: 1 - Total Size: 23 MB

IDENTIFIED MEMORY LEAKS:
1. Leak Type: Static Collection Leak
   - Root Cause: STATIC_LEAK_LIST never cleared
   - Memory Impact: 387 MB
   - Fix: Clear static collections or use weak references

RECOMMENDATIONS:
- Implement cache eviction policies
- Use WeakReference for event listeners
- Add collection size monitoring
- Implement proper cleanup in shutdown hooks
```

## 🤝 Contributing

Feel free to contribute additional memory leak scenarios or analysis techniques:

1. Fork the repository
2. Create a feature branch
3. Add new leak scenarios
4. Update documentation
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎯 Advanced Challenge: E-Commerce Memory Leak

After mastering the basic memory leak scenarios, try the **E-Commerce Memory Leak Challenge** - a realistic application with a subtle, hidden memory leak that you need to discover through investigation.

### **The Challenge**

```bash
# Run the e-commerce application
./run-ecommerce.sh

# Or with custom heap size for faster/slower OOM
./run-ecommerce.sh 512m    # Faster OOM (~10 minutes)
./run-ecommerce.sh 2g      # Slower OOM (~30 minutes)
```

### **What You'll Find**

- **Realistic E-Commerce Simulation**: User registration, product browsing, order processing, inventory management, session handling, notifications, and analytics
- **Professional Code Structure**: Multiple services, proper separation of concerns, realistic business logic
- **Subtle Memory Leak**: ONE hidden leak that represents a common real-world mistake
- **Investigation Challenge**: No obvious leaks - you must use heap dump analysis to find it

### **The Application Simulates**

1. **User Management**: Registration, login, session management
2. **Product Catalog**: 1000+ products across categories
3. **Order Processing**: Shopping carts, order placement, fulfillment
4. **Inventory Management**: Stock tracking, reservations, restocking
5. **Session Management**: User sessions with shopping carts ⚠️ **(leak is here)**
6. **Notifications**: Welcome emails, order confirmations, shipping updates
7. **Analytics**: Product views, user engagement, sales tracking

### **Your Mission**

1. **Run the application** and observe memory growth patterns
2. **Wait for OOM** (10-20 minutes depending on heap size)
3. **Analyze the heap dump** using Eclipse MAT or other tools
4. **Find the memory leak** - it's subtle and realistic
5. **Identify the root cause** and propose a fix

### **Analysis Strategy for the Challenge**

#### **Step 1: Initial Investigation**
```bash
# Start with Eclipse MAT Leak Suspects Report
# Look for:
# - Objects that should be temporary but aren't being GC'd
# - Collections that keep growing
# - Session-related objects (hint!)
```

#### **Step 2: Heap Dump Analysis**
```sql
-- Useful OQL queries for Eclipse MAT:

-- Find all UserSession objects
SELECT * FROM com.example.ecommerce.model.UserSession

-- Look for large collections
SELECT * FROM java.util.concurrent.ConcurrentHashMap WHERE this.@retainedHeapSize > 1000000

-- Find SessionManager instances
SELECT * FROM com.example.ecommerce.service.SessionManager

-- Check for expired sessions
SELECT s FROM com.example.ecommerce.model.UserSession s WHERE s.valid = false
```

#### **Step 3: Root Cause Analysis**
- Use **"Path to GC Roots"** to see what's preventing garbage collection
- Look at the **Dominator Tree** to find memory-consuming objects
- Check **Reference chains** to understand object relationships
- Examine **Collection contents** for unexpected accumulations

### **Expected Findings**

🔍 **What you should discover:**
- A specific service class accumulating objects
- Objects that should be temporary but have strong references
- A collection that grows indefinitely
- Session-related objects that aren't being cleaned up properly

🎯 **The actual leak location:**
- **Class**: `SessionManager`
- **Issue**: Expired sessions moved to `expiredSessions` map but never removed
- **Root cause**: Cleanup method has a bug - stores expired sessions instead of discarding them
- **Fix**: Remove the line that stores expired sessions, just let them be GC'd

### **Learning Objectives**

This challenge teaches:
- **Realistic leak patterns** found in production applications
- **Investigation techniques** using professional tools
- **Code review skills** to spot subtle bugs
- **Memory management** best practices
- **Session management** pitfalls

### **Difficulty Levels**

**Beginner**: Use Eclipse MAT's automatic Leak Suspects Report
**Intermediate**: Manually explore heap dump using Dominator Tree
**Advanced**: Use OQL queries and trace reference chains
**Expert**: Find the exact line of code causing the leak

### **Success Criteria**

You've successfully completed the challenge when you can:
1. ✅ Identify that `SessionManager` is causing the leak
2. ✅ Explain that expired sessions accumulate in `expiredSessions` map
3. ✅ Point to the specific line in `cleanupExpiredSessions()` method
4. ✅ Propose the correct fix (don't store expired sessions)
5. ✅ Understand why this pattern is dangerous in production

---

**Happy Memory Leak Hunting! 🐛🔍**
