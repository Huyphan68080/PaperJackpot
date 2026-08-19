# PaperJackpot

> Plugin Casino Quay Hũ Nổ Hũ 3x3 hiệu ứng cuộn mượt mà dành cho máy chủ Minecraft Paper / Spigot 1.20+.

---

## 📖 Giới Thiệu

**PaperJackpot** là giải pháp Casino Quay Hũ dành cho máy chủ Minecraft hiện đại. Plugin được xây dựng với mục tiêu tối ưu hiệu năng (Paper 1.20+), tích hợp giao diện GUI cuộn 3x3 mượt mà, quản lý 3 nguồn tiền đặt cược riêng biệt, cơ chế chống dupe 5 lớp và tự động chốt thưởng Mùa Giải Tuần vào đúng 23:59 Chủ Nhật (giờ Việt Nam).

---

## 🌟 Tính Năng Nổi Bật

### 1. Hiệu Ứng Slot Machine 3x3
- Cuộn trượt 3 cột khoáng sản từ trên xuống với tốc độ mượt mà.
- Tự động kiểm tra và Highlight 3 ô hàng ngang thắng cược (Slots 21, 22, 23).

### 2. Quỹ Hũ Server & BossBar Real-time
- 100% tiền thua cược từ Tiền Vault ($) tự động được nạp trực tiếp vào Quỹ Hũ chung của server.
- Thanh BossBar ở mép trên màn hình liên tục cập nhật số dư Quỹ Hũ cho toàn bộ người chơi online.

### 3. Nguồn Thanh Toán Linh Hoạt (Slot 38)
- **Tiền Vault ($)**: Cược các mức 1k, 10k, 100k, 500k.
- **Vé Quay Thường (CMD 777)**: Dùng vé nạp từ kho đồ, cho phép quay mức 1k, 10k, 100k (tự động khóa mức 500k).
- **Vé VIP Highroller (CMD 888)**: Dùng vé VIP chuyên biệt cho mức cược tối đa 500,000$.

### 4. Đua Top Mùa Giải Tuần & Trao Thưởng Tự Động
- Tự động chốt giải thưởng lúc **23:59 Chủ Nhật (UTC+7)**.
- Trao tiền Vault + Vé Quay trực tiếp vào CSDL cho Top 1, Top 2, Top 3.
- Tự động xóa lịch sử cũ và cập nhật Bảng Hologram 3D sang Mùa Giải Tuần mới.

### 5. Cơ Chế Chống Dupe Vé 5 Lớp
- Gắn UUID Serial NBT riêng cho từng item vé.
- Lưu trữ bền vững trên SQLite và khóa tuyệt đối hành vi click nhanh / Shift-click trong GUI.

---

## 📊 Tỷ Lệ & Phần Thưởng

| Hạng Mục | Tỷ Lệ | Điều Kiện | Phần Thưởng |
| :--- | :---: | :--- | :--- |
| **Thắng Thường** | `25.0%` | Trùng 3 ô khoáng sản hàng ngang | x2.0 Tiền cược (Trừ 10% thuế) |
| **Nổ Hũ Jackpot** | `0.50%` | Trùng 3x Khối Netherite | x5.0 Tiền cược + 100% Quỹ Hũ Server (Trừ 10% thuế) |
| **Giờ Vàng (Happy Hour)** | `45.0%` | 20:00 - 21:00 (UTC+7) hằng ngày | Nhân đôi tỷ lệ trúng thưởng |

---

## 🎛️ Bố Trí Giao Diện GUI

```
+-------------------------------------------------------+
|  Row 1 - 4: Slots 00 - 35 (Khung Cuộn Slot Machine)   |
+-------------------------------------------------------+
|  Slot 36: Lịch Sử Cược    |  Slot 41: Top 10 Thần Tài  |
|  Slot 37: Dư Vé Thường    |  Slot 42: Thống Kê ROI %   |
|  Slot 38: Chọn Nguồn Tiền  |  Slot 43: Quay Free 24h    |
|  Slot 39: Dư Vé VIP       |  Slot 44: Thoát           |
|  Slot 40: Auto Spin       |                           |
+-------------------------------------------------------+
|  Slots 45 - 48: Mức Cược (1k, 10k, 100k, 500k)        |
|  Slot 49: Nút Quay Ngay                               |
+-------------------------------------------------------+
```

---

## 📜 Lệnh Command & Quyền Hạn

### Lệnh Người Chơi (Dành cho tất cả người chơi)

Lệnh chính: `/jackpot` *(Alias: `/jp`, `/quayhu`, `/no-hu`, `/casino`)*

| Câu Lệnh | Mô Tả |
| :--- | :--- |
| `/jackpot` | Mở giao diện Casino Quay Hũ cá nhân |
| `/jackpot top` | Xem Bảng Xếp Hạng Top 10 Thần Tài Tuần |
| `/jackpot stats` | Xem thống kê chỉ số may mắn & ROI % |
| `/jackpot tickets` | Kiểm tra số dư Vé Thường & Vé VIP trong ví |
| `/jackpot time` | Xem giờ Việt Nam UTC+7 & trạng thái Giờ Vàng |
| `/jackpot help` | Xem menu hướng dẫn câu lệnh |

### Lệnh Quản Trị Viên (`paperjackpot.admin`)

| Câu Lệnh | Mô Tả |
| :--- | :--- |
| `/jackpot reload` | Reload lại file cấu hình `config.yml` |
| `/jackpot setpool <số_tiền>` | Đặt lại số dư Quỹ Hũ Server |
| `/jackpot resetseason` | Chốt thưởng tuần & reset Bảng Hologram ngay lập tức |
| `/jackpot giveticket <player> <amount>` | Nạp Vé Thường trực tiếp vào ví CSDL |
| `/jackpot giveitemticket <player> <amount>` | Trao item Vé Thường (CMD 777) vào kho đồ |
| `/jackpot givevipticket <player> <amount>` | Nạp Vé VIP trực tiếp vào ví CSDL |
| `/jackpot giveitemvipticket <player> <amount>` | Trao item Vé VIP (CMD 888) vào kho đồ |
| `/jackpot test` | Ép buộc lượt quay tiếp theo Nổ Hũ Jackpot |

---

## 🧩 PlaceholderAPI (PAPI)

| Placeholder | Mô Tả | Ví Dụ |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Số dư Quỹ Hũ Server đã định dạng | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người trúng Nổ Hũ gần nhất | `HuyPhan` |
| `%paperjackpot_tickets%` | Số dư Vé Thường hiện có trong ví | `10` |
| `%paperjackpot_vip_tickets%` | Số dư Vé VIP hiện có trong ví | `3` |
| `%paperjackpot_top_line_decent_1%` ... `10` | Dòng Bảng Top 1-10 cho DecentHolograms (`/dh`) | `#1. HuyPhan - 50,000,000$` |
| `%paperjackpot_top_line_1%` ... `10` | Dòng Bảng Top 1-10 cho FancyHolograms (`/fholo`) | `<gold>#1. HuyPhan - 50,000,000$</gold>` |

---

## 🔮 Hướng Dẫn Cài Đặt Hologram 3D

Plugin hỗ trợ xuất biến PAPI tương thích 100% với **DecentHolograms (`/dh`)** và **FancyHolograms (`/fholo`)**. Xem chi tiết bộ lệnh tạo bảng tại:
📄 **[hologram_guide.md](hologram_guide.md)**

---

## 🛠️ Cài Đặt & Yêu Cầu Server

1. **Yêu cầu**:
   - Server Paper / Spigot `1.20+` (Java 17 trở lên).
   - Plugin **Vault** và một plugin tiền tệ (EssentialsX, CMI...).
   - *(Tùy chọn)* PlaceholderAPI, DecentHolograms / FancyHolograms.
2. **Cài đặt**:
   - Thả file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
   - Khởi động lại Server.
   - Tùy chỉnh thông số trong file `plugins/PaperJackpot/config.yml` theo nhu cầu.

---

## 👤 Tác Giả
- **Phát triển bởi**: Huy Phan
- **Mã nguồn**: [GitHub Repository](https://github.com/Huyphan68080/PaperJackpot)
- **Phiên bản**: 1.0.0 (Tương thích Paper 1.21.1)
