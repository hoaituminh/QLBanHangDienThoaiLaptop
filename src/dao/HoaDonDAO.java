package dao;

import model.ChiTietHoaDon;
import model.HoaDon;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HoaDonDAO {

    // =========================================================
    // 1. LƯU HÓA ĐƠN (TRANSACTION AN TOÀN)
    // =========================================================
    public boolean luuHoaDonTransaction(HoaDon hd, ArrayList<ChiTietHoaDon> listCT) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction thủ công

            // Bước 1: INSERT vào bảng HOADON
            String sqlHD = "INSERT INTO HOADON (MaHD, NgayLap, MaKH, MaNV, TongTien) "
                         + "VALUES (?, GETDATE(), ?, ?, ?)";
            PreparedStatement pstHD = conn.prepareStatement(sqlHD);
            pstHD.setString(1, hd.getMaHD());
            pstHD.setString(2, hd.getMaKH());
            pstHD.setString(3, hd.getMaNV());
            pstHD.setDouble(4, hd.getTongTien());
            pstHD.executeUpdate();

            // Bước 2: INSERT hàng loạt vào CHITIETHOADON (addBatch)
            String sqlCT = "INSERT INTO CHITIETHOADON (MaHD, MaSP, SoLuong, DonGia, ThanhTien) "
                         + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstCT = conn.prepareStatement(sqlCT);
            for (ChiTietHoaDon ct : listCT) {
                pstCT.setString(1, ct.getMaHD());
                pstCT.setString(2, ct.getMaSP());
                pstCT.setInt   (3, ct.getSoLuong());
                pstCT.setDouble(4, ct.getDonGia());
                pstCT.setDouble(5, ct.getThanhTien());
                pstCT.addBatch();
            }
            pstCT.executeBatch();

            conn.commit(); // Mọi thứ OK -> COMMIT
            return true;

        } catch (Exception e) {
            // Có lỗi bất kỳ -> ROLLBACK để giữ nguyên DB
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) { conn.setAutoCommit(true); conn.close(); }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // =========================================================
    // 2. SINH MÃ HÓA ĐƠN TIẾP THEO (dạng HD001, HD002, ...)
    // =========================================================
    public String sinhMaHD() {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // Lấy mã HD có giá trị số cuối lớn nhất
            String sql = "SELECT TOP 1 MaHD FROM HOADON "
                       + "WHERE MaHD LIKE 'HD%' ORDER BY MaHD DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String last = rs.getString("MaHD");         // Ví dụ: "HD005"
                int so = Integer.parseInt(last.replaceAll("[^\\d]", "")) + 1;
                return String.format("HD%03d", so);         // => "HD006"
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return "HD001"; // Mặc định nếu chưa có hóa đơn nào
    }

    // =========================================================
    // 3. TÌM KIẾM HÓA ĐƠN (Hỗ trợ lọc đa điều kiện)
    //    - maHD   : tìm kiếm theo mã (LIKE)
    //    - tenKH  : tìm theo tên khách hàng (LIKE)
    //    - tuNgay : lọc từ ngày (>=)
    //    - denNgay: lọc đến ngày (<= cuối ngày 23:59:59)
    //    Truyền null / chuỗi rỗng để bỏ qua điều kiện đó
    // =========================================================
    public ArrayList<HoaDon> timKiem(String maHD, String tenKH, Date tuNgay, Date denNgay) {
        ArrayList<HoaDon> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            StringBuilder sql = new StringBuilder(
                "SELECT hd.MaHD, hd.NgayLap, hd.MaKH, hd.MaNV, hd.TongTien, "
              + "       kh.HoTen AS TenKH, nv.HoTen AS TenNV "
              + "FROM   HOADON hd "
              + "LEFT JOIN KHACHHANG kh ON hd.MaKH = kh.MaKH "
              + "LEFT JOIN NHANVIEN  nv ON hd.MaNV = nv.MaNV "
              + "WHERE  1=1 "
            );

            // Gắn điều kiện động
            if (maHD  != null && !maHD.isEmpty())  sql.append("AND hd.MaHD    LIKE ? ");
            if (tenKH != null && !tenKH.isEmpty()) sql.append("AND kh.HoTen   LIKE ? ");
            if (tuNgay  != null)                   sql.append("AND hd.NgayLap >= ? ");
            if (denNgay != null)                   sql.append("AND hd.NgayLap <= ? ");
            sql.append("ORDER BY hd.NgayLap DESC");

            PreparedStatement pst = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (maHD  != null && !maHD.isEmpty())  pst.setString   (idx++, "%" + maHD  + "%");
            if (tenKH != null && !tenKH.isEmpty()) pst.setString   (idx++, "%" + tenKH + "%");
            if (tuNgay  != null)                   pst.setTimestamp(idx++, new Timestamp(tuNgay.getTime()));
            if (denNgay != null) {
                // Đẩy đến cuối ngày 23:59:59 để bao gồm toàn bộ ngày đó
                Calendar cal = Calendar.getInstance();
                cal.setTime(denNgay);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                pst.setTimestamp(idx++, new Timestamp(cal.getTimeInMillis()));
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD    (rs.getString   ("MaHD"));
                hd.setNgayLap (rs.getTimestamp ("NgayLap"));
                hd.setMaKH    (rs.getString   ("MaKH"));
                hd.setMaNV    (rs.getString   ("MaNV"));
                hd.setTongTien(rs.getDouble   ("TongTien"));
                hd.setTenKH   (rs.getString   ("TenKH"));
                hd.setTenNV   (rs.getString   ("TenNV"));
                list.add(hd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    /**
     * Tra cứu lịch sử hóa đơn (mockup): theo mã HD, tên KH, SĐT KH và lọc nhân viên lập.
     * @param maNVFilter "ALL" hoặc mã NV cụ thể
     */
    public ArrayList<HoaDon> timKiemLichSu(String keyword, String maNVFilter) {
        ArrayList<HoaDon> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql =
                "SELECT hd.MaHD, hd.NgayLap, hd.MaKH, hd.MaNV, hd.TongTien, "
              + "       kh.HoTen AS TenKH, kh.SDT AS SDTKH, nv.HoTen AS TenNV "
              + "FROM   HOADON hd "
              + "LEFT JOIN KHACHHANG kh ON hd.MaKH = kh.MaKH "
              + "LEFT JOIN NHANVIEN  nv ON hd.MaNV = nv.MaNV "
              + "WHERE  1=1 ";

            boolean hasKw = keyword != null && !keyword.trim().isEmpty();
            boolean hasNv = maNVFilter != null && !maNVFilter.isEmpty()
                           && !"ALL".equalsIgnoreCase(maNVFilter);

            if (hasKw) {
                sql += "AND (hd.MaHD LIKE ? OR kh.HoTen LIKE ? OR kh.SDT LIKE ?) ";
            }
            if (hasNv) {
                sql += "AND hd.MaNV = ? ";
            }
            sql += "ORDER BY hd.NgayLap DESC";

            PreparedStatement pst = conn.prepareStatement(sql);
            int idx = 1;
            if (hasKw) {
                String like = "%" + keyword.trim() + "%";
                pst.setString(idx++, like);
                pst.setString(idx++, like);
                pst.setString(idx++, like);
            }
            if (hasNv) {
                pst.setString(idx++, maNVFilter);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD    (rs.getString   ("MaHD"));
                hd.setNgayLap (rs.getTimestamp ("NgayLap"));
                hd.setMaKH    (rs.getString   ("MaKH"));
                hd.setMaNV    (rs.getString   ("MaNV"));
                hd.setTongTien(rs.getDouble   ("TongTien"));
                hd.setTenKH   (rs.getString   ("TenKH"));
                hd.setTenNV   (rs.getString   ("TenNV"));
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public ArrayList<ChiTietHoaDon> getChiTietByMaHD(String maHD) {
        ArrayList<ChiTietHoaDon> list = new ArrayList<>();
        String sql =
            "SELECT ct.MaCTHD, ct.MaHD, ct.MaSP, sp.TenSP, ct.SoLuong, ct.DonGia, ct.ThanhTien "
          + "FROM   CHITIETHOADON ct "
          + "INNER JOIN SANPHAM sp ON ct.MaSP = sp.MaSP "
          + "WHERE  ct.MaHD = ? "
          + "ORDER BY ct.MaCTHD ASC";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaCTHD  (rs.getInt   ("MaCTHD"));
                ct.setMaHD    (rs.getString("MaHD"));
                ct.setMaSP    (rs.getString("MaSP"));
                ct.setTenSP   (rs.getString("TenSP"));
                ct.setSoLuong (rs.getInt   ("SoLuong"));
                ct.setDonGia  (rs.getDouble("DonGia"));
                ct.setThanhTien(rs.getDouble("ThanhTien"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================
    // THỐNG KÊ CHO DASHBOARD
    // =========================

    // Lấy tổng doanh thu
    public double getTongDoanhThu() {
        String sql = "SELECT ISNULL(SUM(TongTien), 0) as Total FROM HOADON";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("Total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tổng số hóa đơn
    public int getTongHoaDon() {
        String sql = "SELECT COUNT(*) as Total FROM HOADON";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy hóa đơn gần đây (giới hạn số lượng)
    public ArrayList<HoaDon> getHoaDonGanDay(int limit) {
        ArrayList<HoaDon> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " hd.MaHD, hd.NgayLap, hd.MaKH, hd.MaNV, hd.TongTien, "
                   + "       kh.HoTen AS TenKH "
                   + "FROM   HOADON hd "
                   + "LEFT JOIN KHACHHANG kh ON hd.MaKH = kh.MaKH "
                   + "ORDER BY hd.NgayLap DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("MaHD"));
                hd.setNgayLap(rs.getTimestamp("NgayLap"));
                hd.setMaKH(rs.getString("MaKH"));
                hd.setMaNV(rs.getString("MaNV"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTenKH(rs.getString("TenKH"));
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy top sản phẩm bán chạy
    public ArrayList<Object[]> getTopSanPhamBanChay(int limit) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP " + limit + " sp.TenSP, "
                   + "       SUM(ct.SoLuong) as SoLuong, "
                   + "       SUM(ct.ThanhTien) as DoanhThu "
                   + "FROM   CHITIETHOADON ct "
                   + "INNER JOIN SANPHAM sp ON ct.MaSP = sp.MaSP "
                   + "GROUP BY sp.TenSP "
                   + "ORDER BY SUM(ct.SoLuong) DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = {
                    rs.getString("TenSP"),
                    rs.getInt("SoLuong"),
                    rs.getDouble("DoanhThu")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
