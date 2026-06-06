package view;

import model.LoaiSP;
import model.SanPham;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JFGioHangSanPham extends JDialog {

    public interface ProductPickListener {
        void onProductPicked(SanPham sp, int quantity);
    }

    private static final String ALL_CATEGORY = "T\u1ea5t c\u1ea3 danh m\u1ee5c";

    private final List<SanPham> products;
    private final Map<String, String> categoryNames = new HashMap<>();
    private final Map<String, SanPham> selectedProducts = new LinkedHashMap<>();
    private final Map<String, Integer> selectedQuantities = new HashMap<>();
    private final ProductPickListener listener;
    private final DecimalFormat vndFormat;
    private JTextField txtSearch;
    private JComboBox<CategoryItem> cboCategory;
    private JPanel pnlProducts;
    private JLabel lblSelectedTotal;
    private JLabel lblSelectedCount;
    private JButton btnAddSelected;

    public JFGioHangSanPham(Window owner, List<SanPham> products, List<LoaiSP> categories,
                            ProductPickListener listener) {
        super(owner, "Gi\u1ecf h\u00e0ng - ch\u1ecdn s\u1ea3n ph\u1ea9m", ModalityType.APPLICATION_MODAL);
        this.products = new ArrayList<>(products);
        this.listener = listener;

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        vndFormat = new DecimalFormat("#,###", symbols);

        for (LoaiSP loai : categories) {
            categoryNames.put(loai.getMaLoai(), loai.getTenLoai());
        }

        setSize(1360, 720);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(util.TechStoreUI.BG_MAIN);
        initComponents(categories);
        refreshProducts();
    }

    private void initComponents(List<LoaiSP> categories) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(util.TechStoreUI.BG_MAIN);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(createFilterPanel(categories), BorderLayout.NORTH);
        root.add(createProductsPanel(), BorderLayout.CENTER);
        root.add(createFooterPanel(), BorderLayout.SOUTH);
        add(root);
    }

    private JPanel createFilterPanel(List<LoaiSP> categories) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 12);

        txtSearch = util.TechStoreUI.createField("T\u00ecm ki\u1ebfm s\u1ea3n ph\u1ea9m theo t\u00ean, c\u1ea5u h\u00ecnh...");
        txtSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSearch.putClientProperty("JTextField.placeholderText", "T\u00ecm ki\u1ebfm s\u1ea3n ph\u1ea9m theo t\u00ean, c\u1ea5u h\u00ecnh...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshProducts(); }
            public void removeUpdate(DocumentEvent e) { refreshProducts(); }
            public void changedUpdate(DocumentEvent e) { refreshProducts(); }
        });

        cboCategory = new JComboBox<>();
        cboCategory.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cboCategory.setBackground(util.TechStoreUI.CARD_BG);
        cboCategory.setForeground(util.TechStoreUI.TEXT_TITLE);
        cboCategory.addItem(new CategoryItem("", ALL_CATEGORY));
        for (LoaiSP loai : categories) {
            cboCategory.addItem(new CategoryItem(loai.getMaLoai(), loai.getTenLoai()));
        }
        cboCategory.addActionListener(e -> refreshProducts());

        gbc.gridx = 0;
        gbc.weightx = 0.68;
        panel.add(txtSearch, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(cboCategory, gbc);
        return panel;
    }

    private JComponent createProductsPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setBackground(panelBg());
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(util.TechStoreUI.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("DANH S\u00c1CH S\u1ea2N PH\u1ea8M");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(util.TechStoreUI.isDarkMode() ? new Color(147, 197, 253) : new Color(37, 99, 235));
        JLabel hint = new JLabel("(t\u00edch ch\u1ecdn s\u1ea3n ph\u1ea9m, sau \u0111\u00f3 b\u1ea5m Th\u00eam v\u00e0o danh s\u00e1ch)");
        hint.setFont(new Font("Segoe UI", Font.BOLD, 11));
        hint.setForeground(util.TechStoreUI.TEXT_MUTED);
        header.add(title, BorderLayout.WEST);
        header.add(hint, BorderLayout.EAST);

        pnlProducts = new JPanel(new GridBagLayout());
        pnlProducts.setOpaque(false);

        JScrollPane scroll = new JScrollPane(pnlProducts);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(panelBg());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrap.add(header, BorderLayout.NORTH);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(16, 0));
        footer.setBackground(util.TechStoreUI.CARD_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(util.TechStoreUI.BORDER),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);
        JLabel label = new JLabel("T\u1ed5ng ti\u1ec1n s\u1ea3n ph\u1ea9m \u0111\u00e3 ch\u1ecdn");
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(util.TechStoreUI.TEXT_MUTED);
        lblSelectedTotal = new JLabel(formatVND(0));
        lblSelectedTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSelectedTotal.setForeground(new Color(249, 115, 22));
        lblSelectedCount = new JLabel("0 s\u1ea3n ph\u1ea9m");
        lblSelectedCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSelectedCount.setForeground(util.TechStoreUI.TEXT_MUTED);
        summary.add(label);
        summary.add(Box.createVerticalStrut(3));
        summary.add(lblSelectedTotal);
        summary.add(Box.createVerticalStrut(2));
        summary.add(lblSelectedCount);

        btnAddSelected = new JButton("Th\u00eam v\u00e0o danh s\u00e1ch");
        util.TechStoreUI.stylePrimaryButton(btnAddSelected, "Th\u00eam v\u00e0o danh s\u00e1ch");
        util.TechStoreUI.applyRoundedButton(btnAddSelected, 12);
        btnAddSelected.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAddSelected.setPreferredSize(new Dimension(240, 46));
        btnAddSelected.addActionListener(e -> addSelectedProducts());

        footer.add(summary, BorderLayout.WEST);
        footer.add(btnAddSelected, BorderLayout.EAST);
        updateSelectedSummary();
        return footer;
    }

    private void refreshProducts() {
        if (pnlProducts == null) {
            return;
        }
        pnlProducts.removeAll();

        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        CategoryItem selected = (CategoryItem) cboCategory.getSelectedItem();
        String selectedMaLoai = selected == null ? "" : selected.maLoai;

        List<SanPham> filtered = new ArrayList<>();
        for (SanPham sp : products) {
            boolean matchesKeyword = keyword.isEmpty()
                    || safe(sp.getMaSP()).toLowerCase(Locale.ROOT).contains(keyword)
                    || safe(sp.getTenSP()).toLowerCase(Locale.ROOT).contains(keyword)
                    || safe(sp.getMoTa()).toLowerCase(Locale.ROOT).contains(keyword)
                    || safe(sp.getHangSX()).toLowerCase(Locale.ROOT).contains(keyword);
            boolean matchesCategory = selectedMaLoai.isEmpty() || selectedMaLoai.equals(sp.getMaLoai());
            if (matchesKeyword && matchesCategory) {
                filtered.add(sp);
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 16, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int colCount = 4;
        for (int i = 0; i < filtered.size(); i++) {
            gbc.gridx = i % colCount;
            gbc.gridy = i / colCount;
            pnlProducts.add(createProductCard(filtered.get(i)), gbc);
        }

        if (filtered.isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = colCount;
            JLabel empty = new JLabel("Kh\u00f4ng t\u00ecm th\u1ea5y s\u1ea3n ph\u1ea9m ph\u00f9 h\u1ee3p.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(148, 163, 184));
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            pnlProducts.add(empty, gbc);
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = (filtered.size() / colCount) + 1;
        filler.weighty = 1.0;
        pnlProducts.add(Box.createVerticalGlue(), filler);

        pnlProducts.revalidate();
        pnlProducts.repaint();
    }

    private JPanel createProductCard(SanPham sp) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        applyCardStyle(card, selectedProducts.containsKey(sp.getMaSP()), false);
        card.setPreferredSize(new Dimension(280, 268));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JCheckBox chkSelect = new JCheckBox();
        chkSelect.setOpaque(false);
        chkSelect.setSelected(selectedProducts.containsKey(sp.getMaSP()));
        chkSelect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkSelect.setToolTipText("Ch\u1ecdn s\u1ea3n ph\u1ea9m");

        JLabel icon = new JLabel(resolveProductIcon(sp));
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);
        icon.setPreferredSize(new Dimension(36, 40));
        icon.setMinimumSize(new Dimension(30, 38));

        JTextArea name = new JTextArea(safe(sp.getTenSP()));
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(util.TechStoreUI.TEXT_TITLE);
        name.setOpaque(false);
        name.setEditable(false);
        name.setFocusable(false);
        name.setBorder(null);
        name.setWrapStyleWord(true);
        name.setLineWrap(true);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // CENTER: chỉ icon + tên, dùng glue để đẩy nội dung lên trên
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(Box.createVerticalStrut(10));
        center.add(icon);
        center.add(Box.createVerticalStrut(8));
        center.add(name);
        center.add(Box.createVerticalGlue());

        // --- SOUTH: khoá cứng giá + số lượng + kho/loại ---
        JLabel price = new JLabel(formatVND(sp.getDonGia()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 15));
        price.setForeground(new Color(37, 99, 235));
        price.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel qtyPanel = createQuantityControl(sp);
        qtyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(util.TechStoreUI.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stock = new JLabel("Kho: " + sp.getSoLuong());
        stock.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stock.setForeground(util.TechStoreUI.isDarkMode() ? new Color(125, 211, 252) : new Color(14, 116, 144));

        JLabel maSPLabel = new JLabel(sp.getMaSP());
        maSPLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        maSPLabel.setForeground(new Color(148, 163, 184));
        maSPLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel category = new JLabel(shortCategoryName(sp.getMaLoai()));
        category.setFont(new Font("Segoe UI", Font.BOLD, 11));
        category.setForeground(new Color(129, 140, 248));
        category.setOpaque(true);
        category.setBackground(new Color(49, 46, 129));
        category.setBorder(new EmptyBorder(2, 6, 2, 6));

        JPanel productFooter = new JPanel(new BorderLayout(4, 0));
        productFooter.setOpaque(false);
        productFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        productFooter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        productFooter.add(stock, BorderLayout.WEST);
        productFooter.add(maSPLabel, BorderLayout.CENTER);
        productFooter.add(category, BorderLayout.EAST);

        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        south.setOpaque(false);
        south.add(price);
        south.add(Box.createVerticalStrut(8));
        south.add(qtyPanel);
        south.add(Box.createVerticalStrut(10));
        south.add(sep);
        south.add(Box.createVerticalStrut(6));
        south.add(productFooter);

        card.add(chkSelect, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(south, BorderLayout.SOUTH);
        chkSelect.addActionListener(e -> toggleSelected(sp, chkSelect.isSelected(), card));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chkSelect.setSelected(!chkSelect.isSelected());
                toggleSelected(sp, chkSelect.isSelected(), card);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                applyCardStyle(card, chkSelect.isSelected(), true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                applyCardStyle(card, chkSelect.isSelected(), false);
            }
        });
        return card;
    }

    private void toggleSelected(SanPham sp, boolean selected, JPanel card) {
        if (selected) {
            selectedProducts.put(sp.getMaSP(), sp);
            selectedQuantities.putIfAbsent(sp.getMaSP(), 1);
        } else {
            selectedProducts.remove(sp.getMaSP());
            selectedQuantities.remove(sp.getMaSP());
        }
        applyCardStyle(card, selected, false);
        card.revalidate();
        card.repaint();
        updateSelectedSummary();
    }

    private void addSelectedProducts() {
        if (selectedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui l\u00f2ng t\u00edch ch\u1ecdn \u00edt nh\u1ea5t m\u1ed9t s\u1ea3n ph\u1ea9m.",
                    "Ch\u01b0a ch\u1ecdn s\u1ea3n ph\u1ea9m",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (SanPham sp : new ArrayList<>(selectedProducts.values())) {
            listener.onProductPicked(sp, selectedQuantities.getOrDefault(sp.getMaSP(), 1));
        }
        dispose();
    }

    private void updateSelectedSummary() {
        if (lblSelectedTotal == null || lblSelectedCount == null || btnAddSelected == null) {
            return;
        }
        double total = 0;
        int totalQuantity = 0;
        for (SanPham sp : selectedProducts.values()) {
            int quantity = selectedQuantities.getOrDefault(sp.getMaSP(), 1);
            total += sp.getDonGia() * quantity;
            totalQuantity += quantity;
        }
        int count = selectedProducts.size();
        lblSelectedTotal.setText(formatVND(total));
        lblSelectedCount.setText(count + " m\u1eb7t h\u00e0ng, " + totalQuantity + " s\u1ea3n ph\u1ea9m");
        btnAddSelected.setEnabled(count > 0);
        if (count > 0) {
            btnAddSelected.setBackground(new Color(99, 102, 241));
            btnAddSelected.setForeground(Color.WHITE);
        } else {
            btnAddSelected.setBackground(util.TechStoreUI.isDarkMode()
                    ? new Color(30, 41, 59) : new Color(226, 232, 240));
            btnAddSelected.setForeground(util.TechStoreUI.isDarkMode()
                    ? new Color(100, 116, 139) : new Color(148, 163, 184));
        }
    }

    private JPanel createQuantityControl(SanPham sp) {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        panel.setPreferredSize(new Dimension(140, 32));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnMinus = new JButton("-");
        JButton btnPlus = new JButton("+");
        JLabel lblQuantity = new JLabel(String.valueOf(selectedQuantities.getOrDefault(sp.getMaSP(), 1)));

        styleQtyButton(btnMinus, new Color(249, 115, 22));
        styleQtyButton(btnPlus, new Color(34, 197, 94));
        lblQuantity.setHorizontalAlignment(SwingConstants.CENTER);
        lblQuantity.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQuantity.setForeground(util.TechStoreUI.TEXT_TITLE);
        lblQuantity.setOpaque(true);
        lblQuantity.setBackground(util.TechStoreUI.FIELD_BG);
        lblQuantity.setBorder(BorderFactory.createLineBorder(util.TechStoreUI.BORDER));
        lblQuantity.setPreferredSize(new Dimension(46, 30));

        btnMinus.addActionListener(e -> {
            ensureSelected(sp);
            int current = selectedQuantities.getOrDefault(sp.getMaSP(), 1);
            if (current > 1) {
                current--;
                selectedQuantities.put(sp.getMaSP(), current);
            }
            lblQuantity.setText(String.valueOf(current));
            updateSelectedSummary();
        });
        btnPlus.addActionListener(e -> {
            ensureSelected(sp);
            int current = selectedQuantities.getOrDefault(sp.getMaSP(), 1);
            if (current < sp.getSoLuong()) {
                current++;
                selectedQuantities.put(sp.getMaSP(), current);
            }
            lblQuantity.setText(String.valueOf(current));
            updateSelectedSummary();
        });

        panel.add(btnMinus, BorderLayout.WEST);
        panel.add(lblQuantity, BorderLayout.CENTER);
        panel.add(btnPlus, BorderLayout.EAST);
        return panel;
    }

    private void ensureSelected(SanPham sp) {
        selectedProducts.put(sp.getMaSP(), sp);
        selectedQuantities.putIfAbsent(sp.getMaSP(), 1);
    }

    private void styleQtyButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(32, 30));
        btn.setMargin(new Insets(1, 7, 1, 7));
        util.TechStoreUI.applyRoundedButton(btn, 8);
    }

    private void applyCardStyle(JPanel card, boolean selected, boolean hover) {
        Color bg = selected
                ? (util.TechStoreUI.isDarkMode() ? new Color(30, 41, 59) : new Color(238, 242, 255))
                : (hover
                    ? (util.TechStoreUI.isDarkMode() ? new Color(30, 41, 59) : new Color(248, 250, 252))
                    : util.TechStoreUI.CARD_BG);
        Color border = selected || hover ? util.TechStoreUI.INDIGO : util.TechStoreUI.BORDER;
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                new EmptyBorder(12, 16, 12, 16)
        ));
    }

    private Color panelBg() {
        return util.TechStoreUI.isDarkMode() ? new Color(2, 6, 23) : util.TechStoreUI.BG_MAIN;
    }

    private String resolveProductIcon(SanPham sp) {
        String category = safe(categoryNames.get(sp.getMaLoai())).toLowerCase(Locale.ROOT);
        String name = safe(sp.getTenSP()).toLowerCase(Locale.ROOT);
        if (category.contains("laptop") || category.contains("m\u00e1y") || name.contains("laptop")) {
            return "\ud83d\udcbb";
        }
        if (category.contains("ph\u1ee5") || name.contains("tai nghe") || name.contains("head")) {
            return "\ud83c\udfa7";
        }
        return "\ud83d\udcf1";
    }

    private String shortCategoryName(String maLoai) {
        String name = categoryNames.getOrDefault(maLoai, "Kh\u00e1c");
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.contains("\u0111i\u1ec7n tho\u1ea1i") || lower.contains("phone")) {
            return "\u0110i\u1ec7n tho\u1ea1i";
        }
        if (lower.contains("laptop")) {
            return "Laptop";
        }
        if (lower.contains("m\u00e1y t\u00ednh")) {
            return "M\u00e1y t\u00ednh";
        }
        if (lower.contains("ph\u1ee5 ki\u1ec7n") || lower.contains("ph\u1ee5")) {
            return "Ph\u1ee5 ki\u1ec7n";
        }
        return name;
    }

    private String toHtmlEllipsis(String text, int maxLength) {
        String safeText = safe(text);
        if (safeText.length() > maxLength) {
            safeText = safeText.substring(0, maxLength - 1) + ".";
        }
        return "<html><body style='width: 220px'>" + safeText + "</body></html>";
    }

    private String formatVND(double amount) {
        return vndFormat.format(amount) + " \u0111";
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private static class CategoryItem {
        private final String maLoai;
        private final String tenLoai;

        private CategoryItem(String maLoai, String tenLoai) {
            this.maLoai = maLoai;
            this.tenLoai = tenLoai;
        }

        @Override
        public String toString() {
            return tenLoai;
        }
    }
}