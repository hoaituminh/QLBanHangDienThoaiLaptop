package dao;

import model.NhanVien;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO {

    // =========================
    // Lấy toàn bộ nhân viên
    // =========================
    public ArrayList<NhanVien> getAll() {

        ArrayList<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NHANVIEN";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setSdt(rs.getString("SDT"));
                nv.setDiaChi(rs.getString("DiaChi"));
                nv.setEmail(rs.getString("Email"));

                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================
    // Tìm theo mã
    // =========================
    public NhanVien findById(String maNV) {

        String sql = "SELECT * FROM NHANVIEN WHERE MaNV = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setSdt(rs.getString("SDT"));
                nv.setDiaChi(rs.getString("DiaChi"));
                nv.setEmail(rs.getString("Email"));

                return nv;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================
    // Kiểm tra tồn tại
    // =========================
    public boolean exists(String maNV) {

        String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE MaNV=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maNV);
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
    // Thêm nhân viên
    // =========================
    public boolean insert(NhanVien nv) {

        String sql =
                "INSERT INTO NHANVIEN (MaNV,HoTen,SDT,DiaChi,Email) VALUES (?,?,?,?,?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getDiaChi());
            ps.setString(5, nv.getEmail());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================
    // Cập nhật nhân viên
    // =========================
    public boolean update(NhanVien nv) {

        String sql =
                "UPDATE NHANVIEN SET HoTen=?, SDT=?, DiaChi=?, Email=? WHERE MaNV=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getSdt());
            ps.setString(3, nv.getDiaChi());
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================
    // Xóa nhân viên
    // =========================
    public boolean delete(String maNV) {

        String sql = "DELETE FROM NHANVIEN WHERE MaNV=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================
    // Tìm kiếm
    // =========================
    public ArrayList<NhanVien> search(String keyword) {

        ArrayList<NhanVien> list = new ArrayList<>();

        String sql =
                "SELECT * FROM NHANVIEN WHERE MaNV LIKE ? OR HoTen LIKE ? OR SDT LIKE ?";

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
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setSdt(rs.getString("SDT"));
                nv.setDiaChi(rs.getString("DiaChi"));
                nv.setEmail(rs.getString("Email"));

                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}