#!/bin/bash
# Run script for Game Client CLI (Console version)

echo "========================================="
echo "  Starting Console Client"
echo "========================================="
echo ""

# Check if compiled classes exist
if [ ! -d "out" ]; then
    echo "❌ Error: 'out' directory not found!"
    echo "Please compile the project first: ./compile.sh"
    exit 1
fi

# Run the CLI client
echo "Launching console client..."
echo "Connecting to server at localhost:12345"
echo ""
java -cp out client.GameClientCLI
