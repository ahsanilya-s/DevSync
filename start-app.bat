@echo off
echo Starting DevSync Application...
echo.

echo Checking if MySQL is running...
tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I /N "mysqld.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo ✓ MySQL is running
) else (
    echo ❌ MySQL is not running. Please start MySQL first.
    echo You can start MySQL from XAMPP Control Panel or Services
    pause
    exit /b 1
)

echo.
echo Starting Backend (Spring Boot)...
start "DevSync Backend" cmd /k "cd /d \"%~dp0\" && mvn spring-boot:run"

echo Waiting for backend to start...
timeout /t 10 /nobreak >nul

echo.
echo Starting Frontend (React + Vite)...
start "DevSync Frontend" cmd /k "cd /d \"%~dp0frontend\" && npm run dev"

echo.
echo ✅ DevSync is starting up!
echo.
echo Backend will be available at: http://localhost:8080
echo Frontend will be available at: http://localhost:5173
echo.
echo Press any key to exit this window...
pause >nul