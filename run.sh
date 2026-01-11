#!/bin/bash

# Blackjack Simulator Launcher Script

cd "$(dirname "$0")"

echo "Starting Blackjack Simulator..."
echo ""

# Check if Java is installed
if ! command -v java &>/dev/null; then
    echo "ERROR: Java is not installed!"
    echo "Please install Java 21 or higher from: https://adoptium.net/"
    echo ""
    read -p "Press Enter to exit..."
    exit 1
fi

# Auto-detect and set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    # Try to find Java 25 first, then Java 21
    if [ -d "/usr/lib/jvm/java-25-openjdk" ]; then
        export JAVA_HOME=/usr/lib/jvm/java-25-openjdk
        echo "Using Java 25 from: $JAVA_HOME"
    elif [ -d "/usr/lib/jvm/java-21-openjdk" ]; then
        export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
        echo "Using Java 21 from: $JAVA_HOME"
    else
        echo "WARNING: Could not auto-detect JAVA_HOME"
        echo "Please set JAVA_HOME environment variable"
    fi
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "ERROR: Java 21 or higher is required!"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    echo "Download from: https://adoptium.net/"
    echo ""
    read -p "Press Enter to exit..."
    exit 1
fi

# Make Maven wrapper executable
chmod +x mvnw

# Run the game
./mvnw clean javafx:run

# Keep terminal open if there's an error
if [ $? -ne 0 ]; then
    echo ""
    echo "Error running the game. See above for details."
    read -p "Press Enter to exit..."
fi
