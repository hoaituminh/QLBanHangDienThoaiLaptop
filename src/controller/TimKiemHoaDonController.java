package controller;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.NhanVienDAO;
import model.ChiTietHoaDon;
import model.HoaDon;
import model.KhachHang;
import model.NhanVien;
import view.JFInHoaDonPreview;
import view.JFTimKiemHoaDon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Tra cứu lịch sử hóa đơn theo mockup: tìm realtime + lọc NV + Xem & In Lại.
 */
public class TimKiemHoaDonController {

    private final JFTimKiemHoaDon view;
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final DecimalFormat vndFmt;

    public TimKiemHoaDonController(JFTimKiemHoaDon view) {
        this.view = view;
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("vi-VN"));
        sym.setGroupingSeparator('.');
        vndFmt = new DecimalFormat("#,###", sym);

        loadNhanVienFilter();
        initController();
        thucHienTimKiem();
    }

    private void loadNhanVienFilter() {
        JComboBox<JFTimKiemHoaDon.ComboItem> cbo = view.getCboNhanVien();
        cbo.removeAllItems();
        cbo.addItem(new JFTimKiemHoaDon.ComboItem("ALL", "Mọi nhân viên lập"));
        for (NhanVien nv : nhanVienDAO.getAll()) {
            cbo.addItem(new JFTimKiemHoaDon.ComboItem(nv.getMaNV(), nv.getHoTen()));
        }
    }

    private void initController() {
        view.getBtnDong().addActionListener(e -> view.dispose());

        DocumentListener filterListener = new DocumentListener() {
            private void changed() { thucHienTimKiem(); }
            @Override public void insertUpdate(DocumentEvent e) { changed(); }
            @Override public void removeUpdate(DocumentEvent e) { changed(); }
            @Override public void changedUpdate(DocumentEvent e) { changed(); }
        };
        view.getTxtTimKiem().getDocument().addDocumentListener(filterListener);
        view.getCboNhanVien().addActionListener(e -> thucHienTimKiem());

        view.getTblHoaDon().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getTblHoaDon().rowAtPoint(e.getPoint());
                int col = view.getTblHoaDon().columnAtPoint(e.getPoint());
                if (row < 0) return;
                if (col == JFTimKiemHoaDon.COL_ACTION
                        || col == JFTimKiemHoaDon.COL_MA_HD) {
                    String maHD = (String) view.getModelHD().getValueAt(row, JFTimKiemHoaDon.COL_MA_HD);
                    moXemVaIn(maHD);
                }
            }
        });
    }

    private void thucHienTimKiem() {
        String keyword = view.getTxtTimKiem().getText().trim();
        JFTimKiemHoaDon.ComboItem nvItem =
            (JFTimKiemHoaDon.ComboItem) view.getCboNhanVien().getSelectedItem();
        String maNV = nvItem != null ? nvItem.value : "ALL";

        ArrayList<HoaDon> list = hoaDonDAO.timKiemLichSu(keyword, maNV);
        view.getModelHD().setRowCount(0);
        for (HoaDon hd : list) {
            String ngay = hd.getNgayLap() != null ? sdfDisplay.format(hd.getNgayLap()) : "---";
            String nvText = hd.getTenNV() + " (" + hd.getMaNV() + ")";
            view.getModelHD().addRow(new Object[]{
                hd.getMaHD(),
                ngay,
                hd.getTenKH(),
                nvText,
                vndFmt.format(hd.getTongTien()) + " đ",
                "Xem & In Lại"
            });
        }
        view.getLblEmpty().setVisible(list.isEmpty());
        view.getTblHoaDon().setVisible(!list.isEmpty());
    }

    private void moXemVaIn(String maHD) {
        HoaDon hd = null;
        for (int i = 0; i < view.getModelHD().getRowCount(); i++) {
            if (maHD.equals(view.getModelHD().getValueAt(i, JFTimKiemHoaDon.COL_MA_HD))) {
                String ngay = (String) view.getModelHD().getValueAt(i, JFTimKiemHoaDon.COL_NGAY);
                String khach = (String) view.getModelHD().getValueAt(i, JFTimKiemHoaDon.COL_KH);
                String nv = (String) view.getModelHD().getValueAt(i, JFTimKiemHoaDon.COL_NV);
                String tong = (String) view.getModelHD().getValueAt(i, JFTimKiemHoaDon.COL_TONG);
                hd = new HoaDon();
                hd.setMaHD(maHD);
                hd.setTenKH(khach);
                hd.setTenNV(nv);
                hd.setTongTien(parseTong(tong));
                break;
            }
        }
        if (hd == null) return;

        ArrayList<HoaDon> fromDb = hoaDonDAO.timKiemLichSu(maHD, "ALL");
        if (!fromDb.isEmpty()) {
            hd = fromDb.get(0);
        }

        KhachHang kh = hd.getMaKH() != null ? khachHangDAO.findById(hd.getMaKH()) : null;
        String sdt = kh != null && kh.getSdt() != null ? kh.getSdt() : "---";
        String diaChi = kh != null && kh.getDiaChi() != null ? kh.getDiaChi() : "---";
        String ngay = hd.getNgayLap() != null ? sdfDisplay.format(hd.getNgayLap()) : "---";

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(view);
        JFInHoaDonPreview preview = new JFInHoaDonPreview(owner);
        preview.setHeader(
            hd.getMaHD(), ngay, hd.getTenKH(), sdt, diaChi,
            hd.getTenNV(), vndFmt.format(hd.getTongTien()) + " đ");

        ArrayList<ChiTietHoaDon> listCT = hoaDonDAO.getChiTietByMaHD(maHD);
        preview.getModelCT().setRowCount(0);
        for (ChiTietHoaDon ct : listCT) {
            preview.getModelCT().addRow(new Object[]{
                ct.getTenSP(),
                ct.getSoLuong(),
                vndFmt.format(ct.getDonGia()),
                vndFmt.format(ct.getThanhTien())
            });
        }

        final HoaDon finalHd = hd;
        final String finalNgay = ngay;
        final String finalSdt = sdt;
        final String finalDiaChi = diaChi;

        preview.getBtnDong().addActionListener(e -> preview.dispose());
        preview.getBtnInPdf().addActionListener(e -> {
            xuatPdf(finalHd, finalNgay, finalSdt, finalDiaChi, listCT);
            preview.dispose();
        });
        preview.setVisible(true);
    }

    private double parseTong(String s) {
        try {
            return Double.parseDouble(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void xuatPdf(HoaDon hd, String ngayLap, String sdt, String diaChi,
                         ArrayList<ChiTietHoaDon> listCT) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu Hóa Đơn PDF");
        fc.setSelectedFile(new File("HoaDon_" + hd.getMaHD() + ".pdf"));
        if (fc.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) return;

        String filePath = fc.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".pdf")) filePath += ".pdf";

        try {
            String tongStr = vndFmt.format(hd.getTongTien());
            xuatPDF(filePath, hd.getMaHD(), ngayLap, hd.getTenKH(), sdt, diaChi,
                hd.getTenNV(), tongStr, listCT);
            JOptionPane.showMessageDialog(view,
                "Xuất hóa đơn PDF thành công!\nĐường dẫn: " + filePath,
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }
            } catch (Exception ignored) { }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                "Lỗi khi xuất PDF:\n" + ex.getMessage(),
                "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuatPDF(String filePath, String maHD, String ngayLap,
                         String tenKH, String sdt, String diaChi, String tenNV,
                         String tongTien, ArrayList<ChiTietHoaDon> listCT) throws Exception {

        Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();

        String fontPath = "C:/Windows/Fonts/arial.ttf";
        if (!new File(fontPath).exists()) fontPath = "resources/fonts/arial.ttf";
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        com.itextpdf.text.Font fTitle = new com.itextpdf.text.Font(bf, 18, com.itextpdf.text.Font.BOLD, new BaseColor(15, 23, 42));
        com.itextpdf.text.Font fSub = new com.itextpdf.text.Font(bf, 11, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY);
        com.itextpdf.text.Font fSection = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD, new BaseColor(67, 56, 202));
        com.itextpdf.text.Font fBold = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD, new BaseColor(30, 41, 59));
        com.itextpdf.text.Font fNormal = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.NORMAL, new BaseColor(51, 65, 85));
        com.itextpdf.text.Font fFooter = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY);

        addCenter(doc, "TECHSTORE - THẾ GIỚI CÔNG NGHỆ", fTitle);
        addCenter(doc, "Điện thoại & Laptop chính hãng", fSub);
        doc.add(new Paragraph(" "));
        addCenter(doc, "HÓA ĐƠN BÁN HÀNG", new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD));
        doc.add(new Paragraph(" "));

        PdfPTable infoTbl = new PdfPTable(2);
        infoTbl.setWidthPercentage(100);
        PdfPCell cLeft = noBorderCell();
        cLeft.addElement(new Paragraph("THÔNG TIN GIAO DỊCH", fSection));
        cLeft.addElement(new Paragraph("Mã hóa đơn : " + maHD, fBold));
        cLeft.addElement(new Paragraph("Ngày lập   : " + ngayLap, fNormal));
        cLeft.addElement(new Paragraph("Nhân viên  : " + tenNV, fNormal));
        PdfPCell cRight = noBorderCell();
        cRight.addElement(new Paragraph("THÔNG TIN KHÁCH HÀNG", fSection));
        cRight.addElement(new Paragraph("Khách hàng : " + tenKH, fBold));
        cRight.addElement(new Paragraph("SĐT        : " + sdt, fNormal));
        cRight.addElement(new Paragraph("Địa chỉ    : " + diaChi, fNormal));
        infoTbl.addCell(cLeft);
        infoTbl.addCell(cRight);
        doc.add(infoTbl);
        doc.add(new Paragraph(" "));

        PdfPTable prodTbl = new PdfPTable(4);
        prodTbl.setWidthPercentage(100);
        prodTbl.setWidths(new float[]{4f, 1f, 2f, 2f});
        BaseColor hdrBg = new BaseColor(241, 245, 249);
        BaseColor bdColor = new BaseColor(226, 232, 240);
        for (String h : new String[]{"Tên SP", "SL", "Đơn Giá", "Thành Tiền"}) {
            PdfPCell hc = new PdfPCell(new Phrase(h, fBold));
            hc.setPadding(8);
            hc.setBackgroundColor(hdrBg);
            hc.setBorderColor(bdColor);
            prodTbl.addCell(hc);
        }
        for (ChiTietHoaDon ct : listCT) {
            prodTbl.addCell(dataCell(ct.getTenSP(), fNormal, Element.ALIGN_LEFT, bdColor));
            prodTbl.addCell(dataCell(String.valueOf(ct.getSoLuong()), fNormal, Element.ALIGN_CENTER, bdColor));
            prodTbl.addCell(dataCell(vndFmt.format(ct.getDonGia()), fNormal, Element.ALIGN_RIGHT, bdColor));
            prodTbl.addCell(dataCell(vndFmt.format(ct.getThanhTien()), fNormal, Element.ALIGN_RIGHT, bdColor));
        }
        doc.add(prodTbl);
        doc.add(new Paragraph(" "));

        Paragraph pTong = new Paragraph("TỔNG TIỀN: " + tongTien + " VNĐ",
            new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD, new BaseColor(79, 70, 229)));
        pTong.setAlignment(Element.ALIGN_RIGHT);
        doc.add(pTong);
        doc.add(new Paragraph(" "));
        addCenter(doc, "Cảm ơn Quý khách đã mua sắm tại TechStore!", fFooter);
        doc.close();
    }

    private void addCenter(Document doc, String text, com.itextpdf.text.Font font) throws Exception {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private PdfPCell noBorderCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(PdfPCell.NO_BORDER);
        return c;
    }

    private PdfPCell dataCell(String text, com.itextpdf.text.Font font, int align, BaseColor border) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(6);
        c.setHorizontalAlignment(align);
        c.setBorderColor(border);
        return c;
    }
}
