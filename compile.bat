@echo off
REM Compile script for Rock-Paper-Scissors Game (Windows)

echo =========================================
echo   Compiling Rock-Paper-Scissors Game
echo =========================================
echo.

REM Create out directory if it doesn't exist
if not exist "out" (
    echo Creating out directory...
    mkdir out
)

REM Compile all Java files
echo Compiling Java source files...
javac -d out -sourcepath src src\game\*.java src\server\*.java src\client\*.java src\utils\*.java

REM Check if compilation was successful
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful!
    echo Compiled classes are in the 'out' directory
    echo.
    echo Next steps:
    echo   - Run server: run-server.bat
    echo   - Run client: run-client-gui.bat
) else (
    echo.
    echo ❌ Compilation failed!
    echo Please check the error messages above
    exit /b 1
)
