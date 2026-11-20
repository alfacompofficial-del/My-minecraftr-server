#!/bin/bash
# ============================================
# deploy.sh — Скрипт деплоя плагинов
# Запускать в консоли Minestrator или по SSH
# ============================================

PLUGINS_DIR="./plugins"
echo "=== Деплой плагинов на сервер ==="

# Скачиваем плагины которые нужны но не в репо
echo "[1/6] Скачиваем LuckPerms..."
curl -L -o "$PLUGINS_DIR/LuckPerms.jar" \
  "https://download.luckperms.net/1557/bukkit/loader/LuckPerms-Bukkit-5.4.157.jar"

echo "[2/6] Скачиваем WorldEdit..."
curl -L -o "$PLUGINS_DIR/WorldEdit.jar" \
  "https://cdn.modrinth.com/data/1u6JkXh5/versions/8.3.11-dist/worldedit-bukkit-7.2.19.jar"

echo "[3/6] Скачиваем WorldGuard..."
curl -L -o "$PLUGINS_DIR/WorldGuard.jar" \
  "https://builds.enginehub.org/job/worldguard/last-successful/worldguard-bukkit-7.0.9-dist.jar"

echo "[4/6] Скачиваем GriefPrevention..."
curl -L -o "$PLUGINS_DIR/GriefPrevention.jar" \
  "https://github.com/TechFortress/GriefPrevention/releases/download/16.18.2/GriefPrevention-16.18.2.jar"

echo "[5/6] Права на файлы..."
chmod 644 "$PLUGINS_DIR"/*.jar

echo "[6/6] Готово! Перезапусти сервер."
echo "Затем введи команды из setup_permissions.txt"
echo "============================================="
