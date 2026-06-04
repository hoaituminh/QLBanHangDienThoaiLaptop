package dao;

import model.LoaiSP;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class LoaiSPDAO {

    // Lấy tất cả loại sản phẩm
    public ArrayList<LoaiSP> getAll() {

        ArrayList<LoaiSP> list = new ArrayList<>();

        String sql = "SELECT * FROM LOAISP";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                LoaiSP loai = new LoaiSP();

                loai.setMaLoai(rs.getString("MaLoai"));
                loai.setTenLoai(rs.getString("TenLoai"));

                list.add(loai);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Thêm loại sản phẩm
    public boolean insert(LoaiSP loai) {

        String sql =
                "INSERT INTO LOAISP(MaLoai, TenLoai) VALUES(?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, loai.getMaLoai());
            ps.setString(2, loai.getTenLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Sửa loại sản phẩm
    public boolean update(LoaiSP loai) {

        String sql =
                "UPDATE LOAISP SET TenLoai=? WHERE MaLoai=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa loại sản phẩm
    public boolean delete(String maLoai) {

        String sql =
                "DELETE FROM LOAISP WHERE MaLoai=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maLoai);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Tìm theo mã
    public LoaiSP findById(String maLoai) {

        String sql =
                "SELECT * FROM LOAISP WHERE MaLoai=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maLoai);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new LoaiSP(
                        rs.getString("MaLoai"),
                        rs.getString("TenLoai")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Kiểm tra tồn tại
    public boolean exists(String maLoai) {

        String sql =
                "SELECT * FROM LOAISP WHERE MaLoai=?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, maLoai);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}