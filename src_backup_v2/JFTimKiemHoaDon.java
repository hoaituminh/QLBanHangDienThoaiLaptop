package view;

import util.TechStoreUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.Icon;
import java.awt.*;

/**
 * Dialog tra cứu lịch sử hóa đơn (theo mockup HTML / video).
 * Giao diện đã được thiết kế lại chuẩn Hiện đại, Đồng bộ thẩm mỹ với Trang Chủ Dashboard.
 */
public class JFTimKiemHoaDon extends JDialog {

    private JTextField txtTimKiem;
    private JComboBox<ComboItem> cboNhanVien;
    private JTable tblHoaDon;
    private DefaultTableModel modelHD;
    private JLabel lblEmpty;
    private JButton btnDong;

    public static final int COL_MA_HD = 0;
    public static final int COL_NGAY   = 1;
    public static final int COL_KH     = 2;
    public static final int COL_NV     = 3;
    public static final int COL_TONG   = 4;
    public static final int COL_ACTION = 5;

    // Bộ vẽ Icon Vector bằng Graphics2D sắc nét - Chuyển sang PUBLIC để sửa triệt để cảnh báo của NetBeans
    public static class VectorIcon implements Icon {
        private final String type; // "search", "bill"
        private final int width;
        private final int height;
        private final Color iconColor;

        public VectorIcon(String type, int width, int height, Color iconColor) {
            this.type = type;
            this.width = width;
            this.height = height;
            this.iconColor = iconColor;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Khử răng cưa cho nét vẽ vector mượt mà
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(iconColor);

            if ("search".equals(type)) { // Kính lúp hiện đại chuẩn tỷ lệ vàng
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Thu nhỏ đường kính vòng tròn xuống 8px để tránh nét vẽ cán lấn vào trong
                int d = 8;
                g2.drawOval(x + 2, y + 2, d, d);
                // Cán kính bắt đầu từ rìa ngoài (x+8, y+8) kéo ra góc (width-2, height-2) để tránh tạo thành chữ "Q"
                g2.drawLine(x + 8, y + 8, x + width - 2, y + height - 2);
            } else if ("bill".equals(type)) { // Tờ hóa đơn thanh toán
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(x + 2, y + 1, width - 4, height - 2, 3, 3);
                g2.drawLine(x + 5, y + 5, x + width - 5, y + 5);
                g2.drawLine(x + 5, y + 9, x + width - 5, y + 9);
                g2.drawLine(x + 5, y + 13, x + width - 7, y + 13);
            }
            g2.dispose();
        }

        @Override public int getIconWidth() { return width; }
        @Override public int getIconHeight() { return height; }
    }

    public static class ComboItem {
        public final String value;
        public final String label;
        public ComboItem(String value, String label) {
            this.value = value;
            this.label = label;
        }
        @Override public String toString() { return label; }
    }

    public JFTimKiemHoaDon(Frame parent) {
        super(parent, "Lịch Sử & Tìm Kiếm Hóa Đơn", true);
        setSize(940, 600); // Tăng nhẹ kích thước để bảng thông thoáng hơn
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // ĐỔI NỀN: Sử dụng màu nền xám Slate 100 cao cấp đồng bộ với Dashboard chính
        getContentPane().setBackground(util.TechStoreUI.BG_MAIN); 
        initComponents();
    }

    private void initComponents() {
        // Layout chính của Dialog có padding biên rộng rãi giống Dashboard
        JPanel pnlMainContainer = new JPanel(new BorderLayout(0, 20));
        pnlMainContainer.setBackground(util.TechStoreUI.BG_MAIN);
        pnlMainContainer.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Khung trắng chứa dữ liệu (Card)
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(util.TechStoreUI.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1), // Viền Slate 200 mờ
            new EmptyBorder(24, 24, 24, 24)
        ));

        // 1. Header (Tiêu đề và phụ đề màu tối sang trọng)
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlHeader.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Lịch Sử & Tìm Kiếm Hóa Đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Font chữ to, đậm nét chuyên nghiệp
        lblTitle.setForeground(new Color(15, 23, 42)); // Màu chữ đen Slate 900 cực sâu
        
        JLabel lblSub = new JLabel("Tra cứu nhanh toàn bộ lịch sử bán hàng hệ thống");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139)); // Màu xám nhạt Slate 500 tinh tế
        
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);
        card.add(pnlHeader, BorderLayout.NORTH);

        // 2. Filter Row (Thanh tìm kiếm và bộ lọc nhân viên)
        JPanel pnlFilter = new JPanel(new GridBagLayout());
        pnlFilter.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 0, 12);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weighty = 0;

        txtTimKiem = TechStoreUI.createField("Nhập Mã hóa đơn (Ví dụ: HD01), Tên khách hàng hoặc Số điện thoại...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Ô tìm kiếm bo tròn nhẹ kèm Icon Vector kính lúp cao cấp
        JPanel pnlSearch = new JPanel(new BorderLayout(8, 0));
        pnlSearch.setBackground(util.TechStoreUI.BG_MAIN);
        pnlSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        
        JLabel lblSearchIcon = new JLabel(new VectorIcon("search", 16, 16, new Color(148, 163, 184)));
        pnlSearch.add(lblSearchIcon, BorderLayout.WEST);
        pnlSearch.add(txtTimKiem, BorderLayout.CENTER);

        cboNhanVien = new JComboBox<>();
        cboNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboNhanVien.setBackground(util.TechStoreUI.BG_MAIN);
        cboNhanVien.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1));

        g.gridx = 0; g.gridy = 0; g.weightx = 0.70;
        pnlFilter.add(pnlSearch, g);
        g.gridx = 1; g.weightx = 0.30;
        pnlFilter.add(cboNhanVien, g);

        // 3. Table (Bảng dữ liệu phẳng phong cách Dashboard)
        modelHD = new DefaultTableModel(
            new String[]{"Mã HD", "Ngày Lập", "Khách Hàng", "Nhân Viên Lập", "Tổng Thanh Toán", "Hành Động"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblHoaDon = new JTable(modelHD);
        TechStoreUI.styleTable(tblHoaDon);
        
        // Áp dụng phong cách bảng phẳng (Flat Table UI)
        tblHoaDon.setRowHeight(38); // Tăng khoảng cách dòng rộng rãi
        tblHoaDon.setShowVerticalLines(false); // Ẩn viền dọc hiện đại
        tblHoaDon.setGridColor(util.TechStoreUI.BG_MAIN); // Viền ngang mờ
        
        tblHoaDon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblHoaDon.getTableHeader().setBackground(util.TechStoreUI.BG_MAIN); // Nền header xám mịn
        tblHoaDon.getTableHeader().setForeground(new Color(71, 85, 105)); // Chữ header xám trung tính
        tblHoaDon.getTableHeader().setPreferredSize(new Dimension(0, 35));

        tblHoaDon.getColumnModel().getColumn(COL_TONG).setCellRenderer(rightRenderer());
        tblHoaDon.getColumnModel().getColumn(COL_ACTION).setCellRenderer(new ActionButtonRenderer());
        tblHoaDon.getColumnModel().getColumn(COL_ACTION).setMinWidth(130);
        tblHoaDon.getColumnModel().getColumn(COL_ACTION).setPreferredWidth(140);

        JScrollPane scroll = new JScrollPane(tblHoaDon);
        scroll.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1));
        scroll.getViewport().setBackground(util.TechStoreUI.CARD_BG);

        lblEmpty = new JLabel("Không tìm thấy hóa đơn nào khớp với bộ lọc tra cứu!", SwingConstants.CENTER);
        lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblEmpty.setForeground(new Color(148, 163, 184));
        lblEmpty.setBorder(new EmptyBorder(10, 0, 10, 0));
        lblEmpty.setVisible(false);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 12));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlFilter, BorderLayout.NORTH);
        pnlCenter.add(scroll, BorderLayout.CENTER);
        pnlCenter.add(lblEmpty, BorderLayout.SOUTH);
        card.add(pnlCenter, BorderLayout.CENTER);

        // 4. Footer (Thanh chân trang chứa nút bấm)
        JPanel pnlFoot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlFoot.setOpaque(false);
        pnlFoot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, util.TechStoreUI.BG_MAIN));
        
        btnDong = new JButton();
        TechStoreUI.styleSecondaryButton(btnDong, "Đóng Lại");
        btnDong.setPreferredSize(new Dimension(110, 35));
        pnlFoot.add(btnDong);

        // Thêm các Panel con vào layout chính
        pnlMainContainer.add(card, BorderLayout.CENTER);
        pnlMainContainer.add(pnlFoot, BorderLayout.SOUTH);
        
        add(pnlMainContainer);
    }

    private static DefaultTableCellRenderer rightRenderer() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.RIGHT);
        return r;
    }

    /** Renderer nút "Xem & In Lại" */
    public static class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton btn = new JButton("Xem & In Lại");

        public ActionButtonRenderer() {
            setOpaque(true);
            setLayout(new GridBagLayout());
            TechStoreUI.stylePrimaryButton(btn, "Xem & In Lại");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btn.setMargin(new Insets(4, 10, 4, 10));
            
            // Tích hợp Icon vector tờ hóa đơn nhỏ màu trắng
            btn.setIcon(new VectorIcon("bill", 12, 12, Color.WHITE));
            btn.setIconTextGap(6);
            
            add(btn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JComboBox<ComboItem> getCboNhanVien() { return cboNhanVien; }
    public JTable getTblHoaDon() { return tblHoaDon; }
    public DefaultTableModel getModelHD() { return modelHD; }
    public JLabel getLblEmpty() { return lblEmpty; }
    public JButton getBtnDong() { return btnDong; }
}