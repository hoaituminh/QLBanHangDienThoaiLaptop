package view;

import controller.NhanVienController;
import model.NhanVien;
import util.TechStoreUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Quản lý nhân viên – layout mockup TechStore (Đã fix lỗi giãn ô text). */
public class JFNhanVien extends JFrame {

    public static final int COL_ACTION = 5;

    private JTextField txtMaNV, txtHoTen, txtSDT, txtDiaChi, txtEmail, txtTimKiem;
    private JButton btnLuu, btnLamMoi;
    private JLabel lblFormMode, lblListCount;
    private JTable tblNhanVien;
    private DefaultTableModel model;
    private boolean editMode;

    public JFNhanVien() {
        setTitle("Quản Lý Nhân Viên - TechStore");
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

        JPanel pnlFormCard = TechStoreUI.createCard();
        pnlFormCard.setLayout(new BorderLayout(0, 0));
        // Đặt độ rộng Form bên trái vừa phải hơn
        pnlFormCard.setPreferredSize(new Dimension(360, 0));

        JPanel pnlFormHead = new JPanel(new BorderLayout());
        pnlFormHead.setOpaque(false);
        pnlFormHead.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, TechStoreUI.BORDER),
            new EmptyBorder(0, 0, 12, 0)));
        JLabel lblHead = new JLabel("Thông Tin Nhân Viên");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormMode = TechStoreUI.createFormBadge("Đang thêm mới");
        pnlFormHead.add(lblHead, BorderLayout.WEST);
        pnlFormHead.add(lblFormMode, BorderLayout.EAST);

        JPanel pnlFields = new JPanel();
        pnlFields.setLayout(new BoxLayout(pnlFields, BoxLayout.Y_AXIS));
        pnlFields.setOpaque(false);
        txtMaNV = TechStoreUI.createField("Ví dụ: NV04");
        txtHoTen = TechStoreUI.createField("Nhập tên nhân viên...");
        txtSDT = TechStoreUI.createField("Nhập SĐT nhân viên...");
        txtDiaChi = TechStoreUI.createField("Địa chỉ nhân viên...");
        txtEmail = TechStoreUI.createField("example@gmail.com");
        
        pnlFields.add(TechStoreUI.wrapField("Mã Nhân Viên *", txtMaNV));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Họ Tên *", txtHoTen));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Số Điện Thoại *", txtSDT));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Địa Chỉ", txtDiaChi));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Email", txtEmail));

        // FIX: Ép pnlFields lên trên cùng để không bị tự động kéo dãn chiều cao các ô
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
        TechStoreUI.stylePrimaryButton(btnLuu, "Lưu Nhân Viên");
        pnlActions.add(btnLamMoi);
        pnlActions.add(btnLuu);
        pnlFormCard.add(pnlActions, BorderLayout.SOUTH);

        JPanel pnlTableCard = TechStoreUI.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 12));

        JPanel pnlTop = new JPanel(new BorderLayout(8, 8));
        pnlTop.setOpaque(false);
        JPanel pnlTitles = new JPanel(new GridLayout(2, 1));
        pnlTitles.setOpaque(false);
        JLabel lblList = new JLabel("Danh Sách Nhân Viên (Bảng NHANVIEN)");
        lblList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblListCount = TechStoreUI.createMutedLabel("Tổng cộng: 0 nhân viên");
        pnlTitles.add(lblList);
        pnlTitles.add(lblListCount);
        txtTimKiem = TechStoreUI.createField("Tìm tên, mã, số điện thoại...");
        JPanel pnlSearch = TechStoreUI.createSearchField(txtTimKiem);
        pnlSearch.setPreferredSize(new Dimension(240, 34));
        pnlTop.add(pnlTitles, BorderLayout.WEST);
        pnlTop.add(pnlSearch, BorderLayout.EAST);

        model = new DefaultTableModel(
            new String[]{"Mã NV", "Họ Tên", "Số Điện Thoại", "Địa Chỉ", "Email", "Thao Tác"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhanVien = new JTable(model);
        TechStoreUI.styleTable(tblNhanVien);
        TechStoreUI.configureActionColumn(tblNhanVien, COL_ACTION);

        JScrollPane scroll = new JScrollPane(tblNhanVien);
        scroll.setBorder(BorderFactory.createLineBorder(TechStoreUI.BORDER));

        pnlTableCard.add(pnlTop, BorderLayout.NORTH);
        pnlTableCard.add(scroll, BorderLayout.CENTER);

        pnlMain.add(pnlFormCard, BorderLayout.WEST);
        pnlMain.add(pnlTableCard, BorderLayout.CENTER);
        add(pnlMain);
    }

    public void setEditMode(boolean edit) {
        this.editMode = edit;
        txtMaNV.setEditable(!edit);
        txtMaNV.setBackground(edit ? util.TechStoreUI.BORDER : util.TechStoreUI.BG_MAIN);
        lblFormMode.setText(edit ? " Đang chỉnh sửa " : " Đang thêm mới ");
        if (edit) {
            TechStoreUI.styleUpdateButton(btnLuu, "Cập Nhật Nhân Viên");
        } else {
            TechStoreUI.stylePrimaryButton(btnLuu, "Lưu Nhân Viên");
        }
    }

    public boolean isEditMode() { return editMode; }

    public void setListCount(int n) {
        lblListCount.setText("Tổng cộng: " + n + " nhân viên");
    }

    public void clearForm() {
        setEditMode(false);
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtEmail.setText("");
        tblNhanVien.clearSelection();
        txtMaNV.requestFocus();
    }

    public void loadToForm(NhanVien nv) {
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        txtSDT.setText(nv.getSdt() != null ? nv.getSdt() : "");
        txtDiaChi.setText(nv.getDiaChi() != null ? nv.getDiaChi() : "");
        txtEmail.setText(nv.getEmail() != null ? nv.getEmail() : "");
        setEditMode(true);
    }

    public NhanVien getFormData() {
        if (txtMaNV.getText().trim().isEmpty() || txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Mã NV và Họ Tên không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (txtSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Số điện thoại không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        NhanVien nv = new NhanVien();
        nv.setMaNV(txtMaNV.getText().trim());
        nv.setHoTen(txtHoTen.getText().trim());
        nv.setSdt(txtSDT.getText().trim());
        nv.setDiaChi(txtDiaChi.getText().trim());
        nv.setEmail(txtEmail.getText().trim());
        return nv;
    }

    public JTextField getTxtMaNV() { return txtMaNV; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JButton getBtnLuu() { return btnLuu; }
    public JButton getBtnLamMoi() { return btnLamMoi; }
    public JTable getTblNhanVien() { return tblNhanVien; }
    public DefaultTableModel getModel() { return model; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            JFNhanVien view = new JFNhanVien();
            new NhanVienController(view);
            view.setVisible(true);
        });
    }
}