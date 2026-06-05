package view;

import util.TechStoreUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Modal xem trước hóa đơn trước khi in PDF (mockup invoice-print-modal). */
public class JFInHoaDonPreview extends JDialog {

    private JLabel lblMaHD, lblNgay, lblKhach, lblSdt, lblDiaChi, lblNhanVien, lblTong;
    private DefaultTableModel modelCT;
    private JButton btnInPdf, btnDong;

    public JFInHoaDonPreview(Frame parent) {
        super(parent, "Xem & In Hóa Đơn", true);
        setSize(520, 620);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(15, 23, 42));
        initComponents();
    }

    private void initComponents() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(15, 23, 42, 200));
        overlay.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel card = TechStoreUI.createCard();
        card.setLayout(new BorderLayout(0, 12));

        JPanel pnlPrint = new JPanel();
        pnlPrint.setLayout(new BoxLayout(pnlPrint, BoxLayout.Y_AXIS));
        pnlPrint.setBackground(util.TechStoreUI.CARD_BG);
        pnlPrint.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TechStoreUI.BORDER, 2),
            new EmptyBorder(16, 16, 16, 16)));

        JLabel lblBrand = new JLabel("HÓA ĐƠN BÁN HÀNG TechStore", SwingConstants.CENTER);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblAddr = new JLabel("Đắk Lắk, Việt Nam - Hotline: 0911.111.111", SwingConstants.CENTER);
        lblAddr.setFont(TechStoreUI.FONT_SM);
        lblAddr.setForeground(TechStoreUI.TEXT_MUTED);
        lblAddr.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlPrint.add(lblBrand);
        pnlPrint.add(Box.createVerticalStrut(4));
        pnlPrint.add(lblAddr);
        pnlPrint.add(Box.createVerticalStrut(12));
        pnlPrint.add(new JSeparator());

        JPanel pnlInfo = new JPanel(new GridLayout(3, 2, 8, 6));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(new EmptyBorder(10, 0, 10, 0));
        lblMaHD = infoValue();
        lblNgay = infoValue();
        lblKhach = infoValue();
        lblSdt = infoValue();
        lblDiaChi = infoValue();
        lblNhanVien = infoValue();
        pnlInfo.add(infoPair("Mã hóa đơn:", lblMaHD));
        pnlInfo.add(infoPair("Ngày lập:", lblNgay));
        pnlInfo.add(infoPair("Khách hàng:", lblKhach));
        pnlInfo.add(infoPair("SĐT:", lblSdt));
        pnlInfo.add(infoPair("Địa chỉ:", lblDiaChi));
        pnlInfo.add(infoPair("Thu ngân:", lblNhanVien));
        pnlPrint.add(pnlInfo);

        modelCT = new DefaultTableModel(
            new String[]{"Tên sản phẩm", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(modelCT);
        tbl.setFont(TechStoreUI.FONT_SM);
        tbl.setRowHeight(28);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tbl.getColumnModel().getColumn(1).setCellRenderer(right);
        tbl.getColumnModel().getColumn(2).setCellRenderer(right);
        tbl.getColumnModel().getColumn(3).setCellRenderer(right);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setPreferredSize(new Dimension(400, 160));
        scroll.setBorder(null);
        pnlPrint.add(scroll);

        pnlPrint.add(new JSeparator());
        JPanel pnlTotal = new JPanel(new BorderLayout());
        pnlTotal.setOpaque(false);
        pnlTotal.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel lblT = new JLabel("Tổng tiền thanh toán:");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTong = new JLabel("0 đ");
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTong.setForeground(TechStoreUI.INDIGO);
        lblTong.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlTotal.add(lblT, BorderLayout.WEST);
        pnlTotal.add(lblTong, BorderLayout.EAST);
        pnlPrint.add(pnlTotal);

        JLabel lblThanks = new JLabel("Cảm ơn Quý khách đã mua sắm tại TechStore!", SwingConstants.CENTER);
        lblThanks.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblThanks.setForeground(TechStoreUI.TEXT_MUTED);
        lblThanks.setBorder(new EmptyBorder(12, 0, 0, 0));
        lblThanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlPrint.add(lblThanks);

        card.add(pnlPrint, BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlActions.setOpaque(false);
        btnDong = new JButton();
        btnInPdf = new JButton();
        TechStoreUI.styleSecondaryButton(btnDong, "Đóng");
        TechStoreUI.stylePrimaryButton(btnInPdf, "In (Xuất PDF)");
        pnlActions.add(btnDong);
        pnlActions.add(btnInPdf);
        card.add(pnlActions, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        overlay.add(card, gbc);
        add(overlay);
    }

    private static JLabel infoValue() {
        JLabel l = new JLabel("---");
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private static JPanel infoPair(String label, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(TechStoreUI.FONT_SM);
        l.setForeground(TechStoreUI.TEXT_MUTED);
        p.add(l, BorderLayout.WEST);
        p.add(value, BorderLayout.EAST);
        return p;
    }

    public void setHeader(String maHD, String ngay, String khach, String sdt,
                          String diaChi, String nhanVien, String tongTien) {
        lblMaHD.setText(maHD);
        lblNgay.setText(ngay);
        lblKhach.setText(khach);
        lblSdt.setText(sdt);
        lblDiaChi.setText(diaChi);
        lblNhanVien.setText(nhanVien);
        lblTong.setText(tongTien);
    }

    public DefaultTableModel getModelCT() { return modelCT; }
    public JButton getBtnInPdf() { return btnInPdf; }
    public JButton getBtnDong() { return btnDong; }
}
