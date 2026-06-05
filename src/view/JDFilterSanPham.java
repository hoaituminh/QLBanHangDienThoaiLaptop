package view;

import model.LoaiSP;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class JDFilterSanPham extends JDialog {

    private Map<String, JToggleButton> brandChips = new LinkedHashMap<>();
    private Map<String, JToggleButton> priceChips = new LinkedHashMap<>();
    private Map<String, JToggleButton> categoryChips = new LinkedHashMap<>();

    private JPanel pnlBrands;
    private JPanel pnlCategories;
    private JButton btnBoChon, btnApDung;

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 15);
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 13);

    public JDFilterSanPham(JFrame parent) {
        super(parent, "Tất cả bộ lọc", true);
        setSize(650, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(util.TechStoreUI.CARD_BG);
        initComponents();
    }

    private void initComponents() {
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(util.TechStoreUI.CARD_BG);
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Hãng Sản Xuất (Sẽ được load ĐỘNG từ CSDL)
        JLabel lblHang = new JLabel("Hãng:");
        lblHang.setFont(titleFont);
        lblHang.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        pnlBrands = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlBrands.setBackground(util.TechStoreUI.CARD_BG);
        pnlBrands.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Mức Giá (Cố định)
        JLabel lblGia = new JLabel("Mức Giá:");
        lblGia.setFont(titleFont);
        lblGia.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblGia.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel pnlPrices = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlPrices.setBackground(util.TechStoreUI.CARD_BG);
        pnlPrices.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] prices = {"Dưới 10 triệu", "10 - 15 triệu", "15 - 20 triệu", "20 - 30 triệu", "Trên 30 triệu"};
        for (String p : prices) {
            JToggleButton btn = createChip(p);
            priceChips.put(p, btn);
            pnlPrices.add(btn);
        }

        // 3. Loại SP (Sẽ được load ĐỘNG)
        JLabel lblLoai = new JLabel("Loại SP:");
        lblLoai.setFont(titleFont);
        lblLoai.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLoai.setBorder(new EmptyBorder(20, 0, 0, 0));

        pnlCategories = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlCategories.setBackground(util.TechStoreUI.CARD_BG);
        pnlCategories.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Gắn vào panel chính
        pnlMain.add(lblHang);
        pnlMain.add(pnlBrands);
        pnlMain.add(lblGia);
        pnlMain.add(pnlPrices);
        pnlMain.add(lblLoai);
        pnlMain.add(pnlCategories);

        JScrollPane scroll = new JScrollPane(pnlMain);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Footer - Buttons
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        pnlFooter.setBackground(util.TechStoreUI.CARD_BG);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, util.TechStoreUI.BORDER));

        btnBoChon = new JButton("Bỏ chọn");
        btnBoChon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBoChon.setPreferredSize(new Dimension(150, 40));
        btnBoChon.setBackground(util.TechStoreUI.CARD_BG);
        btnBoChon.setForeground(new Color(239, 68, 68)); 
        btnBoChon.setBorder(BorderFactory.createLineBorder(new Color(239, 68, 68), 1));
        btnBoChon.setFocusPainted(false);
        btnBoChon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnApDung = new JButton("Xem kết quả");
        btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApDung.setPreferredSize(new Dimension(150, 40));
        btnApDung.setBackground(new Color(14, 165, 233)); 
        btnApDung.setForeground(Color.WHITE);
        btnApDung.setBorderPainted(false);
        btnApDung.setFocusPainted(false);
        btnApDung.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlFooter.add(btnBoChon);
        pnlFooter.add(btnApDung);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // --- HÀM XỬ LÝ: TỰ ĐỘNG TẠO NÚT "HÃNG" TỪ DANH SÁCH BÊN NGOÀI CHUYỀN VÀO ---
    public void loadBrands(ArrayList<String> brands) {
        pnlBrands.removeAll();
        brandChips.clear();
        for (String brand : brands) {
            JToggleButton btn = createChip(brand);
            brandChips.put(brand, btn);
            pnlBrands.add(btn);
        }
        pnlBrands.revalidate();
        pnlBrands.repaint();
    }

    public void loadCategories(ArrayList<LoaiSP> list) {
        pnlCategories.removeAll();
        categoryChips.clear();
        for (LoaiSP loai : list) {
            JToggleButton btn = createChip(loai.getTenLoai());
            categoryChips.put(loai.getMaLoai(), btn); 
            pnlCategories.add(btn);
        }
        pnlCategories.revalidate();
        pnlCategories.repaint();
    }

    private JToggleButton createChip(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(mainFont);
        btn.setBackground(util.TechStoreUI.CARD_BG);
        btn.setForeground(new Color(71, 85, 105));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(new Color(238, 242, 255)); 
                btn.setForeground(new Color(79, 70, 229));   
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                    new EmptyBorder(8, 15, 8, 15)
                ));
            } else {
                btn.setBackground(util.TechStoreUI.CARD_BG);
                btn.setForeground(new Color(71, 85, 105));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(util.TechStoreUI.BORDER, 1),
                    new EmptyBorder(8, 15, 8, 15)
                ));
            }
        });
        return btn;
    }

    public void clearAllChips() {
        for (JToggleButton btn : brandChips.values()) btn.setSelected(false);
        for (JToggleButton btn : priceChips.values()) btn.setSelected(false);
        for (JToggleButton btn : categoryChips.values()) btn.setSelected(false);
    }

    public Map<String, JToggleButton> getBrandChips() { return brandChips; }
    public Map<String, JToggleButton> getPriceChips() { return priceChips; }
    public Map<String, JToggleButton> getCategoryChips() { return categoryChips; }
    public JButton getBtnBoChon() { return btnBoChon; }
    public JButton getBtnApDung() { return btnApDung; }
}