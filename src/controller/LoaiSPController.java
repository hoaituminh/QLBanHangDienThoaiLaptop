package controller;

import dao.LoaiSPDAO;
import model.LoaiSP;
import view.JFLoaiSP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LoaiSPController {

    private JFLoaiSP view;
    private LoaiSPDAO dao;

    public LoaiSPController(JFLoaiSP view) {
        this.view = view;
        this.dao = new LoaiSPDAO();

        // Nạp dữ liệu ban đầu
        loadTableData();

        // Khởi tạo các sự kiện lắng nghe
        initController();
    }

    private void initController() {
        // 1. Sự kiện Click vào Bảng
        view.getTblLoaiSP().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiChiTiet();
            }
        });

        // 2. Sự kiện Nút Thêm
        view.getBtnThem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themLoaiSP();
            }
        });

        // 3. Sự kiện Nút Sửa
        view.getBtnSua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaLoaiSP();
            }
        });

        // 4. Sự kiện Nút Xóa
        view.getBtnXoa().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaLoaiSP();
            }
        });

        // 5. Sự kiện Làm mới
        view.getBtnLamMoi().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.clearForm();
                loadTableData();
            }
        });

        // 6. Sự kiện Tìm kiếm
        view.getBtnTimKiem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemLoaiSP();
            }
        });
    }

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void loadTableData() {
        ArrayList<LoaiSP> list = dao.getAll();
        fillTable(list);
    }

    private void fillTable(ArrayList<LoaiSP> list) {
        view.getModel().setRowCount(0); // Xóa dữ liệu cũ
        for (LoaiSP loai : list) {
            view.getModel().addRow(new Object[]{
                    loai.getMaLoai(),
                    loai.getTenLoai()
            });
        }
    }

    private void hienThiChiTiet() {
        int row = view.getTblLoaiSP().getSelectedRow();
        if (row >= 0) {
            String maLoai = (String) view.getTblLoaiSP().getValueAt(row, 0);
            LoaiSP loai = dao.findById(maLoai);
            
            if (loai != null) {
                // Đổ dữ liệu lên form
                view.getTxtMaLoai().setText(loai.getMaLoai());
                view.getTxtTenLoai().setText(loai.getTenLoai());
                
                // Khóa ô Mã Loại không cho sửa (Vì là khóa chính)
                view.getTxtMaLoai().setEditable(false);
            }
        }
    }

    private void themLoaiSP() {
        LoaiSP loai = view.getFormData(); 
        if (loai != null) {
            // Kiểm tra trùng mã
            if (dao.exists(loai.getMaLoai())) {
                JOptionPane.showMessageDialog(view, "Mã loại sản phẩm đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                view.getTxtMaLoai().requestFocus();
                return;
            }

            // Thực hiện thêm
            if (dao.insert(loai)) {
                JOptionPane.showMessageDialog(view, "Thêm loại sản phẩm thành công!");
                loadTableData();
                view.clearForm();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void suaLoaiSP() {
        if (view.getTxtMaLoai().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn loại sản phẩm cần cập nhật từ bảng!");
            return;
        }

        LoaiSP loai = view.getFormData();
        if (loai != null) {
            if (dao.update(loai)) {
                JOptionPane.showMessageDialog(view, "Cập nhật thông tin thành công!");
                loadTableData();
                view.clearForm();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaLoaiSP() {
        String maLoai = view.getTxtMaLoai().getText().trim();
        if (maLoai.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn loại sản phẩm cần xóa từ bảng!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa loại sản phẩm: " + maLoai + "?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                
        if (choice == JOptionPane.YES_OPTION) {
            if (dao.delete(maLoai)) {
                JOptionPane.showMessageDialog(view, "Xóa loại sản phẩm thành công!");
                loadTableData();
                view.clearForm();
            } else {
                JOptionPane.showMessageDialog(view, "Không thể xóa. Loại sản phẩm này đang chứa Sản Phẩm bên trong!", "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void timKiemLoaiSP() {
        String keyword = view.getTxtTimKiem().getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadTableData(); // Trống thì load lại tất cả
        } else {
            // Lọc dữ liệu trực tiếp trong code (Vì LoaiSPDAO chưa có hàm search)
            ArrayList<LoaiSP> allList = dao.getAll();
            ArrayList<LoaiSP> filteredList = new ArrayList<>();
            
            for (LoaiSP loai : allList) {
                if (loai.getMaLoai().toLowerCase().contains(keyword) || 
                    loai.getTenLoai().toLowerCase().contains(keyword)) {
                    filteredList.add(loai);
                }
            }
            
            fillTable(filteredList);
            if(filteredList.isEmpty()){
                 JOptionPane.showMessageDialog(view, "Không tìm thấy loại sản phẩm nào phù hợp!");
            }
        }
    }
}