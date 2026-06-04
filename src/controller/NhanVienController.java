package controller;

import dao.NhanVienDAO;
import model.NhanVien;
import view.JFNhanVien;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.util.ArrayList;

public class NhanVienController {

    private final JFNhanVien view;
    private final NhanVienDAO dao = new NhanVienDAO();

    public NhanVienController(JFNhanVien view) {
        this.view = view;
        refreshTable();
        initController();
    }

    private void initController() {
        view.getBtnLuu().addActionListener(e -> luuNhanVien());
        view.getBtnLamMoi().addActionListener(e -> {
            view.clearForm();
            refreshTable();
        });

        view.getTxtTimKiem().getDocument().addDocumentListener(new DocumentListener() {
            private void changed() { refreshTable(); }
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
        });

        view.getTblNhanVien().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblNhanVien().rowAtPoint(e.getPoint());
                int col = view.getTblNhanVien().columnAtPoint(e.getPoint());
                if (row < 0) return;

                if (col == JFNhanVien.COL_ACTION) {
                    Rectangle r = view.getTblNhanVien().getCellRect(row, col, true);
                    int relX = e.getX() - r.x;
                    String maNV = (String) view.getModel().getValueAt(row, 0);
                    if (relX < r.width / 2) {
                        editByMa(maNV);
                    } else {
                        xoaByMa(maNV);
                    }
                } else {
                    String maNV = (String) view.getModel().getValueAt(row, 0);
                    editByMa(maNV);
                }
            }
        });
    }

    private void refreshTable() {
        String kw = view.getTxtTimKiem().getText().trim();
        ArrayList<NhanVien> list = kw.isEmpty() ? dao.getAll() : dao.search(kw);
        fillTable(list);
    }

    private void fillTable(ArrayList<NhanVien> list) {
        view.getModel().setRowCount(0);
        for (NhanVien nv : list) {
            view.getModel().addRow(new Object[]{
                nv.getMaNV(),
                nv.getHoTen(),
                nv.getSdt() != null ? nv.getSdt() : "---",
                nv.getDiaChi() != null ? nv.getDiaChi() : "---",
                nv.getEmail() != null ? nv.getEmail() : "---",
                ""
            });
        }
        view.setListCount(list.size());
    }

    private void editByMa(String maNV) {
        NhanVien nv = dao.findById(maNV);
        if (nv != null) {
            view.loadToForm(nv);
        }
    }

    private void luuNhanVien() {
        NhanVien nv = view.getFormData();
        if (nv == null) return;

        if (view.isEditMode()) {
            if (dao.update(nv)) {
                JOptionPane.showMessageDialog(view, "Đã cập nhật nhân viên \"" + nv.getHoTen() + "\" thành công!");
                view.clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (dao.exists(nv.getMaNV())) {
                JOptionPane.showMessageDialog(view,
                    "Mã nhân viên \"" + nv.getMaNV() + "\" đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dao.insert(nv)) {
                JOptionPane.showMessageDialog(view, "Đã thêm mới nhân viên \"" + nv.getHoTen() + "\" thành công!");
                view.clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaByMa(String maNV) {
        NhanVien nv = dao.findById(maNV);
        if (nv == null) return;

        int choice = JOptionPane.showConfirmDialog(view,
            "Bạn có thực sự muốn xóa nhân viên \"" + nv.getHoTen() + "\"?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        if (dao.delete(maNV)) {
            JOptionPane.showMessageDialog(view, "Đã xóa nhân viên khỏi cơ sở dữ liệu!");
            view.clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(view,
                "Không thể xóa! Nhân viên \"" + nv.getHoTen() + "\" đã ký lập hóa đơn hoặc còn tài khoản liên kết.",
                "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method để reload data khi được gọi từ JFMenu
    public void loadData() {
        refreshTable();
    }
}
