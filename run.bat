@echo off
title Courier & Parcel Tracking Management System
cls
echo ============================================================
echo   Courier and Parcel Tracking Management System
echo ============================================================
echo.
echo   [1] Open Website in Browser (Live Web Portal)
echo   [2] Run Python Data Analytics
echo   [3] Run Java Console Application
echo   [4] Start Local Web Server (http://localhost:8000)
echo   [5] Exit
echo.
echo ============================================================
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" (
    echo Opening website...
    start "" "index.html"
    exit /b
)

if "%choice%"=="2" (
    echo.
    echo Running Python Data Retrieval...
    cd python
    python data_retrieval.py
    echo.
    echo Running Python Data Preprocessing...
    python data_preprocessing.py
    echo.
    echo Done!
    pause
    exit /b
)

if "%choice%"=="3" (
    call java\compile_and_run.bat
    exit /b
)

if "%choice%"=="4" (
    echo Starting server on http://localhost:8000...
    start http://localhost:8000
    python -m http.server 8000
    exit /b
)

if "%choice%"=="5" (
    exit /b
)

echo Invalid choice.
pause
