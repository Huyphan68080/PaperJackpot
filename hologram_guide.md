# ✦ HƯỚNG DẪN TẠO HOLOGRAM 3D TOP 10 BẰNG FANCYHOLOGRAMS ✦

Plugin **PaperJackpot** hỗ trợ đầy đủ các **PlaceholderAPI** cho người chơi đứng Top 1 đến Top 10 để bạn tạo Bảng Hologram 3D mượt mà bằng **FancyHolograms** (hoặc **DecentHolograms**).

---

## ⚠️ NGUYÊN NHÂN HOLOGRAM KHÔNG TỰ CẬP NHẬT REAL-TIME & CÁCH SỬA

Khi vừa tạo Hologram bằng FancyHolograms, mặc định FancyHolograms đặt **thời gian tự động cập nhật Placeholder là `-1` (Tắt tự động cập nhật)**. Đó là lý do tại sao Bảng Hologram bị đứng nguyên ở chữ `---` mà không đổi sang tên đại gia khi có người thắng cược!

### 🔧 Cách Bật Cập Nhật Real-Time 100%:

Mở ô Chat trong game và chạy lệnh sau để bật làm mới real-time cho FancyHolograms:

```cmd
/hologram edit topcasino setUpdateTextInterval 5
```
*(Hoặc `setUpdateTextInterval 20` để làm mới mỗi 1 giây)*

---

## ⚡ CÁC LỆNH TẠO BẢNG FANCYHOLOGRAMS HOÀN CHỈNH IN-GAME

Đứng tại vị trí muốn đặt Bảng Xếp Hạng Top 10 và mở khung Chat chạy lần lượt các lệnh sau:

```cmd
/hologram create text topcasino
/hologram edit topcasino setBillboard center
/hologram edit topcasino setShadow true
/hologram edit topcasino setUpdateTextInterval 5
/hologram edit topcasino addLine &b&l✦ &f&lTOP ĐẠI GIA CASINO &b&l✦
/hologram edit topcasino addLine &7&m━━━━━━━━━━━━━━━━━━━━
/hologram edit topcasino addLine &e#1 &f%paperjackpot_top_1_name% &7» &a%paperjackpot_top_1_amount%
/hologram edit topcasino addLine &e#2 &f%paperjackpot_top_2_name% &7» &a%paperjackpot_top_2_amount%
/hologram edit topcasino addLine &e#3 &f%paperjackpot_top_3_name% &7» &a%paperjackpot_top_3_amount%
/hologram edit topcasino addLine &e#4 &f%paperjackpot_top_4_name% &7» &a%paperjackpot_top_4_amount%
/hologram edit topcasino addLine &e#5 &f%paperjackpot_top_5_name% &7» &a%paperjackpot_top_5_amount%
/hologram edit topcasino addLine &e#6 &f%paperjackpot_top_6_name% &7» &a%paperjackpot_top_6_amount%
/hologram edit topcasino addLine &e#7 &f%paperjackpot_top_7_name% &7» &a%paperjackpot_top_7_amount%
/hologram edit topcasino addLine &e#8 &f%paperjackpot_top_8_name% &7» &a%paperjackpot_top_8_amount%
/hologram edit topcasino addLine &e#9 &f%paperjackpot_top_9_name% &7» &a%paperjackpot_top_9_amount%
/hologram edit topcasino addLine &e#10 &f%paperjackpot_top_10_name% &7» &a%paperjackpot_top_10_amount%
/hologram edit topcasino addLine &7&m━━━━━━━━━━━━━━━━━━━━
```

---

## 📝 CẤU HÌNH TRỰC TIẾP TRONG FILE `plugins/FancyHolograms/holograms.yml`

Bạn cũng có thể dán trực tiếp đoạn cấu hình dưới đây vào file `holograms.yml` của FancyHolograms:

```yaml
holograms:
  topcasino:
    type: TEXT
    update_text_interval: 5
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
