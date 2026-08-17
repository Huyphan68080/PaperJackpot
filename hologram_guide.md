# ✦ HƯỚNG DẪN CẤU HÌNH BẢNG XẾP HẠNG HOLOGRAM 3D TOP 10 ✦

Tài liệu hướng dẫn chi tiết cách thiết lập và quản lý **Bảng Xếp Hạng 3D Hologram (Top 10 Đại Gia Casino)** cho plugin **PaperJackpot**.

---

## 🌟 CÁCH 1: Hologram 3D Nổi Trực Tiếp (Tích Hợp Sẵn Trong Plugin)

Plugin **PaperJackpot** tích hợp sẵn công nghệ **TextDisplay Native (Minecraft 1.19.4 / 1.20 / 1.21)**. Bạn **KHÔNG CẦN** cài đặt thêm bất kỳ plugin Hologram nào (như DecentHolograms hay HolographicDisplays) mà vẫn tạo được bảng 3D nổi cực đẹp.

### 📌 Các bước thực hiện:
1. Đăng nhập vào game với tài khoản Admin/OP (có quyền `paperjackpot.admin`).
2. Di chuyển nhân vật đến vị trí bạn muốn đặt Bảng Xếp Hạng 3D (ví dụ: Khu vực Spawn / Lobby Casino).
3. Gõ lệnh:
   ```cmd
   /jackpot sethologram
   ```
   *(Hoặc lệnh tắt: `/jackpot setholo`)*
4. Bảng Xếp Hạng 3D Top 10 sẽ xuất hiện lơ lửng ngay vị trí bạn đứng.

### ✨ Đặc điểm nổi bật:
- **Xoay mặt 360 độ**: Hologram sẽ tự động xoay mặt về phía người chơi đứng ở bất kỳ góc nhìn nào trong Lobby.
- **Tự động cập nhật**: Dữ liệu Top 10 sẽ tự động làm mới mỗi 15 giây.
- **Lưu vị trí tự động**: Tọa độ Hologram được tự động lưu vào `config.yml`. Dù khởi động lại Server hay Reload plugin thì Hologram vẫn giữ nguyên vị trí.

### ❌ Xóa Hologram 3D:
Khi muốn di chuyển hoặc xóa Bảng Xếp Hạng khỏi thế giới, bạn chỉ cần gõ:
```cmd
/jackpot removehologram
```
*(Hoặc lệnh tắt: `/jackpot delholo`)*

---

## 💎 CÁCH 2: Cấu Hình Với Plugin DecentHolograms / HolographicDisplays

Nếu Server của bạn đã cài đặt plugin **DecentHolograms** hoặc **HolographicDisplays**, bạn có thể sử dụng các **PlaceholderAPI** của PaperJackpot để tự thiết kế giao diện Hologram theo ý muốn.

### 📊 Danh sách PlaceholderAPI hỗ trợ:

| Placeholder | Mô Tả | Ví Dụ Đầu Ra |
| :--- | :--- | :--- |
| `%paperjackpot_pool%` | Tổng Quỹ Jackpot Tích Lũy Server | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người vừa Nổ Hũ Jackpot | `HuyPhan` |
| `%paperjackpot_top_1_name%` | Tên người đứng Top 1 | `Im_Noah` |
| `%paperjackpot_top_1_amount%` | Tiền thưởng đã thắng của Top 1 | `32,500,000$` |
| `%paperjackpot_top_2_name%` | Tên người đứng Top 2 | `HACKER52` |
| `%paperjackpot_top_2_amount%` | Tiền thưởng đã thắng của Top 2 | `25,110,000$` |
| `%paperjackpot_top_3_name%` | Tên người đứng Top 3 | `PE_M30W_54` |
| `%paperjackpot_top_3_amount%` | Tiền thưởng đã thắng của Top 3 | `20,000,000$` |
| ... | *(Hỗ trợ đầy đủ từ Top 1 đến Top 10)* | ... |
| `%paperjackpot_top_10_name%` | Tên người đứng Top 10 | `Yeagonn_` |
| `%paperjackpot_top_10_amount%` | Tiền thưởng đã thắng của Top 10 | `1,000,000$` |

---

### 📝 Mẫu File Config Tạo Hologram Bằng DecentHolograms (`plugins/DecentHolograms/holograms/top_casino.yml`):

```yaml
name: top_casino
location: world,101.5,31.0,84.5
facing: 0.0
lines:
  - '&e&l✦ &6&lTOP ĐẠI GIA CASINO NỔ HŨ &e&l✦'
  - ''
  - '&6&l#1 &f&l%paperjackpot_top_1_name% &7≫ &a&l%paperjackpot_top_1_amount%'
  - '&7&l#2 &f&l%paperjackpot_top_2_name% &7≫ &a&l%paperjackpot_top_2_amount%'
  - '&c&l#3 &f&l%paperjackpot_top_3_name% &7≫ &a&l%paperjackpot_top_3_amount%'
  - '&e#4 &f&l%paperjackpot_top_4_name% &7≫ &a&l%paperjackpot_top_4_amount%'
  - '&e#5 &f&l%paperjackpot_top_5_name% &7≫ &a&l%paperjackpot_top_5_amount%'
  - '&e#6 &f&l%paperjackpot_top_6_name% &7≫ &a&l%paperjackpot_top_6_amount%'
  - '&e#7 &f&l%paperjackpot_top_7_name% &7≫ &a&l%paperjackpot_top_7_amount%'
  - '&e#8 &f&l%paperjackpot_top_8_name% &7≫ &a&l%paperjackpot_top_8_amount%'
  - '&e#9 &f&l%paperjackpot_top_9_name% &7≫ &a&l%paperjackpot_top_9_amount%'
  - '&e#10 &f&l%paperjackpot_top_10_name% &7≫ &a&l%paperjackpot_top_10_amount%'
  - ''
  - '&e&o👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!'
```

---

## 🔐 Quyền Hạn (Permissions)

| Quyền (Permission) | Mô Tả | Mặc Định |
| :--- | :--- | :--- |
| `paperjackpot.admin` | Quyền tạo/xóa Hologram 3D (`/jackpot sethologram`, `/jackpot removehologram`) | OP |
