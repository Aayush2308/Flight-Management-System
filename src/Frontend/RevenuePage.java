package Frontend;

import DBConnection.DBConnection;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class RevenuePage extends JPanel {
    // Component declarations
    private JTable revenueTable;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> paymentTypeComboBox;
    private JTextField minAmountField;
    private JTextField maxAmountField;
    private JPanel searchInputPanel;
    private JPanel bottomPanel;
    private JPanel contentWrapper;

    // Design constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public RevenuePage() {
        setupMainPanel();
        createComponents();
        layoutComponents();
        setupListeners();
        initializeData();
    }

    private void setupMainPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createComponents() {
        // Create main wrapper
        contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(createShadowBorder());

        // Create navbar
        createNavbar();

        // Create search section
        createSearchSection();

        // Create table
        createTable();

        // Create summary section
        createSummarySection();
    }

    private Border createShadowBorder() {
        return BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                new EmptyBorder(10, 15, 10, 15)
            )
        );
    }

    private void createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), 0, SECONDARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        navbar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 70));

        // Navigation buttons
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navButtons.setOpaque(false);
        
        JButton backButton = createNavButton("←");
        JButton forwardButton = createNavButton("→");
        
        navButtons.add(backButton);
        navButtons.add(forwardButton);

        // Title
        JLabel title = new JLabel("Revenue Dashboard", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);

        navbar.add(navButtons, BorderLayout.WEST);
        navbar.add(title, BorderLayout.CENTER);
        contentWrapper.add(navbar);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(ACCENT_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(ACCENT_COLOR);
                } else {
                    g2d.setColor(new Color(255, 255, 255, 80));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(HEADER_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text, 
                    (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2
                );
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(45, 45));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createSearchSection() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(20, 10, 20, 10),
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 20))
        ));

        
        // Search type selector
        JLabel searchLabel = new JLabel("Filter by:");
        searchLabel.setFont(HEADER_FONT);
        searchLabel.setForeground(PRIMARY_COLOR);
        
        searchTypeComboBox = new JComboBox<>(new String[]{"PaymentType", "Amount"});
        styleComboBox(searchTypeComboBox);
        
        // Search input panel
        searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchInputPanel.setBackground(Color.WHITE);
        
        // Search button
        JButton searchButton = createStyledButton("Search", ACCENT_COLOR);

        searchButton.addActionListener(e -> {
            String selectedFilter = (String) searchTypeComboBox.getSelectedItem();
        
            if ("PaymentType".equals(selectedFilter)) {
                String paymentType = (String) paymentTypeComboBox.getSelectedItem();
                if (paymentType.equals("ALL")) {
                    fetchAndDisplayRevenue(null, null, null);
                } else {
                    fetchAndDisplayRevenue("PaymentType", paymentType, null);
                }
            } else if ("Amount".equals(selectedFilter)) {
                try {
                    double min = Double.parseDouble(minAmountField.getText());
                    double max = Double.parseDouble(maxAmountField.getText());
                    fetchAndDisplayRevenue("Amount", min, max);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for Min and Max Amount.");
                }
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchInputPanel);
        searchPanel.add(searchButton);

        contentWrapper.add(searchPanel);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(REGULAR_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        comboBox.setForeground(PRIMARY_COLOR);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(REGULAR_FONT);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return this;
            }
        });
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.setFont(REGULAR_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text,
                    (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2
                );
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createTable() {
        revenueTable = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isCellSelected(row, column)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return comp;
            }
        };

        revenueTable.setFont(REGULAR_FONT);
        revenueTable.setRowHeight(35);
        revenueTable.setShowGrid(false);
        revenueTable.setIntercellSpacing(new Dimension(0, 0));
        revenueTable.getTableHeader().setDefaultRenderer(new ModernHeaderRenderer());

        JScrollPane scrollPane = new JScrollPane(revenueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        contentWrapper.add(scrollPane);
    }

    private class ModernHeaderRenderer extends DefaultTableCellRenderer {
        public ModernHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setFont(HEADER_FONT);
            setBackground(PRIMARY_COLOR);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
    }

    private void createSummarySection() {
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        contentWrapper.add(bottomPanel);
    }

    private void layoutComponents() {
        add(contentWrapper, BorderLayout.CENTER);
    }

    private void setupListeners() {
        searchTypeComboBox.addActionListener(e -> updateSearchInputUI());
    }

    private void updateSearchInputUI() {
        searchInputPanel.removeAll();
        String selectedType = (String) searchTypeComboBox.getSelectedItem();
        
        if ("PaymentType".equals(selectedType)) {
            paymentTypeComboBox = new JComboBox<>(new String[]{"ALL", "Card", "UPI", "Cash"});
            paymentTypeComboBox.setFont(new Font("SansSerif", Font.BOLD, 14));
            searchInputPanel.add(paymentTypeComboBox);
        } else if ("Amount".equals(selectedType)) {
            minAmountField = new JTextField(6);
            maxAmountField = new JTextField(6);
            minAmountField.setFont(new Font("SansSerif", Font.BOLD, 14));
            maxAmountField.setFont(new Font("SansSerif", Font.BOLD, 14));
        
            JLabel minLabel = new JLabel("Min:");
            minLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            JLabel maxLabel = new JLabel("Max:");
            maxLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
            searchInputPanel.add(minLabel);
            searchInputPanel.add(minAmountField);
            searchInputPanel.add(maxLabel);
            searchInputPanel.add(maxAmountField);
        }
        
        searchInputPanel.revalidate();
        searchInputPanel.repaint();
    }
        
    private void fetchAndDisplayRevenue(String filterType, Object value1, Object value2) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT transactionId, amount, paymentType, ticketNumber FROM Revenue";
        
            if ("PaymentType".equals(filterType)) {
                sql += " WHERE paymentType = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, value1.toString());
            } else if ("Amount".equals(filterType)) {
                sql += " WHERE amount BETWEEN ? AND ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, (Double) value1);
                pstmt.setDouble(2, (Double) value2);
            } else {
                pstmt = conn.prepareStatement(sql);
            }
        
            rs = pstmt.executeQuery();
        
            String[] columns = {"Transaction ID", "Amount", "Payment Type", "Ticket Number"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            revenueTable.setModel(model);
        
            double totalRevenue = 0;
        
            while (rs.next()) {
                int transactionId = rs.getInt("transactionId");
                double amount = rs.getDouble("amount");
                String paymentType = rs.getString("paymentType");
                String ticketNumber = rs.getString("ticketNumber");
        
                model.addRow(new Object[]{transactionId, amount, paymentType, ticketNumber});
                totalRevenue += amount;
            }
        
            bottomPanel.removeAll();
            JLabel totalRevenueLabel = new JLabel("Total Revenue: ₹" + totalRevenue);
            totalRevenueLabel.setForeground(new Color(0, 102, 204));
            totalRevenueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            bottomPanel.add(totalRevenueLabel);
        
            revenueTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
            revenueTable.setRowHeight(25);
        
            JTableHeader header = revenueTable.getTableHeader();
            header.setFont(new Font("SansSerif", Font.BOLD, 16));
            header.setBackground(new Color(240, 240, 240));
            header.setForeground(Color.BLACK);
        
            bottomPanel.revalidate();
            bottomPanel.repaint();
        
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                DBConnection.closeConnection();
            } catch (SQLException e) {
                System.err.println("Error closing DB: " + e.getMessage());
            }
        }
    }

    private void initializeData() {
        updateSearchInputUI();
        fetchAndDisplayRevenue(null, null, null);
    }
}
