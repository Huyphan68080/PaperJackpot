package com.project.paperjackpot.placeholder;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.manager.ConfigManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PaperJackpotExpansion - Tích hợp PlaceholderAPI cho PaperJackpot & FancyHolograms / DecentHolograms.
 *   %paperjackpot_pool%
 *   %paperjackpot_pool_raw%
 *   %paperjackpot_last_winner%
 *   %paperjackpot_my_total_wins%
 *   %paperjackpot_top_1_name% ... %paperjackpot_top_10_name%
 *   %paperjackpot_top_1_amount% ... %paperjackpot_top_10_amount%
 *   %paperjackpot_top_line_1% ... %paperjackpot_top_line_10%
 */
public class PaperJackpotExpansion extends PlaceholderExpansion {

    private final PaperJackpot plugin;
    private static final Pattern TOP_PATTERN = Pattern.compile("^top_?(\\d+)(?:_(name|amount|value|val|raw|amount_raw))?$");

    public PaperJackpotExpansion(PaperJackpot plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "paperjackpot";
    }

    @Override
    public @NotNull String getAuthor() {
        return "HuyPhan";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String lower = params.toLowerCase();

        if (lower.equals("pool")) {
            return ConfigManager.formatMoney(plugin.getJackpotManager().getJackpotPool()) + "$";
        }

        if (lower.equals("pool_raw")) {
            return String.valueOf(plugin.getJackpotManager().getJackpotPool());
        }

        if (lower.equals("last_winner")) {
            String winner = plugin.getJackpotManager().getLastWinnerName();
            return winner != null ? winner : "Chưa có";
        }

        if (lower.equals("my_total_wins")) {
            if (player == null) return "0$";
            double total = plugin.getDatabaseManager().getPlayerTotalWins(player.getUniqueId());
            return ConfigManager.formatMoney(total) + "$";
        }

        if (lower.equals("top_header")) {
            return "<gradient:#FFD700:#FFA500><bold>✦ TOP ĐẠI GIA CASINO NỔ HŨ ✦</bold></gradient>";
        }

        if (lower.equals("top_footer")) {
            return "<yellow><italic>👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!</italic></yellow>";
        }

        // Xử lý Dòng Đã Định Dạng Sẵn: %paperjackpot_top_line_1% ... %paperjackpot_top_line_10%
        if (lower.startsWith("top_line_legacy_") || lower.startsWith("top_line_decent_")) {
            try {
                int rank = Integer.parseInt(lower.substring(lower.lastIndexOf('_') + 1));
                return getFormattedRankLineLegacy(rank);
            } catch (Exception ignored) {}
        } else if (lower.startsWith("top_line_")) {
            try {
                int rank = Integer.parseInt(lower.substring(lower.lastIndexOf('_') + 1));
                return getFormattedRankLineMiniMessage(rank);
            } catch (Exception ignored) {}
        }

        // Xử lý Regex chuẩn cho các biến Top (%paperjackpot_top_1_name%, %paperjackpot_top_1_amount%, %paperjackpot_top1_name%, v.v.)
        Matcher matcher = TOP_PATTERN.matcher(lower);
        if (matcher.find()) {
            try {
                int rank = Integer.parseInt(matcher.group(1));
                String type = matcher.group(2) != null ? matcher.group(2) : "name";

                if (rank >= 1 && rank <= 10) {
                    List<DatabaseManager.TopWinnerEntry> topList = plugin.getDatabaseManager().getTopWinners(10);
                    if (rank <= topList.size()) {
                        DatabaseManager.TopWinnerEntry entry = topList.get(rank - 1);
                        if (type.equals("amount") || type.equals("value") || type.equals("val")) {
                            return ConfigManager.formatMoney(entry.totalPayout()) + "$";
                        } else if (type.equals("raw") || type.equals("amount_raw")) {
                            return String.valueOf(entry.totalPayout());
                        } else {
                            return entry.name();
                        }
                    } else {
                        return "---";
                    }
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    /**
     * Trả về Dòng Rank định dạng MiniMessage (Dùng cho FancyHolograms)
     */
    public String getFormattedRankLineMiniMessage(int rank) {
        if (rank < 1 || rank > 10) return "";
        List<DatabaseManager.TopWinnerEntry> topList = plugin.getDatabaseManager().getTopWinners(10);
        if (rank <= topList.size()) {
            DatabaseManager.TopWinnerEntry entry = topList.get(rank - 1);
            String rankPrefix = switch (rank) {
                case 1 -> "<gold><bold>#1</bold></gold> <white><bold>" + entry.name() + "</bold></white> <gray>≫</gray> <green><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></green>";
                case 2 -> "<gray><bold>#2</bold></gray> <white><bold>" + entry.name() + "</bold></white> <gray>≫</gray> <green><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></green>";
                case 3 -> "<gradient:#CD7F32:#8B4513><bold>#3</bold></gradient> <white><bold>" + entry.name() + "</bold></white> <gray>≫</gray> <green><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></green>";
                case 4, 5 -> "<yellow><bold>#" + rank + "</bold></yellow> <white>" + entry.name() + "</white> <gray>≫</gray> <green>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</green>";
                default -> "<gray>#" + rank + "</gray> <white>" + entry.name() + "</white> <gray>≫</gray> <green>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</green>";
            };
            return rankPrefix;
        } else {
            return "<dark_gray>#" + rank + " --- ≫ ---</dark_gray>";
        }
    }

    /**
     * Trả về Dòng Rank định dạng Legacy Color Code & (Dùng cho DecentHolograms / HolographicDisplays)
     */
    public String getFormattedRankLineLegacy(int rank) {
        if (rank < 1 || rank > 10) return "";
        List<DatabaseManager.TopWinnerEntry> topList = plugin.getDatabaseManager().getTopWinners(10);
        if (rank <= topList.size()) {
            DatabaseManager.TopWinnerEntry entry = topList.get(rank - 1);
            String rankPrefix = switch (rank) {
                case 1 -> "&6&l#1 &f&l" + entry.name() + " &7≫ &a&l" + ConfigManager.formatMoney(entry.totalPayout()) + "$";
                case 2 -> "&7&l#2 &f&l" + entry.name() + " &7≫ &a&l" + ConfigManager.formatMoney(entry.totalPayout()) + "$";
                case 3 -> "&c&l#3 &f&l" + entry.name() + " &7≫ &a&l" + ConfigManager.formatMoney(entry.totalPayout()) + "$";
                case 4, 5 -> "&e&l#" + rank + " &f" + entry.name() + " &7≫ &a" + ConfigManager.formatMoney(entry.totalPayout()) + "$";
                default -> "&7#" + rank + " &f" + entry.name() + " &7≫ &a" + ConfigManager.formatMoney(entry.totalPayout()) + "$";
            };
            return rankPrefix;
        } else {
            return "&8#" + rank + " --- ≫ ---";
        }
    }
}
