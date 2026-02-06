@echo off
echo ========================================
echo DevSync - Push to GitHub
echo ========================================
echo.

REM Initialize git if not already initialized
if not exist ".git" (
    echo Initializing Git repository...
    git init
    echo.
)

REM Add all files
echo Adding all files...
git add .
echo.

REM Create commit
echo Creating commit...
git commit -m "Initial commit - DevSync FYP project ready for deployment"
echo.

REM Remove existing remote if any
git remote remove origin 2>nul

REM Add GitHub remote
echo Adding GitHub remote...
git remote add origin https://github.com/ahsanilya-s/DevSync.git
echo.

REM Rename branch to main
echo Setting branch to main...
git branch -M main
echo.

REM Push to GitHub
echo Pushing to GitHub...
git push -u origin main
echo.

echo ========================================
echo Done! Check: https://github.com/ahsanilya-s/DevSync
echo ========================================
pause
