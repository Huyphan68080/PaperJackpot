package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        // Chạy timer tự động kiểm tra mỗi 1 phút (1,200 ticks)
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkAndRewardWeeklyTop, 1200L, 1200L);
    }

    public void checkAndRewardWeeklyTop() {
        if (!configManager.isWeeklyRewardsEnabled()) return;
        if (databaseManager == null) return;

        long lastReward = databaseManager.getLastWeeklyRewardTime();
        long now = System.currentTimeMillis();
        long oneWeekMs = 7 * 24 * 3600 * 1000L;

        // Tự động kiểm tra múi giờ Việt Nam (Asia/Ho_Chi_Minh UTC+7):
        // Nếu hiện tại là Chủ Nhật lúc 23:59 đêm và trong tuần này chưa chốt -> TỰ ĐỘNG CHỐT THƯỞNG & RESET HOÀN TOÀN!
        ZonedDateTime nowVn = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        boolean isSundayNight = (nowVn.getDayOfWeek() == DayOfWeek.SUNDAY && nowVn.getHour() == 23 && nowVn.getMinute() >= 55);

        if ((isSundayNight && (now - lastReward >= 6 * 24 * 3600 * 1000L)) || (now - lastReward >= oneWeekMs)) {
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
        int[] ticketRewards = {10, 5, 3};

        StringBuilder broadcastText = new StringBuilder("\n<gradient:gold:yellow><bold>🏆 BẢNG VÀNG CASINO - CHỐT THƯỞNG MÙA GIẢI TUẦN 🏆</bold></gradient>\n");
        broadcastText.append("<gray>Chúc mừng Top 3 Thần Tài Casino đã nhận thưởng tiền & vé quay khủng tuần này!</gray>\n");

        for (int i = 0; i < topList.size() && i < 3; i++) {
            DatabaseManager.TopWinnerEntry entry = topList.get(i);
            double rewardAmount = rewards[i];
            int ticketCount = ticketRewards[i];
            int rank = i + 1;

            if (entry.uuid() != null) {
                // 1. Cộng tiền Vault ($)
                if (economy != null && rewardAmount > 0) {
                    Player onlineTarget = Bukkit.getPlayer(entry.uuid());
                    if (onlineTarget != null) {
                        economy.depositPlayer(onlineTarget, rewardAmount);
                    } else {
                        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(entry.uuid());
                        economy.depositPlayer(offlineTarget, rewardAmount);
                    }
                }
                // 2. Cộng Vé Quay vào CSDL
                if (databaseManager != null && ticketCount > 0) {
                    databaseManager.addTickets(entry.uuid(), ticketCount);
                }
            } else if (entry.name() != null) {
                // Fallback tên người chơi
                OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(entry.name());
                if (economy != null && rewardAmount > 0) {
                    economy.depositPlayer(offlineTarget, rewardAmount);
                }
                if (databaseManager != null && ticketCount > 0) {
                    databaseManager.addTickets(offlineTarget.getUniqueId(), ticketCount);
                }
            }

            // Gửi tin nhắn mừng & âm thanh chúc mừng trực tiếp cho người chơi nếu đang online
            Player onlineP = entry.uuid() != null ? Bukkit.getPlayer(entry.uuid()) : (entry.name() != null ? Bukkit.getPlayer(entry.name()) : null);
            if (onlineP != null) {
                onlineP.sendMessage(mm.deserialize(
                        "<gradient:#FFD700:#FFA500><bold>🎁 [THƯỞNG ĐUA TOP MÙA GIẢI TUẦN]</bold></gradient> <green>Chúc mừng bạn đoạt <gold><bold>Top " + rank + " Casino</bold></gold>! Đã nhận <gold><bold>+" + ConfigManager.formatMoney(rewardAmount) + "$</bold></gold> vào ví & <yellow><bold>+" + ticketCount + " Vé Quay 🎟️</bold></yellow>!</green>"
                ));
                onlineP.playSound(onlineP.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            }

            String rankIcon = switch (rank) {
                case 1 -> "🥇 TOP 1";
                case 2 -> "🥈 TOP 2";
                case 3 -> "🥉 TOP 3";
                default -> "⭐ TOP " + rank;
            };

            broadcastText.append("<yellow>").append(rankIcon).append(": <gold><bold>").append(entry.name())
                    .append("</bold></gold> | Thắng: <green>").append(ConfigManager.formatMoney(entry.totalPayout())).append("$</green>")
                    .append(" → Thưởng Đua Top: <gold><bold>+").append(ConfigManager.formatMoney(rewardAmount)).append("$</bold></gold> + <gradient:#FFD700:#FFA500><bold>")
                    .append(ticketCount).append(" Vé Quay 🎟️</bold></gradient></yellow>\n");
        }

        broadcastText.append("<gray>Hãy tiếp tục quay hũ `/jackpot` để chinh phục Mùa Giải mới!</gray>\n");

        Component msg = mm.deserialize(broadcastText.toString());
        Bukkit.broadcast(msg);

        // Lưu mốc thời gian vừa trao thưởng & làm sạch dữ liệu Bảng Xếp Hạng Hologram cho tuần mới
        databaseManager.saveLastWeeklyRewardTimeAsync(now);
        databaseManager.clearWeeklySpinHistory();
        plugin.getLogger().info("[SeasonManager] Đã tự động chốt trao thưởng Đua Top Mùa Giải Tuần & Reset Bảng Xếp Hạng Hologram thành công!");
    }
}
