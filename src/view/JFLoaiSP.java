package view;

import controller.LoaiSPController;
import model.LoaiSP;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class JFLoaiSP extends JFrame {

    private JTextField txtMaLoai, txtTenLoai, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tblLoaiSP;
    private DefaultTableModel model;

    // Cài đặt Font chuẩn
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

    public JFLoaiSP() {
        setTitle("QUẢN LÝ LOẠI SẢN PHẨM");
        setSize(800, 550); // Form này ít trường nên để size nhỏ hơn cho cân đối
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255)); // Màu nền xanh nhạt (Alice Blue)

        initComponents();
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlMain.setOpaque(false);

        // --- PANEL FORM (Nhập liệu) ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2), 
                "Thông Tin Loại Sản Phẩm", TitledBorder.LEFT, TitledBorder.TOP, boldFont, new Color(25, 25, 112)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Tăng khoảng cách vì form ít trường
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaLoai = createTextField();
        txtTenLoai = createTextField();
        txtTimKiem = createTextField();

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; pnlForm.add(new JLabel("Mã Loại:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtMaLoai, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; pnlForm.add(new JLabel("Tên Loại:"), gbc);
        gbc.gridx = 1; pnlForm.add(txtTenLoai, gbc);

        // Chỉnh Font cho tất cả Label
        for (Component comp : pnlForm.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(boldFont);
            }
        }

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlForm, BorderLayout.CENTER);

        // --- PANEL BẢNG (Table) ---
        model = new DefaultTableModel(new String[]{"Mã Loại Sản Phẩm", "Tên Loại Sản Phẩm"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblLoaiSP = new JTable(model);
        tblLoaiSP.setFont(mainFont);
        tblLoaiSP.setRowHeight(30); 
        tblLoaiSP.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLoaiSP.setSelectionBackground(new Color(173, 216, 230)); 
        
        JTableHeader header = tblLoaiSP.getTableHeader();
        header.setFont(boldFont);
        header.setBackground(new Color(100, 149, 237));
        header.setForeground(Color.WHITE);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollTable = new JScrollPane(tblLoaiSP);
        scrollTable.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2), 
                "Danh Sách Phân Loại", TitledBorder.LEFT, TitledBorder.TOP, boldFont, new Color(25, 25, 112)));
        scrollTable.getViewport().setBackground(Color.WHITE);

        // --- PANEL CHỨC NĂNG ---
        btnThem = new JButton("Thêm Mới");
        btnSua = new JButton("Cập Nhật");
        btnXoa = new JButton("Xóa Loại");
        btnLamMoi = new JButton("Làm Mới");
        btnTimKiem = new JButton("Tìm Kiếm");

        styleButton(btnThem, new Color(46, 139, 87)); 
        styleButton(btnSua, new Color(218, 165, 32)); 
        styleButton(btnXoa, new Color(220, 20, 60));  
        styleButton(btnLamMoi, new Color(112, 128, 144)); 
        styleButton(btnTimKiem, new Color(70, 130, 180));

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButton.setOpaque(false);
        pnlButton.add(btnThem);
        pnlButton.add(btnSua);
        pnlButton.add(btnXoa);
        pnlButton.add(btnLamMoi);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlSearch.setOpaque(false);
        JLabel lblTim = new JLabel("Nhập mã hoặc tên loại:");
        lblTim.setFont(boldFont);
        pnlSearch.add(lblTim);
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setOpaque(false);
        pnlSouth.add(pnlButton, BorderLayout.NORTH);
        pnlSouth.add(pnlSearch, BorderLayout.SOUTH);

        pnlMain.add(pnlTop, BorderLayout.NORTH);
        pnlMain.add(scrollTable, BorderLayout.CENTER);
        pnlMain.add(pnlSouth, BorderLayout.SOUTH);

        add(pnlMain);
    }
    
    // --- HÀM HỖ TRỢ GIAO DIỆN ---
    private JTextField createTextField() {
        JTextField txt = new JTextField(20);
        txt.setFont(mainFont);
        txt.setPreferredSize(new Dimension(300, 35));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return txt;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(boldFont);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setMargin(new Insets(6, 12, 6, 12));
        btn.putClientProperty("JButton.buttonType", "round");
    }

    // --- CÁC HÀM XỬ LÝ DỮ LIỆU FORM ---
    public void clearForm() {
        txtMaLoai.setEditable(true); 
        txtMaLoai.setText("");
        txtTenLoai.setText("");
        txtTimKiem.setText("");
        
        tblLoaiSP.clearSelection();
        txtMaLoai.requestFocus();
    }

    public LoaiSP getFormData() {
        if(txtMaLoai.getText().trim().isEmpty() || txtTenLoai.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Loại và Tên Loại không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        LoaiSP loai = new LoaiSP();
        loai.setMaLoai(txtMaLoai.getText().trim());
        loai.setTenLoai(txtTenLoai.getText().trim());
        
        return loai;
    }

    // --- GETTERS ĐỂ CONTROLLER SỬ DỤNG ---
    public JTextField getTxtMaLoai() { return txtMaLoai; }
    public JTextField getTxtTenLoai() { return txtTenLoai; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JTable getTblLoaiSP() { return tblLoaiSP; }
    public DefaultTableModel getModel() { return model; }

    // --- MAIN ---
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFLoaiSP view = new JFLoaiSP();
            new LoaiSPController(view);
            view.setVisible(true);
        });
    }
}