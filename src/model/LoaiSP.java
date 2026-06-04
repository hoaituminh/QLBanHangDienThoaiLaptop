package model;

public class LoaiSP {

    private String maLoai;
    private String tenLoai;

    public LoaiSP() {
    }

    public LoaiSP(String maLoai, String tenLoai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }
    @Override
public String toString() {
    return tenLoai;
}
}