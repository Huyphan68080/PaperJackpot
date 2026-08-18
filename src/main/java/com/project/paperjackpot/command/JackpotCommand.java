package com.project.paperjackpot.command;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.game.GameMode;
import com.project.paperjackpot.gui.GameSelectGui;
import com.project.paperjackpot.gui.LeaderboardGui;
import com.project.paperjackpot.gui.PlayerStatsGui;
import com.project.paperjackpot.manager.ConfigManager;
import com.project.paperjackpot.manager.JackpotManager;
import com.project.paperjackpot.session.SoloSlotSession;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JackpotCommand - Lệnh chính /jackpot (hoặc /jp, /quayhu).
 * Hỗ trợ các sub-command:
 *   - /jackpot top (Xem Bảng Xếp Hạng Top 10 Thần Tài Casino)
 *   - /jackpot stats (Xem Thống Kê May Mắn Cá Nhân)
 *   - /jackpot time (Kiểm tra múi giờ Việt Nam & thời gian Giờ Vàng)
 *   - /jackpot testhappyhour (Phát thử thông báo & âm thanh Giờ Vàng)
 *   - /jackpot test (Kích hoạt 100% Nổ Hũ Jackpot lượt cược kế tiếp)
 *   - /jackpot setpool <số tiền> (Cài đặt Quỹ Jackpot Server)
 *   - /jackpot reload (Reload cấu hình)
 *   - /jackpot history (Xem lịch sử CSDL)
 */
public class JackpotCommand implements CommandExecutor {

    private final PaperJackpot plugin;
    private final ConfigManager configManager;
    private final JackpotManager jackpotManager;
    private final DatabaseManager databaseManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    public JackpotCommand(PaperJackpot plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.jackpotManager = plugin.getJackpotManager();
        this.databaseManager = plugin.getDatabaseManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            String subCmd = args[0].toLowerCase();

            // 1. LỆNH XEM TOP BẢNG XẾP HẠNG: /jackpot top
            if (subCmd.equals("top") || subCmd.equals("leaderboard")) {
                if (sender instanceof Player player) {
                    new LeaderboardGui(plugin).open(player);
                } else {
                    sender.sendMessage(mm.deserialize("<red>Chỉ người chơi trong game mới dùng được lệnh này!</red>"));
                }
                return true;
            }

            // 2. LỆNH XEM THỐNG KÊ CÁ NHÂN: /jackpot stats
            if (subCmd.equals("stats") || subCmd.equals("me") || subCmd.equals("thongke")) {
                if (sender instanceof Player player) {
                    new PlayerStatsGui(plugin).open(player);
                } else {
                    sender.sendMessage(mm.deserialize("<red>Chỉ người chơi trong game mới dùng được lệnh này!</red>"));
                }
                return true;
            }

            // 3. LỆNH KIỂM TRA MÚI GIỜ VIỆT NAM & THỜI GIAN GIỜ VÀNG: /jackpot time HOẶC /jackpot testtime
            if (subCmd.equals("time") || subCmd.equals("testtime") || subCmd.equals("clock")) {
                sendTimeInfo(sender);
                return true;
            }

            // 4. LỆNH BẮN THỬ THÔNG BÁO GIỜ VÀNG: /jackpot testhappyhour HOẶC /jackpot testhh
            if (subCmd.equals("testhappyhour") || subCmd.equals("testhh") || subCmd.equals("triggerhappyhour")) {
                if (!hasAdminPermission(sender)) {
                    sender.sendMessage(configManager.getNoPermissionMsg());
                    return true;
                }
                if (plugin.getHappyHourManager() != null) {
                    plugin.getHappyHourManager().broadcastStartMessage();
                    sender.sendMessage(mm.deserialize("<green>✅ Đã phát thử tin nhắn & âm thanh thông báo BẮT ĐẦU Giờ Vàng Casino thành công!</green>"));
                }
                return true;
            }

            // 5. LỆNH TEST NỔ HŨ: /jackpot test HOẶC /jackpot testjackpot HOẶC /jackpot testwin
            if (subCmd.equals("testjackpot") || subCmd.equals("test") || subCmd.equals("testwin")) {
                if (!hasAdminPermission(sender)) {
                    sender.sendMessage(configManager.getNoPermissionMsg());
                    return true;
                }
                handleTestJackpot(sender);
                return true;
            }

            // 6. RELOAD CONFIG: /jackpot reload HOẶC /jackpot rl
            if (subCmd.equals("reload") || subCmd.equals("rl")) {
                if (!hasAdminPermission(sender)) {
                    sender.sendMessage(configManager.getNoPermissionMsg());
                    return true;
                }
                configManager.loadConfig();
                sender.sendMessage(configManager.getReloadSuccessMsg());
                return true;
            }

            // 7. XEM LỊCH SỬ CSDL: /jackpot history HOẶC /jackpot log
            if (subCmd.equals("history") || subCmd.equals("log")) {
                if (!hasAdminPermission(sender)) {
                    sender.sendMessage(configManager.getNoPermissionMsg());
                    return true;
                }
                sendHistoryLog(sender);
                return true;
            }

            // 8. LỆNH SET QUỸ JACKPOT TÍCH LŨY: /jackpot setpool <số tiền>
            if (subCmd.equals("setpool") || subCmd.equals("sethuff")) {
                if (!hasAdminPermission(sender)) {
                    sender.sendMessage(configManager.getNoPermissionMsg());
                    return true;
                }
                if (args.length > 1) {
                    try {
                        double amount = Double.parseDouble(args[1]);
                        jackpotManager.setJackpotPool(amount);
                        sender.sendMessage(mm.deserialize(
                                "<gradient:#FF0000:#FFD700><bold>🔥 [SET QUỸ JACKPOT]</bold></gradient> <green>Đã thiết lập Quỹ Jackpot Tích Lũy Server thành: <gold><bold>" + ConfigManager.formatMoney(amount) + "$</bold></gold>!</green>"
                        ));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(mm.deserialize("<red>Số tiền không hợp lệ! Cú pháp: /jackpot setpool <số tiền></red>"));
                    }
                } else {
                    sender.sendMessage(mm.deserialize("<red>Cú pháp: /jackpot setpool <số tiền></red>"));
                }
                return true;
            }

            sendHelpMenu(sender);
            return true;
        }

        // Vào thẳng phòng Quay Hũ Nổ Hũ cá nhân
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize("<red>Chỉ người chơi trong game mới có thể chơi Nổ Hũ Jackpot!</red>"));
            return true;
        }

        SoloSlotSession session = plugin.getOrCreateSession(player, GameMode.MINERAL_SLOT);
        session.open(true);
        return true;
    }

    private void sendTimeInfo(CommandSender sender) {
        ZonedDateTime vnNow = configManager.getNowInTimezone();
        boolean isActive = configManager.isHappyHourActive();
        int startHour = configManager.getHappyHourStart();
        int endHour = configManager.getHappyHourEnd();

        String statusStr = isActive
                ? "<gradient:#FF0000:#FFD700><bold>🔥 ĐANG DIỄN RA (TỶ LỆ NỔ HŨ X2)</bold></gradient>"
                : "<red><bold>❌ CHƯA ĐẾN GIỜ (Khung " + startHour + ":00 - " + endHour + ":00)</bold></red>";

        sender.sendMessage(mm.deserialize("\n<gradient:gold:yellow><bold>⏰ [KIỂM TRA MÚI GIỜ SERVER & GIỜ VÀNG] ⏰</bold></gradient>"));
        sender.sendMessage(mm.deserialize(" <gray>• Múi Giờ Cấu Hình: <gold><bold>" + configManager.getTimezone().getId() + " (UTC+7 - Việt Nam)</bold></gold></gray>"));
        sender.sendMessage(mm.deserialize(" <gray>• Giờ Việt Nam Hiện Tại: <gold><bold>" + vnNow.format(TIME_FORMATTER) + "</bold></gold></gray>"));
        sender.sendMessage(mm.deserialize(" <gray>• Khung Giờ Vàng Casino: <yellow><bold>" + startHour + ":00 - " + endHour + ":00 hằng ngày</bold></yellow></gray>"));
        sender.sendMessage(mm.deserialize(" <gray>• Trạng Thái Sự Kiện: " + statusStr + "</gray>\n"));
    }

    private void handleTestJackpot(CommandSender sender) {
        if (sender instanceof Player player) {
            SoloSlotSession session = plugin.getSession(player);
            if (session == null) {
                session = plugin.getOrCreateSession(player, GameMode.MINERAL_SLOT);
                session.open();
            }
            session.setForceNextJackpot(true);
            player.sendMessage(mm.deserialize(
                    "\n<gradient:#FF0000:#FFD700><bold>🔥 [TEST JACKPOT CẢM ỨNG NỔ HŨ] 🔥</bold></gradient>\n" +
                            "<green>✅ Đã kích hoạt 100% NỔ HŨ <gradient:#FF0000:#FFD700><bold>3x KHỐI NETHERITE (NTR)</bold></gradient> HỐT SẠCH QUỸ HŨ cho lượt quay tiếp theo của bạn!</green>\n" +
                            "<yellow>👉 Nhấp nút 🎰 BẤM QUAY trong GUI để xem hiệu ứng Nổ Hũ!</yellow>\n"
            ));
            return;
        }
        sender.sendMessage(mm.deserialize("<yellow>Chỉ người chơi trong game mới dùng được lệnh test nổ hũ!</yellow>"));
    }

    private void sendHistoryLog(CommandSender sender) {
        sender.sendMessage(mm.deserialize("<gradient:gold:yellow><bold>========= LỊCH SỬ QUAY HŨ CSDL DATABASE =========</bold></gradient>"));
        if (databaseManager != null) {
            List<String> logs = databaseManager.getRecentHistory(10);
            if (logs.isEmpty()) {
                sender.sendMessage(mm.deserialize("<gray>Chưa có lịch sử quay cược nào trong CSDL database.</gray>"));
            } else {
                for (String log : logs) {
                    sender.sendMessage(mm.deserialize("<yellow>" + log + "</yellow>"));
                }
            }
        } else {
            sender.sendMessage(mm.deserialize("<red>CSDL Database chưa được khởi tạo.</red>"));
        }
        sender.sendMessage(mm.deserialize("<gold><bold>===============================================</bold></gold>"));
    }

    private void sendHelpMenu(CommandSender sender) {
        sender.sendMessage(mm.deserialize("<gradient:gold:yellow><bold>🎰 PAPERJACKPOT COMMAND HELP 🎰</bold></gradient>"));
        sender.sendMessage(mm.deserialize(" <yellow>/jackpot</yellow> <gray>(hoặc <yellow>/jp</yellow>) - Vào phòng Quay Hũ Jackpot Cá Nhân</gray>"));
        sender.sendMessage(mm.deserialize(" <gold>/jackpot top</gold> - Xem Bảng Xếp Hạng Top 10 Thần Tài Casino"));
        sender.sendMessage(mm.deserialize(" <gold>/jackpot stats</gold> - Xem Bảng Thống Kê May Mắn Cá Nhân"));
        sender.sendMessage(mm.deserialize(" <gold>/jackpot time</gold> - Kiểm tra múi giờ Việt Nam UTC+7 & Giờ Vàng"));
        if (hasAdminPermission(sender)) {
            sender.sendMessage(mm.deserialize(" <gold>/jackpot testhappyhour</gold> - Bắn thử thông báo & âm thanh Giờ Vàng"));
            sender.sendMessage(mm.deserialize(" <gold>/jackpot test</gold> - Kích hoạt 100% Nổ Hũ Hốt Sạch Hũ lượt quay tới"));
            sender.sendMessage(mm.deserialize(" <gold>/jackpot setpool <tiền></gold> - Thao tác thiết lập Quỹ Hũ Tích Lũy"));
            sender.sendMessage(mm.deserialize(" <gold>/jackpot reload</gold> - Reload cấu hình"));
            sender.sendMessage(mm.deserialize(" <gold>/jackpot history</gold> - Xem lịch sử cược CSDL"));
        }
    }

    private boolean hasAdminPermission(CommandSender sender) {
        if (sender.isOp()) return true;
        return sender.hasPermission("paperjackpot.admin") || sender.hasPermission("paperjackpot.testjackpot") || sender.hasPermission("paperjackpot.*");
    }
}
