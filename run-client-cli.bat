@echo off
REM Run script for Game Client CLI (Windows)

echo =========================================
echo   Starting Console Client
echo =========================================
echo.

REM Check if compiled classes exist
if not exist "out" (
    echo ❌ Error: 'out' directory not found!
    echo Please compile the project first: compile.bat
    exit /b 1
)

REM Run the CLI client
echo Launching console client...
echo Connecting to server at localhost:12345
echo.
java -cp out client.GameClientCLI
