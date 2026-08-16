package com.project.paperjackpot.gui;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.manager.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * PlayerStatsGui - Giao diện Bảng Thống Kê May Mắn Cá Nhân (ROI, Winrate, Lợi nhuận).
 */
public class PlayerStatsGui {

    private final PaperJackpot plugin;
    private final MiniMessage mm;

    public PlayerStatsGui(PaperJackpot plugin) {
        this.plugin = plugin;
        this.mm = plugin.getConfigManager().getMiniMessage();
    }

    public void open(Player player) {
        // Tiêu đề ngắn gọn không bị đè nút JEI
        Component title = mm.deserialize("<gradient:gold:yellow><bold>📈 THỐNG KÊ CÁ NHÂN</bold></gradient>");
        Inventory gui = Bukkit.createInventory(null, 27, title);

        // Viền kính xám
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize("<dark_gray> "));
            glass.setItemMeta(meta);
        }
        for (int i = 0; i < 27; i++) {
            gui.setItem(i, glass);
        }

        DatabaseManager db = plugin.getDatabaseManager();
        DatabaseManager.PlayerStatsEntry stats = db != null ? db.getPlayerStats(player.getUniqueId()) : new DatabaseManager.PlayerStatsEntry(0, 0, 0, 0, 0, 0, 0);

        // Slot 4: Head Player
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skullMeta.displayName(mm.deserialize("<yellow><bold>👤 HỒ SƠ CASINO: " + player.getName() + "</bold></yellow>"));
            skullMeta.lore(List.of(
                    mm.deserialize(""),
                    mm.deserialize(" <gray>Thống kê được cập nhật tự động"),
                    mm.deserialize(" <gray>từ hệ thống CSDL Database!"),
                    mm.deserialize("")
            ));
            skull.setItemMeta(skullMeta);
        }
        gui.setItem(4, skull);

        // Slot 11: 🎯 Ván & Tỉ Lệ Thắng
        ItemStack spinItem = buildItem(Material.COMPASS,
                "<gold><bold>🎯 VÁN QUAY & TỶ LỆ THẮNG</bold></gold>",
                List.of(
                        "",
                        " <gray>Tổng số lượt đã quay: <yellow><bold>" + stats.totalSpins() + " lượt</bold></yellow>",
                        " <gray>Số ván đã thắng: <green><bold>" + stats.totalWins() + " ván</bold></green>",
                        " <gray>Tỷ lệ thắng (Winrate): <gold><bold>" + String.format("%.2f", stats.winRate()) + "%</bold></gold>",
                        ""
                ));
        gui.setItem(11, spinItem);

        // Slot 13: 💰 Tiền Cược & Lợi Nhuận
        String profitColor = stats.profit() >= 0 ? "<green>" : "<red>";
        ItemStack moneyItem = buildItem(Material.GOLD_BLOCK,
                "<gradient:gold:yellow><bold>💰 TIỀN CƯỢC & LỢI NHUẬN</bold></gradient>",
                List.of(
                        "",
                        " <gray>Tổng tiền đã nạp cược: <yellow>" + ConfigManager.formatMoney(stats.totalWagered()) + "$</yellow>",
                        " <gray>Tổng tiền đã thắng nhận: <gold>" + ConfigManager.formatMoney(stats.totalWon()) + "$</gold>",
                        " <gray>Lợi nhuận ròng (Net Profit): " + profitColor + "<bold>" + (stats.profit() >= 0 ? "+" : "") + ConfigManager.formatMoney(stats.profit()) + "$</bold>" + profitColor + "</gray>",
                        ""
                ));
        gui.setItem(13, moneyItem);

        // Slot 15: 📊 ROI % (Hiệu Suất Đầu Tư)
        ItemStack roiItem = buildItem(Material.EMERALD_BLOCK,
                "<gradient:#00AA00:#55FF55><bold>📊 HIỆU SUẤT ROI (%)</bold></gradient>",
                List.of(
                        "",
                        " <gray>Chỉ số ROI (Return on Investment):",
                        " " + profitColor + "<bold>" + String.format("%.2f", stats.roi()) + "%</bold>" + profitColor,
                        "",
                        stats.roi() >= 0 ? " <green>★ Đang sinh lời tốt!</green>" : " <red>★ Đang lỗ nhẹ, chúc bạn may mắn!</red>",
                        ""
                ));
        gui.setItem(15, roiItem);

        // Slot 22: Nút Quay Về
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.displayName(mm.deserialize("<red><bold>◀ QUAY VỀ CASINO</bold></red>"));
            backMeta.lore(List.of(mm.deserialize("<gray>Trở lại phòng quay hũ cá nhân")));
            back.setItemMeta(backMeta);
        }
        gui.setItem(22, back);

        player.openInventory(gui);
    }

    private ItemStack buildItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize(name));
            meta.lore(lore.stream().map(mm::deserialize).toList());
            item.setItemMeta(meta);
        }
        return item;
    }
}
