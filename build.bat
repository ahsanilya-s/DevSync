@echo off
echo Building DevSync Application...

echo.
echo [1/4] Cleaning previous builds...
call mvn clean

echo.
echo [2/4] Running tests...
call mvn test

echo.
echo [3/4] Building backend...
call mvn package -DskipTests

echo.
echo [4/4] Building frontend...
cd frontend
call npm install
call npm run build
cd ..

echo.
echo Build completed successfully!
echo Backend JAR: target/devsync-0.0.1-SNAPSHOT.jar
echo Frontend build: frontend/dist/

pause