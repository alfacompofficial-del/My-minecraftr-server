package ru.myserver;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.*;

public class MyServerPlugin extends JavaPlugin implements Listener {

    private static Economy economy;
    private final Set<UUID> usedStartKit = new HashSet<>();
    private final Set<UUID> authorizedPlayers = new HashSet<>();
    private Location authSpawnLocation;
    private Location spawnLocation;

    @Override
    public void onEnable() {
        // Регистрация слушателей
        getServer().getPluginManager().registerEvents(this, this);

        // Настройка Vault / экономики
        setupEconomy();

        // Запуск обновления скорборда каждую секунду
        getServer().getScheduler().runTaskTimer(this, this::updateAllScoreboards, 20L, 20L);

        // Установка дефолтных локаций
        authSpawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 255, 0.5);
        spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 65, 0.5);

        getLogger().info("MyServerPlugin успешно загружен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyServerPlugin выгружен.");
    }

    // ==================== ЭКОНОМИКА ====================
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    // ==================== СОБЫТИЯ ====================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Телепортируем на высокую точку до авторизации
        player.teleport(authSpawnLocation);
        player.setGameMode(GameMode.SPECTATOR); // Нельзя двигаться/взаимодействовать
        player.sendMessage(ChatColor.GOLD + "=================================");
        player.sendMessage(ChatColor.YELLOW + "       Добро пожаловать на сервер!");
        player.sendMessage(ChatColor.WHITE + "  Введите: " + ChatColor.GREEN + "/register <пароль> <пароль>");
        player.sendMessage(ChatColor.WHITE + "  или: " + ChatColor.GREEN + "/login <пароль>");
        player.sendMessage(ChatColor.GOLD + "=================================");

        // Скорборд
        setupScoreboard(player);

        // Сообщение о входе
        event.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " 
            + getPlayerColoredName(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        authorizedPlayers.remove(player.getUniqueId());
        event.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " 
            + getPlayerColoredName(player));
    }

    // Слушаем авторизацию через AuthMe (через кастомное событие или команду /login)
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();

        // Если игрок не авторизован — блокируем все команды кроме /login и /register
        if (!authorizedPlayers.contains(player.getUniqueId())) {
            if (!cmd.startsWith("/login") && !cmd.startsWith("/l ") 
                    && !cmd.startsWith("/register") && !cmd.startsWith("/reg ")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "❌ Сначала авторизуйтесь: /login <пароль>");
            }
        }

        // Блокируем опасные команды для не-adminов
        if (!player.hasPermission("server.admin")) {
            String[] blocked = {"/kill", "/ban", "/unban", "/kick", "/op", "/deop",
                                "/stop", "/restart", "/reload", "/whitelist", "/pardon",
                                "/gamemode", "/gm", "/tp", "/teleport", "/give", "/clear"};
            for (String b : blocked) {
                if (cmd.startsWith(b)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "❌ У вас нет доступа к этой команде!");
                    return;
                }
            }
        }
    }

    // Симуляция события авторизации через AuthMe (если AuthMe не подключен - делаем через команду)
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Блокируем чат до авторизации
        Player player = event.getPlayer();
        if (!authorizedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "❌ Сначала авторизуйтесь!");
        }
    }

    // Метод авторизации (вызывается AuthMe через API или нашим обработчиком)
    public void onPlayerAuthenticated(Player player) {
        authorizedPlayers.add(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(spawnLocation);
        player.sendMessage(ChatColor.GREEN + "✅ Вы успешно вошли! Добро пожаловать на сервер!");
        setupScoreboard(player);
    }

    // ==================== СКОРБОРД ====================
    private void setupScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("info", "dummy",
            ChatColor.GOLD + "" + ChatColor.BOLD + "⚡ МОЙ СЕРВЕР");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Заполняем скорборд (обновляется каждую секунду)
        updateScoreboard(player, obj);
        player.setScoreboard(board);
    }

    private void updateScoreboard(Player player, Objective obj) {
        Scoreboard board = obj.getScoreboard();
        // Очищаем старые записи
        for (String entry : board.getEntries()) board.resetScores(entry);

        String role = getRoleDisplay(player);
        String timeStr = getMinecraftTime();
        double balance = (economy != null) ? economy.getBalance(player) : 0;

        int line = 10;
        setScore(obj, ChatColor.YELLOW + "▬▬▬▬▬▬▬▬▬▬▬▬▬", line--);
        setScore(obj, ChatColor.WHITE + "👤 " + player.getName(), line--);
        setScore(obj, ChatColor.WHITE + "🎭 Статус: " + role, line--);
        setScore(obj, ChatColor.YELLOW + "▬▬▬▬▬▬▬▬▬▬▬▬▬", line--);
        setScore(obj, ChatColor.WHITE + "💰 Баланс: " + ChatColor.GREEN + String.format("%.0f", balance) + "$", line--);
        setScore(obj, ChatColor.YELLOW + "▬▬▬▬▬▬▬▬▬▬▬▬▬", line--);
        setScore(obj, ChatColor.WHITE + "⏰ Время: " + ChatColor.AQUA + timeStr, line--);
        setScore(obj, ChatColor.YELLOW + "▬▬▬▬▬▬▬▬▬▬▬▬▬", line);
    }

    private void setScore(Objective obj, String name, int score) {
        obj.getScore(name).setScore(score);
    }

    private void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard board = player.getScoreboard();
            if (board.getObjective("info") != null) {
                Objective obj = board.getObjective("info");
                updateScoreboard(player, obj);
            } else {
                setupScoreboard(player);
            }
            updateTabList(player);
        }
    }

    // ==================== TAB ЛИСТ ====================
    private void updateTabList(Player player) {
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.GOLD + "" + ChatColor.BOLD + "    ⚡ МОЙ МАЙНКРАФТ СЕРВЕР ⚡    \n");
        header.append(ChatColor.YELLOW + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        header.append(ChatColor.WHITE + " Игроков онлайн: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size());

        StringBuilder footer = new StringBuilder();
        footer.append(ChatColor.YELLOW + "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        footer.append(ChatColor.GRAY + "  IP: " + ChatColor.WHITE + "91.197.6.19:23134");

        player.setPlayerListHeaderFooter(header.toString(), footer.toString());

        // Обновляем имена в таблице для отображения роли
        for (Player online : Bukkit.getOnlinePlayers()) {
            String roleTag = online.hasPermission("server.admin") 
                ? ChatColor.RED + "[ADMIN] " 
                : ChatColor.GRAY + "[USER] ";
            // PlayerListName устаравивает у каждого игрока отображение другого
            online.setPlayerListName(roleTag + online.getName());
        }
    }

    // ==================== СТАРТОВЫЙ КИТ ====================
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("kit")) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Использование: /kit start");
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {
                if (usedStartKit.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "❌ Вы уже получили стартовый кит!");
                    return true;
                }

                giveStartKit(player);
                usedStartKit.add(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "✅ Вы получили стартовый набор!");
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("setspawn") && player.hasPermission("server.admin")) {
            spawnLocation = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "✅ Спавн установлен!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("authspawn") && player.hasPermission("server.admin")) {
            authSpawnLocation = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "✅ Точка авторизации установлена!");
            return true;
        }

        return false;
    }

    private void giveStartKit(Player player) {
        // Инструменты
        player.getInventory().addItem(createNamedItem(Material.IRON_SWORD, "§e⚔ Начальный Меч"));
        player.getInventory().addItem(createNamedItem(Material.IRON_PICKAXE, "§e⛏ Начальная Кирка"));
        player.getInventory().addItem(createNamedItem(Material.IRON_AXE, "§e🪓 Начальный Топор"));
        player.getInventory().addItem(createNamedItem(Material.IRON_SHOVEL, "§e🔧 Начальная Лопата"));
        
        // Еда
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 32);
        player.getInventory().addItem(food);
        
        // Материалы
        player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 64));
        player.getInventory().addItem(new ItemStack(Material.TORCH, 64));
        player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 2));
        player.getInventory().addItem(new ItemStack(Material.CHEST, 4));
        player.getInventory().addItem(new ItemStack(Material.OAK_LOG, 32));
        
        // Начальный баланс
        if (economy != null) economy.depositPlayer(player, 500);
    }

    private ItemStack createNamedItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    // ==================== УТИЛИТЫ ====================
    private String getRoleDisplay(Player player) {
        if (player.hasPermission("server.admin")) {
            return ChatColor.RED + "" + ChatColor.BOLD + "АДМИН";
        }
        return ChatColor.GRAY + "Пользователь";
    }

    private String getPlayerColoredName(Player player) {
        if (player.hasPermission("server.admin")) {
            return ChatColor.RED + player.getName();
        }
        return ChatColor.GRAY + player.getName();
    }

    private String getMinecraftTime() {
        if (Bukkit.getWorld("world") == null) return "N/A";
        long time = Bukkit.getWorld("world").getTime();
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }
}
