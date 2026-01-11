@echo off
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

REM Auto-detect and set JAVA_HOME if not set
if "%JAVA_HOME%"=="" (
    echo Detecting JAVA_HOME...
    
    REM Try common Java installation locations
    if exist "C:\Program Files\Eclipse Adoptium\jdk-21*" (
        for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do set "JAVA_HOME=%%i"
    ) else if exist "C:\Program Files\Java\jdk-21*" (
        for /d %%i in ("C:\Program Files\Java\jdk-21*") do set "JAVA_HOME=%%i"
    ) else if exist "C:\Program Files\OpenJDK\jdk-21*" (
        for /d %%i in ("C:\Program Files\OpenJDK\jdk-21*") do set "JAVA_HOME=%%i"
    ) else if exist "C:\Program Files\Eclipse Adoptium\jdk-25*" (
        for /d %%i in ("C:\Program Files\Eclipse Adoptium\jdk-25*") do set "JAVA_HOME=%%i"
    ) else if exist "C:\Program Files\Java\jdk-25*" (
        for /d %%i in ("C:\Program Files\Java\jdk-25*") do set "JAVA_HOME=%%i"
    )
    
    if not "%JAVA_HOME%"=="" (
        echo Using Java from: %JAVA_HOME%
    ) else (
        echo WARNING: Could not auto-detect JAVA_HOME
        echo Please set JAVA_HOME environment variable
    )
)

REM Check Java version
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -version 2^>^&1') do set "JAVA_VERSION=%%j"
if %JAVA_VERSION% LSS 21 (
    echo ERROR: Java 21 or higher is required!
    echo Current version: 
    java -version 2>&1
    echo.
    echo Please download from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Run the game
call mvnw.cmd clean javafx:run

REM Keep window open if there's an error
if %errorlevel% neq 0 (
    echo.
    echo Error running the game. See above for details.
    pause
)
