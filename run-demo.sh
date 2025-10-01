#!/bin/bash

# Memory Leak Demo Runner Script
# This script runs the Java application with appropriate JVM settings for heap dump generation

echo "=== OOM Memory Leak Demo Runner ==="
echo

# Default JVM settings for heap dump generation
HEAP_SIZE="512m"
HEAP_DUMP_PATH="./heap-dumps"
JVM_OPTS="-Xmx${HEAP_SIZE} -Xms256m"
JVM_OPTS="${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="${JVM_OPTS} -XX:HeapDumpPath=${HEAP_DUMP_PATH}/"
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCDetails"
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCTimeStamps"
JVM_OPTS="${JVM_OPTS} -Xloggc:gc.log"
JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"

# Create heap dump directory if it doesn't exist
mkdir -p "${HEAP_DUMP_PATH}"

# Function to display usage
show_usage() {
    echo "Usage: $0 [scenario] [heap_size]"
    echo
    echo "Scenarios:"
    echo "  all        - Run all memory leak scenarios (default)"
    echo "  collection - Collection memory leak"
    echo "  static     - Static collection memory leak"
    echo "  listener   - Event listener memory leak"
    echo "  thread     - Thread memory leak"
    echo "  cache      - Cache memory leak"
    echo
    echo "Heap Size Examples:"
    echo "  256m, 512m, 1g, 2g (default: 512m)"
    echo
    echo "Examples:"
    echo "  $0                    # Run all scenarios with 512m heap"
    echo "  $0 collection         # Run collection leak with 512m heap"
    echo "  $0 static 256m        # Run static leak with 256m heap"
    echo "  $0 all 1g             # Run all scenarios with 1g heap"
    echo
}

# Parse command line arguments
SCENARIO="all"
if [ $# -ge 1 ]; then
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        show_usage
        exit 0
    fi
    SCENARIO="$1"
fi

if [ $# -ge 2 ]; then
    HEAP_SIZE="$2"
    JVM_OPTS=$(echo "$JVM_OPTS" | sed "s/-Xmx[^ ]*/-Xmx${HEAP_SIZE}/")
fi

echo "Configuration:"
echo "  Scenario: $SCENARIO"
echo "  Heap Size: $HEAP_SIZE"
echo "  Heap Dump Path: $HEAP_DUMP_PATH"
echo "  JVM Options: $JVM_OPTS"
echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven to run this demo"
    exit 1
fi

# Compile the project if needed
echo "Compiling the project..."
mvn compile -q

if [ $? -ne 0 ]; then
    echo "Error: Failed to compile the project"
    exit 1
fi

echo "Starting memory leak demo..."
echo "Press Ctrl+C to stop the application"
echo "When OOM occurs, heap dump will be saved to: $HEAP_DUMP_PATH"
echo

# Run the application
mvn exec:java \
    -Dexec.mainClass="com.example.oom.MemoryLeakDemo" \
    -Dexec.args="$SCENARIO" \
    -Dexec.jvmArgs="$JVM_OPTS"

echo
echo "Demo finished. Check the following files:"
echo "  - Heap dump: $HEAP_DUMP_PATH/*.hprof"
echo "  - GC log: gc.log"
echo
echo "To analyze the heap dump, use tools like:"
echo "  - Eclipse MAT (Memory Analyzer Tool)"
echo "  - VisualVM"
echo "  - JProfiler"
echo "  - jhat (built-in with JDK)"
