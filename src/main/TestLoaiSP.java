package main;

import dao.LoaiSPDAO;
import model.LoaiSP;

public class TestLoaiSP {

    public static void main(String[] args) {

        LoaiSPDAO dao = new LoaiSPDAO();

        for (LoaiSP loai : dao.getAll()) {

            System.out.println(
                    loai.getMaLoai()
                    + " - "
                    + loai.getTenLoai()
            );
        }
    }
}