package main;

import java.sql.Connection;
import util.DBConnection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();

            if (conn != null) {
                System.out.println("Kết nối SQL Server thành công!");
                conn.close();
            } else {
                System.out.println("Kết nối thất bại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}