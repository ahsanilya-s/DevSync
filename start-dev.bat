@echo off
echo Starting DevSync Development Environment...

REM Check if port 8080 is available
netstat -an | find "8080" >nul
if %errorlevel% == 0 (
    echo Port 8080 is busy, trying 8081...
    set BACKEND_PORT=8081
) else (
    echo Port 8080 is available
    set BACKEND_PORT=8080
)

echo Starting backend on port %BACKEND_PORT%...
start "DevSync Backend" cmd /k "cd /d %~dp0 && set SERVER_PORT=%BACKEND_PORT% && mvnw.cmd spring-boot:run"

timeout /t 5 /nobreak >nul

echo Starting frontend...
cd frontend
start "DevSync Frontend" cmd /k "npm run dev"

echo DevSync is starting...
echo Backend: http://localhost:%BACKEND_PORT%
echo Frontend: http://localhost:5173
pause