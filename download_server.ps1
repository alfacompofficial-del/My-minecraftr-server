# ============================================
#   Скрипт для скачивания Paper и плагинов
#   Запусти этот скрипт ОДИН РАЗ перед стартом
# ============================================
# Используй: powershell -ExecutionPolicy Bypass -File download_server.ps1

$serverDir = "d:\My minicraft server"
$pluginsDir = "$serverDir\plugins"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Скачивание Minecraft сервера..." -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Скачиваем Paper 1.19.4
Write-Host "`n[1/10] Скачиваем Paper 1.19.4..." -ForegroundColor Yellow
$paperUrl = "https://api.papermc.io/v2/projects/paper/versions/1.19.4/builds/550/downloads/paper-1.19.4-550.jar"
Invoke-WebRequest -Uri $paperUrl -OutFile "$serverDir\paper.jar" -UseBasicParsing

# Скачиваем плагины
Write-Host "[2/10] Скачиваем AuthMe..." -ForegroundColor Yellow
$authMeUrl = "https://github.com/AuthMe/AuthMeReloaded/releases/download/5.6.0-beta2/AuthMe-5.6.0-beta2.jar"
Invoke-WebRequest -Uri $authMeUrl -OutFile "$pluginsDir\AuthMe.jar" -UseBasicParsing

Write-Host "[3/10] Скачиваем Vault..." -ForegroundColor Yellow
$vaultUrl = "https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar"
Invoke-WebRequest -Uri $vaultUrl -OutFile "$pluginsDir\Vault.jar" -UseBasicParsing

Write-Host "[4/10] Скачиваем LuckPerms..." -ForegroundColor Yellow
$lpUrl = "https://download.luckperms.net/1543/bukkit/loader/LuckPerms-Bukkit-5.4.146.jar"
Invoke-WebRequest -Uri $lpUrl -OutFile "$pluginsDir\LuckPerms.jar" -UseBasicParsing

Write-Host "[5/10] Скачиваем EssentialsX..." -ForegroundColor Yellow
$essUrl = "https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar"
Invoke-WebRequest -Uri $essUrl -OutFile "$pluginsDir\EssentialsX.jar" -UseBasicParsing

Write-Host "[6/10] Скачиваем WorldEdit..." -ForegroundColor Yellow
$weUrl = "https://cdn.modrinth.com/data/1u6JkXh5/versions/8.3.11-dist/worldedit-bukkit-7.2.19.jar"
Invoke-WebRequest -Uri $weUrl -OutFile "$pluginsDir\WorldEdit.jar" -UseBasicParsing

Write-Host "[7/10] Скачиваем WorldGuard..." -ForegroundColor Yellow
$wgUrl = "https://builds.enginehub.org/job/worldguard/last-successful/worldguard-bukkit-7.0.9-dist.jar"
Invoke-WebRequest -Uri $wgUrl -OutFile "$pluginsDir\WorldGuard.jar" -UseBasicParsing

Write-Host "[8/10] Скачиваем GriefPrevention..." -ForegroundColor Yellow
$gpUrl = "https://github.com/TechFortress/GriefPrevention/releases/download/16.18.2/GriefPrevention-16.18.2.jar"
Invoke-WebRequest -Uri $gpUrl -OutFile "$pluginsDir\GriefPrevention.jar" -UseBasicParsing

Write-Host "[9/10] Скачиваем EssentialsX Chat..." -ForegroundColor Yellow
$escUrl = "https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsXChat-2.20.1.jar"
Invoke-WebRequest -Uri $escUrl -OutFile "$pluginsDir\EssentialsXChat.jar" -UseBasicParsing

Write-Host "[10/10] Скачиваем EssentialsX Spawn..." -ForegroundColor Yellow
$sspUrl = "https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsXSpawn-2.20.1.jar"
Invoke-WebRequest -Uri $sspUrl -OutFile "$pluginsDir\EssentialsXSpawn.jar" -UseBasicParsing

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "  Все файлы скачаны!" -ForegroundColor Green
Write-Host "  Теперь запусти start.bat" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
