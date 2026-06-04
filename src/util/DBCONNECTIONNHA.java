package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCONNECTIONNHA {

    // 1. Đổi sang chuỗi kết nối sử dụng Windows Authentication (integratedSecurity=true)
    private static final String URL =
            "jdbc:sqlserver://LAPTOP-U6C4EJLJ;" // Đã đổi localhost thành tên Server trên ảnh của bạn
            + "databaseName=QLBanHangDienThoaiLaptop;"
            + "integratedSecurity=true;"        // Bật xác thực bằng Windows
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // 2. Không truyền USER và PASSWORD vào đây nữa
            return DriverManager.getConnection(URL);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
