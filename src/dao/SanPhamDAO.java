package dao;

import model.SanPham;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SanPhamDAO {

    // Lấy tất cả sản phẩm
    public ArrayList<SanPham> getAll() {

        ArrayList<SanPham> list = new ArrayList<>();

        String sql = "SELECT * FROM SANPHAM";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                SanPham sp = new SanPham();

                sp.setMaSP(rs.getString("MaSP"));
                sp.setTenSP(rs.getString("TenSP"));
                sp.setDonGia(rs.getDouble("DonGia"));
                sp.setSoLuong(rs.getInt("SoLuong"));
                sp.setHinh(rs.getString("Hinh"));
                sp.setMoTa(rs.getString("MoTa"));
                sp.setHangSX(rs.getString("HangSX"));
                sp.setMaLoai(rs.getString("MaLoai"));

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Thêm sản phẩm
    public boolean insert(SanPham sp) {

        String sql =
            "INSERT INTO SANPHAM "
            + "(MaSP,TenSP,DonGia,SoLuong,Hinh,MoTa,HangSX,MaLoai) "
            + "VALUES(?,?,?,?,?,?,?,?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setDouble(3, sp.getDonGia());
            ps.setInt(4, sp.getSoLuong());
            ps.setString(5, sp.getHinh());
            ps.setString(6, sp.getMoTa());
            ps.setString(7, sp.getHangSX());
            ps.setString(8, sp.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Sửa sản phẩm
    public boolean update(SanPham sp) {

        String sql =
            "UPDATE SANPHAM SET "
            + "TenSP=?, "
            + "DonGia=?, "
            + "SoLuong=?, "
            + "Hinh=?, "
            + "MoTa=?, "
            + "HangSX=?, "
            + "MaLoai=? "
            + "WHERE MaSP=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getDonGia());
            ps.setInt(3, sp.getSoLuong());
            ps.setString(4, sp.getHinh());
            ps.setString(5, sp.getMoTa());
            ps.setString(6, sp.getHangSX());
            ps.setString(7, sp.getMaLoai());
            ps.setString(8, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa sản phẩm
    public boolean delete(String maSP) {

        String sql =
            "DELETE FROM SANPHAM WHERE MaSP=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maSP);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Tìm theo mã
    public SanPham findById(String maSP) {

        String sql =
            "SELECT * FROM SANPHAM WHERE MaSP=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maSP);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new SanPham(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getDouble("DonGia"),
                        rs.getInt("SoLuong"),
                        rs.getString("Hinh"),
                        rs.getString("MoTa"),
                        rs.getString("HangSX"),
                        rs.getString("MaLoai")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Kiểm tra tồn tại
    public boolean exists(String maSP) {

        String sql =
            "SELECT * FROM SANPHAM WHERE MaSP=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maSP);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Tìm kiếm theo mã hoặc tên
    public ArrayList<SanPham> search(String keyword) {

        ArrayList<SanPham> list = new ArrayList<>();

        String sql =
            "SELECT * FROM SANPHAM "
            + "WHERE MaSP LIKE ? "
            + "OR TenSP LIKE ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                SanPham sp = new SanPham();

                sp.setMaSP(rs.getString("MaSP"));
                sp.setTenSP(rs.getString("TenSP"));
                sp.setDonGia(rs.getDouble("DonGia"));
                sp.setSoLuong(rs.getInt("SoLuong"));
                sp.setHinh(rs.getString("Hinh"));
                sp.setMoTa(rs.getString("MoTa"));
                sp.setHangSX(rs.getString("HangSX"));
                sp.setMaLoai(rs.getString("MaLoai"));

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // Thống kê số lượng sản phẩm
    // =========================
    public int getCount() {
        String sql = "SELECT COUNT(*) as Total FROM SANPHAM";
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

    // Tổng số lượng tồn kho (SUM SoLuong)
    public int getTongSoLuong() {
        String sql = "SELECT ISNULL(SUM(SoLuong), 0) AS TongSoLuong FROM SANPHAM";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("TongSoLuong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Sản phẩm cần nhập (số lượng tồn <= ngưỡng)
    public int getSoSanPhamCanNhap(int nguongSoLuong) {
        String sql = "SELECT COUNT(*) as Total FROM SANPHAM WHERE SoLuong <= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nguongSoLuong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}