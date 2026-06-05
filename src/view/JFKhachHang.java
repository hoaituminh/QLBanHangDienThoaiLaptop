package view;

import controller.KhachHangController;
import model.KhachHang;
import util.TechStoreUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Quản lý khách hàng – layout 1/3 form + 2/3 bảng (Đã fix lỗi giãn ô text). */
public class JFKhachHang extends JFrame {

    public static final int COL_ACTION = 5;

    private JTextField txtMaKH, txtHoTen, txtSDT, txtDiaChi, txtEmail, txtTimKiem;
    private JButton btnLuu, btnLamMoi;
    private JLabel lblFormMode, lblListCount;
    private JTable tblKhachHang;
    private DefaultTableModel model;
    private boolean editMode;

    public JFKhachHang() {
        setTitle("Quản Lý Khách Hàng - TechStore");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(TechStoreUI.BG_MAIN);
        initComponents();
        setEditMode(false);
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel(new BorderLayout(24, 0));
        pnlMain.setBackground(TechStoreUI.BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(24, 24, 24, 24));

        // --- Form trái ---
        JPanel pnlFormCard = TechStoreUI.createCard();
        pnlFormCard.setLayout(new BorderLayout(0, 0));
        // Đặt độ rộng Form bên trái vừa phải hơn
        pnlFormCard.setPreferredSize(new Dimension(360, 0));

        JPanel pnlFormHead = new JPanel(new BorderLayout());
        pnlFormHead.setOpaque(false);
        pnlFormHead.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, TechStoreUI.BORDER),
            new EmptyBorder(0, 0, 12, 0)));
        JLabel lblHead = new JLabel("Thông Tin Khách Hàng");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormMode = TechStoreUI.createFormBadge("Đang thêm mới");
        pnlFormHead.add(lblHead, BorderLayout.WEST);
        pnlFormHead.add(lblFormMode, BorderLayout.EAST);

        JPanel pnlFields = new JPanel();
        pnlFields.setLayout(new BoxLayout(pnlFields, BoxLayout.Y_AXIS));
        pnlFields.setOpaque(false);
        txtMaKH = TechStoreUI.createField("Ví dụ: KH04");
        txtHoTen = TechStoreUI.createField("Nhập tên khách hàng...");
        txtSDT = TechStoreUI.createField("Nhập SĐT khách hàng...");
        txtDiaChi = TechStoreUI.createField("Buôn Ma Thuột, Đắk Lắk...");
        txtEmail = TechStoreUI.createField("example@gmail.com");
        
        // Nới rộng khoảng cách giữa các ô lên 16 để nhìn thoáng hơn
        pnlFields.add(TechStoreUI.wrapField("Mã Khách Hàng *", txtMaKH));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Họ Tên *", txtHoTen));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Số Điện Thoại *", txtSDT));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Địa Chỉ", txtDiaChi));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Email", txtEmail));

        // FIX: Tạo Panel bọc ngoài ép các ô text lên sát trên cùng (BorderLayout.NORTH)
        // Tránh bị BoxLayout kéo giãn ô text lấp đầy khoảng trống dọc.
        JPanel pnlFieldsWrapper = new JPanel(new BorderLayout());
        pnlFieldsWrapper.setOpaque(false);
        pnlFieldsWrapper.add(pnlFields, BorderLayout.NORTH);

        JPanel pnlFormCenter = new JPanel(new BorderLayout(0, 20));
        pnlFormCenter.setOpaque(false);
        pnlFormCenter.add(pnlFormHead, BorderLayout.NORTH);
        pnlFormCenter.add(pnlFieldsWrapper, BorderLayout.CENTER);
        pnlFormCard.add(pnlFormCenter, BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlActions.setOpaque(false);
        pnlActions.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, TechStoreUI.BORDER),
            new EmptyBorder(16, 0, 0, 0)));
        btnLamMoi = new JButton();
        btnLuu = new JButton();
        TechStoreUI.styleSecondaryButton(btnLamMoi, "Làm Mới");
        TechStoreUI.stylePrimaryButton(btnLuu, "Lưu Khách Hàng");
        pnlActions.add(btnLamMoi);
        pnlActions.add(btnLuu);
        pnlFormCard.add(pnlActions, BorderLayout.SOUTH);

        // --- Bảng phải ---
        JPanel pnlTableCard = TechStoreUI.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 12));

        JPanel pnlTop = new JPanel(new BorderLayout(8, 8));
        pnlTop.setOpaque(false);
        JPanel pnlTitles = new JPanel(new GridLayout(2, 1));
        pnlTitles.setOpaque(false);
        JLabel lblList = new JLabel("Danh Sách Khách Hàng (Bảng KHACHHANG)");
        lblList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblListCount = TechStoreUI.createMutedLabel("Tổng cộng: 0 khách hàng");
        pnlTitles.add(lblList);
        pnlTitles.add(lblListCount);
        txtTimKiem = TechStoreUI.createField("Tìm tên, mã, số điện thoại...");
        JPanel pnlSearch = TechStoreUI.createSearchField(txtTimKiem);
        pnlSearch.setPreferredSize(new Dimension(240, 34));
        pnlTop.add(pnlTitles, BorderLayout.WEST);
        pnlTop.add(pnlSearch, BorderLayout.EAST);

        model = new DefaultTableModel(
            new String[]{"Mã KH", "Họ Tên", "Số Điện Thoại", "Địa Chỉ", "Email", "Thao Tác"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKhachHang = new JTable(model);
        TechStoreUI.styleTable(tblKhachHang);
        TechStoreUI.configureActionColumn(tblKhachHang, COL_ACTION);

        JScrollPane scroll = new JScrollPane(tblKhachHang);
        scroll.setBorder(BorderFactory.createLineBorder(TechStoreUI.BORDER));

        pnlTableCard.add(pnlTop, BorderLayout.NORTH);
        pnlTableCard.add(scroll, BorderLayout.CENTER);

        pnlMain.add(pnlFormCard, BorderLayout.WEST);
        pnlMain.add(pnlTableCard, BorderLayout.CENTER);
        add(pnlMain);
    }

    public void setEditMode(boolean edit) {
        this.editMode = edit;
        txtMaKH.setEditable(!edit);
        txtMaKH.setBackground(edit ? util.TechStoreUI.BORDER : util.TechStoreUI.BG_MAIN);
        lblFormMode.setText(edit ? " Đang chỉnh sửa " : " Đang thêm mới ");
        if (edit) {
            TechStoreUI.styleUpdateButton(btnLuu, "Cập Nhật Khách Hàng");
        } else {
            TechStoreUI.stylePrimaryButton(btnLuu, "Lưu Khách Hàng");
        }
    }

    public boolean isEditMode() { return editMode; }

    public void setListCount(int n) {
        lblListCount.setText("Tổng cộng: " + n + " khách hàng");
    }

    public void clearForm() {
        setEditMode(false);
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtEmail.setText("");
        tblKhachHang.clearSelection();
        txtMaKH.requestFocus();
    }

    public void loadToForm(KhachHang kh) {
        txtMaKH.setText(kh.getMaKH());
        txtHoTen.setText(kh.getHoTen());
        txtSDT.setText(kh.getSdt() != null ? kh.getSdt() : "");
        txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
        txtEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
        setEditMode(true);
    }

    public KhachHang getFormData() {
        if (txtMaKH.getText().trim().isEmpty() || txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Mã KH và Họ Tên không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (txtSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Số điện thoại không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        KhachHang kh = new KhachHang();
        kh.setMaKH(txtMaKH.getText().trim());
        kh.setHoTen(txtHoTen.getText().trim());
        kh.setSdt(txtSDT.getText().trim());
        kh.setDiaChi(txtDiaChi.getText().trim());
        kh.setEmail(txtEmail.getText().trim());
        return kh;
    }

    public JTextField getTxtMaKH() { return txtMaKH; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JButton getBtnLuu() { return btnLuu; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JTable getTblKhachHang() { return tblKhachHang; }
    public DefaultTableModel getModel() { return model; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            JFKhachHang view = new JFKhachHang();
            new KhachHangController(view);
            view.setVisible(true);
        });
    }
}