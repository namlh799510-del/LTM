#!/bin/bash
# Run script for Game Client GUI

echo "========================================="
echo "  Starting Rock-Paper-Scissors Client"
echo "========================================="
echo ""

# Check if compiled classes exist
if [ ! -d "out" ]; then
    echo "❌ Error: 'out' directory not found!"
    echo "Please compile the project first: ./compile.sh"
    exit 1
fi

# Run the GUI client
echo "Launching GUI client..."
echo "Connecting to server at localhost:12345"
echo ""
java -cp out client.GameClientUI
