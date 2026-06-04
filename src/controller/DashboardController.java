package controller;

import dao.HoaDonDAO;
import dao.SanPhamDAO;
import model.HoaDon;
import view.JFDashboard;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DashboardController {

    private final JFDashboard view;
    private final HoaDonDAO hoaDonDAO;
    private final SanPhamDAO sanPhamDAO;
    private static final int NGUONG_CAN_NHAP = 10;

    private final DecimalFormat currencyFormat = new DecimalFormat("#,###,###,### đ");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public DashboardController(JFDashboard view) {
        this.view = view;
        this.hoaDonDAO = new HoaDonDAO();
        this.sanPhamDAO = new SanPhamDAO();

        initController();
        loadData();
    }

    private void initController() {
        // Có thể thêm các event listener cho dashboard nếu cần
    }

    public void loadData() {
        // Load các số liệu thống kê
        SwingUtilities.invokeLater(() -> {
            try {
                // Tổng doanh thu
                double tongDoanhThu = hoaDonDAO.getTongDoanhThu();
                view.getLblTongDoanhThu().setText(formatCurrency(tongDoanhThu));

                // Tổng số hóa đơn
                int tongHoaDon = hoaDonDAO.getTongHoaDon();
                view.getLblTongHoaDon().setText(String.valueOf(tongHoaDon));

                // Số sản phẩm cần nhập
                int sanPhamCanNhap = sanPhamDAO.getSoSanPhamCanNhap(NGUONG_CAN_NHAP);
                view.getLblSanPhamCanNhap().setText(String.valueOf(sanPhamCanNhap));

                // Tổng số lượng tồn kho (SUM SoLuong)
                int tongSoLuong = sanPhamDAO.getTongSoLuong();
                view.getLblTongSanPham().setText(String.valueOf(tongSoLuong));

                // Số loại sản phẩm (COUNT) hiển thị ở mô tả
                int tongSanPham = sanPhamDAO.getCount();
                view.getLblTongSanPhamDesc().setText(tongSanPham + " loại sản phẩm trong kho");

                // Load hóa đơn gần đây
                loadHoaDonGanDay();

                // Load top sản phẩm bán chạy
                loadTopSanPham();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view, 
                    "Lỗi khi tải dữ liệu Dashboard: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadHoaDonGanDay() {
        ArrayList<HoaDon> danhSachHD = hoaDonDAO.getHoaDonGanDay(5);
        view.getModelHoaDonGanDay().setRowCount(0);

        for (HoaDon hd : danhSachHD) {
            view.getModelHoaDonGanDay().addRow(new Object[]{
                hd.getMaHD(),
                hd.getTenKH() != null ? hd.getTenKH() : "---",
                hd.getNgayLap() != null ? dateFormat.format(hd.getNgayLap()) : "---",
                formatCurrency(hd.getTongTien())
            });
        }
    }

    private void loadTopSanPham() {
        ArrayList<Object[]> topSanPham = hoaDonDAO.getTopSanPhamBanChay(5);
        view.getModelTopSanPham().setRowCount(0);

        int stt = 1;
        for (Object[] row : topSanPham) {
            view.getModelTopSanPham().addRow(new Object[]{
                stt++,
                row[0], // Tên sản phẩm
                row[1], // Số lượng
                formatCurrency((Double) row[2]) // Doanh thu
            });
        }
    }

    private String formatCurrency(double value) {
        return currencyFormat.format(value);
    }
}