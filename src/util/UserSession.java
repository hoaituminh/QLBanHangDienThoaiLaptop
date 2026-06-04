package util;

import model.TaiKhoan;

/** Phiên đăng nhập hiện tại (dùng cho lập hóa đơn, phân quyền, v.v.). */
public final class UserSession {

    private static TaiKhoan current;

    private UserSession() {}

    public static void setCurrent(TaiKhoan tk) {
        current = tk;
    }

    public static TaiKhoan getCurrent() {
        return current;
    }

    public static void clear() {
        current = null;
    }

    public static String getUsername() {
        return current != null ? current.getUsername() : "";
    }

    public static String getMaNV() {
        return current != null && current.getMaNV() != null ? current.getMaNV() : "";
    }

    /** Định dạng hiển thị: Mã NV - Họ tên */
    public static String getNhanVienDisplay() {
        if (current == null) {
            return "";
        }
        String ma = current.getMaNV() != null ? current.getMaNV() : "";
        String ten = current.getHoTenNV();
        if (ten == null || ten.isEmpty()) {
            return ma;
        }
        return ma + " - " + ten;
    }
}
