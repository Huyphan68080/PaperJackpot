# 🎰 PaperJackpot

Plugin Quay Hũ Nổ Hũ (Slot Machine 3x3) dành cho server Minecraft Paper / Spigot 1.20+.

---

## 📌 Tổng Quan

**PaperJackpot** là plugin Casino Nổ Hũ được tối ưu cho các server Paper/Spigot hiện đại. Plugin cung cấp giao diện quay hũ 3x3 cuộn dọc mượt mà, tích hợp hệ thống Quỹ Hũ Server tích lũy 100% tiền thua cược, chế độ chơi bằng Tiền Vault ($) hoặc Vé Quay (Vé Thường / Vé VIP Highroller), cơ chế Anti-Dupe bảo mật và tính năng tự động trao thưởng & reset Bảng Xếp Hạng Hologram hằng tuần.

---

## ⚙️ Tính Năng Chính

- **Giao Diện Slot Machine 3x3**: Cuộn mượt từ trên xuống với các biểu tượng khoáng sản Minecraft.
- **Quỹ Hũ Tích Lũy Server**: 100% tiền cược khi người chơi thua (bằng tiền Vault) sẽ được nạp thẳng vào Quỹ Hũ chung.
- **BossBar Quỹ Hũ Server**: Hiển thị số dư Quỹ Hũ trên cùng màn hình real-time cho toàn bộ người chơi online.
- **Nguồn Thanh Toán Linh Hoạt (Slot 38)**:
  - `Tiền Vault ($)`: Đặt cược tùy ý từ 1,000$ đến 500,000$.
  - `Vé Quay Thường (CMD 777)`: Quay miễn phí 100% cho các mức cược 1k, 10k, 100k. Tự động khóa ô cược 500k.
  - `Vé VIP Highroller (CMD 888)`: Đặt cược mức tối đa 500,000$.
- **Hệ Thống Chống Dupe Vé (5 Lớp)**: Gắn UUID Serial NBT, lưu SQLite, cập nhật giao dịch Atomic và chặn hành vi tráo tay/click nhanh.
- **Sự Kiện Giờ Vàng (Happy Hour 20:00 - 21:00 UTC+7)**: Tự động nhân tỷ lệ thắng trong khung giờ vàng hằng ngày.
- **Đua Top Mùa Giải Tuần (Weekly Season)**: Tự động chốt thưởng Top 1, Top 2, Top 3 vào 23:59 Chủ Nhật (giờ Việt Nam), phát thưởng tiền + vé và tự động reset Bảng Xếp Hạng Hologram 3D.
- **Tích Hợp PlaceholderAPI & Holograms**: Hỗ trợ xuất các biến PAPI hiển thị Bảng Xếp Hạng 3D trên DecentHolograms (`/dh`) và FancyHolograms (`/fholo`).
- **Thống Kê & Lịch Sử Cược**: Lưu vết chi tiết ván chơi, tính tỷ lệ may mắn, ROI % và lịch sử 10 ván quay gần nhất.

---

## 📊 Tỷ Lệ Trúng Thưởng

| Loại Kết Quả | Tỷ Lệ Mặc Định | Điều Kiện Thắng | Phần Thưởng |
| :--- | :---: | :--- | :--- |
| **Thắng Thường** | **25.0%** | Khớp 3 ô hàng ngang (Slots 21, 22, 23) | **x2.0 Tiền Cược** (Thuế 10%) |
| **Nổ Hũ Jackpot** | **0.50%** | Khớp 3x Khối Netherite (NTR) | **x5.0 Tiền Cược + 100% Quỹ Hũ Server** (Thuế 10%) |
| **Thua** | **74.5%** | Không trùng 3 ô hàng ngang | Nạp 100% tiền thua vào Quỹ Hũ (nếu dùng tiền Vault) |

---

## 🛠️ Lệnh Admin & Người Chơi

### Lệnh Người Chơi
- `/jackpot` (hoặc `/jp`): Mở giao diện Casino Quay Hũ cá nhân.
- `/jackpot top`: Xem GUI Top 10 Thần Tài Tuần.
- `/jackpot stats`: Xem thống kê ván chơi và chỉ số may mắn cá nhân.
- `/jackpot tickets`: Xem số dư Vé Thường và Vé VIP trong ví.
- `/jackpot time`: Kiểm tra giờ Việt Nam (UTC+7) và trạng thái Giờ Vàng.

### Lệnh Quản Trị Viên (Permission: `paperjackpot.admin`)
- `/jackpot reload`: Reload cấu hình `config.yml`.
- `/jackpot setpool <số_tiền>`: Đặt số dư Quỹ Hũ Server.
- `/jackpot resetseason`: Chốt thưởng Mùa Giải Tuần & reset Bảng Hologram ngay lập tức.
- `/jackpot giveticket <người_chơi> <số_lượng>`: Nạp Vé Thường vào ví CSDL của người chơi.
- `/jackpot giveitemticket <người_chơi> <số_lượng>`: Trao vật phẩm Vé Thường (CMD 777) vào kho đồ.
- `/jackpot givevipticket <người_chơi> <số_lượng>`: Nạp Vé VIP vào ví CSDL của người chơi.
- `/jackpot giveitemvipticket <người_chơi> <số_lượng>`: Trao vật phẩm Vé VIP (CMD 888) vào kho đồ.
- `/jackpot test`: Ép buộc ván quay tiếp theo trúng Nổ Hũ (Dành cho test).

---

## 🧩 PlaceholderAPI (PAPI)

| Placeholder | Mô Tả |
| :--- | :--- |
| `%paperjackpot_pool%` | Số dư Quỹ Hũ Server (Ví dụ: `50,000$`) |
| `%paperjackpot_last_winner%` | Tên người trúng Nổ Hũ gần nhất |
| `%paperjackpot_tickets%` | Số Vé Thường hiện có trong ví người chơi |
| `%paperjackpot_vip_tickets%` | Số Vé VIP hiện có trong ví người chơi |
| `%paperjackpot_top_line_decent_1%` ... `10` | Dòng định dạng Bảng Top 1-10 cho DecentHolograms (`/dh`) |
| `%paperjackpot_top_line_1%` ... `10` | Dòng định dạng Bảng Top 1-10 cho FancyHolograms (`/fholo`) |

---

## 🚀 Hướng Dẫn Cài Đặt

1. **Yêu cầu**:
   - Minecraft Paper / Spigot `1.20+` (Java 17+).
   - Plugin Vault + Plugin Kinh tế (EssentialsX, CMI, etc.).
   - (Tùy chọn) PlaceholderAPI, DecentHolograms hoặc FancyHolograms.
2. **Cài đặt**:
   - Thả file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
   - Khởi động lại Server.
   - Tùy chỉnh thông số trong `plugins/PaperJackpot/config.yml` nếu cần.

---

## 📖 Hướng Dẫn Hologram
Chi tiết câu lệnh tạo Bảng Hologram 3D tại Spawn xem tại file [hologram_guide.md](file:///d:/PaperJackpot/hologram_guide.md).

---

## 👨‍💻 Tác Giả & Bản Quyền
- **Tác giả**: Huy Phan
- **Mã nguồn**: [GitHub Repository](https://github.com/Huyphan68080/PaperJackpot)
- **Phiên bản**: 1.0.0 (Paper 1.21.1 Compatible)
