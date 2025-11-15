@echo off
chcp 65001 >nul
title 校园二手交易平台 - 快捷启动

echo 正在检查并停止之前运行的服务...

:: 查找并杀死后端Java进程（Spring Boot）
for /f "tokens=2" %%i in ('tasklist ^| findstr "java.exe"') do (
    taskkill /F /PID %%i >nul 2>&1
    if !errorlevel! equ 0 (
        echo 已停止后端Java进程 PID: %%i
    )
)

:: 查找并杀死前端Node.js进程
for /f "tokens=2" %%i in ('tasklist ^| findstr "node.exe"') do (
    taskkill /F /PID %%i >nul 2>&1
    if !errorlevel! equ 0 (
        echo 已停止前端Node进程 PID: %%i
    )
)

:: 等待1秒确保进程完全停止
timeout /t 1 /nobreak >nul

echo.
echo 正在启动服务...

:: 启动后端服务
cd backend
echo 启动后端Spring Boot服务...
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..

:: 等待2秒让后端服务初步启动
timeout /t 2 /nobreak >nul

:: 启动前端服务
cd frontend
echo 启动前端开发服务器...
start "前端服务" cmd /k "npm run dev"
cd ..

echo.
echo 服务启动完成！
echo 后端服务: Spring Boot (通常在8080端口)
echo 前端服务: 开发服务器 (通常在3000或5173端口)
echo.
echo 按任意键退出此窗口...
pause >nul