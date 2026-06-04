package controller;

import dao.KhachHangDAO;
import model.KhachHang;
import view.JFKhachHang;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.util.ArrayList;

public class KhachHangController {

    private final JFKhachHang view;
    private final KhachHangDAO dao = new KhachHangDAO();

    public KhachHangController(JFKhachHang view) {
        this.view = view;
        refreshTable();
        initController();
    }

    private void initController() {
        view.getBtnLuu().addActionListener(e -> luuKhachHang());
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

        view.getTblKhachHang().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblKhachHang().rowAtPoint(e.getPoint());
                int col = view.getTblKhachHang().columnAtPoint(e.getPoint());
                if (row < 0) return;

                if (col == JFKhachHang.COL_ACTION) {
                    Rectangle r = view.getTblKhachHang().getCellRect(row, col, true);
                    int relX = e.getX() - r.x;
                    String maKH = (String) view.getModel().getValueAt(row, 0);
                    if (relX < r.width / 2) {
                        editByMa(maKH);
                    } else {
                        xoaByMa(maKH);
                    }
                } else {
                    String maKH = (String) view.getModel().getValueAt(row, 0);
                    editByMa(maKH);
                }
            }
        });
    }

    private void refreshTable() {
        String kw = view.getTxtTimKiem().getText().trim();
        ArrayList<KhachHang> list = kw.isEmpty() ? dao.getAll() : dao.search(kw);
        fillTable(list);
    }

    private void fillTable(ArrayList<KhachHang> list) {
        view.getModel().setRowCount(0);
        for (KhachHang kh : list) {
            view.getModel().addRow(new Object[]{
                kh.getMaKH(),
                kh.getHoTen(),
                kh.getSdt() != null ? kh.getSdt() : "---",
                kh.getDiaChi() != null ? kh.getDiaChi() : "---",
                kh.getEmail() != null ? kh.getEmail() : "---",
                ""
            });
        }
        view.setListCount(list.size());
    }

    private void editByMa(String maKH) {
        KhachHang kh = dao.findById(maKH);
        if (kh != null) {
            view.loadToForm(kh);
        }
    }

    private void luuKhachHang() {
        KhachHang kh = view.getFormData();
        if (kh == null) return;

        if (view.isEditMode()) {
            if (dao.update(kh)) {
                JOptionPane.showMessageDialog(view, "Đã cập nhật khách hàng \"" + kh.getHoTen() + "\" thành công!");
                view.clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (dao.exists(kh.getMaKH())) {
                JOptionPane.showMessageDialog(view,
                    "Mã khách hàng \"" + kh.getMaKH() + "\" đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dao.insert(kh)) {
                JOptionPane.showMessageDialog(view, "Đã thêm mới khách hàng \"" + kh.getHoTen() + "\" thành công!");
                view.clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaByMa(String maKH) {
        KhachHang kh = dao.findById(maKH);
        if (kh == null) return;

        int choice = JOptionPane.showConfirmDialog(view,
            "Bạn có thực sự muốn xóa khách hàng \"" + kh.getHoTen() + "\"?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        if (dao.delete(maKH)) {
            JOptionPane.showMessageDialog(view, "Đã xóa khách hàng khỏi cơ sở dữ liệu!");
            view.clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(view,
                "Không thể xóa! Khách hàng \"" + kh.getHoTen() + "\" đã phát sinh lịch sử giao dịch hóa đơn.",
                "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method để reload data khi được gọi từ JFMenu
    public void loadData() {
        refreshTable();
    }
}
