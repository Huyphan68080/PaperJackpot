package com.project.paperjackpot.game;

import org.bukkit.Material;

import java.util.List;

/**
 * GameMode - Danh sách 9 Trò Chơi Slot Casino Nổ Hũ đa dạng cho người chơi tự do chọn lựa.
 */
public enum GameMode {
    MINERAL_SLOT(
            1,
            "🎰 NỔ HŨ KHOÁNG SẢN",
            Material.DIAMOND,
            "Slot 3x3 Truyền Thống",
            List.of(
                    " <gray>Chế độ quay máy Slot 3x3 kinh điển.",
                    " <gray>Cuộn các loại quặng: Kim Cương, Lục Bảo, Thỏi Vàng...",
                    " <gradient:#FF0000:#FFD700>🔥 Thắng 3x Khối Netherite (NTR) = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    TAI_XIU_SLOT(
            2,
            "🃏 NỔ HŨ TÀI XỈU MAY MẮN",
            Material.NETHER_STAR,
            "Slot Xúc Xắc Tài Lộc",
            List.of(
                    " <gray>Chế độ Slot Tài Xỉu âm dương thần tài.",
                    " <gray>Xúc xắc may mắn & vật phẩm lộc vàng.",
                    " <gradient:#FF0000:#FFD700>🔥 Thắng 3x Ngôi Sao Nether = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    DIAMOND_FRENZY(
            3,
            "💎 NỔ HŨ KIM CƯƠNG (5x3)",
            Material.DIAMOND_BLOCK,
            "Slot 5x3 Đại Gia",
            List.of(
                    " <gray>Chế độ Slot 5x3 lộng lẫy dành cho đại gia.",
                    " <gray>Tỉ lệ nhân tiền cực khủng x15 cược!",
                    " <gradient:#FF0000:#FFD700>🔥 Thắng 5x Khối Kim Cương = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    XOC_DIA_SLOT(
            4,
            "🪙 NỔ HŨ XÓC ĐĨA THẦN TÀI",
            Material.GOLD_BLOCK,
            "Slot Linh Vật Thần Tài",
            List.of(
                    " <gray>Chế độ Slot Xóc Đĩa linh vật may mắn dân gian.",
                    " <gray>Tùy chọn cược chẵn lẻ và quay hũ đồng xu.",
                    " <gradient:#FF0000:#FFD700>🔥 Thắng 4x Khối Vàng Thần Tài = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    SWEET_BONANZA(
            5,
            "🍬 NỔ HŨ KẸO NGỌT",
            Material.CAKE,
            "Slot Bánh Kẹo Rực Rỡ",
            List.of(
                    " <gray>Chế độ Slot Bánh Kẹo & Bom Kẹo Nổ Thưởng.",
                    " <gray>Cuộn Bánh Kem, Kẹo Bảy Màu, Táo Đỏ, Bom Kẹo...",
                    " <gradient:#FF0000:#FFD700>🔥 Nổ Bom Kẹo Thần Tài = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    DRAGON_TIGER(
            6,
            "🐲 NỔ HŨ RỒNG HỔ",
            Material.DRAGON_HEAD,
            "Slot Đấu Điểm Rồng vs Hổ",
            List.of(
                    " <gray>Chế độ Slot Rồng Hổ đại chiến kịch tính.",
                    " <gray>So điểm Ngọc Rồng vs Vuốt Hổ nhân thưởng khủng.",
                    " <gradient:#FF0000:#FFD700>🔥 Thắng 3x Đầu Rồng = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    PIRATE_TREASURE(
            7,
            "🏴‍☠️ NỔ HŨ VUA CƯỚP BIỂN",
            Material.CHEST,
            "Slot Rương Vàng Kho Báu",
            List.of(
                    " <gray>Chế độ Slot Cướp Biển săn kho báu đại dương.",
                    " <gray>Cuộn Rương Vàng, Đầu Lâu, Mỏ Neo, Xẻng Báu...",
                    " <gradient:#FF0000:#FFD700>🔥 Mở 3x Rương Vàng Kho Báu = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    GOD_OF_FORTUNE(
            8,
            "🎆 NỔ HŨ THẦN TÀI ĐÓN LỘC",
            Material.RED_CANDLE,
            "Slot Lì Xì & Đèn Lồng",
            List.of(
                    " <gray>Chế độ Slot Thần Tài mang lộc xuân đỏ thắm.",
                    " <gray>Cuộn Bao Lì Xì, Đèn Lồng Đỏ, Bánh Chưng, Thỏi Vàng...",
                    " <gradient:#FF0000:#FFD700>🔥 Thắng 3x Bao Lì Xì Đỏ = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    ),
    WHEEL_OF_FORTUNE(
            9,
            "⚡ VÒNG QUAY MAY MẮN",
            Material.COMPASS,
            "Vòng Quay Kim Cương 8 Ô",
            List.of(
                    " <gray>Chế độ Vòng Quay Kim Cương cuộn tròn 8 ô nhân thưởng.",
                    " <gray>Nhân thưởng phong phú x1.5, x2.0, x5.0, x10.0!",
                    " <gradient:#FF0000:#FFD700>🔥 Quay trúng ô Nổ Hũ = Hốt Sạch Quỹ Hũ!</gradient>",
                    "",
                    " <yellow>👉 Click để chơi ngay!"
            )
    );

    private final int id;
    private final String displayName;
    private final Material icon;
    private final String subtitle;
    private final List<String> description;

    GameMode(int id, String displayName, Material icon, String subtitle, List<String> description) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.subtitle = subtitle;
        this.description = description;
    }

    public int getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getIcon() { return icon; }
    public String getSubtitle() { return subtitle; }
    public List<String> getDescription() { return description; }

    public static GameMode getById(int id) {
        for (GameMode gm : values()) {
            if (gm.id == id) return gm;
        }
        return MINERAL_SLOT;
    }
}
