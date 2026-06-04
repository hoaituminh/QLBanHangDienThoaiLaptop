package controller;

import dao.HoaDonDAO;
import view.JFThongKe;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThongKeController {

    private static final int REFRESH_INTERVAL_MS = 15000;
    private static final int TOP_NGANH_HANG = 8;

    private final JFThongKe view;
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final Timer refreshTimer;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public ThongKeController(JFThongKe view) {
        this.view = view;
        this.refreshTimer = new Timer(REFRESH_INTERVAL_MS, e -> loadData());
        this.refreshTimer.start();
        loadData();
    }

    public void loadData() {
        new SwingWorker<ThongKeData, Void>() {
            @Override
            protected ThongKeData doInBackground() {
                return new ThongKeData(
                    hoaDonDAO.getNhomNganhHangBanChay(TOP_NGANH_HANG),
                    hoaDonDAO.getDoanhThuTrong7NgayQua()
                );
            }

            @Override
            protected void done() {
                try {
                    ThongKeData data = get();
                    updateNganhHangChart(data.nganhHang);
                    updateDoanhThuChart(data.doanhThu);
                    view.setLastUpdated("Cập nhật lúc: " + dateTimeFormat.format(new java.util.Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                    view.setLastUpdated("Không tải được dữ liệu thống kê");
                }
            }
        }.execute();
    }

    public void stopAutoRefresh() {
        refreshTimer.stop();
    }

    private void updateNganhHangChart(ArrayList<Object[]> rows) {
        view.getDatasetNganhHang().clear();
        if (rows == null || rows.isEmpty()) {
            view.getDatasetNganhHang().setValue("Chưa có dữ liệu", 1);
            return;
        }

        for (Object[] row : rows) {
            String tenLoai = row[0] != null ? row[0].toString() : "Chưa phân loại";
            Number soLuong = row[1] instanceof Number ? (Number) row[1] : 0;
            view.getDatasetNganhHang().setValue(tenLoai, soLuong);
        }
    }

    private void updateDoanhThuChart(ArrayList<Object[]> rows) {
        view.getDatasetDoanhThu().clear();
        Map<String, Double> doanhThuTheoNgay = buildLastSevenDaysMap();

        if (rows != null) {
            for (Object[] row : rows) {
                if (row[0] instanceof java.util.Date && row[1] instanceof Number) {
                    String label = dayFormat.format((java.util.Date) row[0]);
                    doanhThuTheoNgay.put(label, ((Number) row[1]).doubleValue());
                }
            }
        }

        for (Map.Entry<String, Double> entry : doanhThuTheoNgay.entrySet()) {
            view.getDatasetDoanhThu().addValue(entry.getValue(), "Doanh thu", entry.getKey());
        }
    }

    private Map<String, Double> buildLastSevenDaysMap() {
        Map<String, Double> map = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -6);
        for (int i = 0; i < 7; i++) {
            map.put(dayFormat.format(cal.getTime()), 0.0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return map;
    }

    private static class ThongKeData {
        final ArrayList<Object[]> nganhHang;
        final ArrayList<Object[]> doanhThu;

        ThongKeData(ArrayList<Object[]> nganhHang, ArrayList<Object[]> doanhThu) {
            this.nganhHang = nganhHang;
            this.doanhThu = doanhThu;
        }
    }
}
