<div align="center">

# PaperJackpot

Plugin Casino Quay Hũ Nổ Hũ 3x3 dành cho máy chủ Minecraft Paper / Spigot 1.20+.

![Paper Version](https://img.shields.io/badge/Paper-1.20%2B-blue?style=flat-square)
![Java Version](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square)
![Vault Supported](https://img.shields.io/badge/Vault-Supported-green?style=flat-square)
![PAPI Supported](https://img.shields.io/badge/PlaceholderAPI-Supported-purple?style=flat-square)

---

</div>

## Tính Năng Chính

- **Slot Machine 3x3**: Hiệu ứng cuộn trượt 3 cột khoáng sản mượt mà, tự động highlight hàng ngang trúng thưởng.
- **Quỹ Hũ Server & BossBar**: 100% tiền thua cược (bằng tiền Vault) được nạp vào Quỹ Hũ chung. Hiển thị BossBar real-time trên cùng màn hình.
- **3 Nguồn Thanh Toán (Slot 38)**:
  - `Tiền Vault ($)`: Cược tự do từ 1,000$ đến 500,000$.
  - `Vé Quay Thường (CMD 777)`: Dùng vé nạp từ kho đồ, cược mức 1k, 10k, 100k (tự động khóa ô 500k).
  - `Vé VIP Highroller (CMD 888)`: Dùng vé VIP chuyên biệt cho mức cược 500,000$.
- **Tự Động Reset Tuần**: Tự động chốt thưởng Top 1, Top 2, Top 3 vào **23:59 Chủ Nhật (UTC+7)**, phát thưởng và reset Bảng Hologram 3D.
- **Chống Dupe 5 Lớp**: NBT Serial UUID cho từng vé, lưu SQLite, khóa giao dịch atomic và chặn tráo tay / Shift-click trong GUI.
- **Giờ Vàng (Happy Hour)**: Khung giờ 20:00 - 21:00 (UTC+7) tự động nhân đôi tỷ lệ thắng.

---

## Tỷ Lệ & Phần Thưởng

| Kết Quả | Tỷ Lệ | Điều Kiện Thắng | Phần Thưởng |
| :--- | :---: | :--- | :--- |
| **Thắng Thường** | `25.0%` | Trùng 3 ô khoáng sản hàng ngang | x2.0 Tiền cược (Thuế 10%) |
| **Nổ Hũ Jackpot** | `0.50%` | Trùng 3x Khối Netherite | x5.0 Tiền cược + 100% Quỹ Hũ Server (Thuế 10%) |
| **Giờ Vàng (Happy Hour)** | `45.0%` | 20:00 - 21:00 (UTC+7) hằng ngày | Nhân đôi tỷ lệ thắng |
| **Thua** | `74.5%` | Không trùng 3 ô hàng ngang | Nạp 100% tiền thua vào Quỹ Hũ |

---

## Danh Sách Câu Lệnh & Quyền Hạn

### Lệnh Người Chơi (`paperjackpot.use`)

Lệnh chính: `/jackpot` *(Alias: `/jp`, `/quayhu`, `/no-hu`, `/casino`)*

| Câu Lệnh | Mô Tả |
| :--- | :--- |
| `/jackpot` | Mở giao diện Casino Quay Hũ |
| `/jackpot top` | Xem Bảng Xếp Hạng Top 10 Thần Tài Tuần |
| `/jackpot stats` | Xem thống kê cá nhân & chỉ số ROI % |
| `/jackpot tickets` | Kiểm tra số dư Vé Thường & Vé VIP |
| `/jackpot time` | Xem giờ Việt Nam (UTC+7) & trạng thái Giờ Vàng |
| `/jackpot help` | Xem menu hướng dẫn câu lệnh |

### Lệnh Quản Trị Viên (`paperjackpot.admin`)

| Câu Lệnh | Mô Tả |
| :--- | :--- |
| `/jackpot reload` | Reload file cấu hình `config.yml` |
| `/jackpot setpool <số_tiền>` | Đặt lại số dư Quỹ Hũ Server |
| `/jackpot resetseason` | Chốt thưởng Mùa Giải Tuần & reset Bảng Hologram ngay lập tức |
| `/jackpot giveticket <player> <amount>` | Nạp Vé Thường vào ví CSDL người chơi |
| `/jackpot giveitemticket <player> <amount>` | Cấp vật phẩm Vé Thường (CMD 777) vào kho đồ |
| `/jackpot givevipticket <player> <amount>` | Nạp Vé VIP vào ví CSDL người chơi |
| `/jackpot giveitemvipticket <player> <amount>` | Cấp vật phẩm Vé VIP (CMD 888) vào kho đồ |
| `/jackpot test` | Ép buộc lượt quay tiếp theo Nổ Hũ Jackpot |

---

## Biến PlaceholderAPI (PAPI)

| Placeholder | Mô Tả | Ví Dụ |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Số dư Quỹ Hũ Server đã định dạng | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người trúng Nổ Hũ gần nhất | `HuyPhan` |
| `%paperjackpot_tickets%` | Số dư Vé Thường cá nhân | `10` |
| `%paperjackpot_vip_tickets%` | Số dư Vé VIP cá nhân | `3` |
| `%paperjackpot_top_line_decent_1%` ... `10` | Dòng Top 1-10 cho DecentHolograms (`/dh`) | `#1. HuyPhan - 50,000,000$` |
| `%paperjackpot_top_line_1%` ... `10` | Dòng Top 1-10 cho FancyHolograms (`/fholo`) | `<gold>#1. HuyPhan - 50,000,000$</gold>` |

---

## Hướng Dẫn Hologram 3D

Plugin hỗ trợ hiển thị Bảng Top 10 trên **DecentHolograms (`/dh`)** và **FancyHolograms (`/fholo`)**. Xem chi tiết lệnh tạo bảng tại:
📄 **[hologram_guide.md](hologram_guide.md)**

---

## Hướng Dẫn Cài Đặt

1. Copy file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
2. Đảm bảo server đã cài **Vault** và plugin kinh tế.
3. Khởi động lại server và chỉnh sửa `plugins/PaperJackpot/config.yml` theo ý muốn.

---

<div align="center">

**Tác giả: Huy Phan** • [GitHub Repository](https://github.com/Huyphan68080/PaperJackpot)

</div>
