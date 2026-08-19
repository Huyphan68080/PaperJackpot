package com.project.paperjackpot.listener;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.game.GameMode;
import com.project.paperjackpot.gui.LeaderboardGui;
import com.project.paperjackpot.gui.PersonalHistoryGui;
import com.project.paperjackpot.gui.PlayerStatsGui;
import com.project.paperjackpot.manager.ConfigManager;
import com.project.paperjackpot.session.SoloSlotSession;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MenuListener - Lắng nghe tương tác GUI Casino & BẢO VỆ CHỐNG DUPE / EXPLOIT VÉ 100%.
 */
public class MenuListener implements Listener {

    private final PaperJackpot plugin;
    private final ConcurrentHashMap<UUID, Long> redeemCooldowns = new ConcurrentHashMap<>();

    public MenuListener(PaperJackpot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getJackpotManager() != null) {
            plugin.getJackpotManager().showBossBarToPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 1. Khóa 100% việc double-trigger giữa tay chính và tay phụ (Off-hand)
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        ConfigManager cfg = plugin.getConfigManager();
        DatabaseManager db = plugin.getDatabaseManager();
        if (cfg == null || db == null) return;

        boolean isVip = cfg.isVipTicketItem(item);
        boolean isNormal = cfg.isTicketItem(item);
        if (!isVip && !isNormal) return;

        event.setCancelled(true);

        // 2. Cooldown chống Macro / Auto-clicker Spam tay Nạp Vé (Giới hạn 250ms)
        long now = System.currentTimeMillis();
        long lastRedeem = redeemCooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now - lastRedeem < 250L) {
            return;
        }
        redeemCooldowns.put(player.getUniqueId(), now);

        // 3. Kiểm tra Mã Định Danh Serial NBT UUID - Chống Dupe Vật Phẩm Bằng Mọi Giá!
        String serialUuid = cfg.getTicketSerialUuid(item);
        if (serialUuid != null && !serialUuid.isEmpty()) {
            if (db.isTicketSerialRedeemed(serialUuid)) {
                // Tấm vé này đã được nạp trước đó -> TIÊU HỦY VẬT PHẨM DUPE NGAY TỨC THÌ!
                item.setAmount(0);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
                player.sendMessage(cfg.getMiniMessage().deserialize(
                        "<red><bold>❌ CẢNH BÁO ANTI-DUPE:</bold> Tấm vé này mang mã Serial UUID đã được sử dụng từ trước! Đã tiêu hủy vật phẩm trùng lặp.</red>"
                ));
                return;
            }
            // Đánh dấu mã Serial UUID vừa nạp vào CSDL
            db.markTicketSerialRedeemed(serialUuid, player.getUniqueId());
        }

        // 4. Trừ vật phẩm trên tay trước một cách Atomic
        int amount = item.getAmount();
        item.setAmount(0);

        // 5. Nạp tiền vé vào CSDL cá nhân
        if (isVip) {
            db.addVipTickets(player.getUniqueId(), amount);
            int newTotal = db.getVipTickets(player.getUniqueId());

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            player.sendMessage(cfg.getMiniMessage().deserialize(
                    "<gradient:#FF0000:#FFD700><bold>🎫 [VÉ VIP HIGHROLLER 500K$]</bold></gradient> <green>Đã nạp thành công <gold><bold>+" + amount + "x Vé VIP (500k$)</bold></gold> vào tài khoản! (Tổng số dư: <gold><bold>" + newTotal + " vé VIP</bold></gold>)</green>"
            ));
        } else {
            db.addTickets(player.getUniqueId(), amount);
            int newTotal = db.getTickets(player.getUniqueId());

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
            player.sendMessage(cfg.getMiniMessage().deserialize(
                    "<gradient:#FFD700:#FFA500><bold>🎟️ [VÉ QUAY CASINO]</bold></gradient> <green>Đã nạp thành công <gold><bold>+" + amount + "x Vé Quay Thường (1k-100k)</bold></gold> vào tài khoản! (Tổng số dư: <gold><bold>" + newTotal + " vé</bold></gold>)</green>"
            ));
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

            // Khóa tuyệt đối Shift-click, phím số (1-9), tráo Off-hand, Double click
            ClickType click = event.getClick();
            if (click == ClickType.NUMBER_KEY || click == ClickType.SWAP_OFFHAND || click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT || click == ClickType.DOUBLE_CLICK) {
                return;
            }

            if (event.getClickedInventory().equals(event.getInventory())) {
                int slot = event.getSlot();

                switch (slot) {
                    case 36 -> new PersonalHistoryGui(plugin).open(player); // 📜 LỊCH SỬ CƯỢC CÁ NHÂN
                    case 38 -> session.cyclePaymentMode();                   // 💳 CHỌN CHẾ ĐỘ THANH TOÁN (TIỀN VAULT / VÉ THƯỜNG / VÉ VIP)
                    case 40 -> session.toggleAutoSpin();                     // ⚡ BẬT / TẮT QUAY TỰ ĐỘNG (AUTO)
                    case 41 -> new LeaderboardGui(plugin).open(player);       // 🏆 TOP 10 BẢNG XẾP HẠNG
                    case 42 -> new PlayerStatsGui(plugin).open(player);        // 📈 THỐNG KÊ CÁ NHÂN
                    case 43 -> session.claimDailyFreeSpin();                   // 🎁 QUAY MIỄN PHÍ HẰNG NGÀY
                    case 44 -> player.closeInventory();                        // 🚪 THOÁT GAME
                    case 45 -> session.setBetAmount(1000);
                    case 46 -> session.setBetAmount(10000);
                    case 47 -> session.setBetAmount(100000);
                    case 48 -> session.setBetAmount(500000);
                    case 49 -> session.executeSpin();                        // 🎰 BẤM QUAY CƯỢC NGAY TỨC THÌ
                }
            }
            return;
        }

        // 2. Tương tác Bảng Xếp Hạng, Thống Kê, Lịch Sử GUI -> Khóa chặt tuyệt đối 100%
        if (titlePlain.contains("CASINO") || titlePlain.contains("BẢNG XẾP HẠNG") || titlePlain.contains("THỐNG KÊ") || titlePlain.contains("LỊCH SỬ")) {
            event.setCancelled(true);

            ClickType click = event.getClick();
            if (click == ClickType.NUMBER_KEY || click == ClickType.SWAP_OFFHAND || click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT || click == ClickType.DOUBLE_CLICK) {
                return;
            }

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
