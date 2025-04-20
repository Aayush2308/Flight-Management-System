package Frontend;

import Components.NavigationBar;
import Components.ShadowBorder;
import Interfaces.NavigationListener;
import Models.Revenue;
import Service.RevenueService;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class RevenuePage extends JPanel {
    private int adminId;
    private NavigationListener navigationListener;
    // Component declarations
    private JTable revenueTable;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> paymentTypeComboBox;
    private JTextField minAmountField;
    private JTextField maxAmountField;
    private JPanel searchInputPanel;
    private JPanel bottomPanel;
    private JPanel contentWrapper;

    private RevenueService revenueService;

    // Design constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public RevenuePage(int adminId, NavigationListener navigationListener) {
        try{
            this.adminId = adminId;
            this.navigationListener = navigationListener;
            revenueService = new RevenueService();
            setupMainPanel();
            createComponents();
            layoutComponents();
            setupListeners();
            initializeData();
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error initializing RevenuePage: " + e.getMessage());
            e.printStackTrace();
        }
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
        contentWrapper.setBorder(ShadowBorder.createShadowBorder());

        // Create navbar
        createNavbar();

        // Create search section
        createSearchSection();

        // Create table
        createTable();

        // Create summary section
        createSummarySection();
    }

    //Uses the Component
    private void createNavbar() {
        NavigationBar navbar = new NavigationBar("Revenue Dashboard", e -> {
        // Handle navigation button clicks
        String command = ((JButton)e.getSource()).getText();
        if ("←".equals(command)) {
            if(navigationListener != null){
                navigationListener.navigateTo(new HomePage(adminId, navigationListener));
            }
        }
    },70);
    contentWrapper.add(navbar);
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
        try {
            List<Revenue> revenues = revenueService.getRevenueData(filterType, value1, value2);
            double totalRevenue = revenueService.calculateTotalRevenue(revenues);
            
            // Update table model
            String[] columns = {"Transaction ID", "Amount", "Payment Type", "Ticket Number"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            
            for (Revenue revenue : revenues) {
                model.addRow(new Object[]{
                    revenue.getTransactionId(),
                    revenue.getAmount(),
                    revenue.getPaymentType(),
                    revenue.getTicketNumber()
                });
            }
            
            revenueTable.setModel(model);
            
            // Update summary
            bottomPanel.removeAll();
            JLabel totalRevenueLabel = new JLabel("Total Revenue: " + 
                CURRENCY_FORMAT.format(totalRevenue));
            totalRevenueLabel.setForeground(PRIMARY_COLOR);
            totalRevenueLabel.setFont(HEADER_FONT);
            bottomPanel.add(totalRevenueLabel);
            
            bottomPanel.revalidate();
            bottomPanel.repaint();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage());
        }
    }

    private void initializeData() {
        updateSearchInputUI();
        fetchAndDisplayRevenue(null, null, null);
    }
}
