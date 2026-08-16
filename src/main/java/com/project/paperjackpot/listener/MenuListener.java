package com.project.paperjackpot.listener;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.game.GameMode;
import com.project.paperjackpot.gui.LeaderboardGui;
import com.project.paperjackpot.gui.PersonalHistoryGui;
import com.project.paperjackpot.gui.PlayerStatsGui;
import com.project.paperjackpot.session.SoloSlotSession;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * MenuListener - Lắng nghe tương tác Click GUI Casino, Leaderboard, Thống Kê, Lịch Sử & Auto Spin.
 */
public class MenuListener implements Listener {

    private final PaperJackpot plugin;

    public MenuListener(PaperJackpot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getJackpotManager() != null) {
            plugin.getJackpotManager().showBossBarToPlayer(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String titlePlain = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        // 1. Tương tác GUI Casino Quay Hũ
        SoloSlotSession session = plugin.getSession(player);
        if (session != null && event.getInventory().equals(session.getGui())) {
            event.setCancelled(true);

            if (event.getClickedInventory().equals(event.getInventory())) {
                int slot = event.getSlot();

                switch (slot) {
                    case 42 -> new PersonalHistoryGui(plugin).open(player); // 📜 LỊCH SỬ CƯỢC CÁ NHÂN
                    case 43 -> session.toggleAutoSpin();                     // ⚡ BẬT / TẮT QUAY TỰ ĐỘNG (AUTO)
                    case 45 -> session.setBetAmount(1000);
                    case 46 -> session.setBetAmount(10000);
                    case 47 -> session.setBetAmount(100000);
                    case 48 -> session.setBetAmount(500000);
                    case 49 -> session.executeSpin();                        // BẤM QUAY CƯỢC NGAY TỨC THÌ
                    case 50 -> new LeaderboardGui(plugin).open(player);       // 🏆 TOP 10 BẢNG XẾP HẠNG
                    case 51 -> new PlayerStatsGui(plugin).open(player);        // 📈 THỐNG KÊ CÁ NHÂN
                    case 52 -> session.claimDailyFreeSpin();                   // 🎁 QUAY MIỄN PHÍ HẰNG NGÀY
                    case 53 -> player.closeInventory();                        // 🚪 THOÁT GAME
                }
            }
            return;
        }

        // 2. Tương tác Bảng Xếp Hạng, Thống Kê, Lịch Sử GUI -> Khóa chặt tuyệt đối 100%
        if (titlePlain.contains("CASINO") || titlePlain.contains("BẢNG XẾP HẠNG") || titlePlain.contains("THỐNG KÊ") || titlePlain.contains("LỊCH SỬ")) {
            event.setCancelled(true);

            if (event.getClickedInventory().equals(event.getInventory())) {
                if (event.getSlot() == 22) {
                    SoloSlotSession slotSession = plugin.getOrCreateSession(player, GameMode.MINERAL_SLOT);
                    slotSession.open(false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        String titlePlain = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (titlePlain.contains("CASINO") || titlePlain.contains("BẢNG XẾP HẠNG") || titlePlain.contains("THỐNG KÊ") || titlePlain.contains("LỊCH SỬ")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        SoloSlotSession session = plugin.getSession(player);
        if (session != null && event.getInventory().equals(session.getGui())) {
            session.close();
        }
    }
}
