package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ConfigManager - Quản lý config.yml (Happy Hour Messages, Streak Bonus, BossBar Server, Weekly Rewards).
 */
public class ConfigManager {

    private final PaperJackpot plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private double minBetAmount;
    private double maxBetAmount;
    private double taxRate;
    private double normalWinMultiplier;
    private double jackpotMultiplier;
    private String guiTitle;

    private boolean globalBossbar;
    private boolean weeklyRewardsEnabled;
    private double top1Reward;
    private double top2Reward;
    private double top3Reward;

    private boolean happyHourEnabled;
    private int happyHourStart;
    private int happyHourEnd;

    private boolean streakBonusEnabled;
    private int streakRequiredSpins;

    private final Map<Material, Double> symbolMultipliers = new HashMap<>();

    // Messages
    private String noPermissionMsg;
    private String reloadSuccessMsg;
    private String notEnoughMoneyMsg;
    private String welcomeDealerMsg;
    private String winMsg;
    private String loseMsg;
    private String jackpotWinMsg;
    private String happyHourStartMsg;
    private String happyHourEndMsg;

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,###");

    public ConfigManager(PaperJackpot plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        minBetAmount = config.getDouble("settings.min-bet-amount", 1000.0);
        maxBetAmount = config.getDouble("settings.max-bet-amount", 500000.0);
        taxRate = config.getDouble("settings.tax-rate", 0.10);
        normalWinMultiplier = config.getDouble("settings.normal-win-multiplier", 2.0);
        jackpotMultiplier = config.getDouble("settings.jackpot-multiplier", 5.0);
        guiTitle = config.getString("settings.gui-title", "<gradient:gold:yellow><bold>🎰 CASINO NỔ HŨ</bold></gradient>");

        globalBossbar = config.getBoolean("settings.global-bossbar", true);
        weeklyRewardsEnabled = config.getBoolean("settings.weekly-rewards.enabled", true);
        top1Reward = config.getDouble("settings.weekly-rewards.top1-reward", 500000.0);
        top2Reward = config.getDouble("settings.weekly-rewards.top2-reward", 250000.0);
        top3Reward = config.getDouble("settings.weekly-rewards.top3-reward", 100000.0);

        happyHourEnabled = config.getBoolean("settings.happy-hour.enabled", true);
        happyHourStart = config.getInt("settings.happy-hour.start-hour", 20);
        happyHourEnd = config.getInt("settings.happy-hour.end-hour", 21);

        streakBonusEnabled = config.getBoolean("settings.streak-bonus.enabled", true);
        streakRequiredSpins = config.getInt("settings.streak-bonus.required-spins", 10);

        // Load symbols
        symbolMultipliers.clear();
        ConfigurationSection symbolsSection = config.getConfigurationSection("symbols");
        if (symbolsSection != null) {
            for (String key : symbolsSection.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    double multiplier = symbolsSection.getDouble(key + ".multiplier", 2.0);
                    symbolMultipliers.put(mat, multiplier);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("[Config] Vật phẩm không hợp lệ: " + key);
                }
            }
        }

        // Messages
        noPermissionMsg = config.getString("messages.no-permission", "<red>❌ Bạn không có quyền sử dụng lệnh này!</red>");
        reloadSuccessMsg = config.getString("messages.reload-success", "<green>✅ Đã reload cấu hình PaperJackpot thành công!</green>");
        notEnoughMoneyMsg = config.getString("messages.not-enough-money", "<red>❌ Bạn không đủ tiền! Cần tối thiểu {amount}$ để quay lượt này.</red>");
        welcomeDealerMsg = config.getString("messages.welcome-dealer", "<gradient:gold:yellow><bold>🤖 [CASINO DEALER]</bold></gradient> <yellow>Kính chúc <gold><bold>Ông Chủ / Bà Chủ {player}</bold></gold> quay hũ phát tài, may mắn phát lộc và trúng đại lễ Jackpot! 🎉</yellow>");
        winMsg = config.getString("messages.win", "<green>🎉 TRÚNG! 3x {item} (x2.0) | Cược: {bet}$ → Thưởng: {gross}$ - Thuế (10%): {tax}$ = Nhận thực tế: {net}$</green>");
        loseMsg = config.getString("messages.lose", "<red>❌ Không trúng! Mất {amount}$ cược. (Tiền thua đã nạp 100% vào Quỹ Hũ Server)</red>");
        jackpotWinMsg = config.getString("messages.jackpot-win", "<gradient:#FF0000:#FFD700><bold>🔥 JACKPOT NỔ HŨ X5 + QUỸ HŨ! Cược: {bet}$ → Nhận: {gross}$ - Thuế (10%): {tax}$ = Nhận thực tế: {net}$ 🔥</bold></gradient>");

        happyHourStartMsg = config.getString("messages.happy-hour-start", "\n<gradient:#FFD700:#FF4500><bold>🎆 [CASINO] SỰ KIỆN GIỜ VÀNG ĐÃ BẮT ĐẦU! 🎆</bold></gradient>\n<yellow>⏰ Khung giờ vàng từ <gold><bold>{start}:00</bold></gold> đến <gold><bold>{end}:00</bold></gold> hằng ngày!</yellow>\n<yellow>🔥 Quỹ Hũ hiện tại: <gold><bold>{pool}$</bold></gold> | Nhận ngay <gradient:#FF0000:#FFD700><bold>X2 TỶ LỆ NỔ HŨ JACKPOT</bold></gradient>!</yellow>\n<yellow>👉 Nhanh tay gõ <gold><bold>/jackpot</bold></gold> để hốt trọn Quỹ Hũ ngay kẻo lỡ!</yellow>\n");
        happyHourEndMsg = config.getString("messages.happy-hour-end", "\n<gradient:#FF4500:#FFD700><bold>⏰ [CASINO] SỰ KIỆN GIỜ VÀNG ĐÃ KẾT THÚC! ⏰</bold></gradient>\n<yellow>Khung giờ vàng Nổ Hũ hôm nay đã chính thức khép lại. Cảm ơn các Đại Gia đã tham gia!</yellow>\n<yellow>🎉 Hẹn gặp lại các chủ hũ vào <gold><bold>{start}:00 ngày mai</bold></gold> nhé!</yellow>\n");

        plugin.getLogger().info("[Config] Đã load cấu hình PaperJackpot thành công!");
    }

    public boolean isHappyHourActive() {
        if (!happyHourEnabled) return false;
        int currentHour = LocalTime.now().getHour();
        return currentHour >= happyHourStart && currentHour < happyHourEnd;
    }

    // === Getters ===

    public MiniMessage getMiniMessage() { return mm; }
    public double getMinBetAmount() { return minBetAmount; }
    public double getMaxBetAmount() { return maxBetAmount; }
    public double getTaxRate() { return taxRate; }
    public double getNormalWinMultiplier() { return normalWinMultiplier; }
    public double getJackpotMultiplier() { return jackpotMultiplier; }
    public String getGuiTitle() { return guiTitle; }

    public boolean isGlobalBossbarEnabled() { return globalBossbar; }
    public boolean isWeeklyRewardsEnabled() { return weeklyRewardsEnabled; }
    public double getTop1Reward() { return top1Reward; }
    public double getTop2Reward() { return top2Reward; }
    public double getTop3Reward() { return top3Reward; }

    public boolean isHappyHourEnabled() { return happyHourEnabled; }
    public int getHappyHourStart() { return happyHourStart; }
    public int getHappyHourEnd() { return happyHourEnd; }

    public boolean isStreakBonusEnabled() { return streakBonusEnabled; }
    public int getStreakRequiredSpins() { return streakRequiredSpins; }

    public double getSymbolMultiplier(Material mat) {
        if (mat == Material.NETHERITE_BLOCK) return jackpotMultiplier;
        return normalWinMultiplier;
    }

    // === Message Getters ===

    public Component getNoPermissionMsg() { return mm.deserialize(noPermissionMsg); }
    public Component getReloadSuccessMsg() { return mm.deserialize(reloadSuccessMsg); }

    public Component getNotEnoughMoneyMsg(double amount) {
        return mm.deserialize(notEnoughMoneyMsg.replace("{amount}", formatMoney(amount)));
    }

    public Component getWelcomeDealerMsg(String playerName) {
        return mm.deserialize(welcomeDealerMsg.replace("{player}", playerName));
    }

    public Component getWinMsg(String itemName, double multiplier, double bet, double gross, double tax, double net) {
        return mm.deserialize(winMsg
                .replace("{item}", itemName)
                .replace("{multiplier}", String.format("%.1f", multiplier))
                .replace("{bet}", formatMoney(bet))
                .replace("{gross}", formatMoney(gross))
                .replace("{tax}", formatMoney(tax))
                .replace("{net}", formatMoney(net)));
    }

    public Component getLoseMsg(double amount) {
        return mm.deserialize(loseMsg.replace("{amount}", formatMoney(amount)));
    }

    public Component getJackpotWinMsg(double bet, double gross, double tax, double net) {
        return mm.deserialize(jackpotWinMsg
                .replace("{bet}", formatMoney(bet))
                .replace("{gross}", formatMoney(gross))
                .replace("{tax}", formatMoney(tax))
                .replace("{net}", formatMoney(net)));
    }

    public Component getHappyHourStartMsg() {
        double pool = plugin.getJackpotManager() != null ? plugin.getJackpotManager().getJackpotPool() : 0.0;
        return mm.deserialize(happyHourStartMsg
                .replace("{start}", String.valueOf(happyHourStart))
                .replace("{end}", String.valueOf(happyHourEnd))
                .replace("{pool}", formatMoney(pool)));
    }

    public Component getHappyHourEndMsg() {
        return mm.deserialize(happyHourEndMsg
                .replace("{start}", String.valueOf(happyHourStart))
                .replace("{end}", String.valueOf(happyHourEnd)));
    }

    public static String formatMoney(double amount) {
        return MONEY_FORMAT.format(amount);
    }
}
