#!/bin/bash

# Heap Dump Analysis Helper Script
# This script provides commands and guidance for analyzing heap dumps

echo "=== Heap Dump Analysis Helper ==="
echo

HEAP_DUMP_DIR="./heap-dumps"

# Function to find heap dump files
find_heap_dumps() {
    if [ -d "$HEAP_DUMP_DIR" ]; then
        find "$HEAP_DUMP_DIR" -name "*.hprof" -type f 2>/dev/null
    fi
}

# Function to show available heap dumps
show_heap_dumps() {
    echo "Available heap dump files:"
    local dumps=$(find_heap_dumps)
    if [ -z "$dumps" ]; then
        echo "  No heap dump files found in $HEAP_DUMP_DIR"
        echo "  Run the demo first with: ./run-demo.sh"
        return 1
    else
        echo "$dumps" | nl -w2 -s'. '
        return 0
    fi
    echo
}

# Function to show analysis options
show_analysis_options() {
    echo "Heap Dump Analysis Options:"
    echo
    echo "1. Using jhat (built-in with JDK):"
    echo "   jhat -port 7000 <heap-dump-file>"
    echo "   Then open: http://localhost:7000"
    echo
    echo "2. Using Eclipse MAT (Memory Analyzer Tool):"
    echo "   - Download from: https://eclipse.dev/mat/"
    echo "   - Open MAT and load the .hprof file"
    echo "   - Use 'Leak Suspects Report' for automatic analysis"
    echo
    echo "3. Using VisualVM:"
    echo "   - Start VisualVM"
    echo "   - File -> Load -> Select .hprof file"
    echo "   - Navigate to 'Classes' or 'Instances' view"
    echo
    echo "4. Using JProfiler:"
    echo "   - Commercial tool with excellent heap analysis"
    echo "   - File -> Open Snapshot -> Select .hprof file"
    echo
    echo "5. Command line analysis with jmap and jstack:"
    echo "   - Get process ID: jps"
    echo "   - Generate heap dump: jmap -dump:format=b,file=heap.hprof <pid>"
    echo "   - Analyze histogram: jmap -histo <pid>"
    echo
}

# Function to start jhat analysis
start_jhat_analysis() {
    local dumps=$(find_heap_dumps)
    if [ -z "$dumps" ]; then
        echo "No heap dump files found. Run the demo first."
        return 1
    fi
    
    echo "Select a heap dump file to analyze:"
    echo "$dumps" | nl -w2 -s'. '
    echo
    read -p "Enter selection (1-$(echo "$dumps" | wc -l)): " selection
    
    local selected_file=$(echo "$dumps" | sed -n "${selection}p")
    if [ -z "$selected_file" ]; then
        echo "Invalid selection"
        return 1
    fi
    
    echo "Starting jhat analysis on: $selected_file"
    echo "This may take a few minutes for large heap dumps..."
    echo "Once started, open http://localhost:7000 in your browser"
    echo "Press Ctrl+C to stop jhat"
    echo
    
    jhat -port 7000 "$selected_file"
}

# Function to show what to look for in heap dumps
show_analysis_guide() {
    echo "=== What to Look for in Heap Dump Analysis ==="
    echo
    echo "1. MEMORY LEAK INDICATORS:"
    echo "   - Large number of instances of custom classes"
    echo "   - Objects that should have been garbage collected"
    echo "   - Growing collections (ArrayList, HashMap, etc.)"
    echo "   - Retained heap size vs. shallow heap size"
    echo
    echo "2. SPECIFIC PATTERNS IN THIS DEMO:"
    echo "   - Look for 'LargeObject' instances"
    echo "   - Check static collections (STATIC_LEAK_LIST, STATIC_CACHE)"
    echo "   - Find EventListener instances accumulating"
    echo "   - Identify Thread objects holding references"
    echo
    echo "3. KEY METRICS TO EXAMINE:"
    echo "   - Retained Heap: Memory that would be freed if object is GC'd"
    echo "   - Shallow Heap: Memory consumed by the object itself"
    echo "   - Dominator Tree: Objects preventing others from being GC'd"
    echo "   - GC Roots: Objects that prevent garbage collection"
    echo
    echo "4. ECLIPSE MAT SPECIFIC FEATURES:"
    echo "   - Leak Suspects Report (automatic analysis)"
    echo "   - Dominator Tree view"
    echo "   - Histogram view (grouped by class)"
    echo "   - Path to GC Roots"
    echo "   - OQL (Object Query Language) for custom queries"
    echo
    echo "5. SAMPLE OQL QUERIES FOR THIS DEMO:"
    echo "   - Find all LargeObject instances:"
    echo "     SELECT * FROM com.example.oom.MemoryLeakDemo\$LargeObject"
    echo
    echo "   - Find objects in static collections:"
    echo "     SELECT * FROM java.util.ArrayList WHERE this.@retainedHeapSize > 1000000"
    echo
    echo "   - Find EventListener instances:"
    echo "     SELECT * FROM com.example.oom.MemoryLeakDemo\$EventListener"
    echo
    echo "6. COMMON HEAP DUMP ANALYSIS WORKFLOW:"
    echo "   a) Start with Leak Suspects Report (MAT)"
    echo "   b) Examine Histogram for suspicious classes"
    echo "   c) Look at Dominator Tree for memory hogs"
    echo "   d) Use 'Path to GC Roots' to find leak sources"
    echo "   e) Analyze thread dumps if available"
    echo
}

# Function to generate a sample analysis report
generate_sample_report() {
    echo "=== Sample Analysis Report Template ==="
    echo
    echo "HEAP DUMP ANALYSIS REPORT"
    echo "========================="
    echo "Date: $(date)"
    echo "Application: OOM Memory Leak Demo"
    echo "Heap Dump File: [filename]"
    echo "Total Heap Size: [size] MB"
    echo
    echo "TOP MEMORY CONSUMERS:"
    echo "1. Class Name: [class] - Instances: [count] - Total Size: [size] MB"
    echo "2. Class Name: [class] - Instances: [count] - Total Size: [size] MB"
    echo "3. Class Name: [class] - Instances: [count] - Total Size: [size] MB"
    echo
    echo "IDENTIFIED MEMORY LEAKS:"
    echo "1. Leak Type: [Static Collection Leak]"
    echo "   - Root Cause: [STATIC_LEAK_LIST never cleared]"
    echo "   - Memory Impact: [size] MB"
    echo "   - Fix: [Clear static collections or use weak references]"
    echo
    echo "2. Leak Type: [Event Listener Leak]"
    echo "   - Root Cause: [Listeners not unregistered]"
    echo "   - Memory Impact: [size] MB"
    echo "   - Fix: [Implement proper listener cleanup]"
    echo
    echo "RECOMMENDATIONS:"
    echo "- [Specific recommendations based on findings]"
    echo "- [Code changes needed]"
    echo "- [Monitoring suggestions]"
    echo
}

# Main script logic
case "${1:-help}" in
    "list"|"ls")
        show_heap_dumps
        ;;
    "jhat")
        start_jhat_analysis
        ;;
    "guide")
        show_analysis_guide
        ;;
    "report")
        generate_sample_report
        ;;
    "help"|"-h"|"--help"|*)
        echo "Usage: $0 [command]"
        echo
        echo "Commands:"
        echo "  list, ls    - List available heap dump files"
        echo "  jhat        - Start jhat analysis (interactive)"
        echo "  guide       - Show heap dump analysis guide"
        echo "  report      - Generate sample analysis report template"
        echo "  help        - Show this help message"
        echo
        show_analysis_options
        ;;
esac
