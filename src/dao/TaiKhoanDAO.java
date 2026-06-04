package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.TaiKhoan;
import util.DBConnection;

public class TaiKhoanDAO {

    /**
     * Đăng nhập
     */
    public TaiKhoan login(String username, String password) {

        String sql = "SELECT tk.Username, tk.Password, tk.Role, tk.MaNV, nv.HoTen AS HoTenNV "
                   + "FROM TAIKHOAN tk "
                   + "LEFT JOIN NHANVIEN nv ON tk.MaNV = nv.MaNV "
                   + "WHERE tk.Username = ? AND tk.Password = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Kiểm tra Username tồn tại
     */
    public boolean exists(String username) {

        String sql = "SELECT * FROM TAIKHOAN WHERE Username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<TaiKhoan> getAll() {
        ArrayList<TaiKhoan> list = new ArrayList<>();
        String sql =
            "SELECT tk.Username, tk.Password, tk.Role, tk.MaNV, nv.HoTen AS HoTenNV "
          + "FROM TAIKHOAN tk "
          + "LEFT JOIN NHANVIEN nv ON tk.MaNV = nv.MaNV "
          + "ORDER BY tk.Username";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public TaiKhoan findByUsername(String username) {
        String sql =
            "SELECT tk.Username, tk.Password, tk.Role, tk.MaNV, nv.HoTen AS HoTenNV "
          + "FROM TAIKHOAN tk "
          + "LEFT JOIN NHANVIEN nv ON tk.MaNV = nv.MaNV "
          + "WHERE tk.Username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<TaiKhoan> search(String keyword) {
        ArrayList<TaiKhoan> list = new ArrayList<>();
        String sql =
            "SELECT tk.Username, tk.Password, tk.Role, tk.MaNV, nv.HoTen AS HoTenNV "
          + "FROM TAIKHOAN tk "
          + "LEFT JOIN NHANVIEN nv ON tk.MaNV = nv.MaNV "
          + "WHERE tk.Username LIKE ? "
          + "OR tk.Role LIKE ? "
          + "OR tk.MaNV LIKE ? "
          + "OR nv.HoTen LIKE ? "
          + "ORDER BY tk.Username";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean update(TaiKhoan tk) {
        String sql =
            "UPDATE TAIKHOAN SET Password = ?, Role = ?, MaNV = ? WHERE Username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, tk.getPassword());
            ps.setString(2, tk.getRole());
            ps.setString(3, tk.getMaNV());
            ps.setString(4, tk.getUsername());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private TaiKhoan mapRow(ResultSet rs) throws Exception {
        TaiKhoan tk = new TaiKhoan();
        tk.setUsername(rs.getString("Username"));
        tk.setPassword(rs.getString("Password"));
        tk.setRole(rs.getString("Role"));
        tk.setMaNV(rs.getString("MaNV"));
        tk.setHoTenNV(rs.getString("HoTenNV"));
        return tk;
    }

    /**
     * Thêm tài khoản
     */
    public boolean insert(TaiKhoan tk) {

        String sql =
            "INSERT INTO TAIKHOAN "
          + "(Username, Password, Role, MaNV) "
          + "VALUES (?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, tk.getUsername());
            ps.setString(2, tk.getPassword());
            ps.setString(3, tk.getRole());
            ps.setString(4, tk.getMaNV());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Đổi mật khẩu
     */
    public boolean updatePassword(String username,
                                  String newPassword) {

        String sql =
            "UPDATE TAIKHOAN "
          + "SET Password = ? "
          + "WHERE Username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Xóa tài khoản
     */
    public boolean delete(String username) {

        String sql =
            "DELETE FROM TAIKHOAN "
          + "WHERE Username = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
