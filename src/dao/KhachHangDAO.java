package dao;

import model.KhachHang;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class KhachHangDAO {

    // =========================
    // Lấy toàn bộ khách hàng
    // =========================
    public ArrayList<KhachHang> getAll() {

        ArrayList<KhachHang> list = new ArrayList<>();

        String sql = "SELECT * FROM KHACHHANG";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                KhachHang kh = new KhachHang();

                kh.setMaKH(rs.getString("MaKH"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SDT"));
                kh.setDiaChi(rs.getString("DiaChi"));
                kh.setEmail(rs.getString("Email"));

                list.add(kh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // Tìm theo mã
    // =========================
    public KhachHang findById(String maKH) {

        String sql =
                "SELECT * FROM KHACHHANG WHERE MaKH = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maKH);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                KhachHang kh = new KhachHang();

                kh.setMaKH(rs.getString("MaKH"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SDT"));
                kh.setDiaChi(rs.getString("DiaChi"));
                kh.setEmail(rs.getString("Email"));

                return kh;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // Kiểm tra tồn tại
    // =========================
    public boolean exists(String maKH) {

        String sql =
                "SELECT COUNT(*) FROM KHACHHANG WHERE MaKH=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maKH);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // Thêm khách hàng
    // =========================
    public boolean insert(KhachHang kh) {

        String sql =
                "INSERT INTO KHACHHANG " +
                "(MaKH,HoTen,SDT,DiaChi,Email) " +
                "VALUES (?,?,?,?,?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getEmail());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // Cập nhật khách hàng
    // =========================
    public boolean update(KhachHang kh) {

        String sql =
                "UPDATE KHACHHANG SET " +
                "HoTen=?, SDT=?, DiaChi=?, Email=? " +
                "WHERE MaKH=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChi());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getMaKH());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // Xóa khách hàng
    // =========================
    public boolean delete(String maKH) {

        String sql =
                "DELETE FROM KHACHHANG WHERE MaKH=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maKH);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // Tìm kiếm
    // =========================
    public ArrayList<KhachHang> search(String keyword) {

        ArrayList<KhachHang> list = new ArrayList<>();

        String sql =
                "SELECT * FROM KHACHHANG " +
                "WHERE MaKH LIKE ? " +
                "OR HoTen LIKE ? " +
                "OR SDT LIKE ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            String key = "%" + keyword + "%";

            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                KhachHang kh = new KhachHang();

                kh.setMaKH(rs.getString("MaKH"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SDT"));
                kh.setDiaChi(rs.getString("DiaChi"));
                kh.setEmail(rs.getString("Email"));

                list.add(kh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // Thống kê số lượng khách hàng
    // =========================
    public int getCount() {
        String sql = "SELECT COUNT(*) as Total FROM KHACHHANG";
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
}
