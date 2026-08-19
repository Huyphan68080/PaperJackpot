<div align="center">

# 🎰 PaperJackpot
### *Thế Hệ Plugin Casino Quay Hũ Minecraft Đẳng Cấp Với Thiết Kế Đồ Họa & Trải Nghiệm Người Dùng (UI/UX) Chuẩn Quốc Tế*

![Minecraft Paper](https://img.shields.io/badge/Paper-1.20%2B-blue?style=for-the-badge&logo=paperMC)
![Java Version](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk)
![UI/UX Design](https://img.shields.io/badge/UI%2FUX-Premium-FFD700?style=for-the-badge)
![Vault Economy](https://img.shields.io/badge/Vault-Supported-green?style=for-the-badge)
![Geyser Bedrock](https://img.shields.io/badge/Bedrock-1--Tap%20UX-purple?style=for-the-badge)

---

</div>

## 🎨 Triết Lý Thiết Kế Giao Diện (UI/UX Architecture)

**PaperJackpot** được định hình nhằm mang lại cảm giác sòng bạc chân thực nhất trên nền tảng Minecraft Container GUI. Mỗi thành phần giao diện, màu sắc, vị trí nút bấm và âm thanh đều được tính toán tối ưu theo chuẩn **User Experience (UX)** hiện đại:

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                      HỆ THỐNG GIAO DIỆN CÂN BẰNG 3X3                     │
├─────────────────────────────────────────────────────────────────────────┤
│  [SLOTS 00-35] KHUNG CUỘN DỌC 3 CỘT - HIỆU ỨNG TRƯỢT 60 FPS              │
├─────────────────────────────────────────────────────────────────────────┤
│  [SLOTS 36-44] HÀNG ĐIỀU KHIỂN & THÔNG TIN (CONTROL BAR - CÂN BẰNG Đối XỨNG)│
│   Lịch Sử | Thường | NGUỒN TIỀN | VIP | Auto | Top 10 | Thống Kê | Free | Exit │
├─────────────────────────────────────────────────────────────────────────┤
│  [SLOTS 45-53] HÀNG ĐẶT CƯỢC & QUAY HŨ (ACTION & BET BAR)              │
│   1k$  |  10k$  |  100k$  |  500k$ (Barrier Lock)  | 🎰 BẤM QUAY CƯỢC │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🌟 Điểm Nổi Bật Về Trải Nghiệm Người Dùng (UX Highlights)

### 💎 1. Thiết Kế Màu Sắc & Gradient Hiện Đại (Visual Aesthetic)
- Sử dụng dải màu **MiniMessage Gradient** sang trọng (`#8B0000` đến `#D2143A` Đỏ Đô Đậm, `#FFD700` đến `#FFA500` Vàng Kim) thay thế các mã màu mặc định đơn điệu.
- Tiêu đề GUI nổi bật trên nền container xám kim loại, hạn chế tối đa tình trạng mỏi mắt khi chơi thời gian dài.

### 🎡 2. Hiệu Ứng Cuộn Dọc Trượt (Micro-Animation 60 FPS)
- 3 cột cuộn khoáng sản trượt mượt từ trên xuống với gia tốc giảm dần tự nhiên.
- Tự động Highlight và phát hiệu ứng hiệu chỉnh khi 3 ô hàng ngang (Slots 21, 22, 23) trúng giải.

### 🔊 3. Âm Thanh Casino Đa Tầng (Multi-Sensory Audio Design)
- Loại bỏ các âm thanh mặc định giật gân, thay bằng bộ âm thanh casino cao cấp layered sound: `UI_TOAST_CHALLENGE_COMPLETE`, `PLAYER_LEVELUP`, và `FIREWORK_ROCKET_LARGE_BLAST` khi Nổ Hũ Jackpot.
- Phát nhạc mừng rực rỡ toàn máy chủ khi có người chiến thắng Quỹ Hũ.

### 📱 4. Tối Ưu Cho Minecraft PE / Bedrock Mobile (1-Tap Touch UX)
- Hỗ trợ người chơi Pocket Edition qua Geyser/Floodgate tương tác **1-Tap Touch** mượt mà.
- Thiết kế nút chức năng độc lập ở Row 5 & Row 6 giúp thao tác ngón tay trên màn hình cảm ứng chính xác 100%, không bị chạm nhầm.

### 💳 5. Thanh Toán Đa Nguồn & Khóa Rào Cản Tự Động (Smart Barrier Lock)
- Chuyển đổi linh hoạt giữa 3 nguồn cược: **Tiền Vault ($)**, **Vé Quay Thường (CMD 777)**, và **Vé VIP Highroller (CMD 888)**.
- Khi dùng **Vé Thường**, hệ thống tự động khóa ô cược 500k$ bằng biểu tượng **Barrier**, ngăn chặn trạng thái bấm nhầm và đảm bảo logic cược mượt mà.

---

## 🛡️ Kiến Trúc An Toàn & Bảo Mật Enterprise (Security Architecture)

- **Quy Trình Anti-Dupe 5 Lớp**:
  1. Mã hóa NBT Tag kèm UUID Serial duy nhất cho từng vật phẩm vé.
  2. Ghi vết giao dịch tức thì vào CSDL SQLite bọc trong khối Atomic.
  3. Khóa tuyệt đối hành vi Shift-Click, tráo tay Off-hand, phím số (1-9) và Double-Click trong GUI.
  4. Chặn Click-flood liên tục khi cuộn chưa dừng hoàn toàn.
  5. Anti-Loss 100%: Bảo toàn vé và số dư khi server reload hoặc hạ cấp bất ngờ.

---

## 📊 Cơ Chế Toán Học Tỷ Lệ Thắng (Probability Distribution)

| Loại Kết Quả | Tỷ Lệ | Điều Kiện Thắng | Công Thức Thưởng |
| :--- | :---: | :--- | :--- |
| **Thắng Thường** | **25.0%** | Trùng 3 ô khoáng sản hàng ngang | `Tiền cược x 2.0 x (1 - 10% Thuế)` |
| **Nổ Hũ Jackpot** | **0.50%** | Trùng 3x Khối Netherite (NTR) | `(Tiền cược x 5.0 + 100% Quỹ Hũ) x (1 - 10% Thuế)` |
| **Giờ Vàng (Happy Hour)** | **45.0%** | Khung giờ 20:00 - 21:00 (UTC+7) | Nhân đôi tỷ lệ thắng |
| **Thua** | **74.5%** | Không trùng 3 ô hàng ngang | Nạp 100% tiền thua vào Quỹ Hũ (Tiền Vault) |

---

## 🎛️ Bảng Thao Tác Chi Tiết Trong GUI (Slot Mappings)

- **Slots 00 - 35**: Khung hiển thị cuộn trượt Slot Machine 3x3.
- **Slot 36 (📜)**: Xem 10 ván cược gần nhất trong `PersonalHistoryGui`.
- **Slot 37 (🎟️)**: Hiển thị số dư Vé Quay Thường cá nhân.
- **Slot 38 (💳)**: Nút bấm chuyển nguồn thanh toán (`Vault $` / `Vé Thường` / `Vé VIP`).
- **Slot 39 (🎫)**: Hiển thị số dư Vé VIP Highroller cá nhân.
- **Slot 40 (⚡)**: Nút bật/tắt Auto Spin tự động quay rảnh tay.
- **Slot 41 (🏆)**: Mở Bảng Xếp Hạng Top 10 Thần Tài Tuần (`LeaderboardGui`).
- **Slot 42 (📈)**: Mở giao diện Thống Kê Chỉ Số & ROI % (`PlayerStatsGui`).
- **Slot 43 (🎁)**: Nhận 1 lượt quay miễn phí hằng ngày (24h cooldown).
- **Slot 44 (🚪)**: Đóng giao diện Casino.
- **Slots 45 - 48**: Chọn mức cược (`1k$`, `10k$`, `100k$`, `500k$`).
- **Slot 49 (🎰)**: Nút Nether Star khởi chạy vòng quay.

---

## 📜 Lệnh Command & Quyền Hạn

### 👤 Lệnh Người Chơi (`paperjackpot.use`)
- `/jackpot` *(Alias: `/jp`, `/quayhu`, `/no-hu`, `/casino`)*: Mở GUI Casino.
- `/jackpot top`: Mở Bảng Xếp Hạng Top 10 Thần Tài Tuần.
- `/jackpot stats`: Xem thống kê chỉ số may mắn & ROI %.
- `/jackpot tickets`: Kiểm tra số dư Vé Thường & Vé VIP.
- `/jackpot time`: Xem giờ Việt Nam (UTC+7) & trạng thái Giờ Vàng.
- `/jackpot help`: Xem menu hướng dẫn.

### 👑 Lệnh Quản Trị Viên (`paperjackpot.admin`)
- `/jackpot reload`: Reload cấu hình `config.yml`.
- `/jackpot setpool <số_tiền>`: Đặt lại số dư Quỹ Hũ Server.
- `/jackpot resetseason`: Trao thưởng Mùa Giải Tuần & reset Bảng Hologram ngay lập tức.
- `/jackpot giveticket <player> <amount>`: Cấp Vé Thường vào ví CSDL.
- `/jackpot giveitemticket <player> <amount>`: Cấp item Vé Thường (CMD 777) vào kho đồ.
- `/jackpot givevipticket <player> <amount>`: Cấp Vé VIP vào ví CSDL.
- `/jackpot giveitemvipticket <player> <amount>`: Cấp item Vé VIP (CMD 888) vào kho đồ.
- `/jackpot test`: Ép buộc lượt quay tiếp theo Nổ Hũ Jackpot.

---

## 🧩 Tích Hợp PlaceholderAPI & Hologram 3D

Plugin tương thích 100% với **PlaceholderAPI**, **DecentHolograms (`/dh`)** và **FancyHolograms (`/fholo`)**:

| Placeholder | Mô Tả |
| :--- | :--- |
| `%paperjackpot_pool%` | Số dư Quỹ Hũ Server hiện tại |
| `%paperjackpot_last_winner%` | Tên người trúng Nổ Hũ gần nhất |
| `%paperjackpot_tickets%` | Số dư Vé Thường cá nhân |
| `%paperjackpot_vip_tickets%` | Số dư Vé VIP cá nhân |
| `%paperjackpot_top_line_decent_1%` ... `10` | Dòng Bảng Top 1-10 cho DecentHolograms |
| `%paperjackpot_top_line_1%` ... `10` | Dòng Bảng Top 1-10 cho FancyHolograms |

*Xem chi tiết hướng dẫn tạo Hologram tại file:* 📄 **[hologram_guide.md](hologram_guide.md)**

---

<div align="center">

### 👨‍💻 Thông Tin Tác Giả

**Phát triển bởi Huy Phan**

[GitHub Repository](https://github.com/Huyphan68080/PaperJackpot) • Paper 1.21.1 Native Support

</div>
