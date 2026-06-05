package util;

import com.formdev.flatlaf.icons.FlatSearchIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/** Tiện ích giao diện TechStore (theo mockup HTML). */
public final class TechStoreUI {

    public static Color BG_MAIN     = new Color(248, 250, 252);
    public static Color CARD_BG     = Color.WHITE;
    public static Color BORDER      = new Color(226, 232, 240);
    public static Color TEXT_TITLE  = new Color(15, 23, 42);
    public static Color TEXT_MUTED  = new Color(100, 116, 139);
    public static Color FIELD_BG    = new Color(248, 250, 252);
    public static Color TABLE_HEAD  = new Color(248, 250, 252);
    public static Color INDIGO      = new Color(79, 70, 229);
    public static Color INDIGO_LIGHT= new Color(238, 242, 255);
    public static Color AMBER       = new Color(245, 158, 11);

    private static boolean darkMode = false;

    public static final Font FONT_MAIN = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SM   = new Font("Segoe UI", Font.PLAIN, 12);

    private static final int BTN_ARC = 12;

    private TechStoreUI() {}

    /** FlatLaf (nếu có) — gọi trước khi tạo bất kỳ cửa sổ Swing nào. */
    public static void installLookAndFeel() {
        installLookAndFeel(darkMode);
    }

    public static void installLookAndFeel(boolean dark) {
        setDarkMode(dark);
        try {
            // 1. Cố gắng cài đặt giao diện FlatLightLaf hiện đại
            UIManager.setLookAndFeel(dark
                ? "com.formdev.flatlaf.FlatDarkLaf"
                : "com.formdev.flatlaf.FlatLightLaf");
            
            // 2. Tinh chỉnh thêm cấu hình mặc định (Bo góc mềm mại cho toàn app)
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            
            // 3. Tắt cái viền đứt nét (focus) xấu xí mặc định của Java Swing khi click vào nút
            UIManager.put("Button.focusWidth", 1);
            UIManager.put("Component.focusWidth", 1);
            
        } catch (Exception e) {
            System.err.println("Cảnh báo: Không tìm thấy thư viện FlatLaf. Ứng dụng sẽ dùng giao diện hệ thống.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // fallback
            }
        }
    }

    public static void setDarkMode(boolean dark) {
        darkMode = dark;
        if (dark) {
            BG_MAIN = new Color(2, 6, 23);
            CARD_BG = new Color(15, 23, 42);
            BORDER = new Color(30, 41, 59);
            TEXT_TITLE = new Color(248, 250, 252);
            TEXT_MUTED = new Color(148, 163, 184);
            FIELD_BG = new Color(15, 23, 42);
            TABLE_HEAD = new Color(15, 23, 42);
            INDIGO_LIGHT = new Color(49, 46, 129);
        } else {
            BG_MAIN = new Color(248, 250, 252);
            CARD_BG = Color.WHITE;
            BORDER = new Color(226, 232, 240);
            TEXT_TITLE = new Color(15, 23, 42);
            TEXT_MUTED = new Color(100, 116, 139);
            FIELD_BG = new Color(248, 250, 252);
            TABLE_HEAD = new Color(248, 250, 252);
            INDIGO_LIGHT = new Color(238, 242, 255);
        }
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static JPanel createCard() {
         JPanel p = new JPanel();
    p.setBackground(CARD_BG);
    
    // Kiểm tra xem LookAndFeel hiện tại có phải FlatLaf không
    boolean isFlatLaf = UIManager.getLookAndFeel().getClass().getName().contains("Flat");
    if (isFlatLaf) {
        // SỬA TẠI ĐÂY: Thay đổi cú pháp định dạng viền chuẩn FlatLaf
        p.putClientProperty("FlatLaf.style",
            "border: 1,1,1,1, " + colorHex(BORDER) + ", 1, 12");
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
    } else {
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 24, 20, 24)));
    }
    return p;
    }

    public static JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(TEXT_TITLE);
        return l;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SM);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JLabel createFormBadge(String text) {
        JLabel l = new JLabel(" " + text + " ");
        l.setOpaque(true);
        l.setBackground(INDIGO_LIGHT);
        l.setForeground(new Color(67, 56, 202));
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    public static JPanel wrapField(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    public static JTextField createField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(FONT_MAIN);
        txt.setBackground(FIELD_BG);
        
        boolean isFlatLaf = UIManager.getLookAndFeel().getClass().getName().contains("Flat");
        if (isFlatLaf) {
            // Giữ lại viền gốc bo tròn xịn xò của FlatLaf, chỉ gán padding trong và màu viền đồng bộ
            txt.setMargin(new Insets(8, 10, 8, 10));
            txt.putClientProperty("FlatLaf.style", 
                "borderColor: " + colorHex(BORDER) + "; " +
                "focusedBorderColor: #4f46e5; " +
                "arc: 12; " +
                "placeholderForeground: " + colorHex(TEXT_MUTED) + ";"
            );
        } else {
            // Dự phòng khi máy không cài FlatLaf
            txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        }
        
        if (placeholder != null && !placeholder.isEmpty()) {
            txt.putClientProperty("JTextField.placeholderText", placeholder);
        }
        return txt;
    }

    public static void stylePrimaryButton(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(INDIGO);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 16, 8, 16));
        applyRoundedButton(btn, BTN_ARC);
    }

    public static void styleSecondaryButton(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(new Color(71, 85, 105));
        btn.setBackground(new Color(241, 245, 249));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 16, 8, 16));
        applyRoundedButton(btn, BTN_ARC);
    }

    public static void styleUpdateButton(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(AMBER);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 16, 8, 16));
        applyRoundedButton(btn, BTN_ARC);
    }

    /** Bo góc nút (FlatLaf + vẽ tay khi không có FlatLaf). */
    public static void applyRoundedButton(JButton btn, int arc) {
        btn.putClientProperty("ts.arc", arc);
        btn.putClientProperty("FlatLaf.style", "arc: " + arc + ";");
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r = arc;
                Object arcProp = b.getClientProperty("ts.arc");
                if (arcProp instanceof Integer) {
                    r = (Integer) arcProp;
                }
                Color bg = b.getBackground();
                if (!b.isEnabled()) {
                    bg = new Color(226, 232, 240);
                } else if (b.getModel().isPressed()) {
                    bg = bg.darker();
                } else if (b.getModel().isRollover()) {
                    bg = bg.brighter();
                }
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), r, r);
                FontMetrics fm = g2.getFontMetrics(b.getFont());
                String text = b.getText();
                if (text != null) {
                    int x = (b.getWidth() - fm.stringWidth(text)) / 2;
                    int y = (b.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.setColor(b.getForeground());
                    g2.setFont(b.getFont());
                    g2.drawString(text, x, y);
                }
                g2.dispose();
            }
        });
    }

    public static void configureActionColumn(JTable table, int colIndex) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(colIndex).setCellRenderer(new ActionCellRenderer("Sửa", "Xóa"));
        table.getColumnModel().getColumn(colIndex).setMinWidth(120);
        table.getColumnModel().getColumn(colIndex).setPreferredWidth(130);
        table.getColumnModel().getColumn(colIndex).setMaxWidth(180);
    }

    /** Renderer cột Thao tác: Sửa | Xóa bo góc. */
    public static class ActionCellRenderer extends JPanel implements TableCellRenderer {
        public ActionCellRenderer(String edit, String del) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));
            setOpaque(true);
            PillLabel lblEdit = new PillLabel(edit, new Color(238, 242, 255), INDIGO);
            PillLabel lblDel = new PillLabel(del, new Color(254, 242, 242), new Color(220, 38, 38));
            JLabel sep = new JLabel("|");
            sep.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            sep.setForeground(new Color(203, 213, 225));
            add(lblEdit);
            add(sep);
            add(lblDel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : CARD_BG);
            return this;
        }
    }

    private static final class PillLabel extends JLabel {
        private final Color pillBg;
        private final Color pillFg;

        PillLabel(String text, Color bg, Color fg) {
            super(text);
            pillBg = bg;
            pillFg = fg;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(fg);
            setBorder(new EmptyBorder(4, 10, 4, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(pillBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(pillFg);
            FontMetrics fm = g2.getFontMetrics(getFont());
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setFont(getFont());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            Insets ins = getInsets();
            return new Dimension(
                    fm.stringWidth(getText()) + ins.left + ins.right,
                    fm.getHeight() + ins.top + ins.bottom);
        }
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_SM);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(TEXT_TITLE);
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(TABLE_HEAD);
        h.setForeground(TEXT_MUTED);
        h.setPreferredSize(new Dimension(0, 38));
        h.setReorderingAllowed(false);
        DefaultTableCellRenderer hr = (DefaultTableCellRenderer) h.getDefaultRenderer();
        hr.setHorizontalAlignment(SwingConstants.LEFT);
    }

    public static JPanel createSearchField(JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(220, 34));
        JLabel icon = new JLabel(new FlatSearchIcon());
        icon.setBorder(new EmptyBorder(0, 8, 0, 4));
        icon.setForeground(TEXT_MUTED);
        field.setFont(FONT_SM);
        field.setBackground(FIELD_BG);

        boolean isFlatLaf = UIManager.getLookAndFeel().getClass().getName().contains("Flat");
        if (isFlatLaf) {
            field.setMargin(new Insets(6, 4, 6, 8));
            field.putClientProperty("FlatLaf.style", "borderColor: " + colorHex(BORDER) + "; focusedBorderColor: #4f46e5; arc: 12;");
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 4, 6, 8)));
        }

        wrap.add(icon, BorderLayout.WEST);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    public static String colorHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
