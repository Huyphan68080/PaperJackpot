# PaperJackpot

PaperJackpot là plugin Casino Quay Hũ 3x3 chuyên nghiệp được thiết kế cho các máy chủ Minecraft Paper / Spigot 1.20+. Plugin mang đến trải nghiệm giải trí quay hũ sinh động cho người chơi, đồng thời cung cấp bộ công cụ quản lý kinh tế và chống gian lận toàn diện cho Admin.

---

## 🎮 Trải Nghiệm Người Chơi (Player Experience)

### 🎰 Giao Diện Slot Machine 3x3
- Cuộn trượt mượt mà 3 cột khoáng sản Minecraft (Kim Cương, Lục Bảo, Vàng, Sắt, Netherite...).
- Tự động bắt cặp và highlight hàng ngang chiến thắng (Slot 21, 22, 23).

### 💳 Nguồn Thanh Toán Đa Dạng
- **Tiền Vault ($)**: Cược trực tiếp bằng tiền game từ 1,000$ đến 500,000$.
- **Vé Quay Thường (CMD 777)**: Nạp vé từ kho đồ để quay miễn phí 100% (cược 1k, 10k, 100k).
- **Vé VIP Highroller (CMD 888)**: Vé VIP dành riêng cho bàn cược cao cấp 500,000$.

### 🎁 Sự Kiện & Tính Năng Phụ
- **BossBar Real-Time**: Theo dõi Quỹ Hũ Tích Lũy Server trực tiếp ở trên cùng màn hình.
- **Giờ Vàng (Happy Hour)**: Nhân đôi tỷ lệ thắng trong khung giờ 20:00 - 21:00 (UTC+7).
- **Điểm Danh Mỗi Ngày**: Nhận 1 lượt quay miễn phí hằng ngày (cooldown 24h).
- **Auto Spin**: Bật/tắt chế độ quay tự động rảnh tay.
- **Lịch Sử & Thống Kê**: Xem lại 10 ván quay gần nhất và chỉ số ROI % may mắn cá nhân.

---

## 🛠️ Công Cụ Quản Trị Admin (Admin Control)

### 🏆 Tự Động Chốt Mùa Giải Tuần
- Hệ thống tự động tổng kết giải thưởng Top 1, Top 2, Top 3 vào đúng **23:59 Chủ Nhật (UTC+7)**.
- Tự động cộng tiền Vault + vé thưởng vào ví CSDL của người chiến thắng và reset Bảng Hologram 3D.

### 🛡️ Anti-Dupe Bảo Mật 5 Lớp
- Kiểm tra UUID Serial NBT riêng cho từng tấm vé vật phẩm.
- Lưu vết SQLite bền vững, khóa chặt hành vi tráo tay, click nhanh hoặc Shift-click trong GUI.

### 📜 Danh Sách Lệnh Admin (`paperjackpot.admin`)
- `/jackpot reload` - Reload lại file cấu hình `config.yml`.
- `/jackpot setpool <số_tiền>` - Thay đổi số dư Quỹ Hũ Server.
- `/jackpot resetseason` - Chốt thưởng Mùa Giải Tuần và reset Bảng Hologram ngay lập tức.
- `/jackpot giveticket <player> <amount>` - Nạp Vé Thường vào CSDL người chơi.
- `/jackpot giveitemticket <player> <amount>` - Cấp item Vé Thường (CMD 777) vào kho đồ.
- `/jackpot givevipticket <player> <amount>` - Nạp Vé VIP vào CSDL người chơi.
- `/jackpot giveitemvipticket <player> <amount>` - Cấp item Vé VIP (CMD 888) vào kho đồ.
- `/jackpot test` - Ép buộc ván quay tiếp theo Nổ Hũ Jackpot.

---

## 🧩 Tích Hợp PlaceholderAPI & Hologram 3D

Plugin hỗ trợ các biến PlaceholderAPI hiển thị Bảng Xếp Hạng Top 10 Thần Tài Tuần 3D trên **DecentHolograms (`/dh`)** và **FancyHolograms (`/fholo`)**:

- `%paperjackpot_pool%` - Số dư Quỹ Hũ Server.
- `%paperjackpot_last_winner%` - Tên người trúng Nổ Hũ gần nhất.
- `%paperjackpot_tickets%` - Số dư Vé Thường của người chơi.
- `%paperjackpot_vip_tickets%` - Số dư Vé VIP của người chơi.
- `%paperjackpot_top_line_decent_1%` ... `10` - Dòng hiển thị Top 1-10 cho DecentHolograms.
- `%paperjackpot_top_line_1%` ... `10` - Dòng hiển thị Top 1-10 cho FancyHolograms.

*Xem hướng dẫn chi tiết tạo Hologram tại:* [hologram_guide.md](hologram_guide.md)

---

## 🚀 Hướng Dẫn Cài Đặt

1. Yêu cầu: Paper / Spigot `1.20+` (Java 17+) & Plugin Vault.
2. Thả file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
3. Khởi động lại server và chỉnh sửa `plugins/PaperJackpot/config.yml`.

---

## 👨‍💻 Thông Tin Tác Giả
- **Phát triển bởi**: Huy Phan
- **Mã nguồn**: [GitHub Repository](https://github.com/Huyphan68080/PaperJackpot)
