package com.project.paperjackpot.session;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.game.GameMode;
import com.project.paperjackpot.manager.ConfigManager;
import com.project.paperjackpot.manager.JackpotManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SoloSlotSession - Quản lý phiên chơi Quay Hũ Nổ Hũ 3x3 Cá Nhân (Single Player Instance).
 * Layout GUI mới cực kỳ cân đối & đẹp mắt:
 * Row 5 (Slots 36-44): Lịch Sử | Ví Vé Thường | CHỌN NGUỒN TIỀN | Ví Vé VIP | Auto Spin | Top 10 | Thống Kê | Quà Ngày | Thoát
 * Row 6 (Slots 45-53): 1k | 10k | 100k | 500k (Barrier nếu Vé Thường) | BẤM QUAY (Nether Star) | Khung Viền Đen
 */
public class SoloSlotSession {

    public enum PaymentMode {
        VAULT_MONEY,
        STANDARD_TICKET,
        VIP_TICKET
    }

    private static final Material[] SYMBOLS = {
            Material.COAL_BLOCK,
            Material.IRON_BLOCK,
            Material.GOLD_BLOCK,
            Material.REDSTONE_BLOCK,
            Material.LAPIS_BLOCK,
            Material.EMERALD_BLOCK,
            Material.DIAMOND_BLOCK,
            Material.NETHERITE_BLOCK
    };

    private final PaperJackpot plugin;
    private final ConfigManager configManager;
    private final JackpotManager jackpotManager;
    private final DatabaseManager databaseManager;
    private final MiniMessage mm;

    private final Player player;
    private final GameMode gameMode;
    private final Inventory gui;
    private final String guiTitlePlain;

    private boolean isSpinning = false;
    private boolean forceNextJackpot = false;
    private boolean isUsingTicket = false;

    // Chế độ thanh toán nguồn tiền (Tiền Vault $, Vé Thường, Vé VIP)
    private PaymentMode paymentMode = PaymentMode.VAULT_MONEY;

    // Tính năng Auto Spin & Streak Bonus
    private boolean isAutoSpinning = false;
    private BukkitTask autoSpinTask = null;
    private int streakCount = 0;

    private double currentBetAmount = 1000.0; // Mức cược mặc định 1k
    private String lastResultText = "<gray>Chưa có lượt chơi nào</gray>";

    public SoloSlotSession(PaperJackpot plugin, Player player, GameMode gameMode) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.jackpotManager = plugin.getJackpotManager();
        this.databaseManager = plugin.getDatabaseManager();
        this.mm = configManager.getMiniMessage();
        this.player = player;
        this.gameMode = gameMode;

        Component title = mm.deserialize(configManager.getGuiTitle());
        this.gui = Bukkit.createInventory(null, 54, title);
        this.guiTitlePlain = PlainTextComponentSerializer.plainText().serialize(title);

        setupGuiFrame();
    }

    public void open() {
        open(true);
    }

    public void open(boolean sendWelcomeMessage) {
        player.openInventory(gui);
        jackpotManager.showBossBarToPlayer(player);
        updateJackpotHUD();

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);

        if (sendWelcomeMessage) {
            player.sendMessage(configManager.getWelcomeDealerMsg(player.getName()));
        }
    }

    public void close() {
        stopAutoSpin();
        plugin.removeSession(player);
    }

    public void setupGuiFrame() {
        // Viền khung đen hàng 1 và viền ô trống hàng 6
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, createDecorPane(Material.BLACK_STAINED_GLASS_PANE));
        }
        for (int i = 50; i <= 53; i++) {
            gui.setItem(i, createDecorPane(Material.BLACK_STAINED_GLASS_PANE));
        }

        int[] sideSlots = {
                9, 10, 11, 15, 16, 17,
                18, 19, 25, 26,
                27, 28, 29, 33, 34, 35
        };
        for (int slot : sideSlots) {
            gui.setItem(slot, createDecorPane(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Slot 4: QUỸ JACKPOT TÍCH LŨY SERVER
        updateJackpotPoolItem();

        // Đèn mũi tên chỉ hàng thưởng (Slot 20 & 24)
        gui.setItem(20, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>▶ HÀNG THƯỞNG ◀</bold></green>", List.of()));
        gui.setItem(24, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>▶ HÀNG THƯỞNG ◀</bold></green>", List.of()));

        // Ma trận Quay 3x3 (Slots 12, 13, 14, 21, 22, 23, 30, 31, 32)
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int[] reelSlots = {12, 13, 14, 21, 22, 23, 30, 31, 32};
        for (int s : reelSlots) {
            Material randomMat = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            gui.setItem(s, buildSymbolItemStatic(randomMat, mm));
        }

        // HÀNG 5 (SLOTS 36 - 44): THANH CHỨC NĂNG CONTROL BAR TỔNG HỢP CÂN ĐỐI
        updateHistoryButtonItem();    // Slot 36: Lịch Sử
        updateTicketItem();           // Slot 37: Ví Vé Thường
        updatePaymentModeItem();      // Slot 38: CHỌN NGUỒN TIỀN (Trung tâm)
        updateVipTicketItem();        // Slot 39: Ví Vé VIP
        updateAutoSpinItem();         // Slot 40: Auto Spin
        updatePlayerPersonalItem();   // Slot 41: Top 10
        updatePlayerStatsItem();      // Slot 42: Thống Kê
        updateDailyFreeSpinItem();    // Slot 43: Quà Ngày
        gui.setItem(44, buildItem(Material.REDSTONE_BLOCK, "<red><bold>🚪 THOÁT GAME</bold></red>", List.of("<gray>Đóng giao diện quay hũ"))); // Slot 44: Thoát

        // HÀNG 6 (SLOTS 45 - 49): CƯỢC & QUAY
        updateBetButtons();
        updateSpinButton();
    }

    public void updatePaymentModeItem() {
        switch (paymentMode) {
            case VAULT_MONEY -> {
                gui.setItem(38, buildItem(Material.EMERALD_BLOCK,
                        "<gradient:#00AA00:#55FF55><bold>💳 CHẾ ĐỘ: DÙNG TIỀN VAULT ($)</bold></gradient>",
                        List.of(
                                "",
                                " <green>✔ Đang chọn: Nguồn tiền xu Vault ($)</green>",
                                " <gray>Cho phép đặt cược mọi hạn mức từ 1k$ đến 500k$.",
                                "",
                                " <yellow>👉 CLICK ĐỂ CHUYỂN: DÙNG VÉ THƯỜNG (1K-100K)"
                        )));
            }
            case STANDARD_TICKET -> {
                int count = databaseManager != null ? databaseManager.getTickets(player.getUniqueId()) : 0;
                ItemStack item = configManager.createTicketItem(1);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName(mm.deserialize("<gradient:#FFD700:#FFA500><bold>🎟️ CHẾ ĐỘ: DÙNG VÉ THƯỜNG (1K-100K)</bold></gradient>"));
                    meta.lore(List.of(
                            "",
                            " <yellow>✔ Đang chọn: Vé Quay Casino Hạng Thường</yellow>",
                            " <gray>Số dư khả dụng: <gold><bold>" + count + " vé</bold></gold></gray>",
                            " <red>🔒 Mức cược 500k$ SẼ BỊ KHÓA KHÔNG CHO CHỌN!</red>",
                            "",
                            " <yellow>👉 CLICK ĐỂ CHUYỂN: DÙNG VÉ VIP HIGHROLLER (500K$)"
                    ).stream().map(mm::deserialize).toList());
                    item.setItemMeta(meta);
                }
                gui.setItem(38, item);
            }
            case VIP_TICKET -> {
                int count = databaseManager != null ? databaseManager.getVipTickets(player.getUniqueId()) : 0;
                ItemStack item = configManager.createVipTicketItem(1);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName(mm.deserialize("<gradient:#FF0000:#FFD700><bold>🎫 CHẾ ĐỘ: DÙNG VÉ VIP HIGHROLLER (500K$)</bold></gradient>"));
                    meta.lore(List.of(
                            "",
                            " <gold>✔ Đang chọn: Vé VIP Highroller (500k$)</gold>",
                            " <gray>Số dư khả dụng: <yellow><bold>" + count + " vé VIP</bold></yellow></gray>",
                            " <gray>Tự động chọn sẵn mức cược Tối Đa 500,000$!</gray>",
                            "",
                            " <yellow>👉 CLICK ĐỂ CHUYỂN: DÙNG TIỀN VAULT ($)"
                    ).stream().map(mm::deserialize).toList());
                    item.setItemMeta(meta);
                }
                gui.setItem(38, item);
            }
        }
    }

    public void cyclePaymentMode() {
        if (isSpinning) return;
        switch (paymentMode) {
            case VAULT_MONEY -> {
                paymentMode = PaymentMode.STANDARD_TICKET;
                if (currentBetAmount > 100000.0) {
                    currentBetAmount = 100000.0;
                }
                player.sendMessage(mm.deserialize("<gradient:#FFD700:#FFA500><bold>🎟️ [PHƯƠNG THỨC CƯỢC]</bold></gradient> <green>Đã chuyển sang dùng <gold><bold>Vé Quay Thường (1k-100k)</bold></gold>! Mức cược 500k$ đã được khóa.</green>"));
            }
            case STANDARD_TICKET -> {
                paymentMode = PaymentMode.VIP_TICKET;
                currentBetAmount = 500000.0;
                player.sendMessage(mm.deserialize("<gradient:#FF0000:#FFD700><bold>🎫 [PHƯƠNG THỨC CƯỢC]</bold></gradient> <green>Đã chuyển sang dùng <gold><bold>Vé VIP Highroller (500k$)</bold></gold>!</green>"));
            }
            case VIP_TICKET -> {
                paymentMode = PaymentMode.VAULT_MONEY;
                player.sendMessage(mm.deserialize("<gradient:#00AA00:#55FF55><bold>💳 [PHƯƠNG THỨC CƯỢC]</bold></gradient> <green>Đã chuyển sang dùng <gold><bold>Tiền Mặt Vault ($)</bold></gold>!</green>"));
            }
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
        updatePaymentModeItem();
        updateBetButtons();
        updateSpinButton();
    }

    public void updateBetButtons() {
        gui.setItem(45, buildItem(Material.EMERALD, "<green><bold>💵 CƯỢC 1,000$</bold></green>", List.of("<gray>Click để chọn cược 1k")));
        gui.setItem(46, buildItem(Material.GOLD_INGOT, "<gold><bold>💰 CƯỢC 10,000$</bold></gold>", List.of("<gray>Click để chọn cược 10k")));
        gui.setItem(47, buildItem(Material.AMETHYST_SHARD, "<light_purple><bold>💎 CƯỢC 100,000$</bold></light_purple>", List.of("<gray>Click để chọn cược 100k")));

        if (paymentMode == PaymentMode.STANDARD_TICKET) {
            // Khi đang chọn Chế Độ Vé Thường -> Ô cược 500k BỊ KHÓA HOÀN TOÀN (BARRIER)!
            gui.setItem(48, buildItem(Material.BARRIER,
                    "<red><bold>🔒 CƯỢC 500,000$ [ĐÃ KHÓA]</bold></red>",
                    List.of(
                            "",
                            " <red>❌ Vé Quay Thường chỉ dành cho cược 1k, 10k, 100k!</red>",
                            " <gray>Chuyển sang Vé VIP hoặc Tiền Vault để mở khóa cược 500k.",
                            ""
                    )));
        } else {
            gui.setItem(48, buildItem(Material.DIAMOND, "<aqua><bold>🔥 CƯỢC 500,000$</bold></aqua>", List.of("<gray>Click để chọn cược 500k (Tối đa)")));
        }
    }

    public void updateSpinButton() {
        if (isSpinning) {
            gui.setItem(49, buildItem(Material.NETHER_STAR, "<yellow><bold>🎰 ĐANG QUAY TỪ TRÊN XUỐNG...</bold></yellow>", List.of("<gray>Đang cuộn vòng quay...")));
        } else {
            String streakText = streakCount > 0 ? " <gold>🔥 Chuỗi quay hiện tại: " + streakCount + "/10 ván</gold>" : "";
            String modeStr = switch (paymentMode) {
                case VAULT_MONEY -> "<green>Tiền Vault ($)</green>";
                case STANDARD_TICKET -> "<yellow>Vé Quay Thường</yellow>";
                case VIP_TICKET -> "<gold>Vé VIP Highroller</gold>";
            };

            String spinText = "<gradient:gold:yellow><bold>🎰 BẤM QUAY CƯỢC " + ConfigManager.formatMoney(currentBetAmount) + "$</bold></gradient>";
            List<String> lore = List.of(
                    "",
                    " <gray>Nguồn thanh toán: " + modeStr,
                    " <gray>Mức cược hiện tại: <gold>" + ConfigManager.formatMoney(currentBetAmount) + "$</gold>",
                    " <gray>Kết quả gần nhất: " + lastResultText,
                    streakText,
                    "",
                    " <yellow>👉 CLICK ĐỂ QUAY NGAY TỨC THÌ!"
            );
            gui.setItem(49, buildItem(Material.NETHER_STAR, spinText, lore));
        }
    }

    public void updateAutoSpinItem() {
        if (isAutoSpinning) {
            gui.setItem(40, buildItem(Material.REDSTONE_TORCH,
                    "<red><bold>⚡ TẮT QUAY TỰ ĐỘNG (AUTO)</bold></red>",
                    List.of(
                            "",
                            " <green>✅ Trạng thái: ĐANG BẬT AUTO SPIN!</green>",
                            " <gray>Máy sẽ tự quay liên tục mỗi 1.5 giây.",
                            "",
                            " <red>👉 CLICK ĐỂ DỪNG QUAY TỰ ĐỘNG!"
                    )));
        } else {
            gui.setItem(40, buildItem(Material.LEVER,
                    "<gradient:gold:yellow><bold>⚡ BẬT QUAY TỰ ĐỘNG (AUTO)</bold></gradient>",
                    List.of(
                            "",
                            " <gray>Trạng thái: Đang Tắt",
                            " <gray>Tự động đặt cược & quay liên tục rảnh tay!",
                            "",
                            " <yellow>👉 CLICK ĐỂ BẬT QUAY TỰ ĐỘNG!"
                    )));
        }
    }

    public void updateHistoryButtonItem() {
        gui.setItem(36, buildItem(Material.BOOK,
                "<gradient:gold:yellow><bold>📜 LỊCH SỬ CƯỢC CÁ NHÂN</bold></gradient>",
                List.of(
                        "",
                        " <gray>Xem lại 10 ván vừa quay gần nhất",
                        " <gray>trực tiếp trong khung CSDL Database!",
                        "",
                        " <yellow>👉 CLICK ĐỂ XEM LỊCH SỬ!"
                )));
    }

    public void toggleAutoSpin() {
        if (isAutoSpinning) {
            stopAutoSpin();
            player.sendMessage(mm.deserialize("<red>🛑 Đã tắt chế độ Quay Tự Động (Auto Spin).</red>"));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 0.8f);
        } else {
            isAutoSpinning = true;
            updateAutoSpinItem();
            player.sendMessage(mm.deserialize("<green>⚡ Đã KÍCH HOẠT chế độ Quay Tự Động (Auto Spin)!</green>"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

            autoSpinTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || !isAutoSpinning) {
                        cancel();
                        isAutoSpinning = false;
                        return;
                    }
                    if (!isSpinning) {
                        executeSpin();
                    }
                }
            }.runTaskTimer(plugin, 10L, 30L); // 1.5s mỗi lượt quay
        }
    }

    public void stopAutoSpin() {
        isAutoSpinning = false;
        if (autoSpinTask != null) {
            autoSpinTask.cancel();
            autoSpinTask = null;
        }
        updateAutoSpinItem();
    }

    public void updatePlayerPersonalItem() {
        ItemStack topItem = buildItem(Material.PLAYER_HEAD,
                "<gradient:gold:yellow><bold>🏆 TOP 10 BẢNG XẾP HẠNG</bold></gradient>",
                List.of(
                        "",
                        " <gray>Xem danh sách Top 10 Đại Gia</gray>",
                        " <gray>thắng thưởng nhiều nhất server!",
                        "",
                        " <yellow>👉 CLICK ĐỂ XEM BẢNG XẾP HẠNG!"
                ));
        if (topItem.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            topItem.setItemMeta(skullMeta);
        }
        gui.setItem(41, topItem);
    }

    public void updatePlayerStatsItem() {
        int wins = databaseManager != null ? databaseManager.getWins(player.getUniqueId()) : 0;
        int total = databaseManager != null ? databaseManager.getTotalSpins(player.getUniqueId()) : 0;
        double winRate = total > 0 ? ((double) wins / total) * 100.0 : 0.0;

        gui.setItem(42, buildItem(Material.COMPASS,
                "<gradient:gold:yellow><bold>📈 THỐNG KÊ MAY MẮN</bold></gradient>",
                List.of(
                        "",
                        " <gray>Tổng số lượt quay: <gold>" + total + "</gold>",
                        " <gray>Số lượt thắng: <green>" + wins + "</green>",
                        " <gray>Tỷ lệ may mắn: <yellow>" + String.format("%.1f", winRate) + "%</yellow>",
                        "",
                        " <yellow>👉 CLICK ĐỂ XEM CHI TIẾT THỐNG KÊ!"
                )));
    }

    public void updateJackpotPoolItem() {
        double currentPool = jackpotManager.getJackpotPool();
        gui.setItem(4, buildItem(Material.NETHERITE_BLOCK,
                "<gradient:#8B0000:#D2143A><bold>🔥 QUỸ JACKPOT TÍCH LŨY SERVER 🔥</bold></gradient>",
                List.of(
                        "",
                        " <gray>Tổng tiền thưởng Quỹ Hũ hiện tại:",
                        " <gold><bold>" + ConfigManager.formatMoney(currentPool) + "$</bold></gold>",
                        "",
                        " <yellow>Tỷ lệ Nổ Hũ Hốt Sạch Hũ: <red><bold>3x NETHERITE (NTR)</bold></red>"
                )));
    }

    public void updateDailyFreeSpinItem() {
        long lastClaim = databaseManager != null ? databaseManager.getLastFreeSpinTime(player.getUniqueId()) : 0L;
        long now = System.currentTimeMillis();
        long cooldownMs = 24 * 3600 * 1000L;
        long remaining = (lastClaim + cooldownMs) - now;

        if (remaining <= 0) {
            gui.setItem(43, buildItem(Material.CHEST,
                    "<gradient:gold:yellow><bold>🎁 LƯỢT QUAY MIỄN PHÍ HẰNG NGÀY</bold></gradient>",
                    List.of(
                            "",
                            " <green>✅ Trạng thái: SẴN SÀNG NHẬN!</green>",
                            " <gray>Tặng 1 lượt quay mức cược 1,000$ miễn phí!",
                            "",
                            " <yellow>👉 CLICK ĐỂ QUAY MIỄN PHÍ NGAY!"
                    )));
        } else {
            long hours = remaining / (3600 * 1000L);
            long minutes = (remaining % (3600 * 1000L)) / (60 * 1000L);
            gui.setItem(43, buildItem(Material.CHEST_MINECART,
                    "<red><bold>🎁 ĐÃ ĐIỂM DANH HÔM NAY</bold></red>",
                    List.of(
                            "",
                            " <gray>Chờ điểm danh lượt tiếp theo:",
                            " <gold><bold>" + hours + " giờ " + minutes + " phút nữa</bold></gold>",
                            ""
                    )));
        }
    }

    public void claimDailyFreeSpin() {
        long lastClaim = databaseManager != null ? databaseManager.getLastFreeSpinTime(player.getUniqueId()) : 0L;
        long now = System.currentTimeMillis();
        long cooldownMs = 24 * 3600 * 1000L;

        if (now - lastClaim < cooldownMs) {
            long remaining = (lastClaim + cooldownMs) - now;
            long hours = remaining / (3600 * 1000L);
            long minutes = (remaining % (3600 * 1000L)) / (60 * 1000L);
            player.sendMessage(mm.deserialize("<red>❌ Bạn đã nhận quà điểm danh hôm nay rồi! Vui lòng chờ " + hours + "h " + minutes + "p nữa.</red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
            return;
        }

        if (databaseManager != null) {
            databaseManager.setLastFreeSpinTimeAsync(player.getUniqueId(), now);
        }

        player.sendMessage(mm.deserialize("<green>🎉 Bạn đã nhận thành công 1 LƯỢT QUAY MIỄN PHÍ hằng ngày (Mức cược 1,000$)!</green>"));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);

        this.currentBetAmount = 1000.0;
        this.isUsingTicket = true; // Đánh dấu lượt quay miễn phí để không trừ tiền/nạp hũ khi thua!
        isSpinning = true;
        updateSpinButton();
        updateDailyFreeSpinItem();
        runVerticalSlidingReelAnimationFree();
    }

    public void updateTicketItem() {
        int count = databaseManager != null ? databaseManager.getTickets(player.getUniqueId()) : 0;
        ItemStack ticketItem = configManager.createTicketItem(1);
        ItemMeta meta = ticketItem.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize("<gradient:#FFD700:#FFA500><bold>🎟️ VÍ VÉ THƯỜNG: " + count + " VÉ</bold></gradient>"));
            List<String> rawLore = List.of(
                    "",
                    " <yellow>Vé Quay Thường hiện có: <gold><bold>" + count + " vé</bold></gold>",
                    " <gray>Áp dụng quay <green><bold>MIỄN PHÍ 100%</bold></green> cho các</gray>",
                    " <gray>mức cược: <gold>1,000$</gold>, <gold>10,000$</gold>, <gold>100,000$</gold>!</gray>",
                    "",
                    " <yellow>👉 Cầm Vé Thường (CMD 777) nhấp chuột phải để nạp!"
            );
            meta.lore(rawLore.stream().map(mm::deserialize).toList());
            ticketItem.setItemMeta(meta);
        }
        gui.setItem(37, ticketItem);
    }

    public void updateVipTicketItem() {
        int count = databaseManager != null ? databaseManager.getVipTickets(player.getUniqueId()) : 0;
        ItemStack vipItem = configManager.createVipTicketItem(1);
        ItemMeta meta = vipItem.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize("<gradient:#FF0000:#FFD700><bold>🎫 VÍ VÉ VIP HIGHROLLER: " + count + " VÉ</bold></gradient>"));
            List<String> rawLore = List.of(
                    "",
                    " <gold>Vé VIP Highroller hiện có: <yellow><bold>" + count + " vé VIP</bold></yellow>",
                    " <gray>Đặc quyền quay <green><bold>MIỄN PHÍ 100%</bold></green> cho mức</gray>",
                    " <gray>cược Tối Đa Táo Bạo: <gold><bold>500,000$</bold></gold>!</gray>",
                    "",
                    " <yellow>👉 Cầm Vé VIP (CMD 888) nhấp chuột phải để nạp!"
            );
            meta.lore(rawLore.stream().map(mm::deserialize).toList());
            vipItem.setItemMeta(meta);
        }
        gui.setItem(39, vipItem);
    }

    public void updateJackpotHUD() {
        jackpotManager.updateGlobalBossBar();
        updateJackpotPoolItem();
        updatePlayerPersonalItem();
        updatePlayerStatsItem();
        updateDailyFreeSpinItem();
        updateAutoSpinItem();
        updateHistoryButtonItem();
        updatePaymentModeItem();
        updateTicketItem();
        updateVipTicketItem();
        updateBetButtons();
        updateSpinButton();
    }

    public void setBetAmount(double amount) {
        if (isSpinning) return;

        if (amount >= 500000.0 && paymentMode == PaymentMode.STANDARD_TICKET) {
            player.sendMessage(mm.deserialize("<red>❌ Mức cược 500,000$ đã bị khóa khi chọn chế độ Vé Quay Thường! Vui lòng chọn mức cược từ 1k$ - 100k$.</red>"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
            return;
        }

        this.currentBetAmount = amount;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
        player.sendMessage(mm.deserialize("<green>✅ Đã chọn mức cược: <gold>" + ConfigManager.formatMoney(amount) + "$</gold>"));
        updateSpinButton();
        updatePlayerStatsItem();
    }

    public void executeSpin() {
        if (isSpinning) return;

        boolean isUsingTicket = false;
        DatabaseManager db = databaseManager;

        if (paymentMode == PaymentMode.STANDARD_TICKET) {
            if (currentBetAmount > 100000.0) {
                player.sendMessage(mm.deserialize("<red>❌ Vé Quay Thường chỉ áp dụng cho mức cược từ 1k$ đến 100k$! Mức cược 500k$ đã bị khóa.</red>"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
                stopAutoSpin();
                return;
            }

            int availableTickets = db != null ? db.getTickets(player.getUniqueId()) : 0;
            if (availableTickets <= 0) {
                player.sendMessage(mm.deserialize("<red>❌ Bạn đã hết Vé Quay Thường! Vui lòng nạp thêm vé hoặc chuyển sang dùng Tiền Vault ($).</red>"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
                stopAutoSpin();
                return;
            }

            boolean deducted = db.removeTickets(player.getUniqueId(), 1);
            if (!deducted) {
                player.sendMessage(mm.deserialize("<red>❌ Giao dịch trừ vé thất bại!</red>"));
                stopAutoSpin();
                return;
            }

            isUsingTicket = true;
            int remaining = availableTickets - 1;
            player.sendMessage(mm.deserialize(
                    "<gradient:#FFD700:#FFA500><bold>🎟️ [VÉ QUAY THƯỜNG]</bold></gradient> <green>Đã dùng 1x Vé Quay Thường cho mức cược <gold><bold>" + ConfigManager.formatMoney(currentBetAmount) + "$</bold></gold>! (Còn lại: <gold><bold>" + remaining + " vé</bold></gold>)</green>"
            ));
        } else if (paymentMode == PaymentMode.VIP_TICKET) {
            currentBetAmount = 500000.0;

            int availableVip = db != null ? db.getVipTickets(player.getUniqueId()) : 0;
            if (availableVip <= 0) {
                player.sendMessage(mm.deserialize("<red>❌ Bạn đã hết Vé VIP Highroller! Vui lòng nạp thêm vé VIP hoặc chuyển sang dùng Tiền Vault ($).</red>"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
                stopAutoSpin();
                return;
            }

            boolean deducted = db.removeVipTickets(player.getUniqueId(), 1);
            if (!deducted) {
                player.sendMessage(mm.deserialize("<red>❌ Giao dịch trừ vé VIP thất bại!</red>"));
                stopAutoSpin();
                return;
            }

            isUsingTicket = true;
            int remaining = availableVip - 1;
            player.sendMessage(mm.deserialize(
                    "<gradient:#FF0000:#FFD700><bold>🎫 [VÉ VIP HIGHROLLER]</bold></gradient> <green>Đã dùng 1x Vé VIP Highroller cho mức cược Tối Đa <gold><bold>500,000$</bold></gold>! (Còn lại: <gold><bold>" + remaining + " vé VIP</bold></gold>)</green>"
            ));
        } else {
            // Thanh toán bằng tiền mặt Vault ($)
            Economy economy = plugin.getEconomy();
            if (economy == null || !economy.has(player, currentBetAmount)) {
                player.sendMessage(configManager.getNotEnoughMoneyMsg(currentBetAmount));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
                stopAutoSpin();
                return;
            }

            var resp = economy.withdrawPlayer(player, currentBetAmount);
            if (!resp.transactionSuccess()) {
                player.sendMessage(mm.deserialize("<red>Giao dịch thất bại: " + resp.errorMessage));
                stopAutoSpin();
                return;
            }
        }

        this.isUsingTicket = isUsingTicket;

        // Tăng chuỗi quay & Reset về 0 khi đủ 10 ván
        streakCount++;
        boolean isLuckyStreak = (streakCount >= 10);
        if (isLuckyStreak) {
            streakCount = 0;
            player.sendMessage(mm.deserialize("<gradient:gold:yellow><bold>🎡 CHUỖI QUAY 10 VÁN!</bold></gradient> <green>Lượt quay này nhận <gold><bold>LUCKY SPIN X1.5 TỶ LỆ THẮNG</bold></gold>!</green>"));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.5f);
        }

        isSpinning = true;
        updateSpinButton();
        updateTicketItem();
        updateVipTicketItem();
        updatePaymentModeItem();
        updateBetButtons();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);

        runVerticalSlidingReelAnimation(isLuckyStreak);
    }

    private void runVerticalSlidingReelAnimationFree() {
        runVerticalSlidingReelAnimation(false);
    }

    // ===== ANIMATION CUỘN DỌC TRƯỢT TỪ TRÊN XUỐNG =====
    private void runVerticalSlidingReelAnimation(boolean isLuckyStreak) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double roll = rand.nextDouble(100.0);

        boolean happyHour = plugin.getHappyHourManager() != null && plugin.getHappyHourManager().isHappyHour();
        double winChance = happyHour ? configManager.getWinRateHappyHour() : configManager.getWinRateNormal(); // 22.0%
        double jackpotChance = configManager.getJackpotRate(); // 0.50%

        if (isLuckyStreak) {
            winChance = Math.min(85.0, winChance * 1.5);
        }

        boolean willWin = (roll < winChance) || forceNextJackpot;
        boolean isJackpotWin = false;

        if (willWin) {
            if (forceNextJackpot) {
                isJackpotWin = true;
                forceNextJackpot = false;
            } else {
                double jRoll = rand.nextDouble(100.0);
                if (jRoll < jackpotChance) {
                    isJackpotWin = true;
                }
            }
        }

        Material finalMiddleSymbol;
        Material[] finalReelMiddle = new Material[3];

        if (willWin) {
            if (isJackpotWin) {
                finalMiddleSymbol = Material.NETHERITE_BLOCK;
            } else {
                Material[] normalSymbols = {
                        Material.COAL_BLOCK, Material.IRON_BLOCK, Material.GOLD_BLOCK,
                        Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK
                };
                finalMiddleSymbol = normalSymbols[rand.nextInt(normalSymbols.length)];
            }
            finalReelMiddle[0] = finalMiddleSymbol;
            finalReelMiddle[1] = finalMiddleSymbol;
            finalReelMiddle[2] = finalMiddleSymbol;
        } else {
            // Lượt THUA: 3 ô hàng ngang trung tâm (Slots 21, 22, 23) ĐẢM BẢO KHÔNG THỂ KHỚP CẢ 3 Ô!
            finalMiddleSymbol = Material.COAL_BLOCK;
            finalReelMiddle[0] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            finalReelMiddle[1] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            do {
                finalReelMiddle[2] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            } while (finalReelMiddle[2] == finalReelMiddle[1] && finalReelMiddle[1] == finalReelMiddle[0]);
        }

        int[] reel1 = {12, 21, 30};
        int[] reel2 = {13, 22, 31};
        int[] reel3 = {14, 23, 32};

        final boolean finalWin = willWin;
        final boolean finalJackpot = isJackpotWin;

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20;

            @Override
            public void run() {
                ticks++;

                if (ticks < 10) {
                    shiftReelDown(reel1, rand);
                } else if (ticks == 10) {
                    setReelFinal(reel1, finalReelMiddle[0], rand);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f);
                }

                if (ticks < 14) {
                    shiftReelDown(reel2, rand);
                } else if (ticks == 14) {
                    setReelFinal(reel2, finalReelMiddle[1], rand);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.4f);
                }

                if (ticks < 18) {
                    shiftReelDown(reel3, rand);
                } else if (ticks == 18) {
                    setReelFinal(reel3, finalReelMiddle[2], rand);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.6f);
                }

                if (ticks >= maxTicks) {
                    cancel();
                    handleSpinResult(finalWin, finalJackpot, finalMiddleSymbol);
                }
            }
        }.runTaskTimer(plugin, 1L, 2L);
    }

    private void shiftReelDown(int[] reelSlots, ThreadLocalRandom rand) {
        gui.setItem(reelSlots[2], gui.getItem(reelSlots[1]));
        gui.setItem(reelSlots[1], gui.getItem(reelSlots[0]));

        Material newTopMat = SYMBOLS[rand.nextInt(SYMBOLS.length)];
        gui.setItem(reelSlots[0], buildSymbolItemStatic(newTopMat, mm));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 1.8f);
    }

    private void setReelFinal(int[] reelSlots, Material middleSymbol, ThreadLocalRandom rand) {
        gui.setItem(reelSlots[1], buildSymbolItemStatic(middleSymbol, mm));
        gui.setItem(reelSlots[0], buildSymbolItemStatic(SYMBOLS[rand.nextInt(SYMBOLS.length)], mm));
        gui.setItem(reelSlots[2], buildSymbolItemStatic(SYMBOLS[rand.nextInt(SYMBOLS.length)], mm));
    }

    private void handleSpinResult(boolean isWin, boolean isJackpot, Material winningSymbol) {
        String detailText = isWin ? formatSymbolNameStatic(winningSymbol) : "Thua";

        if (isWin) {
            double multiplier = configManager.getSymbolMultiplier(winningSymbol);
            double grossReward = currentBetAmount * multiplier;

            if (isJackpot) {
                double jackpotPoolWon = jackpotManager.getJackpotPool();
                grossReward += jackpotPoolWon;
                jackpotManager.resetJackpotPool();
                jackpotManager.broadcastJackpotWin(player.getName(), grossReward);
            }

            double taxRate = configManager.getTaxRate(); // 0.10 (10%)
            double taxAmount = grossReward * taxRate;
            double netReward = grossReward - taxAmount;

            plugin.getEconomy().depositPlayer(player, netReward);

            if (isJackpot) {
                spawnJackpotFireworksAndParticles(player);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 0.8f, 1.2f);
                lastResultText = "<gradient:#FF0000:#FFD700><bold>NỔ HŨ HỐT SẠCH HŨ +" + ConfigManager.formatMoney(netReward) + "$!</bold></gradient>";
                player.sendMessage(configManager.getJackpotWinMsg(currentBetAmount, grossReward, taxAmount, netReward));
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.4f);
                lastResultText = "<green><bold>THẮNG X2 +" + ConfigManager.formatMoney(netReward) + "$ (" + detailText + ")</bold></green>";
                player.sendMessage(configManager.getWinMsg(detailText, multiplier, currentBetAmount, grossReward, taxAmount, netReward));
            }

            if (databaseManager != null) {
                databaseManager.recordSpinAsync(
                        player.getUniqueId(),
                        player.getName(),
                        currentBetAmount,
                        true,
                        netReward,
                        isJackpot ? "JACKPOT NỔ HŨ X5" : "THẮNG " + String.format("%.1fx", multiplier) + " " + detailText
                );
            }
        } else {
            if (!isUsingTicket) {
                // Thua bằng tiền mặt ($): NẠP 100% SỐ TIỀN THUA VÀO QUỸ JACKPOT SERVER!
                jackpotManager.addLossToPool(currentBetAmount);
                player.sendMessage(configManager.getLoseMsg(currentBetAmount));
            } else {
                // Thua bằng vé quay: 0$ tiền bị trừ, 0$ cộng hũ
                player.sendMessage(mm.deserialize(
                        "<gray>🎰 Lượt quay dùng <gold>Vé Quay</gold> không trúng thưởng hàng ngang. (Không bị trừ tiền xu $!)</gray>"
                ));
            }

            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 0.9f);
            lastResultText = isUsingTicket ? "<red>THUA (Vé Quay)</red>" : "<red>THUA (-" + ConfigManager.formatMoney(currentBetAmount) + "$)</red>";

            if (databaseManager != null) {
                databaseManager.recordSpinAsync(
                        player.getUniqueId(),
                        player.getName(),
                        currentBetAmount,
                        false,
                        0,
                        isUsingTicket ? "THUA (Vé Quay)" : "THUA (Đã nạp 100% vào Hũ)"
                );
            }
        }

        isUsingTicket = false;
        isSpinning = false;
        updateJackpotHUD();
    }

    private void spawnJackpotFireworksAndParticles(Player p) {
        Location loc = p.getLocation();
        World world = p.getWorld();

        for (int i = 0; i < 3; i++) {
            Location fwLoc = loc.clone().add((i - 1) * 1.5, 1.0, (i % 2 == 0 ? 1 : -1) * 1.5);
            Firework fw = (Firework) world.spawnEntity(fwLoc, EntityType.FIREWORK_ROCKET);
            FireworkMeta meta = fw.getFireworkMeta();
            meta.addEffect(FireworkEffect.builder()
                    .withColor(Color.YELLOW, Color.ORANGE, Color.RED)
                    .withFade(Color.WHITE)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .trail(true)
                    .flicker(true)
                    .build());
            meta.setPower(1);
            fw.setFireworkMeta(meta);
        }

        for (int i = 0; i < 50; i++) {
            double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 2.5;
            double offsetY = ThreadLocalRandom.current().nextDouble() * 2.0;
            double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 2.5;
            world.spawnParticle(Particle.TOTEM_OF_UNDYING, loc.clone().add(offsetX, offsetY, offsetZ), 3, 0.1, 0.1, 0.1, 0.05);
        }
    }

    public static ItemStack buildSymbolItemStatic(Material material, MiniMessage mm) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = (material == Material.NETHERITE_BLOCK)
                    ? "<gradient:#FF0000:#FFD700><bold>🔥 KHỐI NETHERITE (NTR - NỔ HŨ X5) 🔥</bold></gradient>"
                    : "<gold><bold>✦ " + formatSymbolNameStatic(material) + " (x2.0) ✦</bold></gold>";
            meta.displayName(mm.deserialize(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static String formatSymbolNameStatic(Material mat) {
        String name = mat.name().replace("_", " ").toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
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

    // Getters & Setters
    public Player getPlayer() { return player; }
    public GameMode getGameMode() { return gameMode; }
    public Inventory getGui() { return gui; }
    public String getGuiTitlePlain() { return guiTitlePlain; }
    public boolean isSpinning() { return isSpinning; }
    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setForceNextJackpot(boolean force) { this.forceNextJackpot = force; }
}
