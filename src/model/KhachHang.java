package model;

public class KhachHang {

    private String maKH;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private String email;

    public KhachHang() {
    }

    public KhachHang(String maKH, String hoTen, String sdt,
            String diaChi, String email) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.email = email;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}