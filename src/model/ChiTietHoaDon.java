package model;

public class ChiTietHoaDon {
    private int    maCTHD;
    private String maHD;
    private String maSP;
    private String tenSP;   // Nhận từ JOIN với bảng SANPHAM
    private int    soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietHoaDon() {}

    public int    getMaCTHD()            { return maCTHD; }
    public void   setMaCTHD(int v)       { this.maCTHD = v; }

    public String getMaHD()              { return maHD; }
    public void   setMaHD(String v)      { this.maHD = v; }

    public String getMaSP()              { return maSP; }
    public void   setMaSP(String v)      { this.maSP = v; }

    public String getTenSP()             { return tenSP != null ? tenSP : maSP; }
    public void   setTenSP(String v)     { this.tenSP = v; }

    public int    getSoLuong()           { return soLuong; }
    public void   setSoLuong(int v)      { this.soLuong = v; }

    public double getDonGia()            { return donGia; }
    public void   setDonGia(double v)    { this.donGia = v; }

    public double getThanhTien()         { return thanhTien; }
    public void   setThanhTien(double v) { this.thanhTien = v; }
}