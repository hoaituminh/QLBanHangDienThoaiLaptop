package main;

import dao.SanPhamDAO;
import model.SanPham;

public class TestSanPhamDAO {

    public static void main(String[] args) {

        SanPhamDAO dao = new SanPhamDAO();

        for (SanPham sp : dao.getAll()) {

            System.out.println(
                    sp.getMaSP() + " | "
                    + sp.getTenSP() + " | "
                    + sp.getDonGia()
            );
        }
    }
}