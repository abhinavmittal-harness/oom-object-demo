#!/bin/bash

# E-Commerce Memory Leak Challenge Runner
# This script runs the complex e-commerce application with a hidden memory leak

echo "=== E-Commerce Memory Leak Challenge ==="
echo

# Default JVM settings optimized for memory leak detection
HEAP_SIZE="1g"
HEAP_DUMP_PATH="./heap-dumps"
JVM_OPTS="-Xmx${HEAP_SIZE} -Xms64m"
JVM_OPTS="${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="${JVM_OPTS} -XX:HeapDumpPath=${HEAP_DUMP_PATH}/"
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCDetails"
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCTimeStamps"
JVM_OPTS="${JVM_OPTS} -Xloggc:ecommerce-gc.log"
JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"

# Additional options for better heap dump analysis
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCApplicationStoppedTime"
JVM_OPTS="${JVM_OPTS} -XX:+PrintStringDeduplicationStatistics"

# Create heap dump directory if it doesn't exist
mkdir -p "${HEAP_DUMP_PATH}"

# Function to display usage
show_usage() {
    echo "Usage: $0 [heap_size]"
    echo
    echo "This runs the E-Commerce application with a HIDDEN memory leak."
    echo "Your challenge: Find the memory leak using heap dump analysis!"
    echo
    echo "Heap Size Examples:"
    echo "  512m, 1g, 2g (default: 1g)"
    echo
    echo "Examples:"
    echo "  $0           # Run with 1GB heap"
    echo "  $0 512m      # Run with 512MB heap (faster OOM)"
    echo "  $0 2g        # Run with 2GB heap (slower OOM)"
    echo
    echo "Expected behavior:"
    echo "  - Application will simulate realistic e-commerce operations"
    echo "  - Memory usage will gradually increase due to a hidden leak"
    echo "  - OOM will occur in 10-20 minutes (depending on heap size)"
    echo "  - Heap dump will be generated automatically"
    echo
    echo "Analysis hints:"
    echo "  - Look for objects that should be garbage collected but aren't"
    echo "  - Pay attention to collections that keep growing"
    echo "  - Check for objects with strong references preventing GC"
    echo "  - Use Eclipse MAT's 'Leak Suspects Report' for guidance"
    echo
}

# Parse command line arguments
if [ $# -ge 1 ]; then
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        show_usage
        exit 0
    fi
    HEAP_SIZE="$1"
    JVM_OPTS=$(echo "$JVM_OPTS" | sed "s/-Xmx[^ ]*/-Xmx${HEAP_SIZE}/")
fi

echo "🎯 MEMORY LEAK CHALLENGE"
echo "========================"
echo "There is ONE subtle memory leak hidden in this e-commerce application."
echo "The leak is realistic and represents a common mistake in production code."
echo
echo "Your mission:"
echo "  1. Run this application and let it generate a heap dump"
echo "  2. Analyze the heap dump to find the memory leak"
echo "  3. Identify the root cause and propose a fix"
echo
echo "Configuration:"
echo "  Heap Size: $HEAP_SIZE"
echo "  Heap Dump Path: $HEAP_DUMP_PATH"
echo "  Expected OOM Time: 10-20 minutes"
echo "  GC Log: ecommerce-gc.log"
echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed or not in PATH"
    echo "Please install Maven to run this challenge"
    exit 1
fi

# Compile the project if needed
echo "🔨 Compiling the project..."
mvn compile -q

if [ $? -ne 0 ]; then
    echo "❌ Error: Failed to compile the project"
    exit 1
fi

echo "🚀 Starting E-Commerce Application..."
echo "📊 Watch the memory statistics in the output"
echo "⏰ The application will run until OOM occurs"
echo "🛑 Press Ctrl+C to stop manually"
echo
echo "💡 ANALYSIS TIPS:"
echo "   - The leak is NOT in the obvious places"
echo "   - Look for objects that accumulate over time"
echo "   - Check session-related objects carefully"
echo "   - Use 'Path to GC Roots' in Eclipse MAT"
echo

# Run the application
mvn exec:java \
    -Dexec.mainClass="com.example.ecommerce.ECommerceApplication" \
    -Dexec.jvmArgs="$JVM_OPTS" \
    -q

echo
echo "🏁 Application finished!"
echo
echo "📁 Generated files:"
echo "   - Heap dump: $HEAP_DUMP_PATH/*.hprof"
echo "   - GC log: ecommerce-gc.log"
echo
echo "🔍 Next steps for analysis:"
echo "   1. Use Eclipse MAT to open the heap dump"
echo "   2. Run the 'Leak Suspects Report'"
echo "   3. Examine the Dominator Tree"
echo "   4. Look for suspicious object accumulations"
echo "   5. Use 'Path to GC Roots' to find leak sources"
echo
echo "🎯 Challenge Questions:"
echo "   - Which class is causing the memory leak?"
echo "   - What type of objects are accumulating?"
echo "   - Why aren't these objects being garbage collected?"
echo "   - How would you fix this leak?"
echo
echo "💡 Hint: The leak is in session management..."
echo
echo "Good luck with your analysis! 🕵️‍♂️"
