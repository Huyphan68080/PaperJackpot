package com.project.paperjackpot.manager;

import com.project.paperjackpot.PaperJackpot;
import com.project.paperjackpot.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

/**
 * HologramManager - Quản lý Hologram 3D Nổi Trực Tiếp Trong Thế Giới (TextDisplay native 1.19.4+ / 1.20+ / 1.21+).
 * Nền trong suốt 100%, bóng chữ sắc nét, tự động xoay 360 độ về phía người chơi.
 */
public class HologramManager {

    private final PaperJackpot plugin;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final MiniMessage mm;

    private Location holoLocation;
    private UUID holoEntityUuid;
    private BukkitTask updateTask;

    public HologramManager(PaperJackpot plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.databaseManager = plugin.getDatabaseManager();
        this.mm = configManager.getMiniMessage();

        loadHologramFromConfig();
        startAutoUpdateTask();
    }

    public void loadHologramFromConfig() {
        if (plugin.getConfig().contains("hologram.world")) {
            String worldName = plugin.getConfig().getString("hologram.world");
            if (worldName != null) {
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    double x = plugin.getConfig().getDouble("hologram.x");
                    double y = plugin.getConfig().getDouble("hologram.y");
                    double z = plugin.getConfig().getDouble("hologram.z");
                    this.holoLocation = new Location(world, x, y, z);
                    plugin.getLogger().info("[Hologram] Đã load vị trí Hologram 3D tại: " + worldName + " (" + String.format("%.1f, %.1f, %.1f", x, y, z) + ")");
                }
            }
        }
    }

    public void saveHologramToConfig(Location loc) {
        this.holoLocation = loc;
        if (loc != null && loc.getWorld() != null) {
            plugin.getConfig().set("hologram.world", loc.getWorld().getName());
            plugin.getConfig().set("hologram.x", loc.getX());
            plugin.getConfig().set("hologram.y", loc.getY());
            plugin.getConfig().set("hologram.z", loc.getZ());
            plugin.saveConfig();
        } else {
            plugin.getConfig().set("hologram", null);
            plugin.saveConfig();
        }
    }

    public void setHologramLocation(Location loc) {
        removeHologramEntity();
        saveHologramToConfig(loc);
        updateHologramDisplay();
    }

    public void removeHologram() {
        removeHologramEntity();
        saveHologramToConfig(null);
    }

    private void removeHologramEntity() {
        if (holoLocation != null && holoLocation.getWorld() != null) {
            for (Entity entity : holoLocation.getWorld().getEntities()) {
                if (entity instanceof TextDisplay textDisplay) {
                    if (holoEntityUuid != null && entity.getUniqueId().equals(holoEntityUuid)) {
                        textDisplay.remove();
                    } else if (textDisplay.getScoreboardTags().contains("paperjackpot_hologram")) {
                        textDisplay.remove();
                    }
                }
            }
        }
        holoEntityUuid = null;
    }

    public void startAutoUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        // Tự động cập nhật Hologram 3D mỗi 15 giây (300 ticks)
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateHologramDisplay();
            }
        }.runTaskTimer(plugin, 40L, 300L);
    }

    public void updateHologramDisplay() {
        if (holoLocation == null || holoLocation.getWorld() == null) return;

        World world = holoLocation.getWorld();
        TextDisplay displayEntity = null;

        // Tìm Entity TextDisplay cũ hoặc tạo mới
        if (holoEntityUuid != null) {
            Entity found = Bukkit.getEntity(holoEntityUuid);
            if (found instanceof TextDisplay textDisplay) {
                displayEntity = textDisplay;
            }
        }

        if (displayEntity == null || !displayEntity.isValid()) {
            removeHologramEntity();
            try {
                displayEntity = (TextDisplay) world.spawnEntity(holoLocation, EntityType.TEXT_DISPLAY);
                displayEntity.setBillboard(TextDisplay.Billboard.CENTER);
                displayEntity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0)); // Nền trong suốt 100% không bị ô đen bao quanh!
                displayEntity.setShadowed(true); // Bóng chữ nổi 3D cực đẹp
                displayEntity.addScoreboardTag("paperjackpot_hologram");
                this.holoEntityUuid = displayEntity.getUniqueId();
            } catch (Exception e) {
                plugin.getLogger().warning("[Hologram] Không thể tạo TextDisplay Entity: " + e.getMessage());
                return;
            }
        }

        // Cập nhật cấu hình hiển thị
        displayEntity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        displayEntity.setShadowed(true);
        displayEntity.setBillboard(TextDisplay.Billboard.CENTER);

        // Dựng nội dung Hologram 3D lộng lẫy
        StringBuilder sb = new StringBuilder();
        sb.append("<gradient:#FFD700:#FFA500><bold>✦ TOP ĐẠI GIA CASINO NỔ HŨ ✦</bold></gradient>\n\n");

        if (databaseManager != null) {
            List<DatabaseManager.TopWinnerEntry> topList = databaseManager.getTopWinners(10);
            for (int i = 1; i <= 10; i++) {
                if (i <= topList.size()) {
                    DatabaseManager.TopWinnerEntry entry = topList.get(i - 1);
                    String rankPrefix = switch (i) {
                        case 1 -> "<gold><bold>#1</bold></gold> <white><bold>" + entry.name() + "</bold></white> <gray>≫</gray> <green><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></green>";
                        case 2 -> "<gray><bold>#2</bold></gray> <white><bold>" + entry.name() + "</bold></white> <gray>≫</gray> <green><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></green>";
                        case 3 -> "<gradient:#CD7F32:#8B4513><bold>#3</bold></gradient> <white><bold>" + entry.name() + "</bold></white> <gray>≫</gray> <green><bold>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</bold></green>";
                        case 4, 5 -> "<yellow><bold>#" + i + "</bold></yellow> <white>" + entry.name() + "</white> <gray>≫</gray> <green>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</green>";
                        default -> "<gray>#" + i + "</gray> <white>" + entry.name() + "</white> <gray>≫</gray> <green>" + ConfigManager.formatMoney(entry.totalPayout()) + "$</green>";
                    };
                    sb.append(rankPrefix).append("\n");
                } else {
                    sb.append("<dark_gray>#").append(i).append(" --- ≫ ---</dark_gray>\n");
                }
            }
        } else {
            sb.append("<gray>Chưa có dữ liệu CSDL Top 10</gray>\n");
        }

        sb.append("\n<yellow><italic>👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!</italic></yellow>");

        Component content = mm.deserialize(sb.toString());
        displayEntity.text(content);
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        removeHologramEntity();
    }

    public Location getHoloLocation() {
        return holoLocation;
    }
}
