package controller;

import dao.LoaiSPDAO;
import dao.SanPhamDAO;
import model.LoaiSP;
import model.SanPham;
import view.JDFilterSanPham;
import view.JFSanPham;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;

public class SanPhamController {

    private JFSanPham view;
    private JDFilterSanPham filterDialog; 
    private SanPhamDAO dao;
    private LoaiSPDAO loaiDao;
    
    private ArrayList<LoaiSP> danhSachLoaiSP; 
    
    private String currentSearchKeyword = ""; 
    private JPopupMenu suggestionMenu = new JPopupMenu(); 

    public SanPhamController(JFSanPham view) {
        this.view = view;
        this.dao = new SanPhamDAO();
        this.loaiDao = new LoaiSPDAO();
        
        this.filterDialog = new JDFilterSanPham(view);

        danhSachLoaiSP = loaiDao.getAll();
        
        view.loadLoaiSP();
        filterDialog.loadCategories(danhSachLoaiSP);
        loadTableData();

        initController();
        setupAutoComplete(); 
    }

    private String getTenLoaiBangMa(String maLoai) {
        if (maLoai == null) return "";
        for (LoaiSP loai : danhSachLoaiSP) {
            if (loai.getMaLoai().equals(maLoai)) {
                return loai.getTenLoai(); 
            }
        }
        return maLoai;
    }

    private void setupAutoComplete() {
        JTextField txtSearch = view.getTxtTimKiem();
        suggestionMenu.setFocusable(false); 

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void updateSuggestions() {
                SwingUtilities.invokeLater(() -> {
                    if (!txtSearch.hasFocus()) return;

                    String text = txtSearch.getText().trim().toLowerCase();
                    suggestionMenu.removeAll();

                    if (text.isEmpty()) {
                        suggestionMenu.setVisible(false);
                        return;
                    }

                    ArrayList<SanPham> list = dao.getAll();
                    java.util.Set<String> suggestions = new java.util.LinkedHashSet<>();

                    for (SanPham sp : list) {
                        if (sp.getHangSX() != null && sp.getHangSX().toLowerCase().contains(text)) {
                            suggestions.add(sp.getHangSX());
                        }
                        if (sp.getTenSP() != null && sp.getTenSP().toLowerCase().contains(text)) {
                            suggestions.add(sp.getTenSP());
                        }
                    }

                    if (suggestions.isEmpty()) {
                        suggestionMenu.setVisible(false);
                        return;
                    }

                    int count = 0;
                    for (String s : suggestions) {
                        if (count >= 6) break; 
                        
                        JMenuItem item = new JMenuItem("  " + s);
                        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        item.addActionListener(e -> {
                            txtSearch.setText(s);
                            suggestionMenu.setVisible(false);
                            view.getBtnTimKiem().doClick(); 
                        });
                        suggestionMenu.add(item);
                        count++;
                    }

                    if (suggestionMenu.getComponentCount() > 0) {
                        suggestionMenu.show(txtSearch, 0, txtSearch.getHeight() + 2);
                        txtSearch.requestFocus(); 
                    }
                });
            }

            @Override public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });
        
        txtSearch.addActionListener(e -> view.getBtnTimKiem().doClick());
    }

    private void initController() {
        view.getTblSanPham().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Luôn load dữ liệu lên form ẩn bên dưới mỗi khi click
                hienThiChiTiet();
                
                // NẾU LÀ CLICK ĐÚP CHUỘT -> MỚI CHUYỂN TAB
                if (e.getClickCount() == 2) {
                    view.getTabMain().setSelectedIndex(1);
                }
            }
        });

        view.getBtnTabAll().addActionListener(e -> {
            this.currentSearchKeyword = ""; 
            filterDialog.clearAllChips();   
            locSanPham();                   
        });

        ActionListener tabFilterListener = e -> locSanPham();
        view.getBtnTabDienThoai().addActionListener(tabFilterListener);
        view.getBtnTabLaptop().addActionListener(tabFilterListener);
        view.getBtnTabPhuKien().addActionListener(tabFilterListener);

        view.getBtnTimKiem().addActionListener(e -> {
            this.currentSearchKeyword = view.getTxtTimKiem().getText().trim().toLowerCase();
            locSanPham();
            view.getTxtTimKiem().setText(""); 
            suggestionMenu.setVisible(false); 
        });

        view.getBtnLuu().addActionListener(e -> {
            if (view.isEditMode()) {
                suaSanPham();
            } else {
                themSanPham();
            }
        });
        
        view.getBtnXoa().addActionListener(e -> xoaSanPham());
        view.getBtnChonAnh().addActionListener(e -> chonAnh());
        view.getBtnLamMoi().addActionListener(e -> {
            view.clearForm();
        });

        view.getBtnMoBoLoc().addActionListener(e -> {
            filterDialog.setLocationRelativeTo(view);
            filterDialog.setVisible(true);
        });
        
        filterDialog.getBtnApDung().addActionListener(e -> {
            locSanPham();
            filterDialog.dispose();
        });
        
        filterDialog.getBtnBoChon().addActionListener(e -> {
            filterDialog.clearAllChips();
        });
    }

    private void locSanPham() {
        ArrayList<SanPham> allProducts = dao.getAll();
        ArrayList<SanPham> filteredProducts = new ArrayList<>();

        String quickFilterMaLoai = "";
        if (view.getBtnTabDienThoai().isSelected()) {
            quickFilterMaLoai = "L01";
        } else if (view.getBtnTabLaptop().isSelected()) {
            quickFilterMaLoai = "L02"; 
        } else if (view.getBtnTabPhuKien().isSelected()) {
            quickFilterMaLoai = "L03"; 
        }

        ArrayList<String> selectedBrands = new ArrayList<>();
        for (Map.Entry<String, JToggleButton> entry : filterDialog.getBrandChips().entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedBrands.add(entry.getKey().toLowerCase());
            }
        }

        ArrayList<String> selectedPrices = new ArrayList<>();
        for (Map.Entry<String, JToggleButton> entry : filterDialog.getPriceChips().entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedPrices.add(entry.getKey());
            }
        }

        ArrayList<String> selectedCategories = new ArrayList<>();
        for (Map.Entry<String, JToggleButton> entry : filterDialog.getCategoryChips().entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedCategories.add(entry.getKey());
            }
        }

        String searchKeyword = this.currentSearchKeyword;

        for (SanPham sp : allProducts) {
            if (!quickFilterMaLoai.isEmpty()) {
                if (sp.getMaLoai() == null || !sp.getMaLoai().equals(quickFilterMaLoai)) {
                    continue;
                }
            }

            if (!searchKeyword.isEmpty()) {
                boolean matchesKeyword = sp.getMaSP().toLowerCase().contains(searchKeyword) ||
                                         sp.getTenSP().toLowerCase().contains(searchKeyword) ||
                                         (sp.getHangSX() != null && sp.getHangSX().toLowerCase().contains(searchKeyword));
                if (!matchesKeyword) continue;
            }

            if (!selectedBrands.isEmpty()) {
                String hang = (sp.getHangSX() != null) ? sp.getHangSX().toLowerCase() : "";
                boolean matchesBrand = false;
                for (String b : selectedBrands) {
                    if (hang.contains(b)) {
                        matchesBrand = true;
                        break;
                    }
                }
                if (!matchesBrand) continue;
            }

            if (!selectedCategories.isEmpty()) {
                boolean matchesCategory = selectedCategories.contains(sp.getMaLoai());
                if (!matchesCategory) continue;
            }

            if (!selectedPrices.isEmpty()) {
                double donGia = sp.getDonGia();
                boolean matchesPrice = false;
                for (String priceRange : selectedPrices) {
                    if (priceRange.equals("Dưới 10 triệu") && donGia < 10000000) {
                        matchesPrice = true;
                    } else if (priceRange.equals("10 - 15 triệu") && donGia >= 10000000 && donGia <= 15000000) {
                        matchesPrice = true;
                    } else if (priceRange.equals("15 - 20 triệu") && donGia >= 15000000 && donGia <= 20000000) {
                        matchesPrice = true;
                    } else if (priceRange.equals("20 - 30 triệu") && donGia >= 20000000 && donGia <= 30000000) {
                        matchesPrice = true;
                    } else if (priceRange.equals("Trên 30 triệu") && donGia > 30000000) {
                        matchesPrice = true;
                    }
                }
                if (!matchesPrice) continue;
            }

            filteredProducts.add(sp);
        }

        fillTable(filteredProducts);
    }

    private void loadTableData() {
        ArrayList<SanPham> list = dao.getAll();
        fillTable(list);
        
        java.util.Set<String> uniqueBrands = new java.util.TreeSet<>();
        for (SanPham sp : list) {
            if (sp.getHangSX() != null && !sp.getHangSX().trim().isEmpty()) {
                uniqueBrands.add(sp.getHangSX().trim());
            }
        }
        filterDialog.loadBrands(new ArrayList<>(uniqueBrands));
    }

    private void fillTable(ArrayList<SanPham> list) {
        view.getModel().setRowCount(0);
        for (SanPham sp : list) {
            // Tạo ảnh thu nhỏ (thumbnail) kích thước 50x50 để đưa vào bảng
            ImageIcon icon = null;
            if (sp.getHinh() != null && !sp.getHinh().trim().isEmpty()) {
                try {
                    File imgFile = new File("src/image/" + sp.getHinh());
                    if (imgFile.exists()) {
                        ImageIcon originalIcon = new ImageIcon(imgFile.getAbsolutePath());
                        Image scaledImg = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImg);
                    }
                } catch (Exception ex) {
                    // Bỏ qua lỗi hiển thị ảnh cho từng dòng nếu ảnh lỗi
                }
            }

            view.getModel().addRow(new Object[]{
                    sp.getMaSP(),
                    icon, // Cột Hình Ảnh ở vị trí số 1
                    sp.getTenSP(),
                    String.format("%,.0f", sp.getDonGia()), 
                    sp.getSoLuong(),
                    sp.getHangSX(),
                    getTenLoaiBangMa(sp.getMaLoai()) 
            });
        }
    }

    private void hienThiChiTiet() {
        int row = view.getTblSanPham().getSelectedRow();
        if (row >= 0) {
            String maSP = (String) view.getTblSanPham().getValueAt(row, 0); // Vẫn lấy Mã SP ở vị trí cột đầu tiên
            SanPham sp = dao.findById(maSP);
            
            if (sp != null) {
                view.getTxtMaSP().setText(sp.getMaSP());
                view.getTxtTenSP().setText(sp.getTenSP());
                view.getTxtDonGia().setText(String.format("%.0f", sp.getDonGia()));
                view.getTxtSoLuong().setText(String.valueOf(sp.getSoLuong()));
                view.getTxtHangSX().setText(sp.getHangSX());
                view.getTxtMoTa().setText(sp.getMoTa());
                
                for (int i = 0; i < view.getCboLoaiSP().getItemCount(); i++) {
                    if (view.getCboLoaiSP().getItemAt(i).getMaLoai().equals(sp.getMaLoai())) {
                        view.getCboLoaiSP().setSelectedIndex(i);
                        break;
                    }
                }
                
                view.showImage(sp.getHinh());
                view.setEditMode(true);
                
                // Đã loại bỏ lệnh tự động chuyển tab ở đây
            }
        }
    }

    private void themSanPham() {
        SanPham sp = view.getFormData(); 
        if (sp != null) {
            if (dao.exists(sp.getMaSP())) {
                JOptionPane.showMessageDialog(view, "Mã sản phẩm đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                view.getTxtMaSP().requestFocus();
                return;
            }

            if (dao.insert(sp)) {
                JOptionPane.showMessageDialog(view, "Thêm sản phẩm thành công!");
                loadTableData();
                view.clearForm();
                view.getTabMain().setSelectedIndex(0); 
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void suaSanPham() {
        if (view.getTxtMaSP().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần cập nhật từ bảng!");
            return;
        }

        SanPham sp = view.getFormData();
        if (sp != null) {
            if (dao.update(sp)) {
                JOptionPane.showMessageDialog(view, "Cập nhật sản phẩm thành công!");
                loadTableData();
                view.clearForm();
                view.getTabMain().setSelectedIndex(0); 
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaSanPham() {
        String maSP = view.getTxtMaSP().getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần xóa từ bảng!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa sản phẩm: " + maSP + "?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                
        if (choice == JOptionPane.YES_OPTION) {
            if (dao.delete(maSP)) {
                JOptionPane.showMessageDialog(view, "Xóa sản phẩm thành công!");
                loadTableData();
                view.clearForm();
                view.getTabMain().setSelectedIndex(0); 
            } else {
                JOptionPane.showMessageDialog(view, "Không thể xóa. Sản phẩm này có thể đang tồn tại trong Hóa Đơn!", "Lỗi Xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imageName = selectedFile.getName();
            
            try {
                File dir = new File("src/image");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dest = new File(dir, imageName);
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                view.showImage(imageName);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi lưu ảnh: " + ex.getMessage());
            }
        }
    }

    public void loadData() {
        loadTableData();
    }
}