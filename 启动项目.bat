
chcp 65001 >nul
title 校园二手交易平台 - 快捷启动

echo.
echo ===============================================
echo           校园二手交易平台快捷启动
echo ===============================================
echo.

:menu
echo 请选择要执行的操作：
echo.
echo [1] 导入数据库
echo [2] 启动后端服务
echo [3] 启动前端服务
echo [4] 同时启动前后端
echo [5] 测试API接口
echo [6] 查看项目状态
echo [0] 退出
echo.
set /p choice=请输入选项 (0-6): 

if "%choice%"=="1" goto import_db
if "%choice%"=="2" goto start_backend
if "%choice%"=="3" goto start_frontend
if "%choice%"=="4" goto start_all
if "%choice%"=="5" goto test_api
if "%choice%"=="6" goto check_status
if "%choice%"=="0" goto exit
echo 无效选项，请重新选择！
pause
cls
goto menu

:import_db
echo.
echo 正在导入数据库...
echo 请在MySQL命令行中执行以下命令：
echo mysql -u root -p
echo SOURCE database/init.sql;
echo.
echo 或者手动在MySQL Workbench中执行 database/init.sql 文件
pause
goto menu

:start_backend
echo.
echo 正在启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
echo 后端服务启动中，请等待...
echo 启动完成后访问: http://localhost:8080/api/test/health
cd ..
pause
goto menu

:start_frontend
echo.
echo 正在启动前端服务...
cd frontend
start "前端服务" cmd /k "npm run dev"
echo 前端服务启动中，请等待...
echo 启动完成后访问: http://localhost:5173
cd ..
pause
goto menu

:start_all
echo.
echo 正在同时启动前后端服务...
echo.
echo 启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"
cd ..
echo.
echo 等待3秒后启动前端服务...
timeout /t 3 /nobreak >nul
echo.
echo 启动前端服务...
cd frontend
start "前端服务" cmd /k "npm run dev"
cd ..
echo.
echo 服务启动完成！
echo 后端地址: http://localhost:8080/api
echo 前端地址: http://localhost:5173
pause
goto menu

:test_api
echo.
echo 正在测试API接口...
echo.
curl http://localhost:8080/api/test/health
echo.
echo.
echo 如果看到JSON响应，说明后端服务正常运行
pause
goto menu

:check_status
echo.
echo 检查项目状态...
echo.
echo 检查后端端口 8080...
netstat -an | find "8080" | find "LISTENING" >nul
if %errorlevel%==0 (
    echo ✓ 后端服务 (8080) 正在运行
) else (
    echo ✗ 后端服务 (8080) 未运行
)
echo.
echo 检查前端端口 5173...
netstat -an | find "5173" | find "LISTENING" >nul
if %errorlevel%==0 (
    echo ✓ 前端服务 (5173) 正在运行
) else (
    echo ✗ 前端服务 (5173) 未运行
)
echo.
echo 检查MySQL服务...
sc query mysql >nul 2>&1
if %errorlevel%==0 (
    echo ✓ MySQL服务已安装
) else (
    echo ✗ MySQL服务未安装或未启动
)
echo.
pause
goto menu

:exit
echo.
echo 感谢使用校园二手交易平台！
echo 再见！
pause
exit

:error
echo.
echo 发生错误，请检查：
echo 1. 是否已安装 Maven 和 Node.js
echo 2. 是否已安装 MySQL 数据库
echo 3. 网络连接是否正常
echo.
pause
goto menu
