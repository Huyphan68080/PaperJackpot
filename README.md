# PaperJackpot

Plugin Casino Quay Hũ (Slot Machine 3x3) dành cho máy chủ Minecraft Paper / Spigot (1.20+).

## Tính năng
- Cuộn slot 3x3: Hiệu ứng cuộn trượt từ trên xuống.
- Quỹ Hũ Server: Tiền thua cược từ Tiền Vault ($) được nạp trực tiếp vào Quỹ Hũ chung.
- BossBar Quỹ Hũ: Hiển thị số dư Quỹ Hũ real-time ở đầu màn hình.
- Chế độ thanh toán (Slot 38):
  - Tiền Vault ($): Cược từ 1,000$ đến 500,000$.
  - Vé Quay Thường (CMD 777): Dùng cho cược 1k, 10k, 100k (tự động khóa mức 500k).
  - Vé VIP Highroller (CMD 888): Dùng cho cược 500,000$.
- Chống dupe 5 lớp: NBT Serial UUID, lưu SQLite, khóa giao dịch atomic và chặn tráo tay / Shift-click trong GUI.
- Sự kiện Giờ Vàng (Happy Hour): 20:00 - 21:00 (UTC+7) tăng tỷ lệ thắng.
- Mùa Giải Tuần: Tự động tổng kết thưởng và reset Bảng Xếp Hạng vào 23:59 Chủ Nhật (giờ Việt Nam).
- Hỗ trợ PlaceholderAPI: Xuất biến hiển thị Bảng Xếp Hạng 3D trên DecentHolograms (/dh) và FancyHolograms (/fholo).

## Tỷ lệ và phần thưởng
- Thắng thường (25.0%): Trùng 3 ô hàng ngang, nhận x2.0 tiền cược (thuế 10%).
- Nổ Hũ Jackpot (0.50%): Trùng 3x Khối Netherite, nhận x5.0 tiền cược + 100% Quỹ Hũ (thuế 10%).
- Thua (74.5%): Không trùng 3 ô, nạp tiền thua vào Quỹ Hũ.

## Vị trí nút trong GUI (Slots)
- Slots 0 - 35: Khung 3 cột cuộn Slot Machine.
- Slot 36: Lịch sử cược cá nhân.
- Slot 37: Số dư Vé Thường.
- Slot 38: Chuyển đổi nguồn thanh toán (Tiền Vault / Vé Thường / Vé VIP).
- Slot 39: Số dư Vé VIP.
- Slot 40: Bật/Tắt Auto Spin.
- Slot 41: Top 10 Thần Tài Tuần.
- Slot 42: Thống kê cá nhân.
- Slot 43: Điểm danh quay miễn phí (cooldown 24h).
- Slot 44: Thoát.
- Slots 45 - 48: Mức cược (1k, 10k, 100k, 500k).
- Slot 49: Nút quay.

## Câu lệnh và quyền hạn

### Người chơi
- `/jackpot` (Alias: `/jp`, `/quayhu`, `/no-hu`, `/casino`): Mở GUI Casino.
- `/jackpot top`: Xem Top 10 Thần Tài Tuần.
- `/jackpot stats`: Xem thống kê cá nhân.
- `/jackpot tickets`: Kiểm tra số dư vé.
- `/jackpot time`: Xem giờ Việt Nam & trạng thái Giờ Vàng.
- `/jackpot help`: Xem hướng dẫn lệnh.

### Quản trị viên (`paperjackpot.admin`)
- `/jackpot reload`: Reload config.yml.
- `/jackpot setpool <số_tiền>`: Đặt số dư Quỹ Hũ.
- `/jackpot resetseason`: Chốt thưởng tuần và reset Bảng Hologram ngay lập tức.
- `/jackpot giveticket <player> <amount>`: Cấp Vé Thường vào CSDL.
- `/jackpot giveitemticket <player> <amount>`: Cấp item Vé Thường (CMD 777).
- `/jackpot givevipticket <player> <amount>`: Cấp Vé VIP vào CSDL.
- `/jackpot giveitemvipticket <player> <amount>`: Cấp item Vé VIP (CMD 888).
- `/jackpot test`: Ép buộc ván tiếp theo Nổ Hũ.

## PlaceholderAPI

- `%paperjackpot_pool%`: Số dư Quỹ Hũ Server.
- `%paperjackpot_last_winner%`: Tên người trúng Nổ Hũ gần nhất.
- `%paperjackpot_tickets%`: Số Vé Thường hiện có.
- `%paperjackpot_vip_tickets%`: Số Vé VIP hiện có.
- `%paperjackpot_top_line_decent_1%` đến `10`: Dòng Top Tuần cho DecentHolograms (/dh).
- `%paperjackpot_top_line_1%` đến `10`: Dòng Top Tuần cho FancyHolograms (/fholo).

## Hướng dẫn Hologram
Xem cú pháp cài đặt Bảng Hologram 3D tại file [hologram_guide.md](hologram_guide.md).

## Cài đặt
1. Thả file `PaperJackpot-1.0.0.jar` vào thư mục `plugins/`.
2. Đảm bảo server đã cài Vault và plugin kinh tế (EssentialsX, CMI...).
3. Khởi động lại server và chỉnh sửa `plugins/PaperJackpot/config.yml`.

## Thông tin
- Tác giả: Huy Phan
- GitHub: https://github.com/Huyphan68080/PaperJackpot
