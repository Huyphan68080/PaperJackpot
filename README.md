<div align="center">

# 🎰 PaperJackpot

**Plugin Casino Quay Hũ Nổ Hũ 3x3 Cao Cấp Dành Cho Máy Chủ Minecraft (Paper / Spigot 1.20+)**

![Minecraft Paper](https://img.shields.io/badge/Paper-1.20%2B-blue?style=for-the-badge&logo=paperMC)
![Java Version](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk)
![Vault Supported](https://img.shields.io/badge/Vault-Economy-green?style=for-the-badge)
![PAPI Supported](https://img.shields.io/badge/PlaceholderAPI-Supported-purple?style=for-the-badge)
![Author](https://img.shields.io/badge/Author-Huy%20Phan-red?style=for-the-badge)

---

</div>

## 📌 Tổng Quan Dịch Vụ

**PaperJackpot** là giải pháp Casino Nổ Hũ toàn diện dành cho các máy chủ Minecraft Paper / Spigot hiện đại. Plugin mang đến trải nghiệm giải trí sòng bạc Slot Machine 3x3 đỉnh cao với hiệu ứng cuộn mượt mà, quản lý 3 nguồn thanh toán linh hoạt, cơ chế chống dupe 5 lớp nghiêm ngặt và tính năng tự động trao thưởng & làm mới Bảng Xếp Hạng Mùa Giải Tuần vào đúng 23:59 Chủ Nhật (UTC+7).

---

## ✨ Tính Năng Nổi Bật

### 🎰 1. Hiệu Ứng Slot Machine 3x3 Cuộn Dọc Real-Time
- Hiệu ứng trượt cuộn 3 cột khoáng sản từ trên xuống với tốc độ giảm dần mượt mà.
- Tự động kiểm tra và Highlight đường thắng cược hàng ngang (Slots 21, 22, 23).

### 💎 2. Quỹ Hũ Server Tích Lũy Real-Time & BossBar
- 100% tiền thua cược từ Tiền Vault ($) của người chơi tự động được nạp trực tiếp vào Quỹ Hũ Server.
- Thanh **BossBar** trên cùng màn hình cập nhật liên tục số dư Quỹ Hũ thời gian thực cho toàn bộ người chơi online.

### 💳 3. Bộ Chọn Nguồn Thanh Toán Đa Dạng (Slot 38)
- **Tiền Vault ($)**: Đặt cược tự do các mức `1,000$`, `10,000$`, `100,000$`, `500,000$`.
- **Vé Quay Thường (CMD 777)**: Nạp từ vật phẩm kho đồ, quay miễn phí mức 1k, 10k, 100k (tự động khóa mức 500k).
- **Vé VIP Highroller (CMD 888)**: Nạp từ vật phẩm kho đồ, dùng chuyên biệt cho bàn cược cao cấp `500,000$`.

### 🏆 4. Đua Top Mùa Giải Tuần & Trao Thưởng Tự Động
- Tự động chốt giải thưởng lúc **23:59 Chủ Nhật (múi giờ Việt Nam Asia/Ho_Chi_Minh UTC+7)**.
- Trao thưởng trực tiếp Tiền Vault ($) & Vé Quay vào tài khoản CSDL cho Top 1, Top 2, Top 3.
- Tự động xóa lịch sử cũ và cập nhật Bảng Hologram 3D sang Mùa Giải Tuần mới.

### ⚡ 5. Tính Năng Điểm Danh & Quay Tự Động (Auto Spin)
- **Auto Spin (Slot 40)**: Tự động đặt cược và quay liên tục rảnh tay.
- **Điểm Danh 24h (Slot 43)**: Nhận 1 lượt quay miễn phí hằng ngày.

### 🛡️ 6. Bảo Mật Chống Dupe Vé 5 Lớp
- Gắn mã UUID Serial NBT độc bản cho từng tấm vé vật phẩm.
- Lưu trữ bền vững trên CSDL SQLite và khóa tuyệt đối hành vi tráo tay / Shift-click / click nhanh trong GUI.

---

## 📊 Cơ Chế Tỷ Lệ & Phần Thưởng

| Kết Quả | Tỷ Lệ Mặc Định | Điều Kiện Thắng | Phần Thưởng Thắng |
| :--- | :---: | :--- | :--- |
| **Thắng Thường** | **25.0%** | Trùng 3 ô khoáng sản hàng ngang | **x2.0 Tiền Cược** (Trừ 10% thuế) |
| **Nổ Hũ Jackpot** | **0.50%** | Trùng 3x Khối Netherite (NTR) | **x5.0 Tiền Cược + 100% Quỹ Hũ Server** (Trừ 10% thuế) |
| **Giờ Vàng (Happy Hour)** | **45.0%** | Khung giờ 20:00 - 21:00 (UTC+7) | Nhân đôi tỷ lệ thắng thường |
| **Thua** | **74.5%** | Không trùng 3 ô hàng ngang | Nạp 100% tiền thua vào Quỹ Hũ (nếu dùng tiền Vault) |

---

## 🎛️ Bố Trí Giao Diện GUI (Slot Mappings)

```text
+-----------------------------------------------------------------------+
|  ROW 1 - 4 (SLOTS 00 - 35): KHUNG CUỘN SLOT MACHINE 3X3               |
+-----------------------------------------------------------------------+
|  SLOT 36: 📜 Lịch Sử Cược     |  SLOT 41: 🏆 Top 10 Thần Tài Tuần     |
|  SLOT 37: 🎟️ Số Dư Vé Thường   |  SLOT 42: 📈 Thống Kê Cá Nhân (ROI %) |
|  SLOT 38: 💳 Nguồn Thanh Toán  |  SLOT 43: 🎁 Điểm Danh Quay Free 24h  |
|  SLOT 39: 🎫 Số Dư Vé VIP      |  SLOT 44: 🚪 Thoát Giao Diện          |
|  SLOT 40: ⚡ Auto Spin (Tự Động)|                                       |
+-----------------------------------------------------------------------+
|  SLOT 45: Cược 1,000$          |  SLOT 48: Cược 500,000$ (VIP Barrier)  |
|  SLOT 46: Cược 10,000$         |  SLOT 49: 🎰 BẤM QUAY CƯỢC NGAY       |
|  SLOT 47: Cược 100,000$        |  SLOTS 50 - 53: Viền Kính Đen         |
+-----------------------------------------------------------------------+
```

---

## 📜 Danh Sách Câu Lệnh & Quyền Hạn

### 👤 Lệnh Người Chơi (`paperjackpot.use`)
Lệnh chính: `/jackpot` *(Alias: `/jp`, `/quayhu`, `/no-hu`, `/casino`)*

| Câu Lệnh | Quyền Hạn | Mô Tả Chi Tiết |
| :--- | :--- | :--- |
| `/jackpot` | `paperjackpot.use` | Mở giao diện Casino Quay Hũ cá nhân |
| `/jackpot top` | `paperjackpot.use` | Mở Bảng Xếp Hạng Top 10 Thần Tài Tuần |
| `/jackpot stats` | `paperjackpot.use` | Mở giao diện Thống Kê Cá Nhân (Tỷ lệ thắng, ROI %) |
| `/jackpot tickets` | `paperjackpot.use` | Kiểm tra số dư Vé Thường & Vé VIP trong ví |
| `/jackpot time` | `paperjackpot.use` | Xem giờ Việt Nam (UTC+7) & trạng thái Giờ Vàng |
| `/jackpot help` | `paperjackpot.use` | Mở danh mục hướng dẫn câu lệnh |

### 👑 Lệnh Quản Trị Viên (`paperjackpot.admin`)

| Câu Lệnh | Quyền Hạn | Mô Tả Chi Tiết |
| :--- | :--- | :--- |
| `/jackpot reload` | `paperjackpot.admin` | Reload lại file cấu hình `config.yml` |
| `/jackpot setpool <số_tiền>` | `paperjackpot.admin` | Đặt lại tổng số tiền Quỹ Hũ Server |
| `/jackpot resetseason` | `paperjackpot.admin` | Chốt thưởng Mùa Giải Tuần & reset Bảng Hologram ngay lập tức |
| `/jackpot giveticket <player> <amount>` | `paperjackpot.admin` | Nạp Vé Thường trực tiếp vào ví CSDL |
| `/jackpot giveitemticket <player> <amount>` | `paperjackpot.admin` | Cấp vật phẩm Vé Thường (CMD 777) vào kho đồ |
| `/jackpot givevipticket <player> <amount>` | `paperjackpot.admin` | Nạp Vé VIP trực tiếp vào ví CSDL |
| `/jackpot giveitemvipticket <player> <amount>` | `paperjackpot.admin` | Cấp vật phẩm Vé VIP (CMD 888) vào kho đồ |
| `/jackpot test` | `paperjackpot.admin` | Ép buộc lượt quay tiếp theo Nổ Hũ Jackpot |

---

## 🧩 Tích Hợp PlaceholderAPI (PAPI)

| Placeholder | Mô Tả | Đầu Ra Mẫu |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Số dư Quỹ Hũ Server đã định dạng | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người trúng Nổ Hũ gần nhất | `HuyPhan` |
| `%paperjackpot_tickets%` | Số dư Vé Thường cá nhân | `10` |
| `%paperjackpot_vip_tickets%` | Số dư Vé VIP cá nhân | `3` |
| `%paperjackpot_top_line_decent_1%` ... `10` | Dòng Bảng Top 1-10 cho DecentHolograms (`/dh`) | `#1. HuyPhan - 50,000,000$` |
| `%paperjackpot_top_line_1%` ... `10` | Dòng Bảng Top 1-10 cho FancyHolograms (`/fholo`) | `<gold>#1. HuyPhan - 50,000,000$</gold>` |

---

## 🔮 Hướng Dẫn Hologram 3D

Plugin hỗ trợ hiển thị Bảng Xếp Hạng Top 10 Thần Tài Tuần 3D trên **DecentHolograms (`/dh`)** và **FancyHolograms (`/fholo`)**. Xem hướng dẫn lệnh cài đặt chi tiết tại:
📄 **[hologram_guide.md](hologram_guide.md)**

---

## 🚀 Cài Đặt & Cấu Hình

1. **Yêu cầu hệ thống**:
   - Server Paper / Purpur / Spigot `1.20+` (Java 17 trở lên).
   - Plugin **Vault** và một plugin tiền tệ (EssentialsX, CMI...).
   - *(Tùy chọn)* PlaceholderAPI, DecentHolograms hoặc FancyHolograms.
2. **Cài đặt**:
   - Copy file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
   - Khởi động lại Server.
   - Tùy chỉnh thông số trong file `plugins/PaperJackpot/config.yml` theo nhu cầu.

---

<div align="center">

### 👨‍💻 Thông Tin Tác Giả & Bản Quyền

**Phát triển bởi Huy Phan**

[GitHub Repository](https://github.com/Huyphan68080/PaperJackpot) • Compatible with Paper 1.21.1

</div>
