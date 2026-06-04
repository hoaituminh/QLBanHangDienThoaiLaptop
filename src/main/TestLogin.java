package main;

import dao.TaiKhoanDAO;
import model.TaiKhoan;

public class TestLogin {

    public static void main(String[] args) {

        TaiKhoanDAO dao = new TaiKhoanDAO();

        TaiKhoan tk =
                dao.login("admin", "123456");

        if (tk != null) {

            System.out.println("Đăng nhập thành công");

            System.out.println("Username: "
                    + tk.getUsername());

            System.out.println("Role: "
                    + tk.getRole());

            System.out.println("MaNV: "
                    + tk.getMaNV());

        } else {

            System.out.println("Sai tài khoản hoặc mật khẩu");
        }
    }
}