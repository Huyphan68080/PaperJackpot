package com.project.paperjackpot;

import com.project.paperjackpot.command.JackpotCommand;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.game.GameMode;
import com.project.paperjackpot.listener.MenuListener;
import com.project.paperjackpot.manager.ConfigManager;
import com.project.paperjackpot.manager.HappyHourManager;
import com.project.paperjackpot.manager.JackpotManager;
import com.project.paperjackpot.manager.SeasonManager;
import com.project.paperjackpot.placeholder.PaperJackpotExpansion;
import com.project.paperjackpot.session.SoloSlotSession;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PaperJackpot - Plugin Casino Nổ Hũ Jackpot Cá Nhân & Quỹ Hũ Tích Lũy Server cho Paper 1.21.1
 */
public class PaperJackpot extends JavaPlugin {

    private Economy economy;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private JackpotManager jackpotManager;
    private SeasonManager seasonManager;
    private HappyHourManager happyHourManager;
    private JackpotCommand jackpotCommand;

    // Quản lý phiên chơi cá nhân của người chơi online
    private final Map<UUID, SoloSlotSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        // Khởi tạo các manager
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);
        jackpotManager = new JackpotManager(this);
        seasonManager = new SeasonManager(this);
        happyHourManager = new HappyHourManager(this);

        // Hook Vault Economy (Không disable plugin nếu Vault load chậm)
        setupEconomy();

        // Đăng ký PlaceholderAPI Expansion nếu server cài PlaceholderAPI
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PaperJackpotExpansion(this).register();
            getLogger().info("📊 Đã đăng ký PlaceholderAPI Expansion (%paperjackpot_pool%, %paperjackpot_top_line_1% -> 10)!");
        }

        // Đăng ký command & listener
        jackpotCommand = new JackpotCommand(this);
        if (getCommand("jackpot") != null) {
            getCommand("jackpot").setExecutor(jackpotCommand);
            getCommand("jackpot").setTabCompleter(jackpotCommand);
        }
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);

        // Hiển thị ASCII Art Banner khi khởi động
        org.bukkit.Bukkit.getConsoleSender().sendMessage(configManager.getMiniMessage().deserialize("""
                <gradient:gold:yellow>
                 888888ba                                             dP                   dP                           dP   \s
                 88    `8b                                            88                   88                           88   \s
                a88aaaa8P' .d8888b. 88d888b. .d8888b. 88d888b.        88 .d8888b. .d8888b. 88  .dP  88d888b. .d8888b. d8888P\s
                 88        88'  `88 88'  `88 88ooood8 88'  `88        88 88'  `88 88'  `"" 88888"   88'  `88 88'  `88   88   \s
                 88        88.  .88 88.  .88 88.  ... 88       88.  .d8P 88.  .88 88.  ... 88  `8b. 88.  .88 88.  .88   88   \s
                 dP        `88888P8 88Y888P' `88888P' dP        `Y8888'  `88888P8 `88888P8 dP   `YP 88Y888P' `88888P'   dP   \s
                                    88                                                              88                       \s
                                    dP                                                              dP                       \s
                 a88888b.                   dP    8888ba.88ba                    oo                                          \s
                d8'   `88                   88    88  `8b  `8b                                                               \s
                88        88d888b. .d8888b.       88   88   88 .d8888b. dP   .dP dP .d8888b.                                 \s
                88        88'  `88 88ooood8       88   88   88 88'  `88 88   d8' 88 88'  `""                                 \s
                Y8.   .88 88       88.  ... dP    88   88   88 88.  .88 88 .88'  88 88.  ...                                 \s
                 Y88888P' dP       `88888P' 88    dP   dP   dP `88888P8 8888P'   dP `88888P'                                 \s
                </gradient>
                <yellow><bold>🎰 PaperJackpot Casino Nổ Hũ (v1.0.0) đã khởi động thành công!</bold></yellow>
                """));
    }

    @Override
    public void onDisable() {
        if (happyHourManager != null) {
            happyHourManager.stop();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        activeSessions.clear();
        getLogger().info("🎰 PaperJackpot đã tắt an toàn.");
    }

    public boolean setupEconomy() {
        if (economy != null) return true;
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public SoloSlotSession getOrCreateSession(Player player, GameMode mode) {
        return activeSessions.compute(player.getUniqueId(), (uuid, existing) -> {
            if (existing != null && existing.getGameMode() == mode) {
                return existing;
            }
            return new SoloSlotSession(this, player, mode);
        });
    }

    public SoloSlotSession getSession(Player player) {
        return activeSessions.get(player.getUniqueId());
    }

    public void removeSession(Player player) {
        SoloSlotSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) {
            session.close();
        }
    }

    // Getters
    public Economy getEconomy() {
        if (economy == null) setupEconomy();
        return economy;
    }
    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public JackpotManager getJackpotManager() { return jackpotManager; }
    public SeasonManager getSeasonManager() { return seasonManager; }
    public HappyHourManager getHappyHourManager() { return happyHourManager; }
    public JackpotCommand getJackpotCommand() { return jackpotCommand; }
}
