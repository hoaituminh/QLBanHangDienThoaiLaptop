package model;

public class NhanVien {

    private String maNV;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private String email;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String sdt,
            String diaChi, String email) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.email = email;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
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