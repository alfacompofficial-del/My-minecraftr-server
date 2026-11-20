@echo off
title Скачиваем Paper Minecraft Server
echo ========================================
echo   Скачиваем файлы сервера...
echo ========================================
echo.

cd /d "d:\My minicraft server"

REM Скачиваем Paper 1.20.4 (Build 499)
echo [1/3] Скачиваем Paper 1.20.4...
curl -L -o paper.jar "https://api.papermc.io/v2/projects/paper/versions/1.20.4/builds/499/downloads/paper-1.20.4-499.jar"
if %errorlevel%==0 (echo Paper OK!) else (echo ОШИБКА: Скачайте Paper вручную с https://papermc.io/downloads)

REM Скачиваем LuckPerms
echo [2/3] Скачиваем LuckPerms...
curl -L -o plugins\LuckPerms.jar "https://download.luckperms.net/1557/bukkit/loader/LuckPerms-Bukkit-5.4.157.jar"
if %errorlevel%==0 (echo LuckPerms OK!) else (echo ОШИБКА LuckPerms)

REM Скачиваем GriefPrevention
echo [3/3] Скачиваем GriefPrevention...
curl -L -o plugins\GriefPrevention.jar "https://github.com/TechFortress/GriefPrevention/releases/latest/download/GriefPrevention.jar"
if %errorlevel%==0 (echo GriefPrevention OK!) else (echo ОШИБКА GriefPrevention)

echo.
echo ========================================
echo   Готово! Запусти start.bat
echo ========================================
pause
