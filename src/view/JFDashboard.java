package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class JFDashboard extends JPanel {

    // Lớp Panel vẽ vòng tròn/hình vuông bo góc chứa biểu tượng với hiệu ứng màu cao cấp
    private static class RoundedIconPanel extends JPanel {
        private final Color backgroundColor;
        private final Color iconColor;
        private final String iconType; // "revenue", "invoice", "database", "alert", "chart", "bill_gradient"

        public RoundedIconPanel(Color bgColor, Color iconColor, String iconType, int size) {
            this.backgroundColor = bgColor;
            this.iconColor = iconColor;
            this.iconType = iconType;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            // Bật chế độ khử răng cưa cho nét vẽ mượt mà, không bị vỡ cạnh
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Vẽ nền bo tròn mềm mại
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, w, h, 14, 14);

            // Vẽ biểu tượng Vector trực tiếp (Rất sắc nét, không phụ thuộc vào file ảnh bên ngoài)
            g2.setColor(iconColor);

            switch (iconType) {
                case "revenue": // Biểu tượng ký hiệu đô la / tiền tệ $
                    g2.setFont(new Font("Segoe UI", Font.BOLD, (int)(w * 0.46)));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (w - fm.stringWidth("$")) / 2;
                    int y = ((h - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString("$", x, y);
                    break;

                case "invoice": // Biểu tượng tờ hóa đơn (Document)
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int docW = (int)(w * 0.3);
                    int docH = (int)(h * 0.42);
                    int docX = (w - docW) / 2;
                    int docY = (h - docH) / 2;
                    g2.drawRoundRect(docX, docY, docW, docH, 4, 4);
                    g2.drawLine(docX + 4, docY + 6, docX + docW - 4, docY + 6);
                    g2.drawLine(docX + 4, docY + 11, docX + docW - 4, docY + 11);
                    g2.drawLine(docX + 4, docY + 16, docX + docW - 7, docY + 16);
                    break;

                case "database": // Biểu tượng cơ sở dữ liệu (Database xếp chồng)
                    g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int dbW = (int)(w * 0.35);
                    int dbH = (int)(h * 0.12);
                    int dbX = (w - dbW) / 2;
                    int startY = (h - (int)(h * 0.42)) / 2;
                    
                    for (int i = 0; i < 3; i++) {
                        int currY = startY + (i * 7);
                        g2.drawOval(dbX, currY, dbW, dbH);
                        if (i < 2) {
                            g2.drawLine(dbX, currY + (dbH / 2), dbX, currY + 7 + (dbH / 2));
                            g2.drawLine(dbX + dbW, currY + (dbH / 2), dbX + dbW, currY + 7 + (dbH / 2));
                        }
                    }
                    break;

                case "alert": // Biểu tượng tam giác cảnh báo nguy hiểm
                    g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int sizeTriangle = (int)(w * 0.42);
                    int ax1 = w / 2;
                    int ay1 = (h - sizeTriangle) / 2 + 1;
                    int ax2 = ax1 - (sizeTriangle / 2);
                    int ay2 = ay1 + sizeTriangle;
                    int ax3 = ax1 + (sizeTriangle / 2);
                    int ay3 = ay2;
                    
                    g2.drawPolygon(new int[]{ax1, ax2, ax3}, new int[]{ay1, ay2, ay3}, 3);
                    g2.drawLine(ax1, ay1 + 7, ax1, ay1 + 13);
                    g2.fillRect(ax1 - 1, ay1 + 16, 2, 2);
                    break;

                case "chart": // Biểu tượng xu hướng tăng trưởng (Trend chart mũi tên đi lên)
                    g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = (w - 18) / 2;
                    int cy = (h - 14) / 2;
                    // Vẽ các đường zic-zac
                    g2.drawLine(cx, cy + 12, cx + 5, cy + 7);
                    g2.drawLine(cx + 5, cy + 7, cx + 10, cy + 10);
                    g2.drawLine(cx + 10, cy + 10, cx + 18, cy + 1);
                    // Vẽ mũi tên chỉ lên ở đầu mút
                    g2.drawLine(cx + 13, cy + 1, cx + 18, cy + 1);
                    g2.drawLine(cx + 18, cy + 1, cx + 18, cy + 6);
                    break;

                case "bill_gradient": // Biểu tượng hóa đơn răng cưa cách điệu màu tím
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int bx = (w - 16) / 2;
                    int by = (h - 20) / 2;
                    // Vẽ khung hóa đơn dạng răng cưa ở hai đáy
                    g2.drawRoundRect(bx, by, 16, 20, 3, 3);
                    // Các vạch dòng thông tin hóa đơn
                    g2.drawLine(bx + 4, by + 5, bx + 12, by + 5);
                    g2.drawLine(bx + 4, by + 10, bx + 12, by + 10);
                    g2.drawLine(bx + 4, by + 15, bx + 9, by + 15);
                    break;
            }
            g2.dispose();
        }
    }

    private static class StatCard {
        private final JPanel panel;
        private final JLabel valueLabel;
        private final JLabel descLabel;

        private StatCard(JPanel panel, JLabel valueLabel, JLabel descLabel) {
            this.panel = panel;
            this.valueLabel = valueLabel;
            this.descLabel = descLabel;
        }
    }

    private JLabel lblTongDoanhThu;
    private JLabel lblTongHoaDon;
    private JLabel lblSanPhamCanNhap;
    private JLabel lblTongSanPham;
    private JLabel lblTongSanPhamDesc;
    private JTable tblHoaDonGanDay;
    private JTable tblTopSanPham;
    private DefaultTableModel modelHoaDonGanDay;
    private DefaultTableModel modelTopSanPham;

    public JFDashboard() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(241, 245, 249)); // Slate 100

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(241, 245, 249));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Stats cards panel (4 Thẻ thống kê hàng đầu)
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Bảng dữ liệu hai cột phía dưới
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        contentPanel.setBackground(new Color(241, 245, 249));

        // Bảng danh sách hóa đơn gần đây
        JPanel recentOrdersPanel = createRecentOrdersPanel();
        contentPanel.add(recentOrdersPanel);

        // Bảng danh sách sản phẩm bán chạy nhất
        JPanel topProductsPanel = createTopProductsPanel();
        contentPanel.add(topProductsPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(new Color(241, 245, 249));

        // 1. Thẻ doanh thu: Nền xanh lá nhạt, biểu tượng màu xanh lá đậm
        StatCard doanhThuCard = createStatCard("TỔNG DOANH THU", "0 đ", 
                new Color(30, 41, 59), 
                new Color(220, 252, 231), 
                new Color(21, 128, 61), 
                "revenue", "Thống kê toàn thời gian");
        lblTongDoanhThu = doanhThuCard.valueLabel;
        statsPanel.add(doanhThuCard.panel);

        // 2. Thẻ hóa đơn: Nền xanh dương nhạt, biểu tượng xanh dương đậm
        StatCard hoaDonCard = createStatCard("SỐ HÓA ĐƠN ĐÃ LẬP", "0 Hóa đơn", 
                new Color(30, 41, 59), 
                new Color(219, 234, 254), 
                new Color(29, 78, 216), 
                "invoice", "Từ bảng dữ liệu HOADON");
        lblTongHoaDon = hoaDonCard.valueLabel;
        statsPanel.add(hoaDonCard.panel);

        // 3. Thẻ tổng sản phẩm: Nền tím nhạt, biểu tượng màu tím đậm
        StatCard sanPhamCard = createStatCard("TỔNG SẢN PHẨM", "0", 
                new Color(30, 41, 59), 
                new Color(243, 232, 255), 
                new Color(126, 34, 206), 
                "database", "Đang tải...");
        lblTongSanPham = sanPhamCard.valueLabel;
        lblTongSanPhamDesc = sanPhamCard.descLabel;
        statsPanel.add(sanPhamCard.panel);

        // 4. Thẻ sản phẩm cần nhập: Nền đỏ nhạt, biểu tượng đỏ đậm
        StatCard sanPhamCanNhapCard = createStatCard("SẢN PHẨM CẦN NHẬP", "0 mặt hàng", 
                new Color(225, 29, 72), 
                new Color(255, 228, 230), 
                new Color(190, 18, 60), 
                "alert", "Mức tồn kho dưới 10!");
        lblSanPhamCanNhap = sanPhamCanNhapCard.valueLabel;
        statsPanel.add(sanPhamCanNhapCard.panel);

        return statsPanel;
    }

    private StatCard createStatCard(String title, String value, Color textColor, 
                                   Color iconBgColor, Color iconColor, String iconType, String description) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(18, 18, 18, 18)
        ));
        card.setPreferredSize(new Dimension(0, 125));

        JPanel textContainer = new JPanel(new GridBagLayout());
        textContainer.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(new Color(148, 163, 184));
        gbc.gridy = 0;
        textContainer.add(titleLabel, gbc);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(textColor);
        gbc.gridy = 1;
        textContainer.add(valueLabel, gbc);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        descLabel.setForeground(new Color(148, 163, 184));
        gbc.gridy = 2;
        textContainer.add(descLabel, gbc);

        card.add(textContainer, BorderLayout.CENTER);

        RoundedIconPanel iconPanel = new RoundedIconPanel(iconBgColor, iconColor, iconType, 52);
        JPanel iconAlignPanel = new JPanel(new GridBagLayout());
        iconAlignPanel.setBackground(Color.WHITE);
        iconAlignPanel.add(iconPanel);

        card.add(iconAlignPanel, BorderLayout.EAST);

        return new StatCard(card, valueLabel, descLabel);
    }

    private JPanel createRecentOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // ĐÃ TỐI ƯU: Sử dụng VectorIcon vẽ trực tiếp hóa đơn màu tím răng cưa làm icon tiêu đề
        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTitle.setOpaque(false);
        
        RoundedIconPanel titleIcon = new RoundedIconPanel(
                new Color(243, 232, 255), // Nền tím cực nhạt
                new Color(126, 34, 206), // Nét vẽ màu tím đậm sang trọng
                "bill_gradient", 34 // Kích thước icon tiêu đề nhỏ gọn
        );
        
        JLabel titleLabel = new JLabel("Danh Sách Hóa Đơn Gần Đây (Bảng HOADON)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(15, 23, 42));

        pnlTitle.add(titleIcon);
        pnlTitle.add(titleLabel);
        panel.add(pnlTitle, BorderLayout.NORTH);

        String[] columns = {"Mã HD", "Khách Hàng", "Ngày Lập", "Tổng Tiền"};
        modelHoaDonGanDay = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDonGanDay = new JTable(modelHoaDonGanDay);
        tblHoaDonGanDay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblHoaDonGanDay.setRowHeight(38);
        tblHoaDonGanDay.setShowVerticalLines(false);
        tblHoaDonGanDay.setGridColor(new Color(241, 245, 249));

        tblHoaDonGanDay.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblHoaDonGanDay.getTableHeader().setBackground(new Color(248, 250, 252));
        tblHoaDonGanDay.getTableHeader().setForeground(new Color(71, 85, 105));
        tblHoaDonGanDay.getTableHeader().setPreferredSize(new Dimension(0, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblHoaDonGanDay.getColumnCount(); i++) {
            tblHoaDonGanDay.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblHoaDonGanDay);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.setPreferredSize(new Dimension(0, 320));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // ĐÃ TỐI ƯU: Sử dụng VectorIcon vẽ trực tiếp biểu đồ xu hướng màu cam làm icon tiêu đề
        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTitle.setOpaque(false);
        
        RoundedIconPanel titleIcon = new RoundedIconPanel(
                new Color(254, 243, 199), // Nền cam nhạt ấm áp
                new Color(217, 119, 6), // Nét vẽ màu cam sậm nổi bật
                "chart", 34
        );

        JLabel titleLabel = new JLabel("Sản Phẩm Bán Chạy Nhất");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(15, 23, 42));

        pnlTitle.add(titleIcon);
        pnlTitle.add(titleLabel);
        panel.add(pnlTitle, BorderLayout.NORTH);

        String[] columns = {"STT", "Tên Sản Phẩm", "Đã Bán", "Doanh Thu"};
        modelTopSanPham = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTopSanPham = new JTable(modelTopSanPham);
        tblTopSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTopSanPham.setRowHeight(38);
        tblTopSanPham.setShowVerticalLines(false);
        tblTopSanPham.setGridColor(new Color(241, 245, 249));

        tblTopSanPham.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTopSanPham.getTableHeader().setBackground(new Color(248, 250, 252));
        tblTopSanPham.getTableHeader().setForeground(new Color(71, 85, 105));
        tblTopSanPham.getTableHeader().setPreferredSize(new Dimension(0, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblTopSanPham.getColumnCount(); i++) {
            tblTopSanPham.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblTopSanPham);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.setPreferredSize(new Dimension(0, 320));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Getters kết nối với Controller
    public JLabel getLblTongDoanhThu() {
        return lblTongDoanhThu;
    }

    public JLabel getLblTongHoaDon() {
        return lblTongHoaDon;
    }

    public JLabel getLblSanPhamCanNhap() {
        return lblSanPhamCanNhap;
    }

    public JLabel getLblTongSanPham() {
        return lblTongSanPham;
    }

    public JLabel getLblTongSanPhamDesc() {
        return lblTongSanPhamDesc;
    }

    public DefaultTableModel getModelHoaDonGanDay() {
        return modelHoaDonGanDay;
    }

    public DefaultTableModel getModelTopSanPham() {
        return modelTopSanPham;
    }
}