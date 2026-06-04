package controller;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import model.NhanVien;
import model.TaiKhoan;
import view.JFTaiKhoan;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.util.ArrayList;

public class TaiKhoanController {

    private final JFTaiKhoan view;
    private final TaiKhoanDAO dao = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    /** Username đang đăng nhập (để chặn tự xóa) – có thể set từ LoginController sau. */
    private String currentUsername = "";

    public TaiKhoanController(JFTaiKhoan view) {
        this.view = view;
        loadNhanVienCombo();
        refreshTable();
        initController();
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username != null ? username : "";
    }

    private void loadNhanVienCombo() {
        JComboBox<NhanVien> cbo = view.getCboNhanVien();
        cbo.removeAllItems();
        for (NhanVien nv : nhanVienDAO.getAll()) {
            cbo.addItem(nv);
        }
    }

    private void initController() {
        view.getBtnLuu().addActionListener(e -> luuTaiKhoan());
        view.getBtnLamMoi().addActionListener(e -> {
            view.clearForm();
            loadNhanVienCombo();
            refreshTable();
        });

        view.getTxtTimKiem().getDocument().addDocumentListener(new DocumentListener() {
            private void changed() { refreshTable(); }
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
        });

        view.getTblTaiKhoan().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblTaiKhoan().rowAtPoint(e.getPoint());
                int col = view.getTblTaiKhoan().columnAtPoint(e.getPoint());
                if (row < 0) return;
                String username = (String) view.getModel().getValueAt(row, 0);

                if (col == JFTaiKhoan.COL_ACTION) {
                    Rectangle r = view.getTblTaiKhoan().getCellRect(row, col, true);
                    int relX = e.getX() - r.x;
                    if (relX < r.width / 2) {
                        editByUsername(username);
                    } else {
                        xoaByUsername(username);
                    }
                } else {
                    editByUsername(username);
                }
            }
        });
    }

    private void refreshTable() {
        String kw = view.getTxtTimKiem().getText().trim();
        ArrayList<TaiKhoan> list = kw.isEmpty() ? dao.getAll() : dao.search(kw);
        fillTable(list);
    }

    private void fillTable(ArrayList<TaiKhoan> list) {
        view.getModel().setRowCount(0);
        for (TaiKhoan tk : list) {
            view.getModel().addRow(new Object[]{
                tk.getUsername(),
                tk.getPassword(),
                tk.getRole(),
                tk.getMaNV(),
                tk.getHoTenNV() != null ? tk.getHoTenNV() : "---",
                ""
            });
        }
        view.setListCount(list.size());
    }

    private void editByUsername(String username) {
        TaiKhoan tk = dao.findByUsername(username);
        if (tk != null) {
            view.loadToForm(tk);
        }
    }

    private void luuTaiKhoan() {
        TaiKhoan tk = view.getFormData();
        if (tk == null) return;

        if (view.isEditMode()) {
            if (dao.update(tk)) {
                JOptionPane.showMessageDialog(view,
                    "Đã thay đổi thông tin đăng nhập của tài khoản \"" + tk.getUsername() + "\"!");
                view.clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (dao.exists(tk.getUsername())) {
                JOptionPane.showMessageDialog(view,
                    "Tài khoản \"" + tk.getUsername() + "\" đã tồn tại trên SQL Server!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dao.insert(tk)) {
                JOptionPane.showMessageDialog(view,
                    "Cấp tài khoản mới \"" + tk.getUsername() + "\" thành công!");
                view.clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaByUsername(String username) {
        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(view,
                "Tài khoản \"admin\" hệ thống gốc không được phép xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (username.equals(currentUsername)) {
            JOptionPane.showMessageDialog(view,
                "Không thể tự xóa tài khoản của chính bạn đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(view,
            "Bạn có chắc chắn muốn xóa vĩnh viễn tài khoản \"" + username + "\"?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        if (dao.delete(username)) {
            JOptionPane.showMessageDialog(view, "Đã xóa tài khoản khỏi hệ thống!");
            view.clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(view, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method để reload data khi được gọi từ JFMenu
    public void loadData() {
        loadNhanVienCombo();
        refreshTable();
    }
}
