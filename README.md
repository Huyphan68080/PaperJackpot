# 🎰 PaperJackpot - Plugin Quay Hũ Nổ Hũ Casino Minecraft 1.20+

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20%2B-brightgreen)
![Java Version](https://img.shields.io/badge/Java-17%2B-orange)
![Software](https://img.shields.io/badge/Platform-Paper%20%7F%20Spigot-blue)
![Bedrock Compatible](https://img.shields.io/badge/Bedrock%2FPE-Geyser--Supported-purple)

**PaperJackpot** là plugin Casino Quay Hũ Nổ Hũ hiện đại, mượt mà và hoành tráng dành cho máy chủ Minecraft (Spigot / PaperMC). Plugin mang đến trải nghiệm sòng bạc chuẩn 3x3 đỉnh cao, tích hợp hiệu ứng hình ảnh/âm thanh sống động, hệ thống BossBar Quỹ Hũ toàn server, hỗ trợ cả người chơi Java lẫn Minecraft PE/Bedrock Mobile!

---

## 🌟 Tính Năng Nổi Bật

- **🎰 Cuộn Dọc Trượt Từ Trên Xuống**: Hiệu ứng cuộn 3 cột khoáng sản mượt mà 60 FPS, mang lại cảm giác chân thực như máy Slot Machine ngoài đời thực.
- **🔥 Quỹ Hũ Tích Lũy Real-Time**: 100% tiền thua cược của tất cả người chơi đều được tự động tích lũy thẳng vào Quỹ Hũ Server.
- **👑 BossBar Server Toàn Thời Gian**: Hiển thị thanh BossBar Quỹ Hũ Tích Lũy liên tục ở đầu màn hình cho tất cả người chơi online.
- **🎆 Pháo Hoa & Particle Vàng Kim Khi Nổ Hũ**: Khi trúng 3x Khối Netherite (Jackpot), người chơi nhận x5.0 tiền cược + hốt trọn 100% Quỹ Hũ Server kèm hiệu ứng bắn pháo hoa & particle rực rỡ.
- **⚡ Chế Độ Auto Spin (Tự Động Quay Rảnh Tay)**: Nút gạt ở Slot 43 cho phép tự động đặt cược và quay liên tục mỗi 1.5 giây mà không cần bấm tay.
- **🎡 Thưởng Chuỗi Quay May Mắn (Spin Streak)**: Quay đủ 10 ván liên tiếp, ván thứ 11 tự động nhận **Lucky Spin (x2 Tỷ Lệ Trúng Thưởng)**.
- **🎆 Sự Kiện Giờ Vàng Casino (Happy Hour 20:00 - 21:00)**: Tự động nhân đôi tỷ lệ Nổ Hũ Jackpot trong khung giờ vàng hằng ngày và **tự động bắn thông báo Chat rực rỡ + âm thanh mừng** tới toàn máy chủ khi bắt đầu/kết thúc sự kiện (Có thể tùy chỉnh lời nhắn trong `config.yml`).
- **🎁 Lượt Quay Miễn Phí Hằng Ngày (24H Cooldown)**: Nút chest ở Slot 52 trao tặng 1 lượt quay miễn phí cược 1,000$ mỗi 24 giờ.
- **🏆 Bảng Xếp Hạng Top 10 & Thống Kê Cá Nhân**: Đầy đủ GUI xem Top 10 Đại Gia Thắng Thưởng và GUI xem Thống Kê (Tổng ván quay, Winrate %, Lợi Nhuận Ròng, Chỉ số ROI %).
- **📜 Giao Diện Lịch Sử Cược (`PersonalHistoryGui`)**: Xem lại 10 ván cược gần nhất trực tiếp trong giao diện game.
- **📱 Hỗ Trợ Minecraft PE / Bedrock Mobile**: Thiết kế nút bấm đơn 1-tap chuyên biệt (Slot 50, 51, 52, 53) để người chơi di động qua Geyser/Floodgate bấm vào là mở ngay mà không cần chuột phải.
- **🔒 Chống Hack / Lấy Vật Phẩm Tuyệt Đối**: Khóa toàn bộ sự kiện click và kéo rê item GUI trên ưu tiên cao nhất (`EventPriority.HIGHEST`).
- **💾 Lưu Trữ CSDL SQLite Tự Động**: Lưu trữ bền vững dữ liệu Quỹ Hũ, thời gian điểm danh 24h và lịch sử quay, không lo bị mất dữ liệu khi khởi động lại server.

---

## 💻 Lệnh (Commands) & Quyền Hạn (Permissions)

### 📌 Lệnh Người Chơi (Player Commands)
Lệnh chính: `/jackpot` (Lệnh phụ: `/jp`, `/quayhu`, `/no-hu`, `/casino`)

| Lệnh | Mô Tả | Quyền Hạn (Permission) | Mặc Định |
| :--- | :--- | :--- | :--- |
| `/jackpot` | Mở giao diện Casino Quay Hũ cá nhân | `paperjackpot.use` | `true` (Mọi người) |
| `/jackpot top` | Mở Bảng Xếp Hạng Top 10 Thắng Thưởng | `paperjackpot.use` | `true` |
| `/jackpot stats` | Mở giao diện Thống Kê Cá Nhân (ROI %, Lợi nhuận) | `paperjackpot.use` | `true` |
| `/jackpot time` | Xem giờ Việt Nam UTC+7 hiện tại & trạng thái Giờ Vàng | `paperjackpot.use` | `true` |
| `/jackpot help` | Xem danh sách hướng dẫn lệnh | `paperjackpot.use` | `true` |

### 🛠️ Lệnh Quản Trị Viên (Admin Commands)

| Lệnh | Mô Tả | Quyền Hạn (Permission) |
| :--- | :--- | :--- |
| `/jackpot sethologram` | Tạo Bảng Xếp Hạng Top 10 3D nổi mượt mà trong thế giới | `paperjackpot.admin` |
| `/jackpot removehologram` | Xóa Bảng Xếp Hạng Top 10 3D nổi khỏi thế giới | `paperjackpot.admin` |
| `/jackpot testhappyhour` | Bắn thử thông báo Chat & âm thanh Giờ Vàng tức thì | `paperjackpot.admin` |
| `/jackpot reload` | Reload lại cấu hình `config.yml` | `paperjackpot.admin` |
| `/jackpot setpool <số_tiền>` | Thay đổi tổng Quỹ Jackpot Server | `paperjackpot.admin` |
| `/jackpot test` | Ép buộc lượt quay tiếp theo của người chơi Nổ Hũ | `paperjackpot.admin` |

---

## 🧩 PlaceholderAPI Expansion

PaperJackpot hỗ trợ tích hợp sẵn với **PlaceholderAPI** để hiển thị thông số Quỹ Hũ và Top 10 Đại Gia lên Scoreboard, TAB, DecentHolograms hoặc Chat:

| Placeholder | Mô Tả | Ví Dụ Đầu Ra |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Quỹ Jackpot Server đã định dạng dấu phẩy | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người vừa Nổ Hũ Jackpot gần nhất | `HuyPhan` |
| `%paperjackpot_top_1_name%` | Tên người chơi đứng Top 1 Casino | `Im_Noah` |
| `%paperjackpot_top_1_amount%` | Số tiền thắng thưởng của Top 1 | `32,500,000$` |
| `%paperjackpot_top_2_name%` ... `%paperjackpot_top_10_name%` | Tên người chơi đứng Top 2 đến Top 10 | `HACKER52` |
| `%paperjackpot_top_2_amount%` ... `%paperjackpot_top_10_amount%` | Tiền thưởng Top 2 đến Top 10 | `25,110,000$` |
| `%paperjackpot_pool_raw%` | Số tiền Quỹ Hũ dạng số thô | `1250000.0` |
| `%paperjackpot_last_winner%` | Tên người chơi vừa Nổ Hũ gần nhất | `HuyPhan` |
| `%paperjackpot_my_total_wins%` | Tổng số ván thắng của bản thân người chơi | `42` |

---

## ⚙️ Hướng Dẫn Cài Đặt

1. **Yêu Cầu Tiên Quyết**:
   - Máy chủ Paper / Purpur / Spigot `1.20+`.
   - Java `17` hoặc mới hơn.
   - Plugin Kinh Tế **Vault** và một plugin tiền tệ (EssentialsX, CMI, CraftConomy, v.v.).
   - (Tùy chọn) Plugin **PlaceholderAPI**.
2. **Cài Đặt**:
   - Tải file `PaperJackpot-1.0.0.jar`.
   - Copy file vào thư mục `plugins/` của máy chủ.
   - Đổi mới hoặc khởi động lại Server (`/reload` hoặc khởi động lại).
3. **Cấu Hình `config.yml`**:
   - Chỉnh sửa mức cược tối thiểu/tối đa, thuế suất (mặc định 10%), tiêu đề GUI và các thông báo hiển thị tại `plugins/PaperJackpot/config.yml`.
   - Gõ `/jackpot reload` để áp dụng thay đổi ngay lập tức!

---

## 📊 Cơ Chế Tỷ Lệ & Thưởng Thắng

- **Tỷ lệ Thắng Thường (21.95%)**:
  - Khi quay trúng 3 biểu tượng khoáng sản giống nhau (Kim Cương, Lục Bảo, Thỏi Vàng, v.v.).
  - Phần thưởng: **x2.0 Tiền Cược** (Sau khi trừ 10% thuế).
- **Tỷ lệ Nổ Hũ Jackpot (0.05%)**:
  - Khi quay trúng 3x Khối Netherite (NTR).
  - Phần thưởng: **x5.0 Tiền Cược + HỐT TRỌN 100% QUỸ HŨ TÍCH LŨY SERVER** (Sau khi trừ 10% thuế).
- **Tỷ lệ Thua (78.0%)**:
  - Không trùng 3 ô giống nhau.
  - 100% tiền thua cược được cộng thẳng trực tiếp vào Quỹ Hũ Server.

---

## 📱 Hướng Dẫn Cho Người Chơi Bedrock / PE Mobile

Người chơi Minecraft PE (Pocket Edition) di động truy cập qua Geyser/Floodgate có thể dễ dàng trải nghiệm:
- Bấm 1 chạm vào **Slot 50 (Đầu Người)** để xem Top 10 Bảng Xếp Hạng.
- Bấm 1 chạm vào **Slot 51 (La Bàn)** để xem Thống Kê Cá Nhân.
- Bấm 1 chạm vào **Slot 52 (Rương)** để Điểm Danh Quay Miễn Phí.
- Bấm 1 chạm vào **Slot 53 (Khối Đỏ)** để Thoát Game.
- Bấm 1 chạm vào **Slot 43 (Cần Gạt)** để Bật/Tắt Quay Tự Động Rảnh Tay!

---

## 🛡️ Tác Giả & Hỗ Trợ

- **Tác Giả / Phát Triển**: Huy Phan
- **Phiên Bản**: `1.0.0`
- **Giấy Phép**: MIT License
