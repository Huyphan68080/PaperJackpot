package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.PluginManager;

/**
 * HologramManager - Quản lý tích hợp Hologram Bảng Xếp Hạng Top 10 qua FancyHolograms & DecentHolograms.
 * Loại bỏ hoàn toàn việc spawn Entity Native trực tiếp để không gây đơ/lag server!
 */
public class HologramManager {

    private final PaperJackpot plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public HologramManager(PaperJackpot plugin) {
        this.plugin = plugin;
        cleanOldNativeEntities();
    }

    /**
     * Dọn dẹp tất cả Entity TextDisplay cũ của plugin nếu còn sót lại trong thế giới để loại bỏ đơ/lag server.
     */
    public void cleanOldNativeEntities() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            int removed = 0;
            for (org.bukkit.World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof TextDisplay textDisplay) {
                        if (textDisplay.getScoreboardTags().contains("paperjackpot_hologram")) {
                            textDisplay.remove();
                            removed++;
                        }
                    }
                }
            }
            if (removed > 0) {
                plugin.getLogger().info("[Hologram] Đã dọn dẹp sạch " + removed + " Hologram Entity Native cũ để tối ưu mượt server!");
            }
        });
    }

    /**
     * Tự động tạo Hologram Bảng Xếp Hạng Top 10 tại vị trí Admin qua FancyHolograms hoặc DecentHolograms.
     */
    public void createHologramWithPlugin(Player player) {
        PluginManager pm = Bukkit.getPluginManager();
        boolean hasFancy = pm.getPlugin("FancyHolograms") != null;
        boolean hasDecent = pm.getPlugin("DecentHolograms") != null;
        boolean hasPapi = pm.getPlugin("PlaceholderAPI") != null;

        if (!hasPapi) {
            player.sendMessage(mm.deserialize("<red>❌ Cần cài đặt plugin <gold>PlaceholderAPI</gold> để hiển thị Hologram Top 10 mượt mà!</red>"));
            return;
        }

        Location loc = player.getLocation();
        String world = loc.getWorld().getName();
        String x = String.format("%.2f", loc.getX());
        String y = String.format("%.2f", loc.getY() + 1.2);
        String z = String.format("%.2f", loc.getZ());

        // 1. Tự động tương thích với FancyHolograms (Khuyên dùng - Siêu mượt Packet-based 0-lag)
        if (hasFancy) {
            cleanOldNativeEntities();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram remove top_casino");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram create top_casino text");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram move top_casino " + world + " " + x + " " + y + " " + z);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino setBillboard center");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino setShadow true");

            // Thêm các dòng Placeholder
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino addLine %paperjackpot_top_header%");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino addLine ");
            for (int i = 1; i <= 10; i++) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino addLine %paperjackpot_top_line_" + i + "%");
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino addLine ");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram edit top_casino addLine %paperjackpot_top_footer%");

            player.sendMessage(mm.deserialize(
                    "\n<gradient:#FFD700:#FFA500><bold>✨ [FANCY HOLOGRAMS INTEGRATION]</bold></gradient>\n" +
                            "<green>✅ Đã tạo Bảng Xếp Hạng Top 10 siêu mượt 0-lag qua <gold><bold>FancyHolograms</bold></gold> tại vị trí của bạn!</green>\n" +
                            "<yellow>👉 Hologram tự động cập nhật real-time qua PlaceholderAPI.</yellow>\n"
            ));
            return;
        }

        // 2. Tự động tương thích với DecentHolograms
        if (hasDecent) {
            cleanOldNativeEntities();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh remove top_casino");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh create top_casino " + world + " " + x + " " + y + " " + z + " &e&l✦ &6&lTOP ĐẠI GIA CASINO NỔ HŨ &e&l✦");
            for (int i = 1; i <= 10; i++) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh line add top_casino %paperjackpot_top_line_legacy_" + i + "%");
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh line add top_casino &e&o👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!");

            player.sendMessage(mm.deserialize(
                    "\n<gradient:#FFD700:#FFA500><bold>✨ [DECENT HOLOGRAMS INTEGRATION]</bold></gradient>\n" +
                            "<green>✅ Đã tạo Bảng Xếp Hạng Top 10 qua <gold><bold>DecentHolograms</bold></gold> tại vị trí của bạn!</green>\n" +
                            "<yellow>👉 Hologram tự động cập nhật real-time qua PlaceholderAPI.</yellow>\n"
            ));
            return;
        }

        // 3. Nếu chưa cài FancyHolograms hay DecentHolograms
        player.sendMessage(mm.deserialize(
                "\n<gradient:gold:yellow><bold>⚠️ [HƯỚNG DẪN TẠO HOLOGRAM SIÊU MƯỢT 0-LAG]</bold></gradient>\n" +
                        "<yellow>Để Bảng Xếp Hạng Top 10 hiển thị mượt mà không gây đơ/lag server, vui lòng cài đặt:</yellow>\n" +
                        " <gray>1. Plugin <gold><bold>FancyHolograms</bold></gold> (hoặc <gold><bold>DecentHolograms</bold></gold>)</gray>\n" +
                        " <gray>2. Plugin <gold><bold>PlaceholderAPI</bold></gold></gray>\n" +
                        "<green>👉 Sau khi cài đặt, gõ lại lệnh <gold><bold>/jackpot sethologram</bold></gold> để tự động tạo bảng mượt 100%!</green>\n"
        ));
    }

    public void removeHologramWithPlugin(Player player) {
        PluginManager pm = Bukkit.getPluginManager();
        boolean hasFancy = pm.getPlugin("FancyHolograms") != null;
        boolean hasDecent = pm.getPlugin("DecentHolograms") != null;

        if (hasFancy) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hologram remove top_casino");
        }
        if (hasDecent) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dh remove top_casino");
        }
        cleanOldNativeEntities();

        player.sendMessage(mm.deserialize("<green>✅ Đã xóa Bảng Xếp Hạng Hologram Top 10 khỏi thế giới!</green>"));
    }

    public void stop() {
        cleanOldNativeEntities();
    }
}
