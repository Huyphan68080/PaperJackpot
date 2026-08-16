package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * SeasonManager - Quản lý Đua Top Mùa Giải & Tự Động Trao Thưởng Cuối Tuần (Weekly Season Rewards).
 */
public class SeasonManager {

    private final PaperJackpot plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final MiniMessage mm;

    public SeasonManager(PaperJackpot plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();
        this.mm = configManager.getMiniMessage();

        // Kiểm tra trao thưởng khi khởi động server
        checkAndRewardWeeklyTop();
    }

    public void checkAndRewardWeeklyTop() {
        if (!configManager.isWeeklyRewardsEnabled()) return;
        if (databaseManager == null) return;

        long lastReward = databaseManager.getLastWeeklyRewardTime();
        long now = System.currentTimeMillis();
        long oneWeekMs = 7 * 24 * 3600 * 1000L;

        if (now - lastReward >= oneWeekMs) {
            processWeeklyRewards(now);
        }
    }

    public void processWeeklyRewards(long now) {
        List<DatabaseManager.TopWinnerEntry> topList = databaseManager.getTopWinners(3);
        if (topList.isEmpty()) {
            databaseManager.saveLastWeeklyRewardTimeAsync(now);
            return;
        }

        Economy economy = plugin.getEconomy();
        double[] rewards = {
                configManager.getTop1Reward(),
                configManager.getTop2Reward(),
                configManager.getTop3Reward()
        };

        StringBuilder broadcastText = new StringBuilder("\n<gradient:gold:yellow><bold>🏆 BẢNG VÀNG CASINO - CHỐT THƯỞNG MÙA GIẢI TUẦN 🏆</bold></gradient>\n");
        broadcastText.append("<gray>Chúc mừng Top 3 Thần Tài Casino đã nhận thưởng khủng tuần này!</gray>\n");

        for (int i = 0; i < topList.size() && i < 3; i++) {
            DatabaseManager.TopWinnerEntry entry = topList.get(i);
            double rewardAmount = rewards[i];
            int rank = i + 1;

            if (economy != null && rewardAmount > 0) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.name());
                economy.depositPlayer(target, rewardAmount);
            }

            String rankIcon = switch (rank) {
                case 1 -> "🥇 TOP 1";
                case 2 -> "🥈 TOP 2";
                case 3 -> "🥉 TOP 3";
                default -> "⭐ TOP " + rank;
            };

            broadcastText.append("<yellow>").append(rankIcon).append(": <gold><bold>").append(entry.name())
                    .append("</bold></gold> | Thắng: <green>").append(ConfigManager.formatMoney(entry.totalPayout())).append("$</green>")
                    .append(" → Nhận Thưởng Đua Top: <gold><bold>+").append(ConfigManager.formatMoney(rewardAmount)).append("$</bold></gold></yellow>\n");
        }

        broadcastText.append("<gray>Hãy tiếp tục quay hũ `/jackpot` để chinh phục Mùa Giải mới!</gray>\n");

        Component msg = mm.deserialize(broadcastText.toString());
        Bukkit.broadcast(msg);

        // Lưu mốc thời gian vừa trao thưởng
        databaseManager.saveLastWeeklyRewardTimeAsync(now);
        plugin.getLogger().info("[SeasonManager] Đã tự động chốt trao thưởng Đua Top Mùa Giải Tuần thành công!");
    }
}
