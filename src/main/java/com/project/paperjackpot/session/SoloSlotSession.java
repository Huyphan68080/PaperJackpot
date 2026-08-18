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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SoloSlotSession - Phân vùng chơi Quay Hũ Nổ Hũ Cá Nhân Chuyên Biệt duy nhất.
 * Tích hợp Auto Spin, Thưởng Chuỗi Quay May Mắn (10 ván), Sự Kiện Giờ Vàng (Happy Hour 20h-21h) & Lịch Sử Cược.
 */
public class SoloSlotSession {

    public static final Material[] SYMBOLS = {
            Material.NETHERITE_BLOCK, Material.DIAMOND, Material.EMERALD,
            Material.GOLD_INGOT, Material.IRON_INGOT, Material.COPPER_INGOT,
            Material.COAL, Material.REDSTONE, Material.LAPIS_LAZULI,
            Material.AMETHYST_SHARD, Material.QUARTZ, Material.ECHO_SHARD
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

    // Tính năng mới: Auto Spin & Streak Bonus
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

        // Tiêu đề ngắn gọn không bị đè các nút giao diện
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

        // Âm thanh chào mừng nhẹ nhàng của game
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);

        // Chỉ hiển thị lời chúc Dealer khi vừa gõ lệnh /jackpot từ Chat (Không hiện lại khi chuyển GUI)
        if (sendWelcomeMessage) {
            player.sendMessage(configManager.getWelcomeDealerMsg(player.getName()));
        }
    }

    public void close() {
        stopAutoSpin();
    }

    /**
     * DỰNG KHUNG GIAO DIỆN PHÒNG QUAY HŨ 3X3 CHUẨN ĐẸP
     */
    public void setupGuiFrame() {
        // Viền khung đen
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, createDecorPane(Material.BLACK_STAINED_GLASS_PANE));
            gui.setItem(45 + i, createDecorPane(Material.BLACK_STAINED_GLASS_PANE));
        }

        int[] sideSlots = {
                9, 10, 11, 15, 16, 17,
                18, 19, 25, 26,
                27, 28, 29, 33, 34, 35,
                36, 37, 38, 39, 40, 41
        };
        for (int slot : sideSlots) {
            gui.setItem(slot, createDecorPane(Material.GRAY_STAINED_GLASS_PANE));
        }

        // Slot 4 (Chính giữa hàng trên): QUỸ HŨ TÍCH LŨY SERVER
        updateJackpotPoolItem();

        // Đèn mũi tên chỉ hàng thưởng (Slot 20 & 24)
        gui.setItem(20, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>▶ HÀNG THƯỞNG ◀</bold></green>", List.of()));
        gui.setItem(24, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>▶ HÀNG THƯỞNG ◀</bold></green>", List.of()));

        // Điền đầy đủ khoáng sản vào Ma trận Quay 3x3 (Slots 12, 13, 14, 21, 22, 23, 30, 31, 32)
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int[] reelSlots = {12, 13, 14, 21, 22, 23, 30, 31, 32};
        for (int s : reelSlots) {
            Material randomMat = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            gui.setItem(s, buildSymbolItemStatic(randomMat, mm));
        }

        // Slot 42: 📜 LỊCH SỬ CƯỢC CÁ NHÂN
        updateHistoryButtonItem();

        // Slot 43: ⚡ AUTO SPIN (TỰ ĐỘNG QUAY RẢNH TAY)
        updateAutoSpinItem();

        // Slot 44: 🎟️ VÍ VÉ QUAY CASINO
        updateTicketItem();

        // các nút chọn tiền cược (Slots 45-48)
        gui.setItem(45, buildItem(Material.EMERALD, "<green><bold>💵 CƯỢC 1,000$</bold></green>", List.of("<gray>Click để chọn cược 1k")));
        gui.setItem(46, buildItem(Material.GOLD_INGOT, "<gold><bold>💰 CƯỢC 10,000$</bold></gold>", List.of("<gray>Click để chọn cược 10k")));
        gui.setItem(47, buildItem(Material.AMETHYST_SHARD, "<light_purple><bold>💎 CƯỢC 100,000$</bold></light_purple>", List.of("<gray>Click để chọn cược 100k")));
        gui.setItem(48, buildItem(Material.DIAMOND, "<aqua><bold>🔥 CƯỢC 500,000$</bold></aqua>", List.of("<gray>Click để chọn cược 500k (Tối đa)")));

        // Slot 49: Nút BẤM QUAY NGAY TỨC THÌ / KẾT QUẢ VỪA QUAY
        updateSpinButton();

        // Slot 50: 🏆 TOP 10 BẢNG XẾP HẠNG (Tách riêng cho PE/Bedrock)
        updatePlayerPersonalItem();

        // Slot 51: 📈 BẢNG THỐNG KÊ CÁ NHÂN (Tách riêng cho PE/Bedrock)
        updatePlayerStatsItem();

        // Slot 52: 🎁 ĐIỂM DANH LƯỢT QUAY MIỄN PHÍ MỖI NGÀY (24H)
        updateDailyFreeSpinItem();

        // Slot 53: Nút Thoát Game
        gui.setItem(53, buildItem(Material.REDSTONE_BLOCK, "<red><bold>🚪 THOÁT RA GAME</bold></red>", List.of("<gray>Đóng giao diện quay hũ")));
    }

    public void updateSpinButton() {
        if (isSpinning) {
            gui.setItem(49, buildItem(Material.NETHER_STAR, "<yellow><bold>🎰 ĐANG QUAY TỪ TRÊN XUỐNG...</bold></yellow>", List.of("<gray>Đang cuộn vòng quay...")));
        } else {
            String streakText = streakCount > 0 ? " <gold>🔥 Chuỗi quay hiện tại: " + streakCount + "/10 ván</gold>" : "";
            String spinText = "<gradient:gold:yellow><bold>🎰 BẤM QUAY CƯỢC " + ConfigManager.formatMoney(currentBetAmount) + "$</bold></gradient>";
            List<String> lore = List.of(
                    "",
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
            gui.setItem(43, buildItem(Material.REDSTONE_TORCH,
                    "<red><bold>⚡ TẮT QUAY TỰ ĐỘNG (AUTO)</bold></red>",
                    List.of(
                            "",
                            " <green>✅ Trạng thái: ĐANG BẬT AUTO SPIN!</green>",
                            " <gray>Máy sẽ tự quay liên tục mỗi 1.5 giây.",
                            "",
                            " <red>👉 CLICK ĐỂ DỪNG QUAY TỰ ĐỘNG!"
                    )));
        } else {
            gui.setItem(43, buildItem(Material.LEVER,
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
        gui.setItem(42, buildItem(Material.BOOK,
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
            player.sendMessage(mm.deserialize("<green>⚡ Đã bật chế độ Quay Tự Động (Auto Spin)! Hàng 1.5s tự quay 1 lượt.</green>"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.5f);

            autoSpinTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!isAutoSpinning || !player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (!isSpinning) {
                        executeSpin();
                    }
                }
            }.runTaskTimer(plugin, 10L, 35L); // Mỗi 1.75s tự bấm quay 1 ván
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
        gui.setItem(50, topItem);
    }

    public void updatePlayerStatsItem() {
        Economy economy = plugin.getEconomy();
        double balance = economy != null ? economy.getBalance(player) : 0;

        ItemStack statsItem = buildItem(Material.COMPASS,
                "<gradient:#00AA00:#55FF55><bold>📈 THỐNG KÊ CÁ NHÂN: " + player.getName() + "</bold></gradient>",
                List.of(
                        "",
                        " <gray>Số dư hiện tại: <gold>" + ConfigManager.formatMoney(balance) + "$</gold>",
                        " <gray>Mức cược đang chọn: <gold>" + ConfigManager.formatMoney(currentBetAmount) + "$</gold>",
                        " <gray>Lần quay trước: " + lastResultText,
                        "",
                        " <yellow>👉 CLICK ĐỂ XEM THỐNG KÊ MAY MẮN!"
                ));
        gui.setItem(51, statsItem);
    }

    public void updateJackpotPoolItem() {
        double pool = jackpotManager.getJackpotPool();
        boolean isHappyHour = configManager.isHappyHourActive();
        String happyHourText = isHappyHour ? " <gradient:#FF0000:#FFD700>🎆 GIỜ VÀNG (20H-21H): TĂNG TỶ LỆ NỔ HŨ X2!</gradient>" : "";

        ItemStack jackpotItem = buildItem(Material.NETHERITE_BLOCK,
                "<gradient:#FF0000:#FFD700><bold>🔥 QUỸ JACKPOT TÍCH LŨY SERVER 🔥</bold></gradient>",
                List.of(
                        "",
                        " <yellow>Tổng Quỹ Jackpot hiện tại: <gold><bold>" + ConfigManager.formatMoney(pool) + "$</bold></gold>",
                        " <gray>Tất cả tiền thua cược của server",
                        " <gray>đều được tích lũy 100% thẳng vào Quỹ Jackpot!",
                        happyHourText,
                        "",
                        " <gradient:#FF0000:#FFD700>🔥 3x Netherite (NTR) = THƯỞNG X5 + HỐT SẠCH QUỸ JACKPOT!</gradient>"
                ));
        gui.setItem(4, jackpotItem);
    }

    public void updateDailyFreeSpinItem() {
        long lastClaim = databaseManager != null ? databaseManager.getLastFreeSpinTime(player.getUniqueId()) : 0L;
        long now = System.currentTimeMillis();
        long cooldownMs = 24 * 3600 * 1000L; // 24 giờ
        long remaining = (lastClaim + cooldownMs) - now;

        if (remaining <= 0) {
            gui.setItem(52, buildItem(Material.CHEST,
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
            gui.setItem(52, buildItem(Material.CHEST_MINECART,
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
            meta.displayName(mm.deserialize("<gradient:#FFD700:#FFA500><bold>🎟️ VÍ VÉ QUAY: " + count + " VÉ</bold></gradient>"));
            List<String> rawLore = List.of(
                    "",
                    " <yellow>Số dư vé quay hiện có: <gold><bold>" + count + " vé</bold></gold>",
                    " <gray>Khi bạn có Vé Quay trong tài khoản, lượt quay</gray>",
                    " <gray>sẽ <green><bold>TỰ ĐỘNG ƯU TIÊN TRỪ VÉ QUAY</bold></green> trước tiền Vault!</gray>",
                    "",
                    " <yellow>👉 Cầm Vé Item nhấp chuột phải để nạp vé vào ví!"
            );
            meta.lore(rawLore.stream().map(mm::deserialize).toList());
            ticketItem.setItemMeta(meta);
        }
        gui.setItem(44, ticketItem);
    }

    public void updateJackpotHUD() {
        jackpotManager.updateGlobalBossBar();
        updateJackpotPoolItem();
        updatePlayerPersonalItem();
        updatePlayerStatsItem();
        updateDailyFreeSpinItem();
        updateAutoSpinItem();
        updateHistoryButtonItem();
        updateTicketItem();
    }

    public void setBetAmount(double amount) {
        if (isSpinning) return;
        this.currentBetAmount = amount;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
        player.sendMessage(mm.deserialize("<green>✅ Đã chọn mức cược: <gold>" + ConfigManager.formatMoney(amount) + "$</gold>"));
        updateSpinButton();
        updatePlayerStatsItem();
    }

    public void executeSpin() {
        if (isSpinning) return;

        int availableTickets = databaseManager != null ? databaseManager.getTickets(player.getUniqueId()) : 0;
        boolean isUsingTicket = false;

        if (availableTickets > 0) {
            boolean deducted = databaseManager.removeTickets(player.getUniqueId(), 1);
            if (deducted) {
                isUsingTicket = true;
                int remaining = availableTickets - 1;
                player.sendMessage(mm.deserialize(
                        "<gradient:#FFD700:#FFA500><bold>🎟️ [VÉ QUAY CASINO]</bold></gradient> <green>Đã dùng 1x Vé Quay Casino! (Số dư còn lại: <gold><bold>" + remaining + " vé</bold></gold>)</green>"
                ));
            }
        }

        this.isUsingTicket = isUsingTicket;

        if (!isUsingTicket) {
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

        // Tăng chuỗi quay
        streakCount++;
        if (streakCount >= 10) {
            player.sendMessage(mm.deserialize("<gradient:gold:yellow><bold>🎡 CHUỖI QUAY 10 VÁN!</bold></gradient> <green>Lượt quay này nhận <gold><bold>LUCKY SPIN X2 TỶ LỆ THẮNG</bold></gold>!</green>"));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.5f);
        }

        isSpinning = true;
        updateSpinButton();
        updateTicketItem();
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);

        runVerticalSlidingReelAnimation();
    }

    private void runVerticalSlidingReelAnimationFree() {
        runVerticalSlidingReelAnimation();
    }

    // ===== ANIMATION CUỘN DỌC TRƯỢT TỪ TRÊN XUỐNG =====
    private void runVerticalSlidingReelAnimation() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double roll = rand.nextDouble(100.0);

        final boolean isWin;
        final boolean isJackpot;
        final Material m1, m2, m3;
        Material jackpotMat = Material.NETHERITE_BLOCK;

        boolean isHappyHour = configManager.isHappyHourActive();
        boolean isLuckyStreak = streakCount >= 10;
        if (isLuckyStreak) streakCount = 0; // Reset sau khi dùng

        double jackpotChance = isHappyHour ? 0.10 : 0.05;
        double winChance = isLuckyStreak ? 44.0 : 22.0;

        if (forceNextJackpot) {
            isWin = true; isJackpot = true;
            m1 = jackpotMat; m2 = jackpotMat; m3 = jackpotMat;
            forceNextJackpot = false;
        } else if (roll < jackpotChance) {
            // Jackpot Nổ Hũ
            isWin = true; isJackpot = true;
            m1 = jackpotMat; m2 = jackpotMat; m3 = jackpotMat;
        } else if (roll < winChance) {
            // Thắng Thường (x2.0 tiền cược)
            isWin = true; isJackpot = false;
            Material winMat;
            do {
                winMat = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            } while (winMat == jackpotMat);
            m1 = winMat; m2 = winMat; m3 = winMat;
        } else {
            // THUA CƯỢC (Tích lũy 100% tiền thua vào Quỹ Hũ Server!)
            isWin = false; isJackpot = false;
            Material mat1 = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            Material mat2 = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            Material mat3 = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            while (mat1 == mat2 && mat2 == mat3) {
                mat3 = SYMBOLS[rand.nextInt(SYMBOLS.length)];
            }
            m1 = mat1; m2 = mat2; m3 = mat3;
        }

        new BukkitRunnable() {
            private int step = 0;
            private final Material[] reel1 = new Material[3];
            private final Material[] reel2 = new Material[3];
            private final Material[] reel3 = new Material[3];

            {
                for (int i = 0; i < 3; i++) {
                    reel1[i] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
                    reel2[i] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
                    reel3[i] = SYMBOLS[rand.nextInt(SYMBOLS.length)];
                }
            }

            @Override
            public void run() {
                step++;

                // Cuộn dọc Cột 1 (Dừng ở step 18)
                if (step <= 18) {
                    Material top = (step == 17) ? m1 : SYMBOLS[rand.nextInt(SYMBOLS.length)];
                    reel1[2] = reel1[1]; reel1[1] = reel1[0]; reel1[0] = top;
                }

                // Cuộn dọc Cột 2 (Dừng ở step 25)
                if (step <= 25) {
                    Material top = (step == 24) ? m2 : SYMBOLS[rand.nextInt(SYMBOLS.length)];
                    reel2[2] = reel2[1]; reel2[1] = reel2[0]; reel2[0] = top;
                }

                // Cuộn dọc Cột 3 (Dừng ở step 32)
                if (step <= 32) {
                    Material top = (step == 31) ? m3 : SYMBOLS[rand.nextInt(SYMBOLS.length)];
                    reel3[2] = reel3[1]; reel3[1] = reel3[0]; reel3[0] = top;
                }

                // Hiển thị hiệu ứng trượt dọc Top (12,13,14) -> Middle (21,22,23) -> Bottom (30,31,32)
                gui.setItem(12, buildSymbolItemStatic(reel1[0], mm));
                gui.setItem(21, buildSymbolItemStatic(reel1[1], mm));
                gui.setItem(30, buildSymbolItemStatic(reel1[2], mm));

                gui.setItem(13, buildSymbolItemStatic(reel2[0], mm));
                gui.setItem(22, buildSymbolItemStatic(reel2[1], mm));
                gui.setItem(31, buildSymbolItemStatic(reel2[2], mm));

                gui.setItem(14, buildSymbolItemStatic(reel3[0], mm));
                gui.setItem(23, buildSymbolItemStatic(reel3[1], mm));
                gui.setItem(32, buildSymbolItemStatic(reel3[2], mm));

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.7f, 1.4f);

                if (step >= 32) {
                    cancel();
                    String symName = formatSymbolNameStatic(m1);
                    onGameComplete(isWin, isJackpot, m1, "3x " + symName);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    /**
     * XỬ LÝ HOÀN TẤT VÀ HIỂN THỊ THÔNG BÁO THẮNG / THUA TRỰC TIẾP LÊN CHAT
     */
    private void onGameComplete(boolean isWin, boolean isJackpot, Material winningMat, String detailText) {
        Economy economy = plugin.getEconomy();
        double taxRate = configManager.getTaxRate();

        if (isWin) {
            if (isJackpot) {
                // JACKPOT NỔ HŨ: (Cược x 5.0) + Hốt 100% Quỹ Hũ Tích Lũy Server
                double wonPool = jackpotManager.claimJackpotPool(player.getName());
                double grossPayout = (currentBetAmount * 5.0) + wonPool;
                double tax = grossPayout * taxRate;
                double netPayout = grossPayout - tax;

                if (economy != null && netPayout > 0) {
                    economy.depositPlayer(player, netPayout);
                }

                // 🎆 BẮN PHÁO HOA & HIỆU ỨNG PARTICLE VÀNG KIM RỰC RỠ XUNG QUANH NGƯỜI CHƠI
                spawnJackpotFireworksAndParticles(player);

                lastResultText = "<gradient:#FF0000:#FFD700><bold>🔥 JACKPOT NỔ HŨ X5 + HŨ + " + ConfigManager.formatMoney(netPayout) + "$ 🔥</bold></gradient>";
                player.sendMessage(configManager.getJackpotWinMsg(currentBetAmount, grossPayout, tax, netPayout));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.2f);

                // Đổi nút Slot 49 thành Banner Nổ Hũ rực rỡ
                gui.setItem(49, buildItem(Material.NETHERITE_BLOCK,
                        "<gradient:#FF0000:#FFD700><bold>🔥 JACKPOT NỔ HŨ X5 HỐT TRỌN " + ConfigManager.formatMoney(netPayout) + "$ 🔥</bold></gradient>",
                        List.of("<yellow>🎉 " + detailText + " - THẮNG LỚN! 🎉", "", "<yellow>👉 Click để quay tiếp lượt nữa!")));

                // Đổi đèn Slot 20 & 24 thành màu xanh lá thắng lớn
                gui.setItem(20, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>★ JACKPOT ★</bold></green>", List.of()));
                gui.setItem(24, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>★ JACKPOT ★</bold></green>", List.of()));

                // Thông báo toàn server
                Component broadcastMsg = mm.deserialize(
                        "\n<gradient:#FF0000:#FFD700><bold>🎉 JACKPOT NỔ HŨ X5! 🎉</bold></gradient>\n" +
                                "<yellow>Chúc mừng người chơi <gold><bold>" + player.getName() + "</bold></gold> vừa nổ hũ <gradient:#FF0000:#FFD700><bold>3x KHỐI NETHERITE (NTR)</bold></gradient> lượt cược <gold>" + ConfigManager.formatMoney(currentBetAmount) + "$</gold> HỐT TRỌN QUỸ HŨ TÍCH LŨY + THƯỞNG X5 nhận <gold><bold>" + ConfigManager.formatMoney(netPayout) + "$</bold></gold>!</yellow>\n"
                );
                Bukkit.broadcast(broadcastMsg);
            } else {
                // THẮNG THƯỜNG: Tất cả khoáng sản đều x2.0 tiền cược
                double multiplier = configManager.getNormalWinMultiplier();
                double grossPayout = currentBetAmount * multiplier;
                double tax = grossPayout * taxRate;
                double netPayout = grossPayout - tax;

                if (economy != null) {
                    economy.depositPlayer(player, netPayout);
                }

                lastResultText = "<green><bold>🎉 THẮNG X2 +" + ConfigManager.formatMoney(netPayout) + "$</bold></green>";
                player.sendMessage(configManager.getWinMsg(detailText, multiplier, currentBetAmount, grossPayout, tax, netPayout));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                // Đổi nút Slot 49 thành Banner Thắng Thưởng
                gui.setItem(49, buildItem(Material.EMERALD_BLOCK,
                        "<green><bold>🎉 BẠN ĐÃ THẮNG X2 +" + ConfigManager.formatMoney(netPayout) + "$ 🎉</bold></green>",
                        List.of("<yellow>★ Kết quả: " + detailText + " (Thưởng x2.0)", "", "<yellow>👉 Click để quay tiếp lượt nữa!")));

                // Đổi đèn Slot 20 & 24 thành màu xanh thắng
                gui.setItem(20, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>▶ THẮNG X2 ◀</bold></green>", List.of()));
                gui.setItem(24, buildItem(Material.LIME_STAINED_GLASS_PANE, "<green><bold>▶ THẮNG X2 ◀</bold></green>", List.of()));
            }
        } else {
            // Thua cược
            if (!isUsingTicket) {
                jackpotManager.addLossToPool(currentBetAmount);
                lastResultText = "<red><bold>✘ THUA -" + ConfigManager.formatMoney(currentBetAmount) + "$</bold></red>";
                player.sendMessage(configManager.getLoseMsg(currentBetAmount));
            } else {
                lastResultText = "<red><bold>✘ KHÔNG TRÚNG (ĐÃ DÙNG VÉ QUAY)</bold></red>";
                player.sendMessage(mm.deserialize(
                        "<red><bold>✘ KHÔNG TRÚNG!</bold></red> <gray>Lượt dùng Vé Quay Casino không may mắn. Cố gắng lượt tiếp theo nhé!</gray>"
                ));
            }
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);

            // Đổi nút Slot 49 thành Banner Báo Thua rõ ràng
            if (!isUsingTicket) {
                gui.setItem(49, buildItem(Material.RED_STAINED_GLASS_PANE,
                        "<red><bold>✘ KHÔNG TRÚNG (THUA -" + ConfigManager.formatMoney(currentBetAmount) + "$) ✘</bold></red>",
                        List.of("<gray>Tiền thua đã tích lũy 100% vào Quỹ Hũ Server!", "", "<yellow>👉 Click để thử vận may lượt tiếp theo!")));
            } else {
                gui.setItem(49, buildItem(Material.RED_STAINED_GLASS_PANE,
                        "<red><bold>✘ KHÔNG TRÚNG (ĐÃ DÙNG VÉ QUAY) ✘</bold></red>",
                        List.of("<gray>Lượt quay bằng Vé Quay Casino không may mắn!", "", "<yellow>👉 Click để thử vận may lượt tiếp theo!")));
            }

            // Đổi đèn Slot 20 & 24 thành màu đỏ
            gui.setItem(20, buildItem(Material.RED_STAINED_GLASS_PANE, "<red><bold>▶ KHÔNG TRÚNG ◀</bold></red>", List.of()));
            gui.setItem(24, buildItem(Material.RED_STAINED_GLASS_PANE, "<red><bold>▶ KHÔNG TRÚNG ◀</bold></red>", List.of()));
        }

        // Ghi lịch sử CSDL SQLite
        if (databaseManager != null) {
            databaseManager.recordSpinAsync(
                    gameMode.getId(),
                    player.getUniqueId(),
                    player.getName(),
                    isUsingTicket ? 0 : currentBetAmount,
                    isWin,
                    isWin ? (isJackpot ? currentBetAmount * 5.0 : currentBetAmount * 2.0) : 0,
                    isJackpot ? "JACKPOT NỔ HŨ X5" : (isWin ? "THẮNG X2 " + detailText : (isUsingTicket ? "THUA (Vé Quay)" : "THUA (Đã cộng vào Hũ)"))
            );
        }

        isUsingTicket = false;
        isSpinning = false;
        updateJackpotHUD();
    }

    /**
     * BẮN PHÁO HOA VÀ HIỆU ỨNG PARTICLE VÀNG KIM KHI NỔ HŨ
     */
    private void spawnJackpotFireworksAndParticles(Player p) {
        Location loc = p.getLocation();
        World world = p.getWorld();

        // 1. Pháo hoa
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

        // 2. Vòng tròn particle vàng kim
        for (int degree = 0; degree < 360; degree += 15) {
            double radians = Math.toRadians(degree);
            double x = Math.cos(radians) * 2.0;
            double z = Math.sin(radians) * 2.0;
            Location particleLoc = loc.clone().add(x, 1.0, z);
            world.spawnParticle(Particle.TOTEM_OF_UNDYING, particleLoc, 5, 0.1, 0.1, 0.1, 0.1);
            world.spawnParticle(Particle.END_ROD, particleLoc, 2, 0.05, 0.05, 0.05, 0.02);
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
    public void setForceNextJackpot(boolean force) { this.forceNextJackpot = force; }
}
