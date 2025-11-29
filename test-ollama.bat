@echo off
echo ========================================
echo Testing Ollama Installation
echo ========================================
echo.

echo [1/4] Checking Ollama version...
ollama --version
echo.

echo [2/4] Listing installed models...
ollama list
echo.

echo [3/4] Testing API endpoint...
curl -s http://localhost:11434/api/tags
echo.
echo.

echo [4/4] Testing deepseek-coder with JSON...
curl -s http://localhost:11434/api/chat -d "{\"model\":\"deepseek-coder:latest\",\"messages\":[{\"role\":\"user\",\"content\":\"Return only this JSON: {\\\"status\\\":\\\"ok\\\"}\"}],\"stream\":false}"
echo.
echo.

echo ========================================
echo Test Complete
echo ========================================
pause
