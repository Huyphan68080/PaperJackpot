# ✦ HƯỚNG DẪN TẠO HOLOGRAM BẢNG XẾP HẠNG TOP TUẦN (PAPERJACKPOT) ✦

Hệ thống Bảng Xếp Hạng 3D Hologram tích hợp sẵn cho cả **DecentHolograms (`/dh`)** và **FancyHolograms (`/fh`)**, tự động làm sạch và chuyển sang Mùa Giải Tuần mới mỗi khi hết 7 ngày hoặc khi Admin gõ `/jackpot resetseason`.

---

## 🚀 CÁCH 1: TẠO BẰNG LỆNH DECENTHOLOGRAMS (`/dh`) - KHUYÊN DÙNG

Đứng tại vị trí bạn muốn tạo Hologram tại Spawn và gõ các lệnh sau:

```bash
# 1. Tạo bảng Hologram mới
/dh create toptuan &e&l✦ TOP 10 THẦN TÀI CASINO (MÙA TUẦN) ✦

# 2. Thêm các dòng (Cú pháp thêm dòng vào Trang 1):
/dh line add toptuan 1 %paperjackpot_top_line_decent_1%
/dh line add toptuan 1 %paperjackpot_top_line_decent_2%
/dh line add toptuan 1 %paperjackpot_top_line_decent_3%
/dh line add toptuan 1 %paperjackpot_top_line_decent_4%
/dh line add toptuan 1 %paperjackpot_top_line_decent_5%
/dh line add toptuan 1 %paperjackpot_top_line_decent_6%
/dh line add toptuan 1 %paperjackpot_top_line_decent_7%
/dh line add toptuan 1 %paperjackpot_top_line_decent_8%
/dh line add toptuan 1 %paperjackpot_top_line_decent_9%
/dh line add toptuan 1 %paperjackpot_top_line_decent_10%
/dh line add toptuan 1 &f
/dh line add toptuan 1 &e&o👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!

# (Hoặc gõ lệnh plural: /dh lines add toptuan %paperjackpot_top_line_decent_1%)
```

---

## 🌟 CÁCH 2: LỆNH TẠO TRỰC TIẾP FANCYHOLOGRAMS (`/fholo`) TRONG GAME

Đứng tại vị trí muốn đặt Hologram và copy-paste lần lượt các lệnh này vào game:

```bash
# 1. Tạo Hologram mới tên 'toptuan'
/fholo create toptuan

# 2. Thêm dòng Tiêu Đề màu Gradient:
/fholo edit toptuan addline <gradient:#FFD700:#FFA500><bold>✦ TOP 10 THẦN TÀI CASINO (TUẦN) ✦</bold></gradient>

# 3. Thêm đường kẻ ngang trang trí:
/fholo edit toptuan addline <gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gray>

# 4. Thêm 10 dòng biến Top Thần Tài Tuần (Dạng MiniMessage gradient):
/fholo edit toptuan addline %paperjackpot_top_line_1%
/fholo edit toptuan addline %paperjackpot_top_line_2%
/fholo edit toptuan addline %paperjackpot_top_line_3%
/fholo edit toptuan addline %paperjackpot_top_line_4%
/fholo edit toptuan addline %paperjackpot_top_line_5%
/fholo edit toptuan addline %paperjackpot_top_line_6%
/fholo edit toptuan addline %paperjackpot_top_line_7%
/fholo edit toptuan addline %paperjackpot_top_line_8%
/fholo edit toptuan addline %paperjackpot_top_line_9%
/fholo edit toptuan addline %paperjackpot_top_line_10%

# 5. Thêm đường kẻ kết và hướng dẫn:
/fholo edit toptuan addline <gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gray>
/fholo edit toptuan addline <yellow><italic>👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!</italic></yellow>
```

---

## 📝 CÁCH 3: CẤU HÌNH BẰNG FILE FANCYHOLOGRAMS (`plugins/FancyHolograms/holograms.yml`)

Paste đoạn cấu hình này vào file `plugins/FancyHolograms/holograms.yml`:

```yaml
holograms:
  toptuan:
    type: TEXT
    update_text_interval: 5
    text:
      - '<gradient:#FFD700:#FFA500><bold>✦ TOP 10 THẦN TÀI CASINO (TUẦN) ✦</bold></gradient>'
      - '<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gray>'
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
      - '<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gray>'
      - '<yellow><italic>👉 Gõ /jackpot để tham gia Quay Hũ Nổ Hũ!</italic></yellow>'
    billboard: CENTER
    shadow: true
```

---

## 💡 DANH SÁCH BIẾN PLACEHOLDERAPI (PAPI) ĐƯỢC HỖ TRỢ:

- **Dòng Định Dạng Sẵn DecentHolograms (Legacy `&`)**:
  `%paperjackpot_top_line_decent_1%` ... `%paperjackpot_top_line_decent_10%`
- **Dòng Định Dạng Sẵn FancyHolograms (MiniMessage)**:
  `%paperjackpot_top_line_1%` ... `%paperjackpot_top_line_10%`
- **Biến Tách Rời Tên & Tiền**:
  `%paperjackpot_top_1_name%` ... `%paperjackpot_top_10_name%`
  `%paperjackpot_top_1_amount%` ... `%paperjackpot_top_10_amount%`
- **Biến Thông Tin Khác**:
  `%paperjackpot_pool%` : Quỹ Jackpot Server hiện tại (ví dụ `50,000$`)
  `%paperjackpot_last_winner%` : Tên người trúng Nổ Hũ gần nhất
  `%paperjackpot_tickets%` : Số Vé Thường cá nhân
  `%paperjackpot_vip_tickets%` : Số Vé VIP Highroller cá nhân

---

## ⚡ CÁC LỆNH ADMIN QUAN TRỌNG:

- **Chốt Thưởng Đua Top Tuần & Reset Bảng Hologram Thủ Công**:
  ```cmd
  /jackpot resetseason
  ```
- **Reload Plugin PaperJackpot**:
  ```cmd
  /jackpot reload
  ```
- **Reload PlaceholderAPI**:
  ```cmd
  /papi reload
  ```
- **Reload FancyHolograms / DecentHolograms**:
  ```cmd
  /fh reload   # (Hoặc /dh reload)
  ```
