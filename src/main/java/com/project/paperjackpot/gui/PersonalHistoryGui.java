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

import java.util.List;

/**
 * PersonalHistoryGui - Giao diện Xem Lịch Sử 10 Ván Vừa Quay Trực Tiếp Trong GUI.
 */
public class PersonalHistoryGui {

    private final PaperJackpot plugin;
    private final MiniMessage mm;

    public PersonalHistoryGui(PaperJackpot plugin) {
        this.plugin = plugin;
        this.mm = plugin.getConfigManager().getMiniMessage();
    }

    public void open(Player player) {
        Component title = mm.deserialize("<gradient:gold:yellow><bold>📜 LỊCH SỬ CƯỢC CÁ NHÂN</bold></gradient>");
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
        if (db != null) {
            List<String> historyLogs = db.getRecentHistory(10);

            int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21};
            for (int i = 0; i < historyLogs.size() && i < slots.length; i++) {
                String rawLog = historyLogs.get(i);
                boolean isWin = rawLog.contains("THẮNG");
                boolean isJackpot = rawLog.contains("JACKPOT");

                Material icon = isJackpot ? Material.NETHERITE_BLOCK : (isWin ? Material.EMERALD : Material.REDSTONE);
                String name = isJackpot
                        ? "<gradient:#FF0000:#FFD700><bold>🔥 VÁN NỔ HŨ JACKPOT 🔥</bold></gradient>"
                        : (isWin ? "<green><bold>🎉 VÁN THẮNG TỐT</bold></green>" : "<red><bold>✘ VÁN THUA CƯỢC</bold></red>");

                ItemStack logItem = new ItemStack(icon);
                ItemMeta logMeta = logItem.getItemMeta();
                if (logMeta != null) {
                    logMeta.displayName(mm.deserialize(name));
                    logMeta.lore(List.of(
                            mm.deserialize(""),
                            mm.deserialize(" <yellow>" + rawLog + "</yellow>"),
                            mm.deserialize("")
                    ));
                    logItem.setItemMeta(logMeta);
                }
                gui.setItem(slots[i], logItem);
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
