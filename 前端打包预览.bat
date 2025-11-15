@echo off
setlocal ENABLEDELAYEDEXPANSION

pushd %~dp0
if not exist frontend (
  echo [错误] 未找到 frontend 目录。
  pause
  exit /b 1
)

cd frontend

where node >nul 2>nul
if errorlevel 1 (
  echo [错误] 未检测到 Node.js，请先安装 Node.js (https://nodejs.org/)。
  pause
  exit /b 1
)

if not exist node_modules (
  echo [信息] 首次安装依赖...
  call npm ci || call npm install || exit /b 1
) else (
  echo [信息] 依赖已存在，跳过安装。
)

echo [信息] 构建生产包...
call npm run build || exit /b 1

echo [信息] 启动本地预览服务 (http://localhost:5050)...
start "Vite Preview" cmd /c "npm run preview -- --port 5050"

REM 稍等片刻再打开浏览器
ping -n 3 127.0.0.1 >nul
start http://localhost:5050

popd
endlocal
exit /b 0
