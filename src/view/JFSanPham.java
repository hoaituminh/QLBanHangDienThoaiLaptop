package view;

import dao.LoaiSPDAO;
import dao.SanPhamDAO;
import model.LoaiSP;
import model.SanPham;
import controller.SanPhamController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class JFSanPham extends JFrame {

    private JTextField txtMaSP, txtTenSP, txtDonGia, txtSoLuong, txtHangSX, txtTimKiem;
    private JTextArea txtMoTa;
    private JComboBox<LoaiSP> cboLoaiSP;
    private JLabel lblHinh, lblTrangThaiForm, lblBadgeForm, lblTongCong, lblThongKe;
    private JButton btnLuu, btnXoa, btnLamMoi, btnChonAnh, btnTimKiem, btnMoBoLoc, btnThemMoiTab;
    private JToggleButton btnTabAll, btnTabDienThoai, btnTabLaptop, btnTabPhuKien;
    private JPanel pnlImageContainer;
    private JTable tblSanPham;
    private DefaultTableModel model;
    private JTabbedPane tabMain;
    private boolean isEditMode = false;
    private boolean isFormatting = false;

    private String imageName = "";
    private LoaiSPDAO loaiSPDAO = new LoaiSPDAO();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();

    // Font chữ hiện đại
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font smallFont = new Font("Segoe UI", Font.PLAIN, 12);

    public JFSanPham() {
        setTitle("Hệ Thống Quản Lý Sản Phẩm - TechStore");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
        setEditMode(false);
        setupAutoStatistics();
    }

    public String generateNextMaSP() {
        try {
            java.util.ArrayList<SanPham> list = sanPhamDAO.getAll();
            int maxNum = 0;
            for (SanPham sp : list) {
                String ma = sp.getMaSP();
                if (ma != null && ma.toUpperCase().startsWith("SP")) {
                    try {
                        int num = Integer.parseInt(ma.substring(2).trim());
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua
                    }
                }
            }
            int nextNum = maxNum + 1;
            return String.format("SP%03d", nextNum);
        } catch (Exception e) {
            return "SP001";
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(new Color(248, 250, 252));
        pnlMain.setBorder(new EmptyBorder(15, 25, 25, 25));

        // Khởi tạo JTabbedPane
        tabMain = new JTabbedPane();
        tabMain.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabMain.setBackground(Color.WHITE);
        tabMain.setForeground(new Color(15, 23, 42));
        tabMain.setFocusable(false);
        tabMain.putClientProperty("JTabbedPane.tabHeight", 40);
        tabMain.putClientProperty("JTabbedPane.tabInsets", new Insets(0, 20, 0, 20));

        // ==========================================
        // TAB 1: BẢNG DỮ LIỆU & TÌM KIẾM
        // ==========================================
        JPanel pnlTableContainer = new JPanel(new BorderLayout(0, 15));
        pnlTableContainer.setBackground(Color.WHITE);
        pnlTableContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlTopBar = new JPanel(new BorderLayout());
        pnlTopBar.setBackground(Color.WHITE);

        JPanel pnlListTitle = new JPanel(new GridLayout(2, 1));
        pnlListTitle.setBackground(Color.WHITE);
        JLabel lblListTitle = new JLabel("Danh Sách Sản Phẩm Kho");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblListTitle.setForeground(new Color(15, 23, 42));
        lblTongCong = new JLabel("Đang tải dữ liệu...");
        lblTongCong.setFont(smallFont);
        lblTongCong.setForeground(new Color(100, 116, 139));
        pnlListTitle.add(lblListTitle);
        pnlListTitle.add(lblTongCong);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setBackground(Color.WHITE);
        
        btnThemMoiTab = new JButton("+ Thêm Sản Phẩm");
        styleButton(btnThemMoiTab, new Color(16, 185, 129), Color.WHITE, "roundRect");
        btnThemMoiTab.setPreferredSize(new Dimension(160, 38));
        
        btnMoBoLoc = new JButton("Bộ Lọc");
        styleButton(btnMoBoLoc, new Color(255, 237, 213), new Color(194, 65, 12), "roundRect");
        btnMoBoLoc.setPreferredSize(new Dimension(100, 38));
        
        txtTimKiem = createTextField("Tìm tên, mã...");
        txtTimKiem.setPreferredSize(new Dimension(250, 38));
        
        btnTimKiem = new JButton("Tìm Kiếm");
        styleButton(btnTimKiem, new Color(56, 189, 248), Color.WHITE, "roundRect");
        btnTimKiem.setPreferredSize(new Dimension(120, 38));

        pnlSearch.add(btnThemMoiTab);
        pnlSearch.add(btnMoBoLoc);
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        pnlTopBar.add(pnlListTitle, BorderLayout.WEST);
        pnlTopBar.add(pnlSearch, BorderLayout.EAST);

        JPanel pnlRightHeader = new JPanel(new BorderLayout(0, 15));
        pnlRightHeader.setBackground(Color.WHITE);
        pnlRightHeader.add(pnlTopBar, BorderLayout.NORTH);

        JPanel pnlCategoryTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlCategoryTabs.setBackground(Color.WHITE);
        pnlCategoryTabs.setBorder(new EmptyBorder(5, 0, 5, 0));

        btnTabAll = new JToggleButton("Tất cả");
        btnTabDienThoai = new JToggleButton("Điện thoại");
        btnTabLaptop = new JToggleButton("Laptop / Máy tính");
        btnTabPhuKien = new JToggleButton("Phụ kiện");

        styleTabButton(btnTabAll, new Color(15, 23, 42));
        styleTabButton(btnTabDienThoai, new Color(13, 148, 136));
        styleTabButton(btnTabLaptop, new Color(2, 132, 199));
        styleTabButton(btnTabPhuKien, new Color(147, 51, 234));

        ButtonGroup tabGroup = new ButtonGroup();
        tabGroup.add(btnTabAll);
        tabGroup.add(btnTabDienThoai);
        tabGroup.add(btnTabLaptop);
        tabGroup.add(btnTabPhuKien);
        btnTabAll.setSelected(true);

        pnlCategoryTabs.add(btnTabAll);
        pnlCategoryTabs.add(btnTabDienThoai);
        pnlCategoryTabs.add(btnTabLaptop);
        pnlCategoryTabs.add(btnTabPhuKien);

        pnlRightHeader.add(pnlCategoryTabs, BorderLayout.SOUTH);
        pnlTableContainer.add(pnlRightHeader, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[] { "Mã SP", "Hình Ảnh", "Tên Sản Phẩm", "Đơn Giá", "Tồn Kho", "Thương Hiệu", "Loại SP" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return ImageIcon.class; // Cột số 1 hiển thị Hình Ảnh
                }
                return Object.class;
            }
        };
        
        tblSanPham = new JTable(model) {
            private int hoverRow = -1;

            {
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoverRow) {
                            hoverRow = row;
                            repaint();
                        }
                    }
                });
                
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hoverRow = -1;
                        repaint();
                    }
                });
            }

            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row == hoverRow ? new Color(241, 245, 249) : Color.WHITE);
                } else {
                    comp.setBackground(getSelectionBackground());
                }
                return comp;
            }
        };
        
        tblSanPham.setFont(mainFont);
        tblSanPham.setRowHeight(60); // Đã nâng độ cao hàng để hiển thị ảnh
        tblSanPham.setShowVerticalLines(false);
        tblSanPham.setGridColor(new Color(241, 245, 249));
        tblSanPham.setSelectionBackground(new Color(238, 242, 255));
        tblSanPham.setSelectionForeground(new Color(15, 23, 42));
        tblSanPham.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTableHeader header = tblSanPham.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(100, 116, 139));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Điều chỉnh lại cột hiển thị sau khi chèn cột ảnh
        tblSanPham.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblSanPham.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollTable = new JScrollPane(tblSanPham);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlTableContainer.add(scrollTable, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                new EmptyBorder(15, 0, 0, 0)));

        lblThongKe = new JLabel("Đang tính toán...");
        lblThongKe.setFont(mainFont);
        pnlFooter.add(lblThongKe, BorderLayout.EAST);
        pnlTableContainer.add(pnlFooter, BorderLayout.SOUTH);


        // ==========================================
        // TAB 2: FORM NHẬP LIỆU
        // ==========================================
        JPanel pnlFormWrapper = new JPanel(new BorderLayout()); 
        pnlFormWrapper.setBackground(new Color(248, 250, 252));
        pnlFormWrapper.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        
        JPanel pnlFormContainer = new JPanel(new BorderLayout());
        pnlFormContainer.setBackground(Color.WHITE);
        pnlFormContainer.putClientProperty("FlatLaf.style", "arc: 20");
        pnlFormContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel pnlFormHeader = new JPanel(new BorderLayout());
        pnlFormHeader.setBackground(Color.WHITE);
        pnlFormHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                new EmptyBorder(0, 0, 15, 0)));

        lblTrangThaiForm = new JLabel("Thêm Sản Phẩm Mới");
        lblTrangThaiForm.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTrangThaiForm.setForeground(new Color(79, 70, 229));

        lblBadgeForm = new JLabel(" Mã tự động ");
        lblBadgeForm.setOpaque(true);
        lblBadgeForm.setBackground(new Color(238, 242, 255));
        lblBadgeForm.setForeground(new Color(67, 56, 202));
        lblBadgeForm.setFont(new Font("Segoe UI", Font.BOLD, 12));

        pnlFormHeader.add(lblTrangThaiForm, BorderLayout.WEST);
        pnlFormHeader.add(lblBadgeForm, BorderLayout.EAST);
        pnlFormContainer.add(pnlFormHeader, BorderLayout.NORTH);

        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0); 
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        String nextMaSP = generateNextMaSP();
        txtMaSP = createTextField(nextMaSP);
        txtMaSP.setText(nextMaSP);
        txtTenSP = createTextField("VD: iPhone 15 Pro Max 256GB");

        JPanel pnlPriceQty = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlPriceQty.setBackground(Color.WHITE);
        txtDonGia = createTextField("15.590.000");

        txtDonGia.getDocument().addDocumentListener(new DocumentListener() {
            private void performFormatting() {
                if (isFormatting) return;
                isFormatting = true;
                SwingUtilities.invokeLater(() -> {
                    try {
                        String text = txtDonGia.getText();
                        int caretPosition = txtDonGia.getCaretPosition();
                        String digits = text.replaceAll("[^0-9]", "");
                        if (!digits.isEmpty()) {
                            double value = Double.parseDouble(digits);
                            DecimalFormat df = new DecimalFormat("#,###");
                            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                            symbols.setGroupingSeparator('.');
                            df.setDecimalFormatSymbols(symbols);
                            String formattedText = df.format(value);
                            int dotsBefore = 0;
                            for (int i = 0; i < Math.min(caretPosition, text.length()); i++) {
                                if (text.charAt(i) == '.') dotsBefore++;
                            }
                            int digitsBefore = Math.min(caretPosition, text.length()) - dotsBefore;
                            txtDonGia.setText(formattedText);
                            int newCaret = 0;
                            int digitCount = 0;
                            while (newCaret < formattedText.length() && digitCount < digitsBefore) {
                                if (formattedText.charAt(newCaret) != '.') {
                                    digitCount++;
                                }
                                newCaret++;
                            }
                            if (newCaret < formattedText.length() && formattedText.charAt(newCaret) == '.') {
                                newCaret++;
                            }
                            txtDonGia.setCaretPosition(Math.min(newCaret, formattedText.length()));
                        } else {
                            txtDonGia.setText("");
                        }
                    } catch (Exception ex) {
                    } finally {
                        isFormatting = false;
                    }
                });
            }
            @Override public void insertUpdate(DocumentEvent e) { performFormatting(); }
            @Override public void removeUpdate(DocumentEvent e) { performFormatting(); }
            @Override public void changedUpdate(DocumentEvent e) { performFormatting(); }
        });

        txtSoLuong = createTextField("10");
        pnlPriceQty.add(wrapWithLabel("Đơn Giá (đ) *", txtDonGia));
        pnlPriceQty.add(wrapWithLabel("Số Lượng *", txtSoLuong));

        JPanel pnlBrandCat = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlBrandCat.setBackground(Color.WHITE);
        txtHangSX = createTextField("Apple, Samsung...");
        
        cboLoaiSP = new JComboBox<>();
        cboLoaiSP.setFont(mainFont);
        cboLoaiSP.setPreferredSize(new Dimension(200, 38));
        cboLoaiSP.setBackground(Color.WHITE);
        
        pnlBrandCat.add(wrapWithLabel("Hãng Sản Xuất *", txtHangSX));
        pnlBrandCat.add(wrapWithLabel("Phân Loại SP *", cboLoaiSP));

        int gridy = 0;
        gbc.gridy = gridy++; pnlLeft.add(wrapWithLabel("Mã Sản Phẩm *", txtMaSP), gbc);
        gbc.gridy = gridy++; pnlLeft.add(wrapWithLabel("Tên Sản Phẩm *", txtTenSP), gbc);
        gbc.gridy = gridy++; pnlLeft.add(pnlPriceQty, gbc);
        gbc.gridy = gridy++; pnlLeft.add(pnlBrandCat, gbc);
        
        gbc.gridy = gridy++;
        gbc.weighty = 1.0;
        pnlLeft.add(Box.createVerticalGlue(), gbc);

        JPanel pnlActions = new JPanel(new GridBagLayout());
        pnlActions.setBackground(Color.WHITE);
        pnlActions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                new EmptyBorder(15, 0, 0, 0)));

        GridBagConstraints gbcAct = new GridBagConstraints();
        gbcAct.fill = GridBagConstraints.BOTH;
        gbcAct.weighty = 1.0;
        
        btnXoa = new JButton("Xóa SP");
        styleButton(btnXoa, new Color(254, 242, 242), new Color(225, 29, 72), "roundRect");
        btnXoa.setVisible(false);
        btnXoa.setPreferredSize(new Dimension(0, 42));

        btnLamMoi = new JButton("Làm Mới Form");
        styleButton(btnLamMoi, new Color(241, 245, 249), new Color(71, 85, 105), "roundRect");
        btnLamMoi.setPreferredSize(new Dimension(0, 42));

        btnLuu = new JButton("Thêm Mới");
        styleButton(btnLuu, new Color(79, 70, 229), Color.WHITE, "roundRect");
        btnLuu.setPreferredSize(new Dimension(0, 48)); 

        gbcAct.gridy = 0;
        gbcAct.gridx = 0;
        gbcAct.weightx = 0.5;
        gbcAct.insets = new Insets(0, 0, 10, 10);
        pnlActions.add(btnXoa, gbcAct);

        gbcAct.gridx = 1;
        gbcAct.insets = new Insets(0, 0, 10, 0);
        pnlActions.add(btnLamMoi, gbcAct);

        gbcAct.gridy = 1;
        gbcAct.gridx = 0;
        gbcAct.gridwidth = 2;
        gbcAct.weightx = 1.0;
        gbcAct.insets = new Insets(0, 0, 0, 0);
        pnlActions.add(btnLuu, gbcAct);

        gbc.gridy = gridy++;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlLeft.add(pnlActions, gbc);


        JPanel pnlMiddle = new JPanel(new BorderLayout(0, 20));
        pnlMiddle.setBackground(Color.WHITE);

        txtMoTa = new JTextArea();
        txtMoTa.setFont(mainFont);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.putClientProperty("JTextField.placeholderText", "Nhập chi tiết về thông số kỹ thuật, bảo hành, tính năng nổi bật...");
        
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        
        pnlMiddle.add(wrapWithLabel("Mô Tả Chi Tiết", scrollMoTa), BorderLayout.CENTER);


        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBackground(Color.WHITE);

        pnlImageContainer = new JPanel(new BorderLayout());
        pnlImageContainer.setBackground(new Color(248, 250, 252));
        pnlImageContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(new Color(203, 213, 225), 2.0f, 5.0f, 2.0f, true),
                new EmptyBorder(10, 10, 10, 10)));
        pnlImageContainer.setPreferredSize(new Dimension(300, 300)); 

        lblHinh = new JLabel("<html><div style='text-align: center;'><b style='color: #475569;'>Nhấn chọn hoặc kéo thả ảnh</b></div></html>", SwingConstants.CENTER);
        pnlImageContainer.add(lblHinh, BorderLayout.CENTER);

        JPanel pnlImageBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlImageBtn.setBackground(Color.WHITE);
        btnChonAnh = new JButton("Chọn Ảnh Sản Phẩm");
        styleButton(btnChonAnh, new Color(241, 245, 249), new Color(71, 85, 105), "roundRect");
        btnChonAnh.setPreferredSize(new Dimension(200, 42)); 
        pnlImageBtn.add(btnChonAnh);

        JPanel pnlImageWrapper = new JPanel(new BorderLayout(0, 12));
        pnlImageWrapper.setBackground(Color.WHITE);
        pnlImageWrapper.add(wrapWithLabel("Hình Ảnh Trực Quan", pnlImageContainer), BorderLayout.CENTER);
        pnlImageWrapper.add(pnlImageBtn, BorderLayout.SOUTH);

        JPanel pnlRightTop = new JPanel(new BorderLayout());
        pnlRightTop.setBackground(Color.WHITE);
        pnlRightTop.add(pnlImageWrapper, BorderLayout.NORTH);

        pnlRight.add(pnlRightTop, BorderLayout.CENTER);


        JPanel pnlFormBody = new JPanel(new GridLayout(1, 3, 35, 0));
        pnlFormBody.setBackground(Color.WHITE);
        pnlFormBody.setBorder(new EmptyBorder(10, 0, 0, 0));
        pnlFormBody.add(pnlLeft);
        pnlFormBody.add(pnlMiddle); 
        pnlFormBody.add(pnlRight);  
        
        pnlFormContainer.add(pnlFormBody, BorderLayout.CENTER);
        pnlFormWrapper.add(pnlFormContainer, BorderLayout.CENTER); 

        tabMain.addTab("  Danh Sách Sản Phẩm  ", pnlTableContainer);
        tabMain.addTab("  Chỉnh Sửa / Thêm Mới  ", pnlFormWrapper);

        pnlMain.add(tabMain, BorderLayout.CENTER);
        add(pnlMain, BorderLayout.CENTER);
        
        btnThemMoiTab.addActionListener(e -> {
            clearForm();
            tabMain.setSelectedIndex(1);
        });
    }

    private JPanel wrapWithLabel(String labelText, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        p.add(lbl, BorderLayout.NORTH);
        p.add(component, BorderLayout.CENTER);
        return p;
    }

    private JTextField createTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(mainFont);
        txt.setPreferredSize(new Dimension(200, 38));
        txt.putClientProperty("JComponent.roundRect", true);
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        return txt;
    }

    // --- ĐÃ FIX: ÉP NÚT BẤM HIỂN THỊ MÀU BẤT CHẤP GIAO DIỆN CỦA MENU CHÍNH ---
    private void styleButton(JButton btn, Color bg, Color fg, String type) {
        btn.setFont(boldFont);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", type);
        
        // Cấu hình bắt buộc để hiện màu nền trên giao diện Windows/System
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        try {
            if (UIManager.getLookAndFeel() != null && !UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
                btn.setBorderPainted(false); // Xóa viền mặc định che mất màu
            }
        } catch (Exception e) {}
    }

    private void styleTabButton(JToggleButton btn, Color selectedColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setPreferredSize(new Dimension(155, 36));

        // Cấu hình bắt buộc để hiện màu nền trên giao diện Windows/System
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        try {
            if (UIManager.getLookAndFeel() != null && !UIManager.getLookAndFeel().getName().contains("FlatLaf")) {
                btn.setBorderPainted(false); 
            }
        } catch (Exception e) {}

        btn.setBackground(new Color(241, 245, 249));
        btn.setForeground(new Color(71, 85, 105));
        btn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(selectedColor);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(selectedColor, 1));
            } else {
                btn.setBackground(new Color(241, 245, 249));
                btn.setForeground(new Color(71, 85, 105));
                btn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
            }
        });
    }

    public void setEditMode(boolean edit) {
        this.isEditMode = edit;
        txtMaSP.setEditable(!edit);
        btnXoa.setVisible(edit);
        if (edit) {
            lblTrangThaiForm.setText("Cập Nhật Sản Phẩm");
            lblTrangThaiForm.setForeground(new Color(217, 119, 6));

            lblBadgeForm.setText(" Đang chỉnh sửa ");
            lblBadgeForm.setBackground(new Color(255, 251, 235));
            lblBadgeForm.setForeground(new Color(180, 83, 9));

            btnLuu.setText("Cập Nhật");
            btnLuu.setBackground(new Color(245, 158, 11));
        } else {
            lblTrangThaiForm.setText("Thêm Sản Phẩm Mới");
            lblTrangThaiForm.setForeground(new Color(79, 70, 229));

            lblBadgeForm.setText(" Mã tự động ");
            lblBadgeForm.setBackground(new Color(238, 242, 255));
            lblBadgeForm.setForeground(new Color(67, 56, 202));

            btnLuu.setText("Thêm Mới");
            btnLuu.setBackground(new Color(79, 70, 229));
        }
    }

    private void setupAutoStatistics() {
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int totalItems = model.getRowCount();
                int lowStock = 0;
                double totalValue = 0;

                for (int i = 0; i < totalItems; i++) {
                    try {
                        int qty = Integer.parseInt(model.getValueAt(i, 4).toString());
                        if (qty < 10)
                            lowStock++;

                        String priceStr = model.getValueAt(i, 3).toString().replaceAll("[,.]", "");
                        double price = Double.parseDouble(priceStr);

                        totalValue += (price * qty);
                    } catch (Exception ex) {
                    }
                }

                lblTongCong.setText("Tổng cộng: " + totalItems + " sản phẩm hiện có");
                lblThongKe.setText(String.format(
                        "<html><span style='color:#64748b;'>Sản phẩm tồn kho thấp (&lt;10):</span> <b style='color:#e11d48; font-size:14px;'>%d</b> &nbsp;&nbsp;&nbsp;&nbsp; <span style='color:#64748b;'>Tổng giá trị kho hàng:</span> <b style='color:#0f172a; font-size:14px;'>%,.0f đ</b></html>",
                        lowStock, totalValue));
            }
        });
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void loadLoaiSP() {
        try {
            cboLoaiSP.removeAllItems();
            for (LoaiSP loai : loaiSPDAO.getAll()) {
                cboLoaiSP.addItem(loai);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách Loại SP: " + e.getMessage());
        }
    }

    public void clearForm() {
        txtMaSP.setText(generateNextMaSP());
        txtTenSP.setText("");
        txtDonGia.setText("");
        txtSoLuong.setText("");
        txtHangSX.setText("");
        txtMoTa.setText("");
        txtTimKiem.setText("");
        if (cboLoaiSP.getItemCount() > 0)
            cboLoaiSP.setSelectedIndex(0);
        showImage("");
        tblSanPham.clearSelection();
        setEditMode(false);
        txtTenSP.requestFocus();
    }

    public void showImage(String fileName) {
        imageName = fileName;
        if (fileName == null || fileName.trim().isEmpty()) {
            lblHinh.setIcon(null);
            lblHinh.setText(
                    "<html><div style='text-align: center;'><b style='color: #475569;'>Nhấn chọn hoặc kéo thả ảnh</b></div></html>");
            return;
        }
        try {
            File imgFile = new File("src/image/" + fileName);
            if (!imgFile.exists()) {
                throw new Exception();
            }
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
            lblHinh.setIcon(new ImageIcon(img));
            lblHinh.setText("");
        } catch (Exception e) {
            lblHinh.setIcon(null);
            lblHinh.setText(
                    "<html><div style='text-align: center;'><b style='color: #ef4444;'>LỖI TẢI ẢNH</b></div></html>");
        }
    }

    public SanPham getFormData() {
        if (txtMaSP.getText().trim().isEmpty() || txtTenSP.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã SP và Tên SP không được để trống!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            SanPham sp = new SanPham();
            sp.setMaSP(txtMaSP.getText().trim());
            sp.setTenSP(txtTenSP.getText().trim());

            String rawDonGia = txtDonGia.getText().trim().replaceAll("[^0-9]", "");
            sp.setDonGia(Double.parseDouble(rawDonGia));

            sp.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
            sp.setHangSX(txtHangSX.getText().trim());
            sp.setMoTa(txtMoTa.getText().trim());
            sp.setHinh(imageName);

            LoaiSP loai = (LoaiSP) cboLoaiSP.getSelectedItem();
            if (loai != null)
                sp.setMaLoai(loai.getMaLoai());

            if (sp.getDonGia() < 0 || sp.getSoLuong() < 0) {
                JOptionPane.showMessageDialog(this, "Giá và Số lượng phải lớn hơn hoặc bằng 0!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return sp;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá và Số lượng phải là số hợp lệ!", "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Getters
    public JTabbedPane getTabMain() { return tabMain; }
    
    public JTextField getTxtMaSP() { return txtMaSP; }

    public JTextField getTxtTenSP() { return txtTenSP; }

    public JTextField getTxtDonGia() { return txtDonGia; }

    public JTextField getTxtSoLuong() { return txtSoLuong; }

    public JTextField getTxtHangSX() { return txtHangSX; }

    public JTextField getTxtTimKiem() { return txtTimKiem; }

    public JTextArea getTxtMoTa() { return txtMoTa; }

    public JComboBox<LoaiSP> getCboLoaiSP() { return cboLoaiSP; }

    public JLabel getLblHinh() { return lblHinh; }

    public JButton getBtnLuu() { return btnLuu; }

    public JButton getBtnXoa() { return btnXoa; }

    public JButton getBtnLamMoi() { return btnLamMoi; }

    public JButton getBtnChonAnh() { return btnChonAnh; }

    public JButton getBtnTimKiem() { return btnTimKiem; }

    public JButton getBtnMoBoLoc() { return btnMoBoLoc; }

    public JToggleButton getBtnTabAll() { return btnTabAll; }

    public JToggleButton getBtnTabDienThoai() { return btnTabDienThoai; }

    public JToggleButton getBtnTabLaptop() { return btnTabLaptop; }

    public JToggleButton getBtnTabPhuKien() { return btnTabPhuKien; }

    public JTable getTblSanPham() { return tblSanPham; }

    public DefaultTableModel getModel() { return model; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
        }

        SwingUtilities.invokeLater(() -> {
            JFSanPham view = new JFSanPham();
            new SanPhamController(view);
            view.setVisible(true);
        });
    }
}