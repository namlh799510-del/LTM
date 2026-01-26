@echo off
REM Run script for Game Client GUI (Windows)

echo =========================================
echo   Starting Rock-Paper-Scissors Client
echo =========================================
echo.

REM Check if compiled classes exist
if not exist "out" (
    echo ❌ Error: 'out' directory not found!
    echo Please compile the project first: compile.bat
    exit /b 1
)

REM Run the GUI client
echo Launching GUI client...
echo Connecting to server at localhost:12345
echo.
java -cp out client.GameClientUI
