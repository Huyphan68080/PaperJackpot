package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * HappyHourManager - Quản lý tự động thông báo sự kiện Giờ Vàng qua Chat & Âm thanh rực rỡ.
 * Cho phép chỉnh sửa nội dung thông báo linh hoạt trong config.yml.
 */
public class HappyHourManager {

    private final PaperJackpot plugin;
    private final ConfigManager configManager;

    private boolean wasHappyHourActive = false;
    private BukkitTask checkTask;

    public HappyHourManager(PaperJackpot plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();

        // Khởi tạo trạng thái ban đầu khi plugin được load
        this.wasHappyHourActive = configManager.isHappyHourActive();

        startAutoChecker();
    }

    /**
     * Chạy task kiểm tra khung giờ tự động mỗi 10 giây (200 ticks)
     */
    public void startAutoChecker() {
        if (checkTask != null) {
            checkTask.cancel();
        }

        checkTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!configManager.isHappyHourEnabled()) return;

                boolean currentlyActive = configManager.isHappyHourActive();

                // 1. Khi Giờ Vàng BẮT ĐẦU (Từ tắt -> bật)
                if (!wasHappyHourActive && currentlyActive) {
                    wasHappyHourActive = true;
                    broadcastStartMessage();
                }
                // 2. Khi Giờ Vàng KẾT THÚC (Từ bật -> tắt)
                else if (wasHappyHourActive && !currentlyActive) {
                    wasHappyHourActive = false;
                    broadcastEndMessage();
                }
            }
        }.runTaskTimer(plugin, 100L, 200L); // Kiểm tra mỗi 10 giây
    }

    /**
     * Bắn thông báo BẮT ĐẦU Giờ Vàng rực rỡ toàn server
     */
    public void broadcastStartMessage() {
        Component message = configManager.getHappyHourStartMsg();
        Bukkit.broadcast(message);

        // Phát âm thanh ăn mừng rực rỡ tới tất cả người chơi online
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        plugin.getLogger().info("🎆 [HappyHour] Đã phát thông báo BẮT ĐẦU Giờ Vàng Casino toàn server!");
    }

    /**
     * Bắn thông báo KẾT THÚC Giờ Vàng toàn server
     */
    public void broadcastEndMessage() {
        Component message = configManager.getHappyHourEndMsg();
        Bukkit.broadcast(message);

        // Phát âm thanh thông báo kết thúc
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }

        plugin.getLogger().info("⏰ [HappyHour] Đã phát thông báo KẾT THÚC Giờ Vàng Casino.");
    }

    public boolean isHappyHour() {
        return configManager.isHappyHourActive();
    }

    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
    }
}
