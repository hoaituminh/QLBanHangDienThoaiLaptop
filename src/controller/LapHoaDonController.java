package controller;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.LoaiSPDAO;
import dao.SanPhamDAO;
import model.ChiTietHoaDon;
import model.HoaDon;
import model.KhachHang;
import model.SanPham;
import util.UserSession;
import view.JFInHoaDonPreview;
import view.JFGioHangSanPham;
import view.JFLapHoaDon;
import view.JFTimKiemHoaDon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// --- Imports từ thư viện iText PDF ---
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class LapHoaDonController {

    private JFLapHoaDon view;
    private HoaDonDAO hoaDonDAO;
    private KhachHangDAO khachHangDAO;
    private LoaiSPDAO loaiSPDAO;
    private SanPhamDAO sanPhamDAO;
    private double currentDonGia = 0;

    public LapHoaDonController(JFLapHoaDon view) {
        this.view = view;
        this.hoaDonDAO = new HoaDonDAO();
        this.khachHangDAO = new KhachHangDAO();
        this.loaiSPDAO = new LoaiSPDAO();
        this.sanPhamDAO = new SanPhamDAO();
        initData();
        initController();
    }

    private void initData() {
        for (KhachHang kh : khachHangDAO.getAll()) {
            view.getCboKhachHang().addItem(kh);
        }
        for (SanPham sp : sanPhamDAO.getAll()) {
            view.getCboSanPham().addItem(sp);
        }
        taoMoiHoaDon();
        applyNhanVienDangNhap();
        if (view.getCboSanPham().getItemCount() > 0) {
            view.getCboSanPham().setSelectedIndex(0);
        }
        view.updateEmptyCartState();
        view.updateCartCount(0);
    }

    private void applyNhanVienDangNhap() {
        String nv = UserSession.getNhanVienDisplay();
        if (!nv.isEmpty()) {
            view.getTxtNhanVien().setText(nv);
        }
    }

    public void loadData() {
        KhachHang selectedKH = (KhachHang) view.getCboKhachHang().getSelectedItem();
        SanPham selectedSP = (SanPham) view.getCboSanPham().getSelectedItem();

        view.getCboKhachHang().removeAllItems();
        for (KhachHang kh : khachHangDAO.getAll()) {
            view.getCboKhachHang().addItem(kh);
        }

        view.getCboSanPham().removeAllItems();
        for (SanPham sp : sanPhamDAO.getAll()) {
            view.getCboSanPham().addItem(sp);
        }

        if (selectedKH != null) {
            for (int i = 0; i < view.getCboKhachHang().getItemCount(); i++) {
                KhachHang kh = view.getCboKhachHang().getItemAt(i);
                if (kh.getMaKH().equals(selectedKH.getMaKH())) {
                    view.getCboKhachHang().setSelectedIndex(i);
                    break;
                }
            }
        }

        if (selectedSP != null) {
            for (int i = 0; i < view.getCboSanPham().getItemCount(); i++) {
                SanPham sp = view.getCboSanPham().getItemAt(i);
                if (sp.getMaSP().equals(selectedSP.getMaSP())) {
                    view.getCboSanPham().setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void initController() {
        // ==========================================
        // 1. SỰ KIỆN NÚT [+] TĂNG SỐ LƯỢNG
        // ==========================================
        view.getBtnTangSL().addActionListener(e -> {
            try {
                int currentSL = Integer.parseInt(view.getTxtSoLuong().getText().trim());
                view.getTxtSoLuong().setText(String.valueOf(currentSL + 1));
            } catch (NumberFormatException ex) {
                view.getTxtSoLuong().setText("1");
            }
        });

        // ==========================================
        // 2. SỰ KIỆN NÚT [-] GIẢM SỐ LƯỢNG
        // ==========================================
        view.getBtnGiamSL().addActionListener(e -> {
            try {
                int currentSL = Integer.parseInt(view.getTxtSoLuong().getText().trim());
                if (currentSL > 1) {
                    view.getTxtSoLuong().setText(String.valueOf(currentSL - 1));
                }
            } catch (NumberFormatException ex) {
                view.getTxtSoLuong().setText("1");
            }
        });

        view.getCboSanPham().addActionListener(e -> {
            SanPham sp = (SanPham) view.getCboSanPham().getSelectedItem();
            if (sp != null) {
                currentDonGia = sp.getDonGia();
                view.getLblPreviewDonGia().setText(view.formatVND(currentDonGia));
                tinhThanhTien();
            }
        });

        view.getTxtSoLuong().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhThanhTien(); }
            public void removeUpdate(DocumentEvent e) { tinhThanhTien(); }
            public void changedUpdate(DocumentEvent e) { tinhThanhTien(); }
        });

        view.getBtnThemSP().addActionListener(e -> themSanPhamVaoGio());
        view.getBtnGioHang().addActionListener(e -> moGioHangSanPham());
        view.getBtnSuaSL().addActionListener(e -> suaSoLuong());
        view.getBtnXoaSP().addActionListener(e -> xoaSanPhamKhoiGio());
        view.getBtnTaoMoi().addActionListener(e -> taoMoiHoaDon());
        view.getBtnLuuHD().addActionListener(e -> luuHoaDon());
        view.getBtnHuy().addActionListener(e -> huyHoaDon());
        view.getBtnTimKiemHD().addActionListener(e -> moManHinhTimKiem());
        
        view.getBtnInHD().addActionListener(e -> inHoaDonPDF());

        view.getTblChiTiet().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblChiTiet().rowAtPoint(e.getPoint());
                int col = view.getTblChiTiet().columnAtPoint(e.getPoint());
                if (row < 0 || col != 5) {
                    return;
                }
                view.getTblChiTiet().setRowSelectionInterval(row, row);
                java.awt.Rectangle cell = view.getTblChiTiet().getCellRect(row, col, false);
                int midX = cell.x + cell.width / 2;
                if (e.getX() < midX) {
                    suaSoLuong();
                } else {
                    xoaSanPhamKhoiGio();
                }
            }
        });
    }

    private void tinhThanhTien() {
        try {
            int soLuong = Integer.parseInt(view.getTxtSoLuong().getText().trim());
            view.getLblPreviewThanhTien().setText(view.formatVND(currentDonGia * soLuong));
        } catch (NumberFormatException ex) {
            view.getLblPreviewThanhTien().setText(view.formatVND(0));
        }
    }

    private void suaSoLuong() {
        int row = view.getTblChiTiet().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn sản phẩm trong bảng cần sửa số lượng!",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int soLuongCu = (int) view.getModel().getValueAt(row, 3);
        String textTenSP = (String) view.getModel().getValueAt(row, 1);

        String input = (String) JOptionPane.showInputDialog(
                view,
                "Sản phẩm: " + textTenSP + "\nNhập số lượng mới:",
                "Sửa Số Lượng",
                JOptionPane.PLAIN_MESSAGE,
                null, null,
                soLuongCu
        );

        if (input == null) {
            return;
        }

        try {
            int soLuongMoi = Integer.parseInt(input.trim());
            if (soLuongMoi <= 0) {
                throw new NumberFormatException();
            }

            double donGia = parseMoney((String) view.getModel().getValueAt(row, 2));
            double thanhTienMoi = soLuongMoi * donGia;

            view.getModel().setValueAt(soLuongMoi, row, 3);
            view.getModel().setValueAt(view.formatVND(thanhTienMoi), row, 4);
            view.getModel().setValueAt("", row, 5);
            tinhTongTienHoaDon();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view,
                    "Số lượng không hợp lệ! Phải là số nguyên lớn hơn 0.",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void huyHoaDon() {
        if (view.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view,
                    "Giỏ hàng đang trống, không có gì để hủy.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc muốn hủy hóa đơn đang lập?\n"
                        + "Toàn bộ sản phẩm trong giỏ sẽ bị xóa sạch.",
                "Xác Nhận Hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            view.getModel().setRowCount(0);
            tinhTongTienHoaDon();
        }
    }

    private void moManHinhTimKiem() {
        JFTimKiemHoaDon dialog = new JFTimKiemHoaDon(view);
        new TimKiemHoaDonController(dialog);
        dialog.setVisible(true);
    }

    private void moGioHangSanPham() {
        JFGioHangSanPham dialog = new JFGioHangSanPham(
                view,
                sanPhamDAO.getAll(),
                loaiSPDAO.getAll(),
                (sp, quantity) -> themSanPhamVaoGio(sp, quantity)
        );
        dialog.setVisible(true);
    }

    private void themSanPhamVaoGio() {
        SanPham sp = (SanPham) view.getCboSanPham().getSelectedItem();
        if (sp == null) {
            return;
        }
        try {
            int soLuong = Integer.parseInt(view.getTxtSoLuong().getText().trim());
            if (soLuong <= 0) {
                throw new NumberFormatException();
            }
            themSanPhamVaoGio(sp, soLuong);
            view.getTxtSoLuong().setText("1");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view,
                    "Số lượng phải là số nguyên lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themSanPhamVaoGio(SanPham sp, int soLuong) {
        if (sp == null || soLuong <= 0) {
            return;
        }

        boolean daTonTai = false;
        for (int i = 0; i < view.getModel().getRowCount(); i++) {
            if (((String) view.getModel().getValueAt(i, 0)).equals(sp.getMaSP())) {
                int sl = (int) view.getModel().getValueAt(i, 3) + soLuong;
                double tt = sl * sp.getDonGia();
                view.getModel().setValueAt(sl, i, 3);
                view.getModel().setValueAt(view.formatVND(tt), i, 4);
                daTonTai = true;
                break;
            }
        }

        if (!daTonTai) {
            view.getModel().addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    view.formatVND(sp.getDonGia()),
                    soLuong,
                    view.formatVND(sp.getDonGia() * soLuong),
                    ""
            });
        }
        tinhTongTienHoaDon();
    }

    private void xoaSanPhamKhoiGio() {
        int row = view.getTblChiTiet().getSelectedRow();
        if (row >= 0) {
            view.getModel().removeRow(row);
            tinhTongTienHoaDon();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng chọn sản phẩm trong bảng để xóa!");
        }
    }

    private void tinhTongTienHoaDon() {
        double tong = 0;
        int count = 0;
        for (int i = 0; i < view.getModel().getRowCount(); i++) {
            tong += parseMoney((String) view.getModel().getValueAt(i, 4));
            count += (int) view.getModel().getValueAt(i, 3);
        }
        view.getLblTongTien().setText(view.formatVND(tong).replace(" đ", " VNĐ"));
        view.updateCartCount(count);
        view.updateEmptyCartState();
    }

    private double parseMoney(String text) {
        if (text == null) {
            return 0;
        }
        return Double.parseDouble(text.replace(".", "").replace(",", "").replace(" đ", "").replace(" VNĐ", "").trim());
    }

    private void taoMoiHoaDon() {
        view.getTxtMaHD().setText(hoaDonDAO.sinhMaHD());
        view.getModel().setRowCount(0);
        applyNhanVienDangNhap();
        tinhTongTienHoaDon();
    }

    private void luuHoaDon() {
        if (view.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhachHang kh = (KhachHang) view.getCboKhachHang().getSelectedItem();
        if (kh == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            HoaDon hd = new HoaDon();
            hd.setMaHD(view.getTxtMaHD().getText());
            hd.setMaKH(kh.getMaKH());
            String maNV = UserSession.getMaNV();
            if (maNV.isEmpty()) {
                String txt = view.getTxtNhanVien().getText().trim();
                maNV = txt.contains(" - ") ? txt.split(" - ")[0].trim() : txt;
            }
            hd.setMaNV(maNV);

            double tong = 0;
            ArrayList<ChiTietHoaDon> list = new ArrayList<>();
            for (int i = 0; i < view.getModel().getRowCount(); i++) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHD(hd.getMaHD());
                ct.setMaSP((String) view.getModel().getValueAt(i, 0));
                ct.setDonGia(parseMoney((String) view.getModel().getValueAt(i, 2)));
                ct.setSoLuong((int) view.getModel().getValueAt(i, 3));
                double tt = parseMoney((String) view.getModel().getValueAt(i, 4));
                ct.setThanhTien(tt);
                tong += tt;
                list.add(ct);
            }
            hd.setTongTien(tong);

            if (hoaDonDAO.luuHoaDonTransaction(hd, list)) {
                String sdt     = (kh.getSdt()    != null && !kh.getSdt().trim().isEmpty())
                                 ? kh.getSdt()    : "---";
                String diaChi  = (kh.getDiaChi() != null && !kh.getDiaChi().trim().isEmpty())
                                 ? kh.getDiaChi() : "---";
                String ngayLap  = view.getTxtNgayLap().getText();
                String tenNV    = view.getTxtNhanVien().getText();
                String tongTienStr = view.getLblTongTien().getText();

                JFInHoaDonPreview preview = new JFInHoaDonPreview(view);
                preview.setHeader(hd.getMaHD(), ngayLap, kh.getHoTen(),
                                  sdt, diaChi, tenNV, tongTienStr);

                for (int i = 0; i < view.getModel().getRowCount(); i++) {
                    preview.getModelCT().addRow(new Object[]{
                        view.getModel().getValueAt(i, 1),   // Tên SP
                        view.getModel().getValueAt(i, 3),   // Số lượng
                        view.getModel().getValueAt(i, 2),   // Đơn giá
                        view.getModel().getValueAt(i, 4)    // Thành tiền
                    });
                }

                preview.getBtnDong().addActionListener(e -> preview.dispose());
                preview.getBtnInPdf().addActionListener(e -> {
                    preview.dispose();   // đóng preview trước
                    inHoaDonPDF();       // rồi mở hộp thoại xuất PDF
                });

                preview.setVisible(true);
                taoMoiHoaDon();
            } else {
                JOptionPane.showMessageDialog(view, "Lưu thất bại! Đã Rollback.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inHoaDonPDF() {
        if (view.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, 
                    "Hóa đơn chưa có sản phẩm nào! Vui lòng thêm sản phẩm trước khi in.", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang kh = (KhachHang) view.getCboKhachHang().getSelectedItem();
        if (kh == null) {
            JOptionPane.showMessageDialog(view, 
                    "Vui lòng chọn thông tin khách hàng trước khi xuất hóa đơn!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu Hóa Đơn PDF");
        
        String suggestedFileName = "HoaDon_" + view.getTxtMaHD().getText() + ".pdf";
        fileChooser.setSelectedFile(new File(suggestedFileName));

        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                Document document = new Document(PageSize.A4, 36, 36, 36, 36);
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                String fontPath = "C:/Windows/Fonts/arial.ttf";
                File fontFile = new File(fontPath);
                
                if (!fontFile.exists()) {
                    fontPath = "resources/fonts/arial.ttf"; 
                }

                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                
                Font fontTitleMain = new Font(bf, 18, Font.BOLD, new BaseColor(15, 23, 42)); 
                Font fontSubHeader = new Font(bf, 11, Font.ITALIC, BaseColor.GRAY);
                Font fontSection = new Font(bf, 12, Font.BOLD, new BaseColor(67, 56, 202));
                Font fontNormalBold = new Font(bf, 10, Font.BOLD, new BaseColor(30, 41, 59));
                Font fontNormal = new Font(bf, 10, Font.NORMAL, new BaseColor(51, 65, 85));
                Font fontFooterLabel = new Font(bf, 10, Font.ITALIC, BaseColor.GRAY);

                Paragraph pBrand = new Paragraph("TECHSTORE - THẾ GIỚI CÔNG NGHỆ", fontTitleMain);
                pBrand.setAlignment(Element.ALIGN_CENTER);
                document.add(pBrand);

                Paragraph pSubBrand = new Paragraph("Điện thoại & Laptop chính hãng - Hotline: 1900.xxxx", fontSubHeader);
                pSubBrand.setAlignment(Element.ALIGN_CENTER);
                document.add(pSubBrand);
                document.add(new Paragraph("---------------------------------------------------------------------------------------------------------------------------------", fontFooterLabel));
                document.add(new Paragraph(" "));

                Paragraph pTitle = new Paragraph("HÓA ĐƠN BÁN HÀNG", new Font(bf, 16, Font.BOLD, new BaseColor(15, 23, 42)));
                pTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(pTitle);
                document.add(new Paragraph(" "));

                PdfPTable infoTable = new PdfPTable(2);
                infoTable.setWidthPercentage(100);
                infoTable.setWidths(new float[]{1f, 1f});

                PdfPCell leftCell = new PdfPCell();
                leftCell.setBorder(PdfPCell.NO_BORDER);
                leftCell.addElement(new Paragraph("THÔNG TIN GIAO DỊCH", fontSection));
                leftCell.addElement(new Paragraph("Mã hóa đơn: " + view.getTxtMaHD().getText(), fontNormalBold));
                leftCell.addElement(new Paragraph("Ngày lập: " + view.getTxtNgayLap().getText(), fontNormal));
                leftCell.addElement(new Paragraph("Nhân viên: " + view.getTxtNhanVien().getText(), fontNormal));

                PdfPCell rightCell = new PdfPCell();
                rightCell.setBorder(PdfPCell.NO_BORDER);
                rightCell.addElement(new Paragraph("THÔNG TIN KHÁCH HÀNG", fontSection));
                rightCell.addElement(new Paragraph("Khách hàng: " + kh.getHoTen(), fontNormalBold));
                rightCell.addElement(new Paragraph("Mã đối tác: " + kh.getMaKH(), fontNormal));
                rightCell.addElement(new Paragraph("Số điện thoại: " + (kh.getSdt() != null && !kh.getSdt().trim().isEmpty() ? kh.getSdt() : "Chưa cập nhật"), fontNormal));

                infoTable.addCell(leftCell);
                infoTable.addCell(rightCell);
                document.add(infoTable);
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable productTable = new PdfPTable(5);
                productTable.setWidthPercentage(100);
                productTable.setWidths(new float[]{1.2f, 3.8f, 1.8f, 1f, 2.2f}); 

                String[] tblHeaders = {"Mã SP", "Tên Sản Phẩm", "Đơn Giá", "SL", "Thành Tiền"};
                BaseColor headerBg = new BaseColor(241, 245, 249); 
                
                for (String headerName : tblHeaders) {
                    PdfPCell hCell = new PdfPCell(new Phrase(headerName, fontNormalBold));
                    hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    hCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    hCell.setPadding(8);
                    hCell.setBackgroundColor(headerBg);
                    hCell.setBorderColor(new BaseColor(226, 232, 240));
                    productTable.addCell(hCell);
                }

                for (int i = 0; i < view.getModel().getRowCount(); i++) {
                    String maSP = (String) view.getModel().getValueAt(i, 0);
                    String tenSP = (String) view.getModel().getValueAt(i, 1);
                    String donGia = (String) view.getModel().getValueAt(i, 2);
                    String soLuong = String.valueOf(view.getModel().getValueAt(i, 3));
                    String thanhTien = (String) view.getModel().getValueAt(i, 4);

                    PdfPCell cellMa = new PdfPCell(new Phrase(maSP, fontNormal));
                    cellMa.setPadding(6);
                    cellMa.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellMa.setBorderColor(new BaseColor(241, 245, 249));
                    productTable.addCell(cellMa);

                    PdfPCell cellTen = new PdfPCell(new Phrase(tenSP, fontNormal));
                    cellTen.setPadding(6);
                    cellTen.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellTen.setBorderColor(new BaseColor(241, 245, 249));
                    productTable.addCell(cellTen);

                    PdfPCell cellGia = new PdfPCell(new Phrase(donGia, fontNormal));
                    cellGia.setPadding(6);
                    cellGia.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cellGia.setBorderColor(new BaseColor(241, 245, 249));
                    productTable.addCell(cellGia);

                    PdfPCell cellSL = new PdfPCell(new Phrase(soLuong, fontNormal));
                    cellSL.setPadding(6);
                    cellSL.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellSL.setBorderColor(new BaseColor(241, 245, 249));
                    productTable.addCell(cellSL);

                    PdfPCell cellThanhTien = new PdfPCell(new Phrase(thanhTien, fontNormal));
                    cellThanhTien.setPadding(6);
                    cellThanhTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cellThanhTien.setBorderColor(new BaseColor(241, 245, 249));
                    productTable.addCell(cellThanhTien);
                }

                document.add(productTable);
                document.add(new Paragraph(" "));

                PdfPTable totalTable = new PdfPTable(2);
                totalTable.setWidthPercentage(100);
                totalTable.setWidths(new float[]{6f, 4f});

                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(PdfPCell.NO_BORDER);
                totalTable.addCell(emptyCell);

                PdfPCell totalContentCell = new PdfPCell();
                totalContentCell.setBorder(PdfPCell.NO_BORDER);
                
                Paragraph pTong = new Paragraph("TỔNG TIỀN THANH TOÁN:  " + view.getLblTongTien().getText(), 
                        new Font(bf, 12, Font.BOLD, new BaseColor(249, 115, 22)));
                pTong.setAlignment(Element.ALIGN_RIGHT);
                totalContentCell.addElement(pTong);

                totalTable.addCell(totalContentCell);
                document.add(totalTable);
                
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable signTable = new PdfPTable(2);
                signTable.setWidthPercentage(100);
                signTable.setWidths(new float[]{1f, 1f});

                PdfPCell signLeft = new PdfPCell();
                signLeft.setBorder(PdfPCell.NO_BORDER);
                Paragraph pKHSign = new Paragraph("Chữ ký Khách Hàng\n(Ký và ghi rõ họ tên)", fontNormalBold);
                pKHSign.setAlignment(Element.ALIGN_CENTER);
                signLeft.addElement(pKHSign);

                PdfPCell signRight = new PdfPCell();
                signRight.setBorder(PdfPCell.NO_BORDER);
                Paragraph pNVSign = new Paragraph("Người lập hóa đơn\n(Ký và ghi rõ họ tên)", fontNormalBold);
                pNVSign.setAlignment(Element.ALIGN_CENTER);
                signRight.addElement(pNVSign);

                signTable.addCell(signLeft);
                signTable.addCell(signRight);
                document.add(signTable);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                Paragraph pThanks = new Paragraph("Cảm ơn Quý khách đã mua sắm tại TechStore! Hẹn gặp lại quý khách lần sau.", fontFooterLabel);
                pThanks.setAlignment(Element.ALIGN_CENTER);
                document.add(pThanks);

                document.close();
                
                JOptionPane.showMessageDialog(view, 
                        "Xuất hóa đơn PDF thành công!\nĐường dẫn lưu trữ: " + filePath, 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                try {
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().open(new File(filePath));
                    }
                } catch (Exception e) {
                    System.out.println("Cảnh báo: Không thể mở tự động file vì hệ thống chưa cấu hình ứng dụng đọc PDF.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, 
                        "Có lỗi xảy ra khi biên soạn hóa đơn PDF:\n" + ex.getMessage(), 
                        "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
