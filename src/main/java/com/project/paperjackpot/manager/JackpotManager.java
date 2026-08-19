package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * JackpotManager - Quản lý Quỹ Nổ Hũ Tích Lũy Server & BossBar Thần Tài Toàn Server.
 */
public class JackpotManager {

    private final PaperJackpot plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;

    private double currentPool;
    private String lastWinnerName = "Chưa có";
    private final double defaultPool = 0.0;
    private final double lossContributionPercent = 100.0;

    private final BossBar globalBossBar;

    public JackpotManager(PaperJackpot plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();

        // Load Quỹ Hũ từ SQLite Database
        if (databaseManager != null) {
            this.currentPool = databaseManager.loadJackpotPool(defaultPool);
        } else {
            this.currentPool = defaultPool;
        }

        // Khởi tạo BossBar Server Real-Time với tiêu đề QUỸ JACKPOT TÍCH LŨY SERVER
        this.globalBossBar = BossBar.bossBar(
                configManager.getMiniMessage().deserialize("<gradient:#FF0000:#FFD700><bold>🔥 QUỸ JACKPOT TÍCH LŨY SERVER: " + ConfigManager.formatMoney(currentPool) + "$ 🔥</bold></gradient>"),
                1.0f,
                BossBar.Color.PURPLE,
                BossBar.Overlay.PROGRESS
        );

        plugin.getLogger().info("[JackpotManager] Quỹ Nổ Hũ Tích Lũy hiện tại: " + ConfigManager.formatMoney(currentPool) + "$");
    }

    public synchronized double getJackpotPool() {
        return currentPool;
    }

    public synchronized String getLastWinnerName() {
        return lastWinnerName;
    }

    public synchronized void setLastWinnerName(String name) {
        this.lastWinnerName = name;
    }

    public synchronized void addLossToPool(double lossBetAmount) {
        double addedAmount = lossBetAmount * (lossContributionPercent / 100.0);
        this.currentPool += addedAmount;

        if (databaseManager != null) {
            databaseManager.saveJackpotPoolAsync(this.currentPool);
        }
        updateGlobalBossBar();
    }

    public synchronized double claimJackpotPool(String winnerName) {
        double wonPool = this.currentPool;
        this.currentPool = 0.0; // Reset về 0$
        this.lastWinnerName = winnerName;

        if (databaseManager != null) {
            databaseManager.saveJackpotPoolAsync(this.currentPool);
        }
        updateGlobalBossBar();
        return wonPool;
    }

    public synchronized void setJackpotPool(double newAmount) {
        this.currentPool = Math.max(0.0, newAmount);
        if (databaseManager != null) {
            databaseManager.saveJackpotPoolAsync(this.currentPool);
        }
        updateGlobalBossBar();
    }

    public synchronized void addJackpotPool(double amount) {
        this.currentPool += amount;
        if (databaseManager != null) {
            databaseManager.saveJackpotPoolAsync(this.currentPool);
        }
        updateGlobalBossBar();
    }

    public synchronized double resetJackpotPool() {
        double won = this.currentPool;
        this.currentPool = 0.0;
        if (databaseManager != null) {
            databaseManager.saveJackpotPoolAsync(this.currentPool);
        }
        updateGlobalBossBar();
        return won;
    }

    public void broadcastJackpotWin(String winnerName, double amount) {
        this.lastWinnerName = winnerName;
        String msg = "<gradient:#FF0000:#FFD700><bold>🎉 CHÚC MỪNG PHÁT LỘC! " + winnerName + " VỪA TRÚNG NỔ HŨ " + ConfigManager.formatMoney(amount) + "$! 🎉</bold></gradient>";
        Bukkit.broadcast(configManager.getMiniMessage().deserialize(msg));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
        }
    }

    public void updateGlobalBossBar() {
        if (!configManager.isGlobalBossbarEnabled()) return;
        globalBossBar.name(configManager.getMiniMessage().deserialize("<gradient:#FF0000:#FFD700><bold>🔥 QUỸ JACKPOT TÍCH LŨY SERVER: " + ConfigManager.formatMoney(currentPool) + "$ 🔥</bold></gradient>"));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showBossBar(globalBossBar);
        }
    }

    public void showBossBarToPlayer(Player player) {
        if (configManager.isGlobalBossbarEnabled()) {
            player.showBossBar(globalBossBar);
        }
    }

    public void hideBossBarFromPlayer(Player player) {
        player.hideBossBar(globalBossBar);
    }
}
