# ✦ HƯỚNG DẪN TÍCH HỢP BẢNG XẾP HẠNG HOLOGRAM 3D TOP 10 ✦

Tài liệu hướng dẫn chi tiết cách kết hợp **PaperJackpot** với **FancyHolograms** hoặc **DecentHolograms** qua **PlaceholderAPI** để hiển thị Bảng Xếp Hạng Top 10 3D **siêu mượt 0-lag 100%**.

---

## 🚀 TẠI SAO NÊN DÙNG FANCYHOLOGRAMS / DECENTHOLOGRAMS?

- **Siêu mượt 0-Lag**: Các plugin chuyên dụng như FancyHolograms quản lý Packet Hologram theo cơ chế Asynchronous, không ngốn CPU và **hoàn toàn loại bỏ hiện tượng đơ/giật lag** khu vực Spawn/Lobby.
- **Tự động cập nhật real-time**: Nhờ tích hợp PlaceholderAPI, dữ liệu Top 10 sẽ tự làm mới liên tục mỗi khi có đại gia Nổ Hũ!

---

## ⚡ CÁCH 1: Tạo Tự Động Bằng 1 Lệnh `/jackpot sethologram`

Khi Server của bạn đã cài đặt plugin **FancyHolograms** (hoặc **DecentHolograms**) và **PlaceholderAPI**:

1. Đăng nhập vào game với quyền Admin/OP (`paperjackpot.admin`).
2. Di chuyển đến vị trí bạn muốn đặt Bảng Xếp Hạng Top 10.
3. Gõ lệnh:
   ```cmd
   /jackpot sethologram
   ```
   *(Hoặc lệnh tắt: `/jackpot setholo`)*
4. Plugin sẽ **tự động gọi FancyHolograms / DecentHolograms** để tạo Bảng Xếp Hạng Top 10 mượt mà tại vị trí của bạn!

### ❌ Xóa Hologram:
Gõ lệnh:
```cmd
/jackpot removehologram
```

---

## 📊 DANH SÁCH PLACEHOLDERAPI HỖ TRỢ

Plugin cung cấp sẵn các **Placeholder đã định dạng màu sắc cực đẹp**:

### 🎯 1. Dòng Đã Định Dạng Sẵn (Khuyên Dùng):
| Placeholder | Dành Cho Plugin | Mô Tả Đầu Ra |
| :--- | :--- | :--- |
| `%paperjackpot_top_header%` | FancyHolograms | `<gradient:#FFD700:#FFA500><bold>✦ TOP ĐẠI GIA CASINO NỔ HŨ ✦</bold></gradient>` |
| `%paperjackpot_top_line_1%` ... `%paperjackpot_top_line_10%` | FancyHolograms | `#1 Im_Noah ≫ 32,500,000$` *(MiniMessage chuẩn mượt)* |
| `%paperjackpot_top_line_legacy_1%` ... `%paperjackpot_top_line_legacy_10%` | DecentHolograms | `&6&l#1 &f&lIm_Noah &7≫ &a&l32,500,000$` *(Mã màu & legacy)* |
| `%paperjackpot_top_footer%` | FancyHolograms | `👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!` |

### 🔍 2. Variable Riêng Lẻ (Nếu Muốn Tự Thiết Kế Màu):
| Placeholder | Mô Tả | Ví Dụ Đầu Ra |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Tổng Quỹ Jackpot Tích Lũy Server | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người vừa Nổ Hũ | `HuyPhan` |
| `%paperjackpot_top_1_name%` ... `%paperjackpot_top_10_name%` | Tên người đứng Top 1 - 10 | `Im_Noah` |
| `%paperjackpot_top_1_amount%` ... `%paperjackpot_top_10_amount%` | Tiền thắng của Top 1 - 10 | `32,500,000$` |

---

## 📝 MẪU FILE CONFIG THỦ CÔNG DÀNH CHO ADMIN

### 1. File Config FancyHolograms (`plugins/FancyHolograms/holograms.yml`):
```yaml
holograms:
  top_casino:
    type: TEXT
    text:
      - '%paperjackpot_top_header%'
      - ''
      - '%paperjackpot_top_line_1%'
      - '%paperjackpot_top_line_2%'
      - '%paperjackpot_top_line_3%'
      - '%paperjackpot_top_line_4%'
      - '%paperjackpot_top_line_5%'
      - '%paperjackpot_top_line_6%'
      - '%paperjackpot_top_line_7%'
      - '%paperjackpot_top_line_8%'
      - '%paperjackpot_top_line_9%'
      - '%paperjackpot_top_line_10%'
      - ''
      - '%paperjackpot_top_footer%'
    billboard: CENTER
    shadow: true
```

### 2. File Config DecentHolograms (`plugins/DecentHolograms/holograms/top_casino.yml`):
```yaml
name: top_casino
lines:
  - '&e&l✦ &6&lTOP ĐẠI GIA CASINO NỔ HŨ &e&l✦'
  - ''
  - '%paperjackpot_top_line_legacy_1%'
  - '%paperjackpot_top_line_legacy_2%'
  - '%paperjackpot_top_line_legacy_3%'
  - '%paperjackpot_top_line_legacy_4%'
  - '%paperjackpot_top_line_legacy_5%'
  - '%paperjackpot_top_line_legacy_6%'
  - '%paperjackpot_top_line_legacy_7%'
  - '%paperjackpot_top_line_legacy_8%'
  - '%paperjackpot_top_line_legacy_9%'
  - '%paperjackpot_top_line_legacy_10%'
  - ''
  - '&e&o👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!'
```
