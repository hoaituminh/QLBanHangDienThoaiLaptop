package view;

import controller.LoginController;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class JFLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JButton btnDangNhap;
    private JButton btnThoat;

    public JFLogin() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập hệ thống");
        // Mở rộng bề ngang và chiều cao để khung nền lộ ra nhiều hơn
        setSize(1300, 850); // tỷ lệ gần giống 2400x1792 
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // =========================================================
        // 1. MAIN PANEL (KHUNG NỀN CHỨA ẢNH PHỦ KÍN MÀN HÌNH)
        // =========================================================
        // Sử dụng GridBagLayout cho Main Panel để tự động căn giữa Form bên trong
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            private Image bgImage;

            {
                try {
                    // Tên file ảnh nền
                    java.net.URL url = getClass().getResource("/image/nenlogin4.png");
                    if (url != null) {
                        bgImage = new ImageIcon(url).getImage();
                    } else {
                        // Đường dẫn dự phòng
                        bgImage = new ImageIcon("src/image/nenlogin.png").getImage();
                    }
                } catch (Exception e) {
                    System.out.println("Lỗi tải ảnh nền: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    // Vẽ ảnh lấp đầy toàn bộ cửa sổ (Width x Height)
                    Graphics2D g2d = (Graphics2D) g;
                    // Kích hoạt khử răng cưa và nội suy chất lượng cao
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // Vẽ ảnh với chất lượng tốt hơn
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                    } else {
                    // Nếu thiếu ảnh, đổ màu Gradient xanh dự phòng
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 120, 215), getWidth(), getHeight(), new Color(100, 180, 255));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // =========================================================
        // 2. FORM PANEL (HỘP TRẮNG BO TRÒN CHỨA CÁC Ô NHẬP LIỆU)
        // =========================================================
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Đổ bóng nhẹ (Shadow) ở dưới
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 25, 25);
                
                // Nền trắng bo góc chính
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 25, 25);
                
                g2.dispose();
            }
        };
        formPanel.setOpaque(false); // Trong suốt để thấy nền ảnh ở các góc bo
        // Thu gọn padding lại một chút để form không quá to
        formPanel.setPreferredSize(new Dimension(400, 600)); // Tăng chiều cao một chút để chứa logo
        formPanel.setBorder(new EmptyBorder(20, 50, 40, 50)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // -- Logo
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        // Tải và scale ảnh logo
        ImageIcon logoIcon = scaleImage("/image/logo.png", 80, 80); // Kích thước logo 80x80
        if (logoIcon != null) {
            lblLogo.setIcon(logoIcon);
        }

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0); // Khoảng cách dưới logo
        formPanel.add(lblLogo, gbc);

        // -- Tiêu đề Form
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(new Color(0, 120, 215)); // Màu xanh chủ đạo

        JLabel lblSub = new JLabel("Hệ thống quản lý bán hàng", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(new Color(120, 120, 120));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        formPanel.add(lblTitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        formPanel.add(lblSub, gbc);

        // -- Ô nhập Username
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 13));
        lblUser.setForeground(new Color(0, 120, 215));
        
        txtUsername = new JTextField();
        // Thu gọn chiều rộng ô text để form bé lại
        txtUsername.setPreferredSize(new Dimension(280, 40));
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        setupRoundedTextField(txtUsername);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(lblUser, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(txtUsername, gbc);

        // -- Ô nhập Password
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 13));
        lblPass.setForeground(new Color(0, 120, 215));

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(280, 40));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        setupRoundedTextField(txtPassword);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(lblPass, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 30, 0);
        formPanel.add(txtPassword, gbc);

        // -- Khu vực Nút bấm (Buttons)
        btnDangNhap = new RoundedButton("Đăng nhập", new Color(0, 120, 215), Color.WHITE);
        btnThoat = new RoundedButton("Thoát", new Color(108, 117, 125), Color.WHITE); 

        // Thu gọn kích thước nút
        btnDangNhap.setPreferredSize(new Dimension(130, 40));
        btnThoat.setPreferredSize(new Dimension(130, 40));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 15, 0)); 
        btnPanel.setOpaque(false);
        btnPanel.add(btnDangNhap);
        btnPanel.add(btnThoat);

        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(btnPanel, gbc);

        // Thêm formPanel vào chính giữa bgPanel
        bgPanel.add(formPanel);

        // Gắn bgPanel vào Frame chính
        add(bgPanel);
    }

    // Hàm hỗ trợ scale ảnh
    private ImageIcon scaleImage(String path, int width, int height) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage();
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.out.println("Lỗi tải ảnh: " + path + " - " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // HÀM HỖ TRỢ BO GÓC (UI CUSTOM) GIỮ NGUYÊN
    // =========================================================
    private void setupRoundedTextField(JTextField textField) {
        textField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15, new Color(200, 200, 200)),
            new EmptyBorder(5, 15, 5, 15)
        ));
    }

    class RoundedBorder implements Border {
        private int radius;
        private Color color;
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius/2, this.radius/2, this.radius/2, this.radius/2);
        }
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    class RoundedButton extends JButton {
        private Color bgColor;

        public RoundedButton(String text, Color bgColor, Color fgColor) {
            super(text);
            this.bgColor = bgColor;
            setForeground(fgColor);
            setFont(new Font("Arial", Font.BOLD, 14));
            setFocusPainted(false);
            setContentAreaFilled(false); 
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(bgColor.darker());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(bgColor);
                }
            });
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            this.bgColor = bg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            super.paintComponent(g); 
            g2.dispose();
        }
    }

    // ==========================
    // GETTERS CHO CONTROLLER
    // ==========================
    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnDangNhap() { return btnDangNhap; }
    public JButton getBtnThoat() { return btnThoat; }

    // ==========================
    // MAIN ENTRY (CHẠY THỬ)
    // ==========================
    public static void main(String[] args) {
        // Thêm dòng này vào đầu tiên để ứng dụng nhận diện DPI chính xác trên Windows
        System.setProperty("sun.java2d.dpiaware", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFLogin view = new JFLogin();
            new LoginController(view);
            view.setVisible(true);
        });
    }
}