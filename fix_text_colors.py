#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_text_colors.py
Sửa màu chữ bị cứng (hardcoded) trong các file view/*.java,
thay bằng biến động từ util.TechStoreUI để hỗ trợ dark mode.
"""

import os, re, shutil

SRC = r"src\view"
BACKUP = r"src_backup_v2"

# ─── Tạo thư mục backup ───────────────────────────────────────────────────────
if not os.path.exists(BACKUP):
    os.makedirs(BACKUP)

def backup(path):
    dest = os.path.join(BACKUP, os.path.basename(path))
    if not os.path.exists(dest):
        shutil.copy2(path, dest)

# ─── Hàm tiện ích ─────────────────────────────────────────────────────────────
def replace_in_file(filepath, replacements):
    """Thực hiện danh sách (old, new) trên nội dung file."""
    with open(filepath, encoding="utf-8", errors="replace") as f:
        content = f.read()
    original = content
    for old, new in replacements:
        content = content.replace(old, new)
    if content != original:
        backup(filepath)
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"  ✅ Đã sửa: {os.path.basename(filepath)}")
    else:
        print(f"  ⚪ Không thay đổi: {os.path.basename(filepath)}")
    return content

# ─────────────────────────────────────────────────────────────────────────────
# 1. TechStoreUI.java  – styleSecondaryButton dùng màu cứng sáng
# ─────────────────────────────────────────────────────────────────────────────
file_ts = os.path.join("src", "util", "TechStoreUI.java")
replace_in_file(file_ts, [
    # styleSecondaryButton: nền & chữ dùng màu cứng
    (
        '        btn.setForeground(new Color(71, 85, 105));\n'
        '        btn.setBackground(new Color(241, 245, 249));',
        '        btn.setForeground(TEXT_MUTED);\n'
        '        btn.setBackground(CARD_BG);'
    ),
    # Thêm TEXT_NORMAL constant nếu chưa có (không cần, dùng TEXT_MUTED)
])

# ─────────────────────────────────────────────────────────────────────────────
# 2. JFDashboard.java  – titleLabel, header foreground
# ─────────────────────────────────────────────────────────────────────────────
file_dash = os.path.join(SRC, "JFDashboard.java")
replace_in_file(file_dash, [
    # titleLabel trong createRecentOrdersPanel & createTopProductsPanel
    (
        '        titleLabel.setForeground(new Color(15, 23, 42));',
        '        titleLabel.setForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # stat card title (chữ mờ nhạt "TỔNG DOANH THU" v.v.) – giữ nguyên 148,163,184 → đó là TEXT_MUTED
    # table header foreground
    (
        '        tblHoaDonGanDay.getTableHeader().setForeground(new Color(71, 85, 105));',
        '        tblHoaDonGanDay.getTableHeader().setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    (
        '        tblTopSanPham.getTableHeader().setForeground(new Color(71, 85, 105));',
        '        tblTopSanPham.getTableHeader().setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    # stat card "valueLabel" textColor – được truyền vào từ createStatCard với new Color(30, 41, 59)
    # Đây là màu đậm tối → cần đổi thành TEXT_TITLE (sáng ở dark mode)
    # Tìm dòng: valueLabel.setForeground(textColor);
    # Thay bằng: valueLabel.setForeground(util.TechStoreUI.TEXT_TITLE);
    (
        '        valueLabel.setForeground(textColor);',
        '        valueLabel.setForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # Sửa chữ ký hàm createStatCard bỏ textColor không dùng nữa (không cần, chỉ cần đổi nội dung)
    # Stat card title label (mờ) – new Color(148, 163, 184) thực ra đã match TEXT_MUTED dark -> OK
    # Vẫn đổi cho nhất quán
    (
        '        titleLabel.setForeground(new Color(148, 163, 184));',
        '        titleLabel.setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    (
        '        descLabel.setForeground(new Color(148, 163, 184));',
        '        descLabel.setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    # table foreground mặc định không set → FlatLaf sẽ tự handle theo L&F
    # Nhưng tblHoaDonGanDay / tblTopSanPham chưa có setForeground → OK
])

# ─────────────────────────────────────────────────────────────────────────────
# 3. JFSanPham.java  – nhiều chỗ hardcoded
# ─────────────────────────────────────────────────────────────────────────────
file_sp = os.path.join(SRC, "JFSanPham.java")
replace_in_file(file_sp, [
    # tabMain foreground
    (
        '        tabMain.setForeground(new Color(15, 23, 42));',
        '        tabMain.setForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # lblListTitle
    (
        '        lblListTitle.setForeground(new Color(15, 23, 42));',
        '        lblListTitle.setForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # lblTongCong
    (
        '        lblTongCong.setForeground(new Color(100, 116, 139));',
        '        lblTongCong.setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    # table header
    (
        '        header.setForeground(new Color(100, 116, 139));',
        '        header.setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    # wrapWithLabel – label foreground
    (
        '        lbl.setForeground(new Color(71, 85, 105));',
        '        lbl.setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    # styleTabButton default foreground
    (
        '        btn.setForeground(new Color(71, 85, 105));\n'
        '        btn.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1));',
        '        btn.setForeground(util.TechStoreUI.TEXT_MUTED);\n'
        '        btn.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1));'
    ),
    # styleTabButton hover-off
    (
        '                btn.setForeground(new Color(71, 85, 105));\n'
        '                btn.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1));',
        '                btn.setForeground(util.TechStoreUI.TEXT_MUTED);\n'
        '                btn.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1));'
    ),
    # prepareRenderer – Color.WHITE → CARD_BG
    (
        '                    comp.setBackground(row == hoverRow ? util.TechStoreUI.BG_MAIN : Color.WHITE);',
        '                    comp.setBackground(row == hoverRow ? util.TechStoreUI.BG_MAIN : util.TechStoreUI.CARD_BG);'
    ),
    # selectionForeground
    (
        '        tblSanPham.setSelectionForeground(new Color(15, 23, 42));',
        '        tblSanPham.setSelectionForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # btnLamMoi & btnChonAnh – nền BG_MAIN + chữ Slate 600
    (
        '        styleButton(btnLamMoi, util.TechStoreUI.BG_MAIN, new Color(71, 85, 105), "roundRect");',
        '        styleButton(btnLamMoi, util.TechStoreUI.BG_MAIN, util.TechStoreUI.TEXT_MUTED, "roundRect");'
    ),
    (
        '        styleButton(btnChonAnh, util.TechStoreUI.BG_MAIN, new Color(71, 85, 105), "roundRect");',
        '        styleButton(btnChonAnh, util.TechStoreUI.BG_MAIN, util.TechStoreUI.TEXT_MUTED, "roundRect");'
    ),
])

# ─────────────────────────────────────────────────────────────────────────────
# 4. JFTimKiemHoaDon.java  – lblTitle, lblSub, header foreground, renderer bg
# ─────────────────────────────────────────────────────────────────────────────
file_tk = os.path.join(SRC, "JFTimKiemHoaDon.java")
replace_in_file(file_tk, [
    (
        '        lblTitle.setForeground(new Color(15, 23, 42)); // Màu chữ đen Slate 900 cực sâu',
        '        lblTitle.setForeground(util.TechStoreUI.TEXT_TITLE); // Động theo theme'
    ),
    (
        '        lblSub.setForeground(new Color(100, 116, 139)); // Màu xám nhạt Slate 500 tinh tế',
        '        lblSub.setForeground(util.TechStoreUI.TEXT_MUTED); // Động theo theme'
    ),
    (
        '        tblHoaDon.getTableHeader().setForeground(new Color(71, 85, 105)); // Chữ header xám trung tính',
        '        tblHoaDon.getTableHeader().setForeground(util.TechStoreUI.TEXT_MUTED); // Động theo theme'
    ),
    (
        '        lblEmpty.setForeground(new Color(148, 163, 184));',
        '        lblEmpty.setForeground(util.TechStoreUI.TEXT_MUTED);'
    ),
    # ActionButtonRenderer – setBackground(Color.WHITE) → CARD_BG
    (
        '            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);',
        '            setBackground(isSelected ? table.getSelectionBackground() : TechStoreUI.CARD_BG);'
    ),
])

# ─────────────────────────────────────────────────────────────────────────────
# 5. JFLapHoaDon.java  – createFieldGroup, createPreviewValue, createReadOnlyField
# ─────────────────────────────────────────────────────────────────────────────
file_hd = os.path.join(SRC, "JFLapHoaDon.java")
replace_in_file(file_hd, [
    # createFieldGroup label
    (
        '        lbl.setForeground(new Color(100, 116, 139));\n'
        '        group.add(lbl, BorderLayout.NORTH);',
        '        lbl.setForeground(util.TechStoreUI.TEXT_MUTED);\n'
        '        group.add(lbl, BorderLayout.NORTH);'
    ),
    # createPreviewRow label
    (
        '        lbl.setForeground(new Color(100, 116, 139));\n'
        '        row.add(lbl, BorderLayout.WEST);',
        '        lbl.setForeground(util.TechStoreUI.TEXT_MUTED);\n'
        '        row.add(lbl, BorderLayout.WEST);'
    ),
    # createPreviewValue – new Color(30, 41, 59) tối → đổi thành TEXT_TITLE
    (
        '        lbl.setForeground(new Color(30, 41, 59));',
        '        lbl.setForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # createReadOnlyField – new Color(51, 65, 85) tối
    (
        '        txt.setForeground(new Color(51, 65, 85));',
        '        txt.setForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
    # tblChiTiet header foreground
    (
        '        header.setForeground(new Color(100, 116, 139));\n'
        '        header.setPreferredSize(new Dimension(0, 36));',
        '        header.setForeground(util.TechStoreUI.TEXT_MUTED);\n'
        '        header.setPreferredSize(new Dimension(0, 36));'
    ),
    # tblChiTiet selectionForeground
    (
        '        tblChiTiet.setSelectionForeground(new Color(30, 41, 59));',
        '        tblChiTiet.setSelectionForeground(util.TechStoreUI.TEXT_TITLE);'
    ),
])

# ─────────────────────────────────────────────────────────────────────────────
# 6. JFNhanVien.java, JFKhachHang.java, JFTaiKhoan.java – xem có hardcoded không
# ─────────────────────────────────────────────────────────────────────────────
for fname in ["JFNhanVien.java", "JFKhachHang.java", "JFTaiKhoan.java"]:
    fp = os.path.join(SRC, fname)
    with open(fp, encoding="utf-8", errors="replace") as f:
        txt = f.read()
    # Kiểm tra nhanh xem có setForeground hardcoded không
    if 'setForeground(new Color(' in txt or 'setForeground(Color.BLACK)' in txt:
        replace_in_file(fp, [
            # Tiêu đề Slate 900
            ('setForeground(new Color(15, 23, 42))',
             'setForeground(util.TechStoreUI.TEXT_TITLE)'),
            # Muted Slate 500
            ('setForeground(new Color(100, 116, 139))',
             'setForeground(util.TechStoreUI.TEXT_MUTED)'),
            # Muted Slate 600
            ('setForeground(new Color(71, 85, 105))',
             'setForeground(util.TechStoreUI.TEXT_MUTED)'),
            # Color.BLACK
            ('setForeground(Color.BLACK)',
             'setForeground(util.TechStoreUI.TEXT_TITLE)'),
        ])
    else:
        print(f"  ✅ {fname}: không có màu chữ cứng cần đổi")

print("\n✅ Hoàn tất! Tất cả màu chữ đã được đổi sang biến động TechStoreUI.")
