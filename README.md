# PaperJackpot 🎰

![Paper Version](https://img.shields.io/badge/Paper-1.20%2B-blue?style=flat-square)
![Java Version](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Author](https://img.shields.io/badge/Author-Huy%20Phan-purple?style=flat-square)

PaperJackpot là plugin Casino Nổ Hũ (Slot Machine 3x3) chuyên nghiệp dành cho máy chủ Minecraft Paper / Spigot (1.20+). Plugin cung cấp hệ thống cuộn dọc mượt mà, quản lý 3 chế độ nguồn tiền, tự động trao thưởng & reset Bảng Xếp Hạng Hologram theo tuần, cùng hệ thống chống dupe 5 lớp bảo mật.

---

## 📌 Tính Năng Nổi Bật

### 🎰 Cuộn Dọc Slot Machine 3x3
- Hiệu ứng trượt cuộn dọc 3 cột khoáng sản sống động, tự động giảm tốc và dừng tại hàng chiến thắng (Slots 21, 22, 23).

### 💰 Quỹ Hũ Server Tích Lũy Real-time
- 100% tiền thua cược của người chơi (khi dùng tiền Vault) tự động được nạp trực tiếp vào Quỹ Hũ chung của server.
- Thanh **BossBar** trên cùng màn hình cập nhật số dư Quỹ Hũ thời gian thực cho tất cả người chơi online.

### 💳 3 Chế Độ Nguồn Thanh Toán (Slot 38)
1. **Tiền Vault ($)**: Đặt cược tự do các mức 1k, 10k, 100k, 500k.
2. **Vé Quay Thường (CMD 777)**: Dùng vé quay miễn phí 100% cho cược 1k, 10k, 100k (tự động khóa mức 500k).
3. **Vé VIP Highroller (CMD 888)**: Dùng vé VIP chuyên biệt cho mức cược cao cấp 500k$.

### 🏆 Đua Top Mùa Giải Tuần & Trao Thưởng Tự Động
- Tự động tổng kết giải thưởng vào **23:59 Chủ Nhật hàng tuần** (múi giờ Việt Nam Asia/Ho_Chi_Minh).
- Tự động cộng Tiền Vault ($) & Vé Quay vào ví CSDL cho Top 1, Top 2, Top 3.
- Tự động làm mới dữ liệu và reset Bảng Hologram 3D sang tuần thi đấu mới.

### 🛡️ Hệ Thống Bảo Mật Anti-Dupe 5 Lớp
- Kiểm tra NBT Tag, UUID Serial duy nhất cho từng tấm vé vật phẩm.
- Ghi vết CSDL SQLite, khóa thao tác click nhanh / tráo tay / Shift-click trong GUI.

---

## 📊 Tỷ Lệ & Phần Thưởng

| Kết Quả | Tỷ Lệ | Điều Kiện Thắng | Phần Thưởng |
| :--- | :---: | :--- | :--- |
| **Thắng Thường** | `25.0%` | Trùng 3 ô khoáng sản hàng ngang | **x2.0 Tiền cược** (Thuế 10%) |
| **Nổ Hũ Jackpot** | `0.50%` | Trùng 3x Khối Netherite | **x5.0 Tiền cược + 100% Quỹ Hũ** (Thuế 10%) |
| **Giờ Vàng (Happy Hour)** | `45.0%` | Khung giờ 20:00 - 21:00 (UTC+7) | Nhân đôi cơ hội thắng |

---

## 🎮 Bố Trí Giao Diện GUI (Slot Mappings)

- **Hàng 1 - 4 (Slots 0 - 35)**: Khung chứa 3 cột cuộn Slot Machine.
- **Hàng 5 (Control Bar - Slots 36 - 44)**:
  - `Slot 36`: 📜 Lịch Sử Cược Cá Nhân (10 ván gần nhất)
  - `Slot 37`: 🎟️ Thông tin Số Dư Vé Thường
  - `Slot 38`: 💳 Nguồn Thanh Toán (`Tiền Vault` / `Vé Thường` / `Vé VIP`)
  - `Slot 39`: 🎫 Thông tin Số Dư Vé VIP
  - `Slot 40`: ⚡ Tự Động Quay (Auto Spin)
  - `Slot 41`: 🏆 Bảng Xếp Hạng Top 10 Thần Tài Tuần
  - `Slot 42`: 📈 Thống Kê Cá Nhân & ROI %
  - `Slot 43`: 🎁 Điểm Danh Quay Miễn Phí (Cooldown 24h)
  - `Slot 44`: 🚪 Thoát Giao Diện
- **Hàng 6 (Bet & Spin Bar - Slots 45 - 53)**:
  - `Slot 45`: Cược `1,000$`
  - `Slot 46`: Cược `10,000$`
  - `Slot 47`: Cược `100,000$`
  - `Slot 48`: Cược `500,000$` (VIP / Barrier Lock)
  - `Slot 49`: 🎰 Nút Quay Ngay

---

## 🛠️ Lệnh Command & Quyền Hạn

### Lệnh Người Chơi (Mặc định mở cho tất cả)
- `/jackpot` (Alias: `/jp`, `/quayhu`, `/no-hu`, `/casino`): Mở giao diện Casino Quay Hũ.
- `/jackpot top`: Mở Bảng Xếp Hạng Top 10 Thần Tài Tuần.
- `/jackpot stats`: Xem thống kê ván quay cá nhân.
- `/jackpot tickets`: Kiểm tra số dư vé trong tài khoản.
- `/jackpot time`: Xem thời gian Việt Nam & trạng thái Giờ Vàng.
- `/jackpot help`: Xem danh sách hướng dẫn lệnh.

### Lệnh Quản Trị Viên (`paperjackpot.admin`)
- `/jackpot reload`: Reload lại file `config.yml`.
- `/jackpot setpool <số_tiền>`: Đặt lại tổng số tiền Quỹ Hũ Server.
- `/jackpot resetseason` (hoặc `/jackpot resettop`): Chốt thưởng tuần & reset Bảng Hologram ngay lập tức.
- `/jackpot giveticket <player> <amount>`: Cấp Vé Thường vào ví CSDL.
- `/jackpot giveitemticket <player> <amount>`: Cấp vật phẩm Vé Thường (CMD 777) vào kho đồ.
- `/jackpot givevipticket <player> <amount>`: Cấp Vé VIP vào ví CSDL.
- `/jackpot giveitemvipticket <player> <amount>`: Cấp vật phẩm Vé VIP (CMD 888) vào kho đồ.
- `/jackpot test`: Ép buộc ván quay tiếp theo Nổ Hũ Jackpot (Test hiệu ứng).

---

## 🧩 Tích Hợp PlaceholderAPI

| Placeholder | Mô Tả | Đầu Ra Mẫu |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Quỹ Hũ Server hiện tại | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người trúng Nổ Hũ gần nhất | `HuyPhan` |
| `%paperjackpot_tickets%` | Số dư Vé Thường cá nhân | `15` |
| `%paperjackpot_vip_tickets%` | Số dư Vé VIP cá nhân | `3` |
| `%paperjackpot_top_line_decent_1%` | Dòng 1 Top Tuần (DecentHolograms) | `#1. HuyPhan - 50,000,000$` |
| `%paperjackpot_top_line_1%` | Dòng 1 Top Tuần (FancyHolograms) | `<gold>#1. HuyPhan - 50,000,000$</gold>` |

---

## 🔮 Hướng Dẫn Hologram 3D

Plugin hỗ trợ hiển thị Bảng Xếp Hạng Top 10 Thần Tài Tuần 3D trên **DecentHolograms (`/dh`)** và **FancyHolograms (`/fholo`)**. Xem hướng dẫn chi tiết lệnh cài đặt tại:
📄 **[hologram_guide.md](file:///d:/PaperJackpot/hologram_guide.md)**

---

## 📥 Cài Đặt

1. Thả file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
2. Đảm bảo server đã cài **Vault** và 1 plugin kinh tế (EssentialsX, CMI...).
3. Khởi động lại server và tùy chỉnh thông số tại `plugins/PaperJackpot/config.yml`.

---

## 📞 Tác Giả & Hỗ Trợ
- **Tác giả**: Huy Phan
- **Repository**: [https://github.com/Huyphan68080/PaperJackpot](https://github.com/Huyphan68080/PaperJackpot)
