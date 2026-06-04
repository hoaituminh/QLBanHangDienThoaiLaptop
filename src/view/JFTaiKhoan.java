package view;

import controller.TaiKhoanController;
import model.NhanVien;
import model.TaiKhoan;
import util.TechStoreUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Phân quyền & quản lý tài khoản (bảng TAIKHOAN - Đã fix giãn dòng). */
public class JFTaiKhoan extends JFrame {

    public static final int COL_ACTION = 5;

    private JTextField txtUsername, txtTimKiem;
    private JPasswordField txtPassword;
    private JComboBox<String> cboRole;
    private JComboBox<NhanVien> cboNhanVien;
    private JButton btnLuu, btnLamMoi;
    private JLabel lblFormMode, lblListCount;
    private JTable tblTaiKhoan;
    private DefaultTableModel model;
    private boolean editMode;

    public JFTaiKhoan() {
        setTitle("Quản Lý Tài Khoản - TechStore");
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
        // Điều chỉnh chiều rộng form gọn lại
        pnlFormCard.setPreferredSize(new Dimension(360, 0));

        JPanel pnlFormHead = new JPanel(new BorderLayout());
        pnlFormHead.setOpaque(false);
        pnlFormHead.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, TechStoreUI.BORDER),
                new EmptyBorder(0, 0, 12, 0)));
        JLabel lblHead = new JLabel("Thông Tin Tài Khoản");
        lblHead.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormMode = TechStoreUI.createFormBadge("Thêm mới");
        pnlFormHead.add(lblHead, BorderLayout.WEST);
        pnlFormHead.add(lblFormMode, BorderLayout.EAST);

        JPanel pnlFields = new JPanel();
        pnlFields.setLayout(new BoxLayout(pnlFields, BoxLayout.Y_AXIS));
        pnlFields.setOpaque(false);
        txtUsername = TechStoreUI.createField("Tên đăng nhập viết liền không dấu");
        txtPassword = new JPasswordField();
        txtPassword.setFont(TechStoreUI.FONT_MAIN);
        txtPassword.setBackground(new Color(248, 250, 252));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TechStoreUI.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        cboRole = new JComboBox<>(new String[] { "Admin", "Nhân Viên" });
        cboRole.setFont(TechStoreUI.FONT_MAIN);
        cboNhanVien = new JComboBox<>();
        cboNhanVien.setFont(TechStoreUI.FONT_MAIN);
        cboNhanVien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhanVien) {
                    NhanVien nv = (NhanVien) value;
                    setText(nv.getMaNV() + " - " + nv.getHoTen());
                }
                return this;
            }
        });

        pnlFields.add(TechStoreUI.wrapField("Username *", txtUsername));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Mật Khẩu *", txtPassword));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Vai Trò (Role) *", cboRole));
        pnlFields.add(Box.createVerticalStrut(16));
        pnlFields.add(TechStoreUI.wrapField("Nhân Viên Liên Kết (MaNV) *", cboNhanVien));

        // FIX: Đóng gói các trường nhập liệu đẩy lên phía trên để không bị kéo giãn xuống
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
        TechStoreUI.stylePrimaryButton(btnLuu, "Lưu Tài Khoản");
        pnlActions.add(btnLamMoi);
        pnlActions.add(btnLuu);
        pnlFormCard.add(pnlActions, BorderLayout.SOUTH);

        JPanel pnlTableCard = TechStoreUI.createCard();
        pnlTableCard.setLayout(new BorderLayout(0, 12));

        JPanel pnlTop = new JPanel(new BorderLayout(8, 8));
        pnlTop.setOpaque(false);
        JPanel pnlTitles = new JPanel(new GridLayout(2, 1));
        pnlTitles.setOpaque(false);
        JLabel lblList = new JLabel("Danh Sách Tài Khoản Hệ Thống (Bảng TAIKHOAN)");
        lblList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblListCount = TechStoreUI.createMutedLabel("Tổng cộng: 0 tài khoản");
        pnlTitles.add(lblList);
        pnlTitles.add(lblListCount);
        txtTimKiem = TechStoreUI.createField("Tìm username, quyền, mã NV, tên NV...");
        JPanel pnlSearch = TechStoreUI.createSearchField(txtTimKiem);
        pnlSearch.setPreferredSize(new Dimension(240, 34));
        pnlTop.add(pnlTitles, BorderLayout.WEST);
        pnlTop.add(pnlSearch, BorderLayout.EAST);

        model = new DefaultTableModel(
                new String[] { "Username", "Mật khẩu (Gốc)", "Quyền hạn", "Mã NV", "Họ Tên NV", "Thao Tác" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblTaiKhoan = new JTable(model);
        TechStoreUI.styleTable(tblTaiKhoan);
        TechStoreUI.configureActionColumn(tblTaiKhoan, COL_ACTION);

        JScrollPane scroll = new JScrollPane(tblTaiKhoan);
        scroll.setBorder(BorderFactory.createLineBorder(TechStoreUI.BORDER));
        scroll.setPreferredSize(new Dimension(0, 400));

        pnlTableCard.add(pnlTop, BorderLayout.NORTH);
        pnlTableCard.add(scroll, BorderLayout.CENTER);

        pnlMain.add(pnlFormCard, BorderLayout.WEST);
        pnlMain.add(pnlTableCard, BorderLayout.CENTER);
        add(pnlMain);
    }

    public void setEditMode(boolean edit) {
        this.editMode = edit;
        txtUsername.setEditable(!edit);
        txtUsername.setBackground(edit ? new Color(226, 232, 240) : new Color(248, 250, 252));
        lblFormMode.setText(edit ? " Đang chỉnh sửa " : " Thêm mới ");
        if (edit) {
            TechStoreUI.styleUpdateButton(btnLuu, "Cập Nhật Tài Khoản");
        } else {
            TechStoreUI.stylePrimaryButton(btnLuu, "Lưu Tài Khoản");
        }
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setListCount(int n) {
        lblListCount.setText("Tổng cộng: " + n + " tài khoản");
    }

    public void clearForm() {
        setEditMode(false);
        txtUsername.setText("");
        txtPassword.setText("");
        cboRole.setSelectedIndex(0);
        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }
        tblTaiKhoan.clearSelection();
        txtUsername.requestFocus();
    }

    public void loadToForm(TaiKhoan tk) {
        txtUsername.setText(tk.getUsername());
        txtPassword.setText(tk.getPassword());
        cboRole.setSelectedItem(tk.getRole());
        for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
            NhanVien nv = cboNhanVien.getItemAt(i);
            if (nv.getMaNV().equals(tk.getMaNV())) {
                cboNhanVien.setSelectedIndex(i);
                break;
            }
        }
        setEditMode(true);
    }

    public TaiKhoan getFormData() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ Username và Mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        NhanVien nv = (NhanVien) cboNhanVien.getSelectedItem();
        if (nv == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên liên kết!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        TaiKhoan tk = new TaiKhoan();
        tk.setUsername(user);
        tk.setPassword(pass);
        tk.setRole((String) cboRole.getSelectedItem());
        tk.setMaNV(nv.getMaNV());
        return tk;
    }

    public JComboBox<NhanVien> getCboNhanVien() {
        return cboNhanVien;
    }

    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public JTable getTblTaiKhoan() {
        return tblTaiKhoan;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            JFTaiKhoan view = new JFTaiKhoan();
            new TaiKhoanController(view);
            view.setVisible(true);
        });
    }
}
