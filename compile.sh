#!/bin/bash
# Compile script for Rock-Paper-Scissors Game

echo "========================================="
echo "  Compiling Rock-Paper-Scissors Game"
echo "========================================="
echo ""

# Create out directory if it doesn't exist
if [ ! -d "out" ]; then
    echo "Creating out directory..."
    mkdir -p out
fi

# Compile all Java files
echo "Compiling Java source files..."
javac -d out -sourcepath src \
    src/game/*.java \
    src/server/*.java \
    src/client/*.java \
    src/utils/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilation successful!"
    echo "Compiled classes are in the 'out' directory"
    echo ""
    echo "Next steps:"
    echo "  - Run server: ./run-server.sh"
    echo "  - Run client: ./run-client-gui.sh"
else
    echo ""
    echo "❌ Compilation failed!"
    echo "Please check the error messages above"
    exit 1
fi
