package com.project.paperjackpot.database;

import com.project.paperjackpot.PaperJackpot;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DatabaseManager - Quản lý CSDL SQLite lưu lịch sử quay hũ, Quỹ Nổ Hũ Tích Lũy, Bảng Xếp Hạng Top & Thống Kê May Mắn Cá Nhân.
 */
public class DatabaseManager {

    private final PaperJackpot plugin;
    private Connection connection;

    public record TopWinnerEntry(UUID uuid, String name, double totalPayout, int winCount) {}

    public record PlayerStatsEntry(
            int totalSpins,
            int totalWins,
            double winRate,
            double totalWagered,
            double totalWon,
            double profit,
            double roi
    ) {}

    public DatabaseManager(PaperJackpot plugin) {
        this.plugin = plugin;
        setupDatabase();
    }

    private void setupDatabase() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();

            File dbFile = new File(dataFolder, "jackpot_history.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                // Bảng lịch sử quay cược
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS spin_history (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "room_id INTEGER NOT NULL, " +
                                "player_uuid TEXT NOT NULL, " +
                                "player_name TEXT NOT NULL, " +
                                "bet_amount REAL NOT NULL, " +
                                "is_win INTEGER NOT NULL, " +
                                "payout REAL NOT NULL, " +
                                "result TEXT NOT NULL, " +
                                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                                ")"
                );

                // Bảng lưu Quỹ Nổ Hũ Tích Lũy Server
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS jackpot_pool (" +
                                "key TEXT PRIMARY KEY, " +
                                "amount REAL NOT NULL" +
                                ")"
                );

                // Bảng lưu thời gian điểm danh lượt quay miễn phí hằng ngày
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS daily_free_spins (" +
                                "player_uuid TEXT PRIMARY KEY, " +
                                "last_claim INTEGER NOT NULL" +
                                ")"
                );

                // Bảng lưu thời điểm chốt thưởng tuần gần nhất
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS weekly_season (" +
                                "key TEXT PRIMARY KEY, " +
                                "last_reward_timestamp INTEGER NOT NULL" +
                                ")"
                );

                // Bảng lưu vé quay cá nhân (Vé Thường & Vé VIP Highroller 500k$)
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS player_tickets (" +
                                "player_uuid TEXT PRIMARY KEY, " +
                                "tickets INTEGER NOT NULL DEFAULT 0, " +
                                "vip_tickets INTEGER NOT NULL DEFAULT 0" +
                                ")"
                );

                try {
                    stmt.executeUpdate("ALTER TABLE player_tickets ADD COLUMN vip_tickets INTEGER NOT NULL DEFAULT 0");
                } catch (SQLException ignored) {} // Cột đã tồn tại

                // Bảng Anti-Dupe: Lưu mã định danh Serial UUID của từng tấm vé vật phẩm đã được nạp
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS redeemed_ticket_serials (" +
                                "ticket_uuid TEXT PRIMARY KEY, " +
                                "redeemed_by TEXT NOT NULL, " +
                                "redeemed_at INTEGER NOT NULL" +
                                ")"
                );
            }

            plugin.getLogger().info("[Database] CSDL SQLite đã khởi tạo thành công!");
        } catch (SQLException e) {
            plugin.getLogger().severe("[Database] Lỗi khởi tạo CSDL: " + e.getMessage());
        }
    }

    public double loadJackpotPool(double defaultAmount) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT amount FROM jackpot_pool WHERE key = 'server_pool'")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("amount");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc Quỹ Hũ: " + e.getMessage());
        }

        saveJackpotPoolAsync(defaultAmount);
        return defaultAmount;
    }

    public void saveJackpotPoolAsync(double amount) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR REPLACE INTO jackpot_pool (key, amount) VALUES ('server_pool', ?)"
            )) {
                ps.setDouble(1, amount);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("[Database] Lỗi lưu Quỹ Hũ: " + e.getMessage());
            }
        });
    }

    public void recordSpinAsync(int roomId, UUID playerUuid, String playerName,
                                 double betAmount, boolean isWin, double payout, String result) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spin_history (room_id, player_uuid, player_name, bet_amount, is_win, payout, result) VALUES (?, ?, ?, ?, ?, ?, ?)"
            )) {
                ps.setInt(1, roomId);
                ps.setString(2, playerUuid.toString());
                ps.setString(3, playerName);
                ps.setDouble(4, betAmount);
                ps.setInt(5, isWin ? 1 : 0);
                ps.setDouble(6, payout);
                ps.setString(7, result);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("[Database] Lỗi ghi lịch sử: " + e.getMessage());
            }
        });
    }

    public List<TopWinnerEntry> getTopWinners(int limit) {
        List<TopWinnerEntry> topList = new ArrayList<>();
        String sql = "SELECT player_uuid, player_name, SUM(payout) as total_won, COUNT(id) as win_count " +
                "FROM spin_history WHERE is_win = 1 GROUP BY player_uuid ORDER BY total_won DESC LIMIT ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuidStr = rs.getString("player_uuid");
                UUID uuid = null;
                if (uuidStr != null) {
                    try {
                        uuid = UUID.fromString(uuidStr);
                    } catch (IllegalArgumentException ignored) {}
                }
                topList.add(new TopWinnerEntry(
                        uuid,
                        rs.getString("player_name"),
                        rs.getDouble("total_won"),
                        rs.getInt("win_count")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc Bảng Xếp Hạng Top: " + e.getMessage());
        }
        return topList;
    }

    public double getPlayerTotalWins(UUID playerUuid) {
        String sql = "SELECT SUM(payout) as total_won FROM spin_history WHERE player_uuid = ? AND is_win = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_won");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc tổng tiền thắng người chơi: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * TÍNH TOÁN BẢNG THỐNG KÊ MAY MẮN CÁ NHÂN (ROI, Tỷ lệ thắng/thua, Lợi nhuận)
     */
    public PlayerStatsEntry getPlayerStats(UUID playerUuid) {
        String sql = "SELECT COUNT(id) as total_spins, " +
                "SUM(CASE WHEN is_win = 1 THEN 1 ELSE 0 END) as total_wins, " +
                "SUM(bet_amount) as total_wagered, " +
                "SUM(payout) as total_won " +
                "FROM spin_history WHERE player_uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int totalSpins = rs.getInt("total_spins");
                int totalWins = rs.getInt("total_wins");
                double totalWagered = rs.getDouble("total_wagered");
                double totalWon = rs.getDouble("total_won");

                double winRate = totalSpins > 0 ? ((double) totalWins / totalSpins) * 100.0 : 0.0;
                double profit = totalWon - totalWagered;
                double roi = totalWagered > 0 ? (profit / totalWagered) * 100.0 : 0.0;

                return new PlayerStatsEntry(totalSpins, totalWins, winRate, totalWagered, totalWon, profit, roi);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi tính thống kê người chơi: " + e.getMessage());
        }
        return new PlayerStatsEntry(0, 0, 0, 0, 0, 0, 0);
    }

    public long getLastFreeSpinTime(UUID playerUuid) {
        String sql = "SELECT last_claim FROM daily_free_spins WHERE player_uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("last_claim");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc điểm danh hằng ngày: " + e.getMessage());
        }
        return 0L;
    }

    public void setLastFreeSpinTimeAsync(UUID playerUuid, long timestamp) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT OR REPLACE INTO daily_free_spins (player_uuid, last_claim) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, playerUuid.toString());
                ps.setLong(2, timestamp);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("[Database] Lỗi lưu điểm danh hằng ngày: " + e.getMessage());
            }
        });
    }

    public long getLastWeeklyRewardTime() {
        String sql = "SELECT last_reward_timestamp FROM weekly_season WHERE key = 'last_reward'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("last_reward_timestamp");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc thời gian thưởng tuần: " + e.getMessage());
        }
        return 0L;
    }

    public void saveLastWeeklyRewardTimeAsync(long timestamp) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT OR REPLACE INTO weekly_season (key, last_reward_timestamp) VALUES ('last_reward', ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, timestamp);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("[Database] Lỗi lưu thời gian thưởng tuần: " + e.getMessage());
            }
        });
    }

    public List<String> getRecentHistory(int limit) {
        List<String> history = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM spin_history ORDER BY id DESC LIMIT ?"
        )) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String line = String.format("[%s] Chế độ #%d | %s | Cược: %,.0f$ | %s | Thưởng: %,.0f$ | %s",
                        rs.getString("timestamp"),
                        rs.getInt("room_id"),
                        rs.getString("player_name"),
                        rs.getDouble("bet_amount"),
                        rs.getInt("is_win") == 1 ? "THẮNG" : "THUA",
                        rs.getDouble("payout"),
                        rs.getString("result")
                );
                history.add(line);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc lịch sử: " + e.getMessage());
        }
        return history;
    }

    public int getTickets(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT tickets FROM player_tickets WHERE player_uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("tickets");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc vé quay: " + e.getMessage());
        }
        return 0;
    }

    public void addTickets(UUID uuid, int count) {
        String sql = "INSERT INTO player_tickets (player_uuid, tickets) VALUES (?, ?) " +
                "ON CONFLICT(player_uuid) DO UPDATE SET tickets = tickets + ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, count);
            ps.setInt(3, count);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi thêm vé quay: " + e.getMessage());
        }
    }

    public boolean removeTickets(UUID uuid, int count) {
        String sql = "UPDATE player_tickets SET tickets = tickets - ? WHERE player_uuid = ? AND tickets >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setString(2, uuid.toString());
            ps.setInt(3, count);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi trừ vé quay: " + e.getMessage());
            return false;
        }
    }

    public int getVipTickets(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT vip_tickets FROM player_tickets WHERE player_uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("vip_tickets");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đọc vé VIP: " + e.getMessage());
        }
        return 0;
    }

    public void addVipTickets(UUID uuid, int count) {
        String sql = "INSERT INTO player_tickets (player_uuid, vip_tickets) VALUES (?, ?) " +
                "ON CONFLICT(player_uuid) DO UPDATE SET vip_tickets = vip_tickets + ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, count);
            ps.setInt(3, count);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi thêm vé VIP: " + e.getMessage());
        }
    }

    public boolean removeVipTickets(UUID uuid, int count) {
        String sql = "UPDATE player_tickets SET vip_tickets = vip_tickets - ? WHERE player_uuid = ? AND vip_tickets >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setString(2, uuid.toString());
            ps.setInt(3, count);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi trừ vé VIP: " + e.getMessage());
            return false;
        }
    }

    // ===== PHẦN TÍNH NĂNG CHỐNG DUPE TẤM VÉ BẰNG MÃ SERIAL NBT UUID =====
    public boolean isTicketSerialRedeemed(String ticketSerialUuid) {
        if (ticketSerialUuid == null || ticketSerialUuid.isEmpty()) return false;
        String sql = "SELECT 1 FROM redeemed_ticket_serials WHERE ticket_uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ticketSerialUuid);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database Anti-Dupe] Lỗi kiểm tra vé trùng lặp: " + e.getMessage());
            return false;
        }
    }

    public boolean markTicketSerialRedeemed(String ticketSerialUuid, UUID playerUuid) {
        if (ticketSerialUuid == null || ticketSerialUuid.isEmpty()) return true;
        String sql = "INSERT INTO redeemed_ticket_serials (ticket_uuid, redeemed_by, redeemed_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ticketSerialUuid);
            ps.setString(2, playerUuid.toString());
            ps.setLong(3, System.currentTimeMillis());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database Anti-Dupe] Lỗi đánh dấu vé đã sử dụng (Phát hiện trùng mã Serial): " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("[Database] Đã đóng kết nối CSDL.");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[Database] Lỗi đóng CSDL: " + e.getMessage());
        }
    }
}
