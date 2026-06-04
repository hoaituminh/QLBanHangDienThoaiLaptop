package model;

import java.util.Date;

public class HoaDon {
    private String maHD;
    private Date ngayLap;
    private String maKH;
    private String maNV;
    private double tongTien;

    // Thêm 2 field này để nhận tên từ câu truy vấn JOIN
    private String tenKH;
    private String tenNV;

    public HoaDon() {}

    public HoaDon(String maHD, Date ngayLap, String maKH, String maNV, double tongTien) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.tongTien = tongTien;
    }

    public String getMaHD()          { return maHD; }
    public void   setMaHD(String v)  { this.maHD = v; }

    public Date   getNgayLap()       { return ngayLap; }
    public void   setNgayLap(Date v) { this.ngayLap = v; }

    public String getMaKH()          { return maKH; }
    public void   setMaKH(String v)  { this.maKH = v; }

    public String getMaNV()          { return maNV; }
    public void   setMaNV(String v)  { this.maNV = v; }

    public double getTongTien()        { return tongTien; }
    public void   setTongTien(double v){ this.tongTien = v; }

    // Getter/Setter cho tenKH, tenNV (phục vụ hiển thị kết quả tìm kiếm)
    public String getTenKH()          { return tenKH != null ? tenKH : maKH; }
    public void   setTenKH(String v)  { this.tenKH = v; }

    public String getTenNV()          { return tenNV != null ? tenNV : maNV; }
    public void   setTenNV(String v)  { this.tenNV = v; }

    @Override
    public String toString() { return maHD; }
}