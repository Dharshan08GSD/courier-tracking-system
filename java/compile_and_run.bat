@echo off
echo ============================================================
echo   Courier and Parcel Tracking Management System
echo   Compile and Run Script
echo ============================================================

set JAR_NAME=sqlite-jdbc-3.42.0.0.jar
set JAR_PATH=java\lib\%JAR_NAME%
set JAR_URL=https://github.com/xerial/sqlite-jdbc/releases/download/3.42.0.0/%JAR_NAME%

REM Download sqlite-jdbc if not present
if not exist "%JAR_PATH%" (
    echo [INFO] Downloading %JAR_NAME%...
    curl -L -o "%JAR_PATH%" "%JAR_URL%"
    if errorlevel 1 (
        echo [ERROR] Download failed. Please manually download sqlite-jdbc jar.
        echo         URL: %JAR_URL%
        echo         Place it in: %JAR_PATH%
        pause
        exit /b 1
    )
    echo [INFO] Download complete.
)

REM Create bin directory
if not exist "java\bin" mkdir java\bin
if not exist "database" mkdir database

REM Compile
echo [INFO] Compiling Java sources...
javac -encoding UTF-8 -cp "%JAR_PATH%" -d java\bin java\src\courier\*.java
if errorlevel 1 (
    echo [ERROR] Compilation failed. Ensure JDK 11+ is installed and on PATH.
    pause
    exit /b 1
)
echo [INFO] Compilation successful.

REM Run
echo [INFO] Starting application...
echo.
java -cp "java\bin;%JAR_PATH%" courier.Main

pause
