package com.project.paperjackpot.gui;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PersonalHistoryGui - Giao diện Lịch Sử Cược Cá Nhân Có Phân Trang (Pagination 21 items/trang).
 */
public class PersonalHistoryGui {

    public static final Map<UUID, Integer> PLAYER_PAGES = new ConcurrentHashMap<>();
    private static final int ITEMS_PER_PAGE = 21;

    private final PaperJackpot plugin;
    private final MiniMessage mm;

    public PersonalHistoryGui(PaperJackpot plugin) {
        this.plugin = plugin;
        this.mm = plugin.getConfigManager().getMiniMessage();
    }

    public void open(Player player) {
        open(player, 1);
    }

    public void open(Player player, int page) {
        DatabaseManager db = plugin.getDatabaseManager();
        if (db == null) return;

        int totalCount = db.getPlayerHistoryCount(player.getUniqueId());
        int maxPage = Math.max(1, (int) Math.ceil((double) totalCount / ITEMS_PER_PAGE));
        int currentPage = Math.max(1, Math.min(page, maxPage));

        PLAYER_PAGES.put(player.getUniqueId(), currentPage);

        Component title = mm.deserialize("<gradient:#8B0000:#D2143A><bold>📜 LỊCH SỬ CƯỢC CÁ NHÂN (" + currentPage + "/" + maxPage + ")</bold></gradient>");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Viền kính xám đen
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.displayName(mm.deserialize("<dark_gray> "));
            glass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, glass);
        }

        // Lấy danh sách lịch sử CÁ NHÂN của chính người chơi (OFFSET)
        int offset = (currentPage - 1) * ITEMS_PER_PAGE;
        List<String> historyLogs = db.getPlayerHistory(player.getUniqueId(), ITEMS_PER_PAGE, offset);

        // Grid 3 hàng x 7 ô (Slots 10-16, 19-25, 28-34) = 21 ô chuẩn đét
        int[] gridSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        if (historyLogs.isEmpty()) {
            ItemStack emptyItem = new ItemStack(Material.PAPER);
            ItemMeta emptyMeta = emptyItem.getItemMeta();
            if (emptyMeta != null) {
                emptyMeta.displayName(mm.deserialize("<yellow><bold>📜 CHƯA CÓ LỊCH SỬ CƯỢC</bold></yellow>"));
                emptyMeta.lore(List.of(
                        mm.deserialize(""),
                        mm.deserialize("<gray>Bạn chưa thực hiện ván quay Casino nào.</gray>"),
                        mm.deserialize("<gold>👉 Hãy bấm Quay Hũ để bắt đầu tích lũy lịch sử!</gold>"),
                        mm.deserialize("")
                ));
                emptyItem.setItemMeta(emptyMeta);
            }
            gui.setItem(22, emptyItem);
        } else {
            for (int i = 0; i < historyLogs.size() && i < gridSlots.length; i++) {
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
                gui.setItem(gridSlots[i], logItem);
            }
        }

        // --- HÀNG ĐIỀU HƯỚNG PHÂN TRANG (ROW 6: SLOTS 45 - 53) ---

        // Slot 45: Nút Trang Trước
        if (currentPage > 1) {
            ItemStack prev = new ItemStack(Material.PAPER);
            ItemMeta prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.displayName(mm.deserialize("<green><bold>◀ TRANG TRƯỚC (" + (currentPage - 1) + ")</bold></green>"));
                prevMeta.lore(List.of(mm.deserialize("<gray>Bấm để về trang " + (currentPage - 1))));
                prev.setItemMeta(prevMeta);
            }
            gui.setItem(45, prev);
        }

        // Slot 48: Thông tin Trang Hiện Tại
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.displayName(mm.deserialize("<gold><bold>📊 TRANG " + currentPage + " / " + maxPage + "</bold></gold>"));
            infoMeta.lore(List.of(
                    mm.deserialize("<gray>Tổng số ván đã quay: <yellow><bold>" + totalCount + " ván</bold></yellow></gray>")
            ));
            info.setItemMeta(infoMeta);
        }
        gui.setItem(48, info);

        // Slot 49: Nút Quay Về Casino
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.displayName(mm.deserialize("<red><bold>◀ QUAY VỀ CASINO</bold></red>"));
            backMeta.lore(List.of(mm.deserialize("<gray>Trở lại phòng quay hũ cá nhân")));
            back.setItemMeta(backMeta);
        }
        gui.setItem(49, back);

        // Slot 53: Nút Trang Sau
        if (currentPage < maxPage) {
            ItemStack next = new ItemStack(Material.PAPER);
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.displayName(mm.deserialize("<green><bold>TRANG SAU (" + (currentPage + 1) + ") ▶</bold></green>"));
                nextMeta.lore(List.of(mm.deserialize("<gray>Bấm để xem trang " + (currentPage + 1))));
                next.setItemMeta(nextMeta);
            }
            gui.setItem(53, next);
        }

        player.openInventory(gui);
    }
}
