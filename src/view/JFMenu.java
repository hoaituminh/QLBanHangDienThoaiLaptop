package view;

import controller.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class JFMenu extends JFrame {

    // ── Sidebar constants ──────────────────────────────────────────────────────
    private static final int MINI_W = 64;   // Mini sidebar width (collapsed)
    private static final int FULL_W = 256;  // Full drawer width (expanded)

    // ── State ──────────────────────────────────────────────────────────────────
    private boolean drawerOpen = false;
    private int     activeIdx  = 0;   // Mặc định: Quản Lý Sản Phẩm

    // ── Logged-in user info ─────────────────────────────────────────────────────
    private final String loggedInName;
    private final String loggedInRole;
    private final String loggedInInitials;

    // ── Panels ─────────────────────────────────────────────────────────────────
    private JPanel    pnlMiniSidebar;
    private JPanel    pnlContent;
    private JPanel    pnlCards;
    private CardLayout cardLayout;
    private JLabel    lblTitle;
    private JLabel    lblHeaderTag;

    // Glass-pane overlay (drawer)
    private JPanel glass;
    private JPanel pnlDrawer;
    private JPanel pnlBackdrop;

    // Item lists for synced active state
    private final List<MiniItem>   miniItems   = new ArrayList<>();
    private final Map<String, Runnable> reloadMap = new HashMap<>();
    private final List<DrawerItem> drawerItems = new ArrayList<>();

    // ── Menu data ──────────────────────────────────────────────────────────────
    private static class MenuEntry {
        final String label, abbr, card, title, tag;
        final boolean badge;
        MenuEntry(String label, String abbr, String card,
                  String title, String tag, boolean badge) {
            this.label = label; this.abbr = abbr; this.card = card;
            this.title = title; this.tag  = tag;  this.badge = badge;
        }
    }

    private final MenuEntry[] ENTRIES = {
        new MenuEntry("Trang Chủ (Dashboard)", "TC", "Dashboard",
                      "Trang Chủ (Dashboard)",                    "Hệ Thống Live", false),
        new MenuEntry("Lập Hóa Đơn (POS)",    "HĐ", "LapHoaDon",
                      "Lập Hóa Đơn Bán Hàng (LapHoaDon)",      "Giao Dịch",    true),
        new MenuEntry("Quản Lý Sản Phẩm",     "SP", "SanPham",
                      "Quản Lý Hệ Thống Sản Phẩm (SanPham)",   "Sản phẩm",    false),
        new MenuEntry("Thống Kê",             "TK", "ThongKe",
                      "Thống Kê Bán Hàng (ThongKe)",            "Biểu đồ",     false),
        new MenuEntry("Quản Lý Khách Hàng",   "KH", "KhachHang",
                      "Quản Lý Khách Hàng (KhachHang)",         "Đối tác",     false),
        new MenuEntry("Quản Lý Nhân Viên",    "NV", "NhanVien",
                      "Quản Lý Nhân Sự (NhanVien)",             "Nhân viên",   false),
        new MenuEntry("Quản Lý Tài Khoản",    "TK", "TaiKhoan",
                      "Phân Quyền & Quản Lý Tài Khoản (TaiKhoan)", "Bảo mật", false),
    };

    // ── Colors ─────────────────────────────────────────────────────────────────
    private final Color sidebarBg    = new Color(15,  23,  42);
    private final Color sidebarHover = new Color(30,  41,  59);
    private final Color sidebarBorder= new Color(30,  41,  59);
    private final Color indigo600    = new Color(79,  70, 229);
    private final Color violet600    = new Color(124, 58, 237);
    private final Color textMuted    = new Color(100, 116, 139);
    private final Color textNormal   = new Color(148, 163, 184);
    private final Color emerald400   = new Color(52,  211, 153);

    // ──────────────────────────────────────────────────────────────────────────
    public JFMenu(String userName, String userRole) {
        this(userName, userRole, 0);
    }

    private JFMenu(String userName, String userRole, int initialActiveIdx) {
        // Lưu thông tin người dùng đăng nhập
        this.loggedInName = (userName != null && !userName.isEmpty()) ? userName : "Người dùng";
        this.loggedInRole = (userRole != null && !userRole.isEmpty()) ? userRole.toUpperCase() : "USER";
        this.loggedInInitials = buildInitials(this.loggedInName);
        this.activeIdx = Math.max(0, Math.min(initialActiveIdx, ENTRIES.length - 1));

        setTitle("Phần Mềm Quản Lý Bán Hàng - TechStore");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildMiniSidebar();
        buildGlassPane();
        buildMainContent();

        add(pnlMiniSidebar, BorderLayout.WEST);
        add(pnlContent,     BorderLayout.CENTER);


// LƯỚI BẢO VỆ: khi JMenu lấy lại focus (sau dialog đóng), repaint lại cards
addWindowFocusListener(new WindowAdapter() {
    @Override
    public void windowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(() -> {
            if (pnlCards != null) {
                pnlCards.revalidate();
                pnlCards.repaint();
            }
        });
    }
});
        // SAU (đúng) — chờ frame visible xong mới navigate
        initForms();
        SwingUtilities.invokeLater(() -> navigate(activeIdx));
    }

    /** Constructor mặc định (fallback) */
    public JFMenu() {
        this("Người dùng", "USER");
    }

    /** Tạo chữ viết tắt từ họ tên (lấy 2 ký tự đầu của 2 từ cuối) */
    private static String buildInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "??";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        // Lấy chữ cái đầu của 2 từ cuối
        String first = parts[parts.length - 2].substring(0, 1);
        String last  = parts[parts.length - 1].substring(0, 1);
        return (first + last).toUpperCase();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MINI SIDEBAR — thanh bên 64px luôn hiển thị
    // ══════════════════════════════════════════════════════════════════════════
    private void buildMiniSidebar() {
        pnlMiniSidebar = new JPanel();
        pnlMiniSidebar.setLayout(new BoxLayout(pnlMiniSidebar, BoxLayout.Y_AXIS));
        pnlMiniSidebar.setBackground(sidebarBg);
        pnlMiniSidebar.setPreferredSize(new Dimension(MINI_W, 0));
        pnlMiniSidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, sidebarBorder));

        // ── Nút hamburger ☰ ─────────────────────────────────────────────────
        JPanel pnlHam = new JPanel(new GridBagLayout());
        pnlHam.setBackground(sidebarBg);
        pnlHam.setMaximumSize(new Dimension(MINI_W, 52));
        pnlHam.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnToggle = new JButton() {
            private boolean overed = false;
            {
                setPreferredSize(new Dimension(38, 38));
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { overed = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { overed = false; repaint(); }
                });
                addActionListener(e -> openDrawer());
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (overed) {
                    g2.setColor(sidebarHover);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(overed ? Color.WHITE : textNormal);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.drawLine(cx - 8, cy - 5, cx + 8, cy - 5);
                g2.drawLine(cx - 8, cy,     cx + 8, cy);
                g2.drawLine(cx - 8, cy + 5, cx + 8, cy + 5);
                g2.dispose();
            }
        };
        pnlHam.add(btnToggle);
        pnlMiniSidebar.add(pnlHam);

        // ── TS Logo ──────────────────────────────────────────────────────────
        JPanel pnlLogoWrap = new JPanel(new GridBagLayout());
        pnlLogoWrap.setBackground(sidebarBg);
        pnlLogoWrap.setMaximumSize(new Dimension(MINI_W, 56));
        pnlLogoWrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel logoBox = buildTSLogo(36, 36);
        pnlLogoWrap.add(logoBox);
        pnlMiniSidebar.add(pnlLogoWrap);

        // ── Divider ──────────────────────────────────────────────────────────
        JPanel div = new JPanel();
        div.setBackground(sidebarBorder);
        div.setMaximumSize(new Dimension(MINI_W - 16, 1));
        div.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlMiniSidebar.add(Box.createVerticalStrut(4));
        pnlMiniSidebar.add(div);
        pnlMiniSidebar.add(Box.createVerticalStrut(8));

        // ── Mini menu items (icon tiles) ─────────────────────────────────────
        for (int i = 0; i < ENTRIES.length; i++) {
            final int idx = i;
            MiniItem mi = new MiniItem(ENTRIES[i].abbr, i);
            mi.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { navigate(idx); }
            });
            miniItems.add(mi);
            pnlMiniSidebar.add(mi);
            pnlMiniSidebar.add(Box.createVerticalStrut(3));
        }

        pnlMiniSidebar.add(Box.createVerticalGlue());

        // ── Avatar AN ────────────────────────────────────────────────────────
        JPanel pnlAvatarWrap = new JPanel(new GridBagLayout());
        pnlAvatarWrap.setOpaque(false);
        pnlAvatarWrap.setMaximumSize(new Dimension(MINI_W, 64));
        pnlAvatarWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlAvatarWrap.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, sidebarBorder));
        pnlAvatarWrap.setBackground(new Color(2, 6, 23));
        pnlAvatarWrap.add(buildAvatar(34));
        pnlMiniSidebar.add(pnlAvatarWrap);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GLASS PANE — overlay toàn màn hình khi drawer mở
    // ══════════════════════════════════════════════════════════════════════════
    private void buildGlassPane() {
        glass = new JPanel(null); // absolute layout
        glass.setOpaque(false);
        glass.setVisible(false);

        // Backdrop tối — click để đóng
        pnlBackdrop = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 165));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        pnlBackdrop.setOpaque(false);
        pnlBackdrop.setCursor(Cursor.getDefaultCursor());
        pnlBackdrop.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { closeDrawer(); }
        });

        // Full sidebar drawer
        pnlDrawer = buildFullDrawer();

        glass.add(pnlDrawer);
        glass.add(pnlBackdrop);
        setGlassPane(glass);

        // Reposition khi resize cửa sổ
        getRootPane().addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (drawerOpen) positionOverlay();
            }
        });
    }

    private JPanel buildFullDrawer() {
        JPanel drawer = new JPanel();
        drawer.setLayout(new BoxLayout(drawer, BoxLayout.Y_AXIS));
        drawer.setBackground(sidebarBg);
        drawer.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, sidebarBorder));

        // ── Header: Logo + nút đóng ✕ ───────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(sidebarBg);
        pnlTop.setMaximumSize(new Dimension(FULL_W, 72));
        pnlTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTop.setBorder(new EmptyBorder(16, 16, 16, 12));

        // Trái: TS logo + thương hiệu
        JPanel pnlBrandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlBrandRow.setOpaque(false);

        JPanel logoBox = buildTSLogo(36, 36);
        JPanel pnlBrandText = new JPanel();
        pnlBrandText.setLayout(new BoxLayout(pnlBrandText, BoxLayout.Y_AXIS));
        pnlBrandText.setOpaque(false);

        JLabel lblBrand = new JLabel("TechStore");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBrand.setForeground(Color.WHITE);

        JPanel pnlStatusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pnlStatusRow.setOpaque(false);
        JPanel greenDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(emerald400);
                g2.fillOval(0, 2, 6, 6);
                g2.dispose();
            }
        };
        greenDot.setPreferredSize(new Dimension(8, 10));
        greenDot.setOpaque(false);
        JLabel lblConn = new JLabel("Database Connected");
        lblConn.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblConn.setForeground(emerald400);
        pnlStatusRow.add(greenDot);
        pnlStatusRow.add(lblConn);

        pnlBrandText.add(lblBrand);
        pnlBrandText.add(pnlStatusRow);

        pnlBrandRow.add(logoBox);
        pnlBrandRow.add(pnlBrandText);

        // Phải: nút ✕
        JButton btnClose = new JButton("X") {
            private boolean overed = false;
            {
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(textNormal);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setPreferredSize(new Dimension(32, 32));
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) {
                        overed = true; setForeground(Color.RED); repaint();
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        overed = false; setForeground(textNormal); repaint();
                    }
                });
                addActionListener(e -> closeDrawer());
            }
            @Override protected void paintComponent(Graphics g) {
                if (overed) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(sidebarHover);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        pnlTop.add(pnlBrandRow, BorderLayout.WEST);
        pnlTop.add(btnClose,    BorderLayout.EAST);
        drawer.add(pnlTop);

        // ── Navigation ────────────────────────────────────────────────────────
        JPanel pnlNav = new JPanel();
        pnlNav.setLayout(new BoxLayout(pnlNav, BoxLayout.Y_AXIS));
        pnlNav.setBackground(sidebarBg);
        pnlNav.setBorder(new EmptyBorder(0, 12, 0, 12));
        pnlNav.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlNav.add(makeCategoryLabel("TỔNG QUAN"));
        addDrawerItem(pnlNav, 0);

        pnlNav.add(makeCategoryLabel("NGHIỆP VỤ"));
        addDrawerItem(pnlNav, 1);
        addDrawerItem(pnlNav, 2);
        addDrawerItem(pnlNav, 3);

        pnlNav.add(makeCategoryLabel("HỆ THỐNG (CHỈ ADMIN)"));
        addDrawerItem(pnlNav, 4);
        addDrawerItem(pnlNav, 5);

        drawer.add(pnlNav);
        drawer.add(Box.createVerticalGlue());

        // ── User profile ──────────────────────────────────────────────────────
        JPanel pnlUser = new JPanel(new BorderLayout(8, 0));
        pnlUser.setBackground(new Color(2, 6, 23));
        pnlUser.setMaximumSize(new Dimension(FULL_W, 72));
        pnlUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, sidebarBorder),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel pnlUserLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlUserLeft.setOpaque(false);

        JPanel pnlUserInfo = new JPanel();
        pnlUserInfo.setLayout(new BoxLayout(pnlUserInfo, BoxLayout.Y_AXIS));
        pnlUserInfo.setOpaque(false);
        JLabel lblUserName = new JLabel(loggedInName);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserName.setForeground(Color.WHITE);
        JLabel lblUserRole = new JLabel(loggedInRole);
        lblUserRole.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblUserRole.setForeground(textNormal);
        pnlUserInfo.add(lblUserName);
        pnlUserInfo.add(lblUserRole);

        pnlUserLeft.add(buildAvatar(36));
        pnlUserLeft.add(pnlUserInfo);

        JButton btnLogout = new JButton("Thoát");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnLogout.setForeground(textNormal);
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(51, 65, 85)),
            new EmptyBorder(4, 10, 4, 10)
        ));
        btnLogout.setOpaque(true);
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnLogout.setForeground(new Color(248, 113, 113)); }
            @Override public void mouseExited (MouseEvent e) { btnLogout.setForeground(textNormal); }
        });
        btnLogout.addActionListener(e -> {
           int c = JOptionPane.showConfirmDialog(JFMenu.this,
        "Bạn có muốn đăng xuất không?", "Đăng xuất", JOptionPane.YES_NO_OPTION);
    if (c == JOptionPane.YES_OPTION) {
        util.UserSession.clear();
        dispose();
        JFLogin login = new JFLogin();
        new LoginController(login);              
        login.setVisible(true);                  
    }
        });

        pnlUser.add(pnlUserLeft, BorderLayout.CENTER);
        pnlUser.add(btnLogout,   BorderLayout.EAST);
        drawer.add(pnlUser);

        return drawer;
    }

    private void addDrawerItem(JPanel nav, int idx) {
        DrawerItem item = new DrawerItem(ENTRIES[idx].label, idx, ENTRIES[idx].badge);
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                navigate(idx);
                closeDrawer();
            }
        });
        drawerItems.add(item);
        nav.add(item);
    }

    private JLabel makeCategoryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(textMuted);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setBorder(new EmptyBorder(16, 8, 6, 8));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MAIN CONTENT
    // ══════════════════════════════════════════════════════════════════════════
    private void buildMainContent() {
        pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBackground(util.TechStoreUI.BG_MAIN);

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(util.TechStoreUI.CARD_BG);
        pnlHeader.setPreferredSize(new Dimension(0, 64));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, util.TechStoreUI.BORDER),
            new EmptyBorder(15, 24, 0, 24)
        ));

        JPanel pnlTitleGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTitleGroup.setOpaque(false);
        lblTitle = new JLabel("Trang Chủ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(util.TechStoreUI.TEXT_TITLE);

        lblHeaderTag = new JLabel("Hệ Thống Live");
        lblHeaderTag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHeaderTag.setForeground(util.TechStoreUI.INDIGO);
        lblHeaderTag.setOpaque(true);
        lblHeaderTag.setBackground(util.TechStoreUI.INDIGO_LIGHT);
        lblHeaderTag.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        pnlTitleGroup.add(lblTitle);
        pnlTitleGroup.add(lblHeaderTag);

        JPanel pnlHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        pnlHeaderRight.setOpaque(false);
        JLabel lblClockLbl = new JLabel("Giờ hệ thống:");
        lblClockLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblClockLbl.setForeground(util.TechStoreUI.TEXT_MUTED);
        JLabel lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblClock.setForeground(util.TechStoreUI.TEXT_TITLE);
        Timer timer = new Timer(1000, e -> {
            lblClock.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        });
        timer.start();
        pnlHeaderRight.add(lblClockLbl);
        pnlHeaderRight.add(lblClock);

        // Divider
        JPanel div = new JPanel();
        div.setBackground(util.TechStoreUI.BORDER);
        div.setPreferredSize(new Dimension(1, 24));
        pnlHeaderRight.add(div);

        // Theme Toggle Button
        JButton btnTheme = new JButton();
        btnTheme.setPreferredSize(new Dimension(36, 36));
        btnTheme.setFocusPainted(false);
        btnTheme.setContentAreaFilled(false);
        btnTheme.setBorderPainted(false);
        btnTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boolean isDark = util.TechStoreUI.isDarkMode();
        btnTheme.setText(isDark ? "\u2600" : "\u263E");
        btnTheme.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        btnTheme.setForeground(isDark ? new Color(250, 204, 21) : new Color(100, 116, 139));
        
        btnTheme.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btnTheme.setOpaque(true);
                btnTheme.setBackground(util.TechStoreUI.isDarkMode() ? new Color(30, 41, 59) : util.TechStoreUI.BG_MAIN);
                btnTheme.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btnTheme.setOpaque(false);
                btnTheme.repaint();
            }
        });
        
        btnTheme.addActionListener(e -> {
            boolean nextDark = !util.TechStoreUI.isDarkMode();
            util.TechStoreUI.installLookAndFeel(nextDark);
            int currentIdx = activeIdx;
            // Khởi động lại JFMenu để áp dụng theme mới
            SwingUtilities.invokeLater(() -> {
                JFMenu newMenu = new JFMenu(loggedInName, loggedInRole, currentIdx);
                newMenu.setExtendedState(getExtendedState());
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                    newMenu.setSize(getSize());
                    newMenu.setLocation(getLocation());
                }
                newMenu.setVisible(true);
                JFMenu.this.dispose();
            });
        });
        
        pnlHeaderRight.add(btnTheme);

        pnlHeader.add(pnlTitleGroup,  BorderLayout.WEST);
        pnlHeader.add(pnlHeaderRight, BorderLayout.EAST);

        // Cards
        cardLayout = new CardLayout();
        pnlCards   = new JPanel(cardLayout);
        pnlCards.setBackground(util.TechStoreUI.BG_MAIN);

        pnlContent.add(pnlHeader, BorderLayout.NORTH);
        pnlContent.add(pnlCards,  BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DRAWER CONTROLS
    // ══════════════════════════════════════════════════════════════════════════
    private void openDrawer() {
        drawerOpen = true;
        positionOverlay();
        glass.setVisible(true);
        glass.repaint();
    }

    private void closeDrawer() {
        drawerOpen = false;
        glass.setVisible(false);
    }

    /** Cập nhật vị trí & kích thước overlay theo kích thước frame hiện tại */
    private void positionOverlay() {
        int w = getRootPane().getWidth();
        int h = getRootPane().getHeight();
        if (w == 0) { w = getWidth(); h = getHeight(); }
        pnlDrawer.setBounds  (0,      0, FULL_W,     h);
        pnlBackdrop.setBounds(FULL_W, 0, w - FULL_W, h);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ══════════════════════════════════════════════════════════════════════════
    private void navigate(int idx) {
        activeIdx = idx;
        MenuEntry e = ENTRIES[idx];
        cardLayout.show(pnlCards, e.card);
        lblTitle.setText(e.title);
        lblHeaderTag.setText(e.tag);
        for (MiniItem   mi : miniItems)   mi.setActive(mi.index == idx);
        for (DrawerItem di : drawerItems) di.setActive(di.index == idx);
        
        // Reload dữ liệu form vừa navigate tới
        Runnable reload = reloadMap.get(e.card);
        if (reload != null) SwingUtilities.invokeLater(reload);

        // [FIX LỖI TRẮNG MÀN HÌNH LẦN ĐẦU CHẠY]: Yêu cầu Java tính toán và vẽ lại form con ngay lập tức
        pnlCards.revalidate();
        pnlCards.repaint();
        validate();   // ← gọi trên JFrame, bắt buộc tính lại layout ngay lập tức
        repaint();    // ← vẽ lại toàn bộ sau khi layout đã xong
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FORMS
    // ══════════════════════════════════════════════════════════════════════════
    private void initForms() {
        try {
            JFDashboard frmDashboard = new JFDashboard();
            DashboardController dashboardCtrl = new DashboardController(frmDashboard);
            pnlCards.add(frmDashboard, "Dashboard");
            reloadMap.put("Dashboard", () -> invokeLoadData(dashboardCtrl));
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Dashboard"), "Dashboard");
        }

        try {
            JFLapHoaDon frmHD = new JFLapHoaDon();
            LapHoaDonController hdCtrl = new LapHoaDonController(frmHD);
            pnlCards.add(wrapFormContent(frmHD.getContentPane()), "LapHoaDon");
            frmHD.setContentPane(new JPanel()); // ← TRẢ PANE RỖNG, diệt zombie
            reloadMap.put("LapHoaDon", () -> invokeLoadData(hdCtrl)); // ← chỉ thêm dòng này
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Lập Hóa Đơn"), "LapHoaDon");
        }
        try {
            JFSanPham frmSP = new JFSanPham();
            SanPhamController   spCtrl = new SanPhamController(frmSP);
            pnlCards.add(wrapFormContent(frmSP.getContentPane()), "SanPham");
            frmSP.setContentPane(new JPanel()); // ← TRẢ PANE RỖNG, diệt zombie
            reloadMap.put("SanPham",   () -> invokeLoadData(spCtrl)); // ← chỉ thêm dòng này
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Sản Phẩm"), "SanPham");
        }
        try {
            JFThongKe frmThongKe = new JFThongKe();
            ThongKeController thongKeCtrl = new ThongKeController(frmThongKe);
            pnlCards.add(wrapFormContent(frmThongKe.getContentPane()), "ThongKe");
            frmThongKe.setContentPane(new JPanel());
            reloadMap.put("ThongKe", () -> invokeLoadData(thongKeCtrl));
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Thống Kê"), "ThongKe");
        }
        try {
            JFKhachHang frmKH = new JFKhachHang();
            KhachHangController khCtrl = new KhachHangController(frmKH);
            pnlCards.add(wrapFormContent(frmKH.getContentPane()), "KhachHang");
            frmKH.setContentPane(new JPanel());
            reloadMap.put("KhachHang", () -> invokeLoadData(khCtrl));
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Khách Hàng"), "KhachHang");
        }
        try {
            JFNhanVien frmNV = new JFNhanVien();
            NhanVienController nvCtrl = new NhanVienController(frmNV);
            pnlCards.add(wrapFormContent(frmNV.getContentPane()), "NhanVien");
            frmNV.setContentPane(new JPanel());
            reloadMap.put("NhanVien", () -> invokeLoadData(nvCtrl));
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Nhân Viên"), "NhanVien");
        }
        try {
            JFTaiKhoan frmTK = new JFTaiKhoan();
            TaiKhoanController tkCtrl = new TaiKhoanController(frmTK);
            pnlCards.add(wrapFormContent(frmTK.getContentPane()), "TaiKhoan");
            frmTK.setContentPane(new JPanel());
            reloadMap.put("TaiKhoan", () -> invokeLoadData(tkCtrl));
        } catch (Exception ex) {
            pnlCards.add(createPlaceholder("Lỗi nạp form Tài Khoản"), "TaiKhoan");
        }
    }

    private JPanel wrapFormContent(Container content) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(util.TechStoreUI.BG_MAIN);
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(util.TechStoreUI.BG_MAIN);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(util.TechStoreUI.TEXT_MUTED);
        p.add(l);
        return p;
    }
    /** Logo TS với gradient indigo → violet */
    private JPanel buildTSLogo(int w, int h) {
        JPanel box = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, indigo600, getWidth(), getHeight(), violet600);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String t = "TS";
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2,
                              (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        box.setPreferredSize(new Dimension(w, h));
        box.setOpaque(false);
        return box;
    }

    /** Avatar tròn với initials động theo user đăng nhập */
    private JPanel buildAvatar(int size) {
        final String initials = loggedInInitials;
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, indigo600, getWidth(), getHeight(), violet600);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, size > 34 ? 12 : 10));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, (getWidth() - fm.stringWidth(initials)) / 2,
                              (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(size, size));
        av.setOpaque(false);
        return av;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INNER CLASS — MiniItem (tile nhỏ trong mini sidebar)
    // ══════════════════════════════════════════════════════════════════════════
    class MiniItem extends JPanel {
        final int    index;
        final String abbr;
        boolean active  = false;
        boolean hovered = false;

        MiniItem(String abbr, int index) {
            this.abbr  = abbr;
            this.index = index;
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(MINI_W, 40));
            setPreferredSize(new Dimension(MINI_W, 40));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setToolTipText(ENTRIES[index].label); // hiện tooltip khi hover

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!active) { hovered = true;  repaint(); }
                }
                @Override public void mouseExited (MouseEvent e) {
                    hovered = false; repaint();
                }
            });
        }

        void setActive(boolean a) { active = a; hovered = false; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = 8, bh = 30;
            int bw = getWidth() - pad * 2;
            int bx = pad, by = (getHeight() - bh) / 2;

            if (active) {
                GradientPaint gp = new GradientPaint(bx, by, indigo600, bx + bw, by, violet600);
                g2.setPaint(gp);
                g2.fillRoundRect(bx, by, bw, bh, 10, 10);
                g2.setColor(Color.WHITE);
            } else if (hovered) {
                g2.setColor(sidebarHover);
                g2.fillRoundRect(bx, by, bw, bh, 10, 10);
                g2.setColor(textNormal);
            } else {
                g2.setColor(new Color(30, 41, 59, 100));
                g2.fillRoundRect(bx, by, bw, bh, 10, 10);
                g2.setColor(textMuted);
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int tx = bx + (bw - fm.stringWidth(abbr)) / 2;
            int ty = by + (bh + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(abbr, tx, ty);
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INNER CLASS — DrawerItem (menu item đầy đủ trong overlay drawer)
    // ══════════════════════════════════════════════════════════════════════════
    class DrawerItem extends JPanel {
        final int index;
        boolean active  = false;
        boolean hovered = false;
        JLabel  lblText, lblChevron;

        DrawerItem(String text, int index, boolean hasBadge) {
            this.index = index;
            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(2, 4, 2, 4));

            JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
            pnlLeft.setOpaque(false);
            lblText = new JLabel(text);
            lblText.setForeground(textNormal);
            lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            pnlLeft.add(lblText);
            add(pnlLeft, BorderLayout.CENTER);

            if (hasBadge) {
                JLabel lblBadge = new JLabel("Mới") {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(239, 68, 68));
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                        g2.setColor(Color.WHITE);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        String t = getText();
                        g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2,
                                      (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                        g2.dispose();
                    }
                };
                lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
                lblBadge.setForeground(Color.WHITE);
                lblBadge.setOpaque(false);
                lblBadge.setBorder(new EmptyBorder(2, 7, 2, 7));
                JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
                pnlRight.setOpaque(false);
                pnlRight.add(lblBadge);
                add(pnlRight, BorderLayout.EAST);
            }

            lblChevron = new JLabel("›");
            lblChevron.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblChevron.setForeground(Color.WHITE);
            lblChevron.setVisible(false);
            JPanel pnlChevron = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
            pnlChevron.setOpaque(false);
            pnlChevron.add(lblChevron);
            if (!hasBadge) add(pnlChevron, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!active) { hovered = true;  repaint(); }
                }
                @Override public void mouseExited (MouseEvent e) {
                    hovered = false; repaint();
                }
            });
        }

        void setActive(boolean a) {
            active = a; hovered = false;
            if (a) {
                lblText.setForeground(Color.WHITE);
                lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblChevron.setVisible(true);
            } else {
                lblText.setForeground(textNormal);
                lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lblChevron.setVisible(false);
            }
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 12, w = getWidth(), h = getHeight();
            if (active) {
                GradientPaint gp = new GradientPaint(0, 0, indigo600, w, 0, violet600);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
            } else if (hovered) {
                g2.setColor(sidebarHover);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
   ///** Gọi loadData() dù private/public — dùng reflection */
    public static void main(String[] args) {
        util.TechStoreUI.installLookAndFeel();
        SwingUtilities.invokeLater(() -> new JFMenu("Người dùng", "USER").setVisible(true));
    }
    /** Gọi loadData() dù private/public — dùng reflection */
private void invokeLoadData(Object ctrl) {
    try {
        java.lang.reflect.Method m = ctrl.getClass().getDeclaredMethod("loadData");
        m.setAccessible(true); // bypass private
        m.invoke(ctrl);
    } catch (Exception ex) {
        System.err.println("Không gọi được loadData() trên " + ctrl.getClass().getSimpleName());
    }
}
}
