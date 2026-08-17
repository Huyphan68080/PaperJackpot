package com.project.paperjackpot.gui;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.game.GameMode;
import com.project.paperjackpot.manager.ConfigManager;
import com.project.paperjackpot.manager.JackpotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * GameSelectGui - Giao Diện Lựa Chọn 9 Trò Chơi Nổ Hũ Casino 54 ô lộng lẫy khi gõ /jackpot.
 */
public class GameSelectGui {

    private final PaperJackpot plugin;
    private final JackpotManager jackpotManager;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final String titlePlain;

    public GameSelectGui(PaperJackpot plugin) {
        this.plugin = plugin;
        this.jackpotManager = plugin.getJackpotManager();

        Component title = mm.deserialize("<gradient:#8B0000:#D2143A><bold>🎰 CASINO NỔ HŨ JACKPOT</bold></gradient>");
        this.titlePlain = PlainTextComponentSerializer.plainText().serialize(title);
    }

    public void open(Player player) {
        Component title = mm.deserialize("<gradient:#8B0000:#D2143A><bold>🎰 CASINO NỔ HŨ JACKPOT</bold></gradient>");
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Hàng 1 (0-8) & Hàng 6 (45-53): Kính đen trang trí
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, createDecorPane(Material.BLACK_STAINED_GLASS_PANE));
            gui.setItem(45 + i, createDecorPane(Material.BLACK_STAINED_GLASS_PANE));
        }

        // Viền hai bên
        int[] sideSlots = {9, 17, 18, 26, 27, 35, 36, 44};
        for (int s : sideSlots) {
            gui.setItem(s, createDecorPane(Material.RED_STAINED_GLASS_PANE));
        }

        // Slot 13 (Hàng 2 giữa): NỔI BẬT TỔNG QUỸ JACKPOT TÍCH LŨY SERVER
        double pool = jackpotManager.getJackpotPool();
        ItemStack jackpotPoolCard = buildItem(Material.NETHERITE_BLOCK,
                "<gradient:#FF0000:#FFD700><bold>🔥 QUỸ JACKPOT TÍCH LŨY SERVER 🔥</bold></gradient>",
                List.of(
                        "",
                        " <yellow>Tổng số tiền Quỹ Jackpot hiện tại: <gold><bold>" + ConfigManager.formatMoney(pool) + "$</bold></gold>",
                        " <gray>Tất cả tiền thua cược của server",
                        " <gray>đều được tích lũy 100% thẳng vào Quỹ này!",
                        "",
                        " <gradient:#FF0000:#FFD700>🔥 Quay trúng Jackpot = HỐT TRỌN QUỸ JACKPOT!</gradient>"
                ));
        gui.setItem(13, jackpotPoolCard);

        // Hàng 3 (Slots 19, 21, 23, 25): 4 Trò Chơi Slot
        gui.setItem(19, buildGameCard(GameMode.MINERAL_SLOT));
        gui.setItem(21, buildGameCard(GameMode.TAI_XIU_SLOT));
        gui.setItem(23, buildGameCard(GameMode.DIAMOND_FRENZY));
        gui.setItem(25, buildGameCard(GameMode.XOC_DIA_SLOT));

        // Hàng 4 (Slots 28, 30, 32, 34): 4 Trò Chơi Slot Mới
        gui.setItem(28, buildGameCard(GameMode.SWEET_BONANZA));
        gui.setItem(30, buildGameCard(GameMode.DRAGON_TIGER));
        gui.setItem(32, buildGameCard(GameMode.PIRATE_TREASURE));
        gui.setItem(34, buildGameCard(GameMode.GOD_OF_FORTUNE));

        // Hàng 5 (Slot 40): Vòng Quay May Mắn
        gui.setItem(40, buildGameCard(GameMode.WHEEL_OF_FORTUNE));

        // Slot 49: Hướng dẫn
        ItemStack infoCard = buildItem(Material.BOOK,
                "<yellow><bold>ℹ️ HƯỚNG DẪN CHƠI CASINO JACKPOT</bold></yellow>",
                List.of(
                        "",
                        " <gray>1. Chọn 1 trong 9 trò chơi Slot bạn yêu thích ở trên.",
                        " <gray>2. Vào phòng chơi cá nhân, chọn mức cược (1k - 500k).",
                        " <gray>3. Bấm Quay cược. Đặt cược là quay ngay không cần chờ!",
                        " <gray>4. Khi thua cược, tiền thua sẽ được nạp thẳng vào Hũ.",
                        " <gray>5. Quay trúng biểu tượng đặc biệt = Hốt trọn Quỹ Jackpot!",
                        ""
                ));
        gui.setItem(49, infoCard);

        // Slot 53: Đóng Menu
        gui.setItem(53, buildItem(Material.BARRIER, "<red><bold>❌ ĐÓNG MENU</bold></red>", List.of("<gray>Thoát giao diện")));

        player.openInventory(gui);
    }

    private ItemStack buildGameCard(GameMode gm) {
        return buildItem(gm.getIcon(),
                "<gradient:gold:yellow><bold>" + gm.getDisplayName() + "</bold></gradient>",
                gm.getDescription()
        );
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

    private ItemStack createDecorPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize("<dark_gray> "));
            pane.setItemMeta(meta);
        }
        return pane;
    }

    public String getTitlePlain() { return titlePlain; }
}
