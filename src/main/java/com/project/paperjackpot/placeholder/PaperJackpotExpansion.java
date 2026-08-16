package com.project.paperjackpot.placeholder;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.manager.ConfigManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PaperJackpotExpansion - Tích hợp PlaceholderAPI cho PaperJackpot.
 * %paperjackpot_pool%
 * %paperjackpot_pool_raw%
 * %paperjackpot_last_winner%
 * %paperjackpot_my_total_wins%
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
        if (params.equalsIgnoreCase("pool")) {
            return ConfigManager.formatMoney(plugin.getJackpotManager().getJackpotPool()) + "$";
        }

        if (params.equalsIgnoreCase("pool_raw")) {
            return String.valueOf(plugin.getJackpotManager().getJackpotPool());
        }

        if (params.equalsIgnoreCase("last_winner")) {
            String winner = plugin.getJackpotManager().getLastWinnerName();
            return winner != null ? winner : "Chưa có";
        }

        if (params.equalsIgnoreCase("my_total_wins")) {
            if (player == null) return "0$";
            double total = plugin.getDatabaseManager().getPlayerTotalWins(player.getUniqueId());
            return ConfigManager.formatMoney(total) + "$";
        }

        return null;
    }
}
