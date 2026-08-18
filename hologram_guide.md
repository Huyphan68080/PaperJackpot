# ✦ HƯỚNG DẪN TẠO HOLOGRAM 3D TOP 10 BẰNG FANCYHOLOGRAMS ✦

Plugin **PaperJackpot** được thiết kế siêu gọn nhẹ và **100% không chứa mã nguồn Hologram dư thừa** gây đơ/lag server.

Bảng Xếp Hạng Top 10 3D nổi trên không trung được giao hoàn toàn cho plugin chuyên dụng **FancyHolograms** (hoặc DecentHolograms) quản lý trực tiếp bằng từng câu lệnh in-game.

---

## ⚡ HƯỚNG DẪN TẠO BẢNG TỪNG DÒNG VỚI FANCYHOLOGRAMS

### 📌 Bước 1: Cài Đặt Plugin Yêu Cầu
- Plugin **FancyHolograms**
- Plugin **PlaceholderAPI**

---

### 📌 Bước 2: Chạy Từng Lệnh Trong Game Để Tạo Bảng

Đứng tại vị trí bạn muốn đặt Bảng Xếp Hạng Top 10 và mở khung Chat chạy lần lượt từng lệnh dưới đây:

```cmd
/hologram create top_casino text
/hologram edit top_casino setBillboard center
/hologram edit top_casino setShadow true
/hologram edit top_casino addLine %paperjackpot_top_header%
/hologram edit top_casino addLine 
/hologram edit top_casino addLine %paperjackpot_top_line_1%
/hologram edit top_casino addLine %paperjackpot_top_line_2%
/hologram edit top_casino addLine %paperjackpot_top_line_3%
/hologram edit top_casino addLine %paperjackpot_top_line_4%
/hologram edit top_casino addLine %paperjackpot_top_line_5%
/hologram edit top_casino addLine %paperjackpot_top_line_6%
/hologram edit top_casino addLine %paperjackpot_top_line_7%
/hologram edit top_casino addLine %paperjackpot_top_line_8%
/hologram edit top_casino addLine %paperjackpot_top_line_9%
/hologram edit top_casino addLine %paperjackpot_top_line_10%
/hologram edit top_casino addLine 
/hologram edit top_casino addLine %paperjackpot_top_footer%
```

---

## 📊 DANH SÁCH PLACEHOLDER HỖ TRỢ TRONG PAPERJACKPOT

### 🎯 Dòng Định Dạng Sẵn Màu Sắc Siêu Đẹp (Khuyên Dùng):
| Placeholder | Dành Cho Plugin | Mô Tả Đầu Ra |
| :--- | :--- | :--- |
| `%paperjackpot_top_header%` | FancyHolograms | `<gradient:#FFD700:#FFA500><bold>✦ TOP ĐẠI GIA CASINO NỔ HŨ ✦</bold></gradient>` |
| `%paperjackpot_top_line_1%` ... `%paperjackpot_top_line_10%` | FancyHolograms | `#1 Im_Noah ≫ 32,500,000$` *(Đã sẵn màu sắc & xếp hạng)* |
| `%paperjackpot_top_line_legacy_1%` ... `%paperjackpot_top_line_legacy_10%` | DecentHolograms | `&6&l#1 &f&lIm_Noah &7≫ &a&l32,500,000$` *(Legacy codes &)* |
| `%paperjackpot_top_footer%` | FancyHolograms | `👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!` |

### 🔍 Variable Riêng Lẻ:
- `%paperjackpot_pool%`: Tổng Quỹ Jackpot Server (`1,250,000$`)
- `%paperjackpot_last_winner%`: Người Nổ Hũ mới nhất (`HuyPhan`)
- `%paperjackpot_top_1_name%` ... `%paperjackpot_top_10_name%`: Tên người chơi
- `%paperjackpot_top_1_amount%` ... `%paperjackpot_top_10_amount%`: Số tiền thưởng
