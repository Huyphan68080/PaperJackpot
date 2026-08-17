package com.project.paperjackpot.placeholder;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import com.project.paperjackpot.manager.ConfigManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * PaperJackpotExpansion - Tích hợp PlaceholderAPI cho PaperJackpot & Hologram Leaderboards.
 *   %paperjackpot_pool%
 *   %paperjackpot_pool_raw%
 *   %paperjackpot_last_winner%
 *   %paperjackpot_my_total_wins%
 *   %paperjackpot_top_1_name% ... %paperjackpot_top_10_name%
 *   %paperjackpot_top_1_amount% ... %paperjackpot_top_10_amount%
 */
public class PaperJackpotExpansion extends PlaceholderExpansion {

    private final PaperJackpot plugin;

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

        // Xử lý Placeholder Top 1 đến Top 10 cho Hologram (%paperjackpot_top_1_name%, %paperjackpot_top1_name%, v.v.)
        if (lower.startsWith("top_") || lower.startsWith("top")) {
            String clean = lower.replace("top_", "").replace("top", "");
            try {
                String[] parts = clean.split("_");
                int rank = Integer.parseInt(parts[0]);
                String type = parts.length > 1 ? parts[1] : "name";

                if (rank >= 1 && rank <= 10) {
                    List<DatabaseManager.TopWinnerEntry> topList = plugin.getDatabaseManager().getTopWinners(10);
                    if (rank <= topList.size()) {
                        DatabaseManager.TopWinnerEntry entry = topList.get(rank - 1);
                        if (type.equals("amount")) {
                            return ConfigManager.formatMoney(entry.totalPayout()) + "$";
                        } else if (type.equals("raw") || type.equals("amount_raw")) {
                            return String.valueOf(entry.totalPayout());
                        } else {
                            return entry.name();
                        }
                    } else {
                        return type.contains("amount") ? "---" : "---";
                    }
                }
            } catch (Exception ignored) {}
        }

        return null;
    }
}
