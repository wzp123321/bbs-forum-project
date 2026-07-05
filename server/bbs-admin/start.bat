@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

set "PORT=9999"
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

echo ============================================
echo  BBS Admin - 启动服务 (端口: %PORT%)
echo ============================================

REM [1/2] 杀掉占用端口的旧进程
echo [1/2] 检查并清理端口 %PORT% 上的旧进程...
set "KILLED=0"
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":%PORT% " ^| findstr "LISTENING"') do (
    if not "%%P"=="" if not "%%P"=="0" (
        echo   找到 PID=%%P, 正在结束...
        taskkill /f /pid %%P >nul 2>&1
        if !errorlevel! equ 0 (
            echo   [OK] 进程 %%P 已结束
            set /a KILLED+=1
        ) else (
            echo   [WARN] 进程 %%P 结束失败 (可能已退出)
        )
    )
)
if !KILLED! equ 0 (
    echo   [INFO] 端口未被占用
) else (
    echo   [INFO] 共结束 !KILLED! 个旧进程, 等待端口释放...
    timeout /t 2 /nobreak >nul
)

REM [2/2] 启动 Spring Boot 应用
echo [2/2] 启动 Spring Boot 应用...
echo --------------------------------------------
call mvnw.cmd spring-boot:run

endlocal
