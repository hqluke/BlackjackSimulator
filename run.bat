@echo off
setlocal enabledelayedexpansion

REM Blackjack Simulator Launcher Script

cd /d "%~dp0"

echo Starting Blackjack Simulator...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed!
    echo Please install Java 21 or higher from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Display Java version
echo Java version:
java -version 2>&1
echo.

REM Set JAVA_HOME if not already set
if "%JAVA_HOME%"=="" (
    echo JAVA_HOME is not set. Attempting to detect...
    
    REM Use java to tell us where it's installed
    for /f "tokens=*" %%i in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr "java.home"') do (
        set "JAVA_LINE=%%i"
    )
    
    REM Extract the path from "java.home = <path>"
    if defined JAVA_LINE (
        for /f "tokens=2* delims==" %%a in ("!JAVA_LINE!") do (
            set "JAVA_HOME=%%a"
            REM Trim leading spaces
            for /f "tokens=* delims= " %%b in ("!JAVA_HOME!") do set "JAVA_HOME=%%b"
        )
    )
    
    REM Fallback: Search common installation directories
    if not defined JAVA_HOME (
        echo Searching common Java installation directories...
        for /d %%d in ("C:\Program Files\Java\jdk*") do (
            if exist "%%d\bin\java.exe" (
                set "JAVA_HOME=%%d"
                echo Found Java at: !JAVA_HOME!
                goto :home_found
            )
        )
        for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
            if exist "%%d\bin\java.exe" (
                set "JAVA_HOME=%%d"
                echo Found Java at: !JAVA_HOME!
                goto :home_found
            )
        )
        for /d %%d in ("C:\Program Files\Oracle\jdk*") do (
            if exist "%%d\bin\java.exe" (
                set "JAVA_HOME=%%d"
                echo Found Java at: !JAVA_HOME!
                goto :home_found
            )
        )
    )
    
    :home_found
    if defined JAVA_HOME (
        echo Detected JAVA_HOME: !JAVA_HOME!
        REM Verify it's valid
        if not exist "!JAVA_HOME!\bin\java.exe" (
            echo WARNING: Detected JAVA_HOME does not contain bin\java.exe
            set "JAVA_HOME="
        )
    )
    
    if not defined JAVA_HOME (
        echo WARNING: Could not detect JAVA_HOME
        echo Maven will attempt to use java from your PATH
    )
    echo.
)

REM Run the game
echo Starting game...
echo.
call mvnw.cmd clean javafx:run

REM Keep window open if there's an error
if %errorlevel% neq 0 (
    echo.
    echo Error running the game. See above for details.
    pause
)

endlocal