#!/bin/bash
# Run script for Game Server

echo "========================================="
echo "  Starting Rock-Paper-Scissors Server"
echo "========================================="
echo ""

# Check if compiled classes exist
if [ ! -d "out" ]; then
    echo "❌ Error: 'out' directory not found!"
    echo "Please compile the project first: ./compile.sh"
    exit 1
fi

# Run the server
echo "Starting server on port 12345..."
echo "Press Ctrl+C to stop the server"
echo ""
java -cp out server.GameServer
