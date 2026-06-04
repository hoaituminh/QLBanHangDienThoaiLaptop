package controller;

import dao.TaiKhoanDAO;
import model.TaiKhoan;
import util.UserSession;
import view.JFLogin;
import view.JFMenu;

import javax.swing.*;

public class LoginController {

    private JFLogin view;
    private TaiKhoanDAO dao;

    public LoginController(JFLogin view) {

        this.view = view;
        this.dao = new TaiKhoanDAO();

        initController();
    }

    private void initController() {

        view.getBtnDangNhap()
                .addActionListener(e -> login());

        view.getBtnThoat()
                .addActionListener(e -> thoat());

        view.getTxtPassword()
                .addActionListener(e -> login());
    }

    private void login() {

        String username =
                view.getTxtUsername()
                        .getText()
                        .trim();

        String password =
                String.valueOf(
                        view.getTxtPassword()
                                .getPassword());

        if(username.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Vui lòng nhập tên đăng nhập!");

            view.getTxtUsername().requestFocus();
            return;
        }

        if(password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Vui lòng nhập mật khẩu!");

            view.getTxtPassword().requestFocus();
            return;
        }

        TaiKhoan tk =
                dao.login(username, password);

        if(tk == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Sai tài khoản hoặc mật khẩu!",
                    "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        UserSession.setCurrent(tk);

        JOptionPane.showMessageDialog(
                view,
                "Đăng nhập thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);

        view.dispose();

        String hoTen = tk.getHoTenNV();
        String role  = tk.getRole();

        SwingUtilities.invokeLater(() -> {

            JFMenu menu = new JFMenu(hoTen, role);

            menu.setVisible(true);
        });
    }

    private void thoat() {

        int choice =
                JOptionPane.showConfirmDialog(
                        view,
                        "Bạn có muốn thoát chương trình?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);

        if(choice == JOptionPane.YES_OPTION) {

            System.exit(0);
        }
    }
}