package com.project.paperjackpot.gui;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.manager.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * LeaderboardGui - Giao diện Bảng Xếp Hạng Top 10 Đại Gia Thắng Thưởng Casino.
 */
public class LeaderboardGui {

    private final PaperJackpot plugin;
    private final MiniMessage mm;

    public LeaderboardGui(PaperJackpot plugin) {
        this.plugin = plugin;
        this.mm = plugin.getConfigManager().getMiniMessage();
    }

    public void open(Player player) {
        // Tiêu đề màu Đỏ Đô đậm nổi bật trên nền xám GUI container
        Component title = mm.deserialize("<gradient:#8B0000:#D2143A><bold>🏆 BẢNG XẾP HẠNG TOP 10 THẦN TÀI TUẦN</bold></gradient>");
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

        // Đọc Top 10 từ CSDL
        DatabaseManager db = plugin.getDatabaseManager();
        if (db != null) {
            List<DatabaseManager.TopWinnerEntry> topList = db.getTopWinners(10);

            int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21};
            for (int i = 0; i < topList.size() && i < slots.length; i++) {
                DatabaseManager.TopWinnerEntry entry = topList.get(i);
                int rank = i + 1;

                String rankPrefix = switch (rank) {
                    case 1 -> "<gradient:gold:yellow><bold>🥇 TOP 1 TUẦN: ";
                    case 2 -> "<gradient:white:gray><bold>🥈 TOP 2 TUẦN: ";
                    case 3 -> "<gradient:#CD7F32:#8B4513><bold>🥉 TOP 3 TUẦN: ";
                    default -> "<yellow><bold>⭐ TOP " + rank + " TUẦN: ";
                };

                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                if (skullMeta != null) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(entry.name());
                    skullMeta.setOwningPlayer(target);
                    skullMeta.displayName(mm.deserialize(rankPrefix + entry.name() + "</bold></gradient>"));
                    skullMeta.lore(List.of(
                            mm.deserialize(""),
                            mm.deserialize(" <yellow>Tổng thưởng đã thắng: <gold><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></gold>"),
                            mm.deserialize(" <gray>Số lượt thắng: <green>" + entry.winCount() + " ván</green>"),
                            mm.deserialize("")
                    ));
                    skull.setItemMeta(skullMeta);
                }
                gui.setItem(slots[i], skull);
            }
        }

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
}
