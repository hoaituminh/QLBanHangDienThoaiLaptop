package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    // Sửa lại URL để dùng tài khoản sa và mật khẩu mới
    private static final String URL =
            //"jdbc:sqlserver://LAPTOP-U6C4EJLJ;"
              "jdbc:sqlserver://localhost:1433;"
            + "databaseName=QLBanHangDienThoaiLaptop;"
            + "user=sa;"                             // Thêm user sa
            + "password= 12345;"         // Nhập mật khẩu của bạn vào đây
            + "encrypt=true;"
            + "trustServerCertificate=true;";        // Xóa integratedSecurity=true đi

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            return DriverManager.getConnection(URL);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // Đoạn code test nhanh xem kết nối có thành công hay không
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("[OK] Ket noi thanh cong den SQL Server bang tai khoan SA!");
        } else {
            System.out.println("[LOI] Ket noi that bai. Vui long kiem tra lai mat khau hoac dich vu SQL Server.");
        }
    }
}