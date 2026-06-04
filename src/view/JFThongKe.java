package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import util.TechStoreUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class JFThongKe extends JFrame {

    private final DefaultPieDataset<String> datasetNganhHang = new DefaultPieDataset<>();
    private final DefaultCategoryDataset datasetDoanhThu = new DefaultCategoryDataset();
    private JFreeChart donutChart;
    private JFreeChart revenueChart;
    private ChartPanel donutChartPanel;
    private ChartPanel revenueChartPanel;
    private JPanel pnlMain;
    private JLabel lblLastUpdated;

    public JFThongKe() {
        setTitle("Thống Kê - TechStore");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(TechStoreUI.BG_MAIN);
        initComponents();
    }

    private void initComponents() {
        pnlMain = new JPanel(new BorderLayout(0, 18));
        pnlMain.setBackground(TechStoreUI.BG_MAIN);
        pnlMain.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel pnlHead = new JPanel(new BorderLayout());
        pnlHead.setOpaque(false);
        JLabel lblTitle = new JLabel("Thống Kê Bán Hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TechStoreUI.TEXT_TITLE);
        lblLastUpdated = TechStoreUI.createMutedLabel("Đang tải dữ liệu...");
        pnlHead.add(lblTitle, BorderLayout.WEST);
        pnlHead.add(lblLastUpdated, BorderLayout.EAST);

        JPanel pnlCharts = new JPanel(new GridLayout(1, 2, 18, 0));
        pnlCharts.setOpaque(false);
        pnlCharts.add(createChartCard("Thống Kê Nhóm Ngành Hàng Bán Chạy", createDonutPanel()));
        pnlCharts.add(createChartCard("Biểu Đồ Doanh Thu 7 Ngày Qua", createRevenuePanel()));

        pnlMain.add(pnlHead, BorderLayout.NORTH);
        pnlMain.add(pnlCharts, BorderLayout.CENTER);
        add(pnlMain);
    }

    private JPanel createChartCard(String title, ChartPanel chartPanel) {
        JPanel card = TechStoreUI.createCard();
        card.setLayout(new BorderLayout(0, 12));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(TechStoreUI.TEXT_TITLE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    private ChartPanel createDonutPanel() {
        JFreeChart chart = ChartFactory.createRingChart(
            "",
            datasetNganhHang,
            true,
            true,
            false
        );
        chart.setBackgroundPaint(Color.WHITE);
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));

        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setSectionDepth(0.35);
        plot.setSeparatorsVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})",
            new DecimalFormat("#,##0"),
            new DecimalFormat("0.0%")
        ));
        plot.setLabelBackgroundPaint(new Color(248, 250, 252));
        plot.setLabelOutlinePaint(TechStoreUI.BORDER);
        plot.setLabelShadowPaint(null);
        plot.setDrawingSupplier(createChartColors());

        donutChart = chart;
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPopupMenu(null);
        panel.setBackground(Color.WHITE);
        donutChartPanel = panel;
        return panel;
    }

    private ChartPanel createRevenuePanel() {
        JFreeChart chart = ChartFactory.createBarChart(
            "",
            "Ngày",
            "Doanh thu",
            datasetDoanhThu,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(226, 232, 240));
        plot.setDomainGridlinesVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0"));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, TechStoreUI.INDIGO);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.12);

        revenueChart = chart;
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPopupMenu(null);
        panel.setBackground(Color.WHITE);
        revenueChartPanel = panel;
        return panel;
    }

    private DefaultDrawingSupplier createChartColors() {
        Paint[] colors = {
            new Color(79, 70, 229),
            new Color(14, 165, 233),
            new Color(16, 185, 129),
            new Color(245, 158, 11),
            new Color(239, 68, 68),
            new Color(139, 92, 246),
            new Color(236, 72, 153),
            new Color(100, 116, 139)
        };
        return new DefaultDrawingSupplier(
            colors,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
        );
    }

    public DefaultPieDataset<String> getDatasetNganhHang() {
        return datasetNganhHang;
    }

    public DefaultCategoryDataset getDatasetDoanhThu() {
        return datasetDoanhThu;
    }

    public void setLastUpdated(String text) {
        lblLastUpdated.setText(text);
    }

    public void applyTheme() {
        getContentPane().setBackground(TechStoreUI.BG_MAIN);
        applyThemeToComponents(this);
        if (pnlMain != null) {
            pnlMain.setBackground(TechStoreUI.BG_MAIN);
        }
        lblLastUpdated.setForeground(TechStoreUI.TEXT_MUTED);
        applyChartTheme(donutChart, donutChartPanel);
        applyChartTheme(revenueChart, revenueChartPanel);
        revalidate();
        repaint();
    }

    private void applyChartTheme(JFreeChart chart, ChartPanel panel) {
        if (chart == null) return;
        Color bg = TechStoreUI.CARD_BG;
        Color fg = TechStoreUI.TEXT_TITLE;
        Color grid = TechStoreUI.BORDER;

        chart.setBackgroundPaint(bg);
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(bg);
            chart.getLegend().setItemPaint(fg);
        }

        if (chart.getPlot() instanceof RingPlot) {
            RingPlot plot = (RingPlot) chart.getPlot();
            plot.setBackgroundPaint(bg);
            plot.setLabelPaint(fg);
            plot.setLabelBackgroundPaint(TechStoreUI.BG_MAIN);
            plot.setLabelOutlinePaint(grid);
            plot.setLabelLinkPaint(TechStoreUI.TEXT_MUTED);
            plot.setOutlineVisible(false);
        } else if (chart.getPlot() instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(bg);
            plot.setRangeGridlinePaint(grid);
            plot.getDomainAxis().setTickLabelPaint(TechStoreUI.TEXT_MUTED);
            plot.getDomainAxis().setLabelPaint(TechStoreUI.TEXT_MUTED);
            plot.getRangeAxis().setTickLabelPaint(TechStoreUI.TEXT_MUTED);
            plot.getRangeAxis().setLabelPaint(TechStoreUI.TEXT_MUTED);
            plot.setOutlineVisible(false);
        }

        if (panel != null) {
            panel.setBackground(bg);
        }
    }

    private void applyThemeToComponents(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            if (panel.isOpaque()) {
                panel.setBackground(TechStoreUI.CARD_BG);
            }
        }
        if (component instanceof JLabel) {
            component.setForeground(TechStoreUI.TEXT_TITLE);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyThemeToComponents(child);
            }
        }
    }

    public static void main(String[] args) {
        TechStoreUI.installLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            JFThongKe view = new JFThongKe();
            new controller.ThongKeController(view);
            view.setVisible(true);
        });
    }
}
