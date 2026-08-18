# ✦ HƯỚNG DẪN CẤU HÌNH BẢNG XẾP HẠNG HOLOGRAM 3D TOP 10 ✦

Plugin **PaperJackpot** hỗ trợ đầy đủ các **PlaceholderAPI** cho người chơi đứng Top 1 đến Top 10 để bạn tự do tạo Bảng Hologram 3D mượt mà bằng **FancyHolograms** (hoặc **DecentHolograms**).

---

## ⚡ CÁC LỆNH CHẠY TRONG GAME TẠO BẢNG FANCYHOLOGRAMS CHUẨN ĐẸP

Đứng tại vị trí muốn đặt Bảng Xếp Hạng Top 10 (ví dụ: Lobby / Spawn Casino) và mở khung Chat chạy lần lượt các lệnh sau:

```cmd
/hologram create top_casino text
/hologram edit top_casino setBillboard center
/hologram edit top_casino setShadow true
/hologram edit top_casino addLine &b&l✦ &f&lTOP ĐẠI GIA CASINO &b&l✦
/hologram edit top_casino addLine &7&m━━━━━━━━━━━━━━━━━━━━
/hologram edit top_casino addLine &e#1 &f%paperjackpot_top_1_name% &7» &a%paperjackpot_top_1_amount%
/hologram edit top_casino addLine &e#2 &f%paperjackpot_top_2_name% &7» &a%paperjackpot_top_2_amount%
/hologram edit top_casino addLine &e#3 &f%paperjackpot_top_3_name% &7» &a%paperjackpot_top_3_amount%
/hologram edit top_casino addLine &e#4 &f%paperjackpot_top_4_name% &7» &a%paperjackpot_top_4_amount%
/hologram edit top_casino addLine &e#5 &f%paperjackpot_top_5_name% &7» &a%paperjackpot_top_5_amount%
/hologram edit top_casino addLine &e#6 &f%paperjackpot_top_6_name% &7» &a%paperjackpot_top_6_amount%
/hologram edit top_casino addLine &e#7 &f%paperjackpot_top_7_name% &7» &a%paperjackpot_top_7_amount%
/hologram edit top_casino addLine &e#8 &f%paperjackpot_top_8_name% &7» &a%paperjackpot_top_8_amount%
/hologram edit top_casino addLine &e#9 &f%paperjackpot_top_9_name% &7» &a%paperjackpot_top_9_amount%
/hologram edit top_casino addLine &e#10 &f%paperjackpot_top_10_name% &7» &a%paperjackpot_top_10_amount%
/hologram edit top_casino addLine &7&m━━━━━━━━━━━━━━━━━━━━
```

---

## 📝 CẤU HÌNH TRỰC TIẾP TRONG FILE `plugins/FancyHolograms/holograms.yml`

Bạn cũng có thể dán trực tiếp đoạn cấu hình dưới đây vào file `holograms.yml` của FancyHolograms:

```yaml
holograms:
  top_casino:
    type: TEXT
    text:
      - '&b&l✦ &f&lTOP ĐẠI GIA CASINO &b&l✦'
      - '&7&m━━━━━━━━━━━━━━━━━━━━'
      - '&e#1 &f%paperjackpot_top_1_name% &7» &a%paperjackpot_top_1_amount%'
      - '&e#2 &f%paperjackpot_top_2_name% &7» &a%paperjackpot_top_2_amount%'
      - '&e#3 &f%paperjackpot_top_3_name% &7» &a%paperjackpot_top_3_amount%'
      - '&e#4 &f%paperjackpot_top_4_name% &7» &a%paperjackpot_top_4_amount%'
      - '&e#5 &f%paperjackpot_top_5_name% &7» &a%paperjackpot_top_5_amount%'
      - '&e#6 &f%paperjackpot_top_6_name% &7» &a%paperjackpot_top_6_amount%'
      - '&e#7 &f%paperjackpot_top_7_name% &7» &a%paperjackpot_top_7_amount%'
      - '&e#8 &f%paperjackpot_top_8_name% &7» &a%paperjackpot_top_8_amount%'
      - '&e#9 &f%paperjackpot_top_9_name% &7» &a%paperjackpot_top_9_amount%'
      - '&e#10 &f%paperjackpot_top_10_name% &7» &a%paperjackpot_top_10_amount%'
      - '&7&m━━━━━━━━━━━━━━━━━━━━'
    billboard: CENTER
    shadow: true
```

---

## 📊 DANH SÁCH PLACEHOLDER SUPPORT

| Placeholder | Mô Tả | Đầu Ra Mẫu |
| :--- | :--- | :--- |
| `%paperjackpot_top_1_name%` | Tên người chơi đứng Top 1 | `Im_Noah` *(Nếu chưa có sẽ trả về `---`)* |
| `%paperjackpot_top_1_amount%` | Số tiền thắng thưởng của Top 1 | `32,500,000$` *(Nếu chưa có sẽ trả về `---`)* |
| `%paperjackpot_top_2_name%` ... `%paperjackpot_top_10_name%` | Tên người đứng Top 2 đến Top 10 | `HACKER52` |
| `%paperjackpot_top_2_amount%` ... `%paperjackpot_top_10_amount%` | Tiền thưởng của Top 2 đến Top 10 | `25,110,000$` |
| `%paperjackpot_pool%` | Tổng Quỹ Jackpot Tích Lũy Server | `1,250,000$` |
| `%paperjackpot_last_winner%` | Tên người nổ hũ gần nhất | `HuyPhan` |
