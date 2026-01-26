@echo off
REM Run script for Game Server (Windows)

echo =========================================
echo   Starting Rock-Paper-Scissors Server
echo =========================================
echo.

REM Check if compiled classes exist
if not exist "out" (
    echo ❌ Error: 'out' directory not found!
    echo Please compile the project first: compile.bat
    exit /b 1
)

REM Run the server
echo Starting server on port 12345...
echo Press Ctrl+C to stop the server
echo.
java -cp out server.GameServer
