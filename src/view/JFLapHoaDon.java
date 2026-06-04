package view;

import controller.LapHoaDonController;
import model.KhachHang;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JFLapHoaDon extends JFrame {

    // Vùng 1: Main
    private JTextField txtMaHD, txtNgayLap, txtNhanVien;
    private JComboBox<KhachHang> cboKhachHang;

    // Vùng 2: Sub
    private JComboBox<SanPham> cboSanPham;
    private JTextField txtSoLuong;
    private JButton btnTangSL, btnGiamSL; // Thêm 2 nút Tăng/Giảm số lượng mới
    private JLabel lblPreviewDonGia, lblPreviewThanhTien;
    private JButton btnThemSP, btnSuaSL, btnXoaSP;
    private JTable tblChiTiet;
    private DefaultTableModel model;
    private JPanel pnlEmptyCart;

    // Vùng 3: Thanh toán
    private JLabel lblTongTien, lblCartCount;
    private JButton btnTaoMoi, btnLuuHD, btnHuy, btnInHD, btnTimKiemHD;

    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font boldFont = new Font("Segoe UI", Font.BOLD, 13);
    private final Font smallBold = new Font("Segoe UI", Font.BOLD, 11);
    private final Color bgMain = new Color(241, 245, 249);
    private final DecimalFormat vndFormat;

    public JFLapHoaDon() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        vndFormat = new DecimalFormat("#,###", symbols);

        setTitle("Lập Hóa Đơn Bán Hàng - TechStore");
        setSize(1150, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgMain);

        initComponents();
    }

    private void initComponents() {
        JPanel pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.setBackground(bgMain);
        pnlRoot.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlGrid = new JPanel(new GridBagLayout());
        pnlGrid.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 16);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0.65;
        pnlGrid.add(buildLeftColumn(), gc);

        gc.gridx = 1;
        gc.weightx = 0.35;
        gc.insets = new Insets(0, 0, 0, 0);
        pnlGrid.add(buildPaymentPanel(), gc);

        pnlRoot.add(pnlGrid, BorderLayout.CENTER);
        add(pnlRoot);
    }

    private JPanel buildLeftColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1.0;
        gc.insets = new Insets(0, 0, 16, 0);

        gc.gridy = 0;
        gc.weighty = 0;
        col.add(buildInvoiceCard(), gc);

        gc.gridy = 1;
        gc.weighty = 1.0;
        gc.insets = new Insets(0, 0, 0, 0);
        col.add(buildProductCard(), gc);
        return col;
    }

    // --- CARD 1: THONG TIN HOA DON ---
    private JPanel buildInvoiceCard() {
        JPanel card = createWhiteCard(new Color(191, 219, 254));
        card.setLayout(new BorderLayout());

        card.add(createBadge("1. THÔNG TIN HÓA ĐƠN (MAIN)", new Color(59, 130, 246)), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 20, 20, 20));

        JLabel lblSection = new JLabel("THÔNG TIN KHÁCH HÀNG & GIAO DỊCH");
        lblSection.setFont(smallBold);
        lblSection.setForeground(new Color(37, 99, 235));
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblSection);
        body.add(Box.createVerticalStrut(16));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 12));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        txtMaHD = createReadOnlyField();
        txtNgayLap = createReadOnlyField();
        txtNgayLap.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

        cboKhachHang = createKhachHangCombo();
        txtNhanVien = createReadOnlyField();

        grid.add(createFieldGroup("Mã Hóa Đơn", txtMaHD));
        grid.add(createFieldGroup("Ngày Lập", txtNgayLap));
        grid.add(createFieldGroup("Khách Hàng", cboKhachHang));
        grid.add(createFieldGroup("Nhân Viên", txtNhanVien));

        body.add(grid);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // --- CARD 2: CHI TIET HOA DON ---
    private JPanel buildProductCard() {
        JPanel card = createWhiteCard(new Color(167, 243, 208));
        card.setLayout(new BorderLayout());

        card.add(createBadge("2. CHI TIẾT HÓA ĐƠN (SUB)", new Color(16, 185, 129)), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 20, 20, 20));

        JLabel lblSection = new JLabel("SẢN PHẨM BÁN RA");
        lblSection.setFont(smallBold);
        lblSection.setForeground(new Color(5, 150, 105));
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblSection);
        body.add(Box.createVerticalStrut(16));

        JPanel pnlQuickAdd = new JPanel(new GridBagLayout());
        pnlQuickAdd.setBackground(new Color(248, 250, 252));
        pnlQuickAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        pnlQuickAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;

        cboSanPham = createSanPhamCombo();
        
        // =========================================================
        // THIẾT KẾ MỚI: Nhóm Số Lượng lồng nút bấm [-] và [+]
        // =========================================================
        JPanel pnlSoLuongGroup = new JPanel(new BorderLayout(4, 0));
        pnlSoLuongGroup.setOpaque(false);

        txtSoLuong = createInputField();
        txtSoLuong.setText("1");
        txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
        txtSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSoLuong.setPreferredSize(new Dimension(45, 32));

        btnGiamSL = new JButton("-");
        btnTangSL = new JButton("+");

        styleMinusButton(btnGiamSL);
        stylePlusButton(btnTangSL);

        pnlSoLuongGroup.add(btnGiamSL, BorderLayout.WEST);
        pnlSoLuongGroup.add(txtSoLuong, BorderLayout.CENTER);
        pnlSoLuongGroup.add(btnTangSL, BorderLayout.EAST);
        // =========================================================

        btnThemSP = new JButton("Thêm Sản Phẩm");
        styleButton(btnThemSP, new Color(37, 99, 235), 13);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.50;
        pnlQuickAdd.add(createFieldGroup("Chọn Sản Phẩm", cboSanPham), gbc);
        gbc.gridx = 1; gbc.weightx = 0.22; // Tăng nhẹ lề ngang để cụm nút hiển thị thoải mái
        pnlQuickAdd.add(createFieldGroup("Số Lượng", pnlSoLuongGroup), gbc);
        gbc.gridx = 2; gbc.weightx = 0.28;
        gbc.insets = new Insets(18, 0, 0, 0);
        pnlQuickAdd.add(btnThemSP, gbc);

        body.add(pnlQuickAdd);
        body.add(Box.createVerticalStrut(14));

        JPanel pnlPreview = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlPreview.setOpaque(false);
        pnlPreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        lblPreviewDonGia = createPreviewValue("0 đ");
        lblPreviewThanhTien = createPreviewValue("0 đ");
        lblPreviewThanhTien.setForeground(new Color(5, 150, 105));
        pnlPreview.add(createPreviewRow("Đơn giá niêm yết:", lblPreviewDonGia));
        pnlPreview.add(createPreviewRow("Thành tiền dự kiến:", lblPreviewThanhTien));
        body.add(pnlPreview);
        body.add(Box.createVerticalStrut(12));

        model = new DefaultTableModel(
                new String[]{"Mã SP", "Tên Sản Phẩm", "Đơn Giá", "Số Lượng", "Thành Tiền", "Thao Tác"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblChiTiet = new JTable(model);
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblChiTiet.setRowHeight(36);
        tblChiTiet.setShowGrid(false);
        tblChiTiet.setIntercellSpacing(new Dimension(0, 0));
        tblChiTiet.setSelectionBackground(new Color(238, 242, 255));
        tblChiTiet.setSelectionForeground(new Color(30, 41, 59));

        JTableHeader header = tblChiTiet.getTableHeader();
        header.setFont(smallBold);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(100, 116, 139));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        util.TechStoreUI.configureActionColumn(tblChiTiet, 5);

        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(180);

        JScrollPane scrollTable = new JScrollPane(tblChiTiet);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249)));
        scrollTable.getViewport().setBackground(Color.WHITE);
        scrollTable.setPreferredSize(new Dimension(0, 240));

        pnlEmptyCart = new JPanel(new GridBagLayout());
        pnlEmptyCart.setBackground(Color.WHITE);
        pnlEmptyCart.setOpaque(true);
        JLabel lblEmpty = new JLabel("Chưa có sản phẩm nào trong hóa đơn này.");
        lblEmpty.setFont(labelFont);
        lblEmpty.setForeground(new Color(148, 163, 184));
        pnlEmptyCart.add(lblEmpty);

        JLayeredPane layeredTable = new JLayeredPane();
        scrollTable.setBounds(0, 0, 800, 240);
        pnlEmptyCart.setBounds(0, 36, 800, 204);
        layeredTable.setPreferredSize(new Dimension(0, 240));
        layeredTable.add(scrollTable, JLayeredPane.DEFAULT_LAYER);
        layeredTable.add(pnlEmptyCart, JLayeredPane.PALETTE_LAYER);
        layeredTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layeredTable.getWidth();
                int h = layeredTable.getHeight();
                scrollTable.setBounds(0, 0, w, h);
                pnlEmptyCart.setBounds(0, 36, w, h - 36);
            }
        });

        body.add(layeredTable);
        body.add(Box.createVerticalStrut(10));

        JPanel pnlRowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlRowActions.setOpaque(false);
        pnlRowActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSuaSL = new JButton("Sửa Số Lượng");
        btnXoaSP = new JButton("Xóa Sản Phẩm");
        styleOutlineButton(btnSuaSL, new Color(37, 99, 235));
        styleOutlineButton(btnXoaSP, new Color(220, 38, 38));
        pnlRowActions.add(btnSuaSL);
        pnlRowActions.add(btnXoaSP);
        body.add(pnlRowActions);

        model.addTableModelListener(e -> updateEmptyCartState());

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // --- CARD 3: THANH TOAN (sidebar phai) ---
    private JPanel buildPaymentPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), 0, getHeight(), new Color(30, 27, 75));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 41, 59)),
                new EmptyBorder(0, 0, 0, 0)
        ));

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(createBadge("3. THANH TOÁN & ĐIỀU KHIỂN", new Color(249, 115, 22)));
        card.add(badgeWrap, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        JLabel lblSection = new JLabel("TỔNG TIỀN & THANH TOÁN");
        lblSection.setFont(smallBold);
        lblSection.setForeground(new Color(251, 146, 60));
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblSection);
        body.add(Box.createVerticalStrut(24));

        JLabel lblTotalLabel = new JLabel("TỔNG TIỀN PHẢI THANH TOÁN:");
        lblTotalLabel.setFont(labelFont);
        lblTotalLabel.setForeground(new Color(148, 163, 184));
        lblTotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTongTien = new JLabel("0 VNĐ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTongTien.setForeground(new Color(251, 146, 60));
        lblTongTien.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(lblTotalLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(lblTongTien);
        body.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(sep);
        body.add(Box.createVerticalStrut(16));

        lblCartCount = createSummaryRow("Tổng số sản phẩm:", "0 sản phẩm");
        body.add(lblCartCount);
        body.add(Box.createVerticalStrut(8));
        body.add(createSummaryRow("Thuế VAT (0%):", "0 VNĐ"));
        body.add(Box.createVerticalStrut(8));
        body.add(createSummaryRow("Phương thức:", "Tiền mặt / Chuyển khoản", new Color(52, 211, 153)));

        body.add(Box.createVerticalGlue());

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 30));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(sep2);
        body.add(Box.createVerticalStrut(16));

        btnLuuHD = new JButton("LƯU HÓA ĐƠN");
        styleButton(btnLuuHD, new Color(5, 150, 105), 14);
        btnLuuHD.setPreferredSize(new Dimension(0, 44));
        btnLuuHD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLuuHD.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(btnLuuHD);
        body.add(Box.createVerticalStrut(10));

        JPanel pnlDual = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlDual.setOpaque(false);
        pnlDual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pnlDual.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTaoMoi = new JButton("Tạo Mới HD");
        btnHuy = new JButton("Hủy Đơn");
        styleDarkButton(btnTaoMoi);
        styleDangerButton(btnHuy);
        pnlDual.add(btnTaoMoi);
        pnlDual.add(btnHuy);
        body.add(pnlDual);
        body.add(Box.createVerticalStrut(10));

        btnInHD = new JButton("In Hóa Đơn Mẫu");
        styleButton(btnInHD, new Color(37, 99, 235), 12);
        btnInHD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnInHD.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(btnInHD);
        body.add(Box.createVerticalStrut(8));

        btnTimKiemHD = new JButton("Tìm Kiếm HD");
        styleDarkButton(btnTimKiemHD);
        btnTimKiemHD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnTimKiemHD.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(btnTimKiemHD);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // --- UI HELPERS ---
    private JPanel createWhiteCard(Color borderColor) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(0, 0, 0, 0)
        ));
        return card;
    }

    private JPanel createBadge(String text, Color bg) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrap.setOpaque(false);
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        wrap.add(badge);
        return wrap;
    }

    private JPanel createFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, 4));
        group.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(100, 116, 139));
        group.add(lbl, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group;
    }

    private JPanel createPreviewRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(new EmptyBorder(8, 10, 8, 10));
        JLabel lbl = new JLabel(label);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(100, 116, 139));
        row.add(lbl, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JLabel createPreviewValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(boldFont);
        lbl.setForeground(new Color(30, 41, 59));
        return lbl;
    }

    private JLabel createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, Color.WHITE);
    }

    private JLabel createSummaryRow(String label, String value, Color valueColor) {
        JLabel lbl = new JLabel("<html><span style='color:#94a3b8;'>" + label + "</span>"
                + " <b style='color:rgb(" + valueColor.getRed() + "," + valueColor.getGreen() + "," + valueColor.getBlue() + ");'>" + value + "</b></html>");
        lbl.setFont(labelFont);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createReadOnlyField() {
        JTextField txt = createInputField();
        txt.setEditable(false);
        txt.setBackground(new Color(241, 245, 249));
        txt.setForeground(new Color(51, 65, 85));
        return txt;
    }

    private JTextField createInputField() {
        JTextField txt = new JTextField();
        txt.setFont(fieldFont);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return txt;
    }

    private JComboBox<KhachHang> createKhachHangCombo() {
        JComboBox<KhachHang> cbo = new JComboBox<>();
        cbo.setFont(fieldFont);
        cbo.setBackground(Color.WHITE);
        cbo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KhachHang) {
                    KhachHang kh = (KhachHang) value;
                    String sdt = kh.getSdt() != null ? kh.getSdt() : "";
                    setText(kh.getMaKH() + " - " + kh.getHoTen() + (sdt.isEmpty() ? "" : " (" + sdt + ")"));
                }
                return this;
            }
        });
        return cbo;
    }

    private JComboBox<SanPham> createSanPhamCombo() {
        JComboBox<SanPham> cbo = new JComboBox<>();
        cbo.setFont(fieldFont);
        cbo.setBackground(Color.WHITE);
        cbo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SanPham) {
                    SanPham sp = (SanPham) value;
                    setText(sp.getMaSP() + " - " + sp.getTenSP() + " (Kho: " + sp.getSoLuong() + ")");
                }
                return this;
            }
        });
        return cbo;
    }

    private void styleButton(JButton btn, Color bg, int fontSize) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 14, 6, 14));
        util.TechStoreUI.applyRoundedButton(btn, 12);
    }

    private void styleOutlineButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(5, 12, 5, 12));
        util.TechStoreUI.applyRoundedButton(btn, 12);
    }

    private void styleDarkButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 12, 6, 12));
        util.TechStoreUI.applyRoundedButton(btn, 12);
    }

    private void styleDangerButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(254, 202, 202));
        btn.setBackground(new Color(127, 29, 29, 180));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 12, 6, 12));
        util.TechStoreUI.applyRoundedButton(btn, 12);
    }

    // Phong cách hiện đại cho hai nút Tăng / Giảm
    private void stylePlusButton(JButton btn) {
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setForeground(Color.WHITE);
    btn.setBackground(new Color(34, 197, 94)); // Green
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(32, 32));
    btn.setMargin(new Insets(2, 6, 2, 6));
    util.TechStoreUI.applyRoundedButton(btn, 8);
}

private void styleMinusButton(JButton btn) {
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setForeground(Color.WHITE);
    btn.setBackground(new Color(249, 115, 22)); // Orange
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(32, 32));
    btn.setMargin(new Insets(2, 6, 2, 6));
    util.TechStoreUI.applyRoundedButton(btn, 8);
}

    public String formatVND(double amount) {
        return vndFormat.format(amount) + " đ";
    }

    public void updateEmptyCartState() {
        boolean empty = model.getRowCount() == 0;
        pnlEmptyCart.setVisible(empty);
    }

    public void updateCartCount(int count) {
        lblCartCount.setText("<html><span style='color:#94a3b8;'>Tổng số sản phẩm:</span>"
                + " <b style='color:white;'>" + count + " sản phẩm</b></html>");
    }

    // --- GETTERS ---
    public JTextField getTxtMaHD() { return txtMaHD; }
    public JTextField getTxtNgayLap() { return txtNgayLap; }
    public JTextField getTxtNhanVien() { return txtNhanVien; }
    public JComboBox<KhachHang> getCboKhachHang() { return cboKhachHang; }
    public JComboBox<SanPham> getCboSanPham() { return cboSanPham; }
    public JLabel getLblPreviewDonGia() { return lblPreviewDonGia; }
    public JLabel getLblPreviewThanhTien() { return lblPreviewThanhTien; }
    public JTextField getTxtSoLuong() { return txtSoLuong; }
    public JButton getBtnTangSL() { return btnTangSL; } // Getter nút tăng
    public JButton getBtnGiamSL() { return btnGiamSL; } // Getter nút giảm
    public JButton getBtnThemSP() { return btnThemSP; }
    public JButton getBtnSuaSL() { return btnSuaSL; }
    public JButton getBtnXoaSP() { return btnXoaSP; }
    public JTable getTblChiTiet() { return tblChiTiet; }
    public DefaultTableModel getModel() { return model; }
    public JLabel getLblTongTien() { return lblTongTien; }
    public JButton getBtnTaoMoi() { return btnTaoMoi; }
    public JButton getBtnLuuHD() { return btnLuuHD; }
    public JButton getBtnHuy() { return btnHuy; }
    public JButton getBtnInHD() { return btnInHD; }
    public JButton getBtnTimKiemHD() { return btnTimKiemHD; }

    public static void main(String[] args) {
        util.TechStoreUI.installLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            JFLapHoaDon view = new JFLapHoaDon();
            new LapHoaDonController(view);
            view.setVisible(true);
        });
    }
}