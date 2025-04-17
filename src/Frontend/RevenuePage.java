package Frontend;

import DBConnection.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RevenuePage extends JPanel {

    private JTable revenueTable;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> paymentTypeComboBox;
    private JTextField minAmountField;
    private JTextField maxAmountField;
    private JPanel searchInputPanel;
    private JPanel bottomPanel;

    public RevenuePage() {
        setLayout(new BorderLayout());

        // 🔍 Search Panel at the Top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchTypeComboBox = new JComboBox<>(new String[]{"PaymentType", "Amount"});
        searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchInputPanel);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // 🧾 Revenue Table in Center
        revenueTable = new JTable();
        add(new JScrollPane(revenueTable), BorderLayout.CENTER);

        // 💰 Bottom Panel for total revenue
        bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        // Load All Records Initially
        fetchAndDisplayRevenue(null, null, null);

        // Update UI based on selected search type
        searchTypeComboBox.addActionListener(e -> updateSearchInputUI());

        // Search button functionality
        searchButton.addActionListener(e -> {
            String type = (String) searchTypeComboBox.getSelectedItem();
            if ("PaymentType".equals(type)) {
                String value = (String) paymentTypeComboBox.getSelectedItem();
                fetchAndDisplayRevenue("PaymentType", value, null);
            } else if ("Amount".equals(type)) {
                try {
                    Double min = Double.parseDouble(minAmountField.getText());
                    Double max = Double.parseDouble(maxAmountField.getText());
                    fetchAndDisplayRevenue("Amount", min, max);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Min and Max amount.");
                }
            }
        });

        // Initialize input fields for first load
        updateSearchInputUI();
    }

    private void updateSearchInputUI() {
        searchInputPanel.removeAll();
        String selectedType = (String) searchTypeComboBox.getSelectedItem();

        if ("PaymentType".equals(selectedType)) {
            paymentTypeComboBox = new JComboBox<>(new String[]{"Card", "UPI", "Cash"});
            searchInputPanel.add(paymentTypeComboBox);
        } else if ("Amount".equals(selectedType)) {
            minAmountField = new JTextField(6);
            maxAmountField = new JTextField(6);
            searchInputPanel.add(new JLabel("Min:"));
            searchInputPanel.add(minAmountField);
            searchInputPanel.add(new JLabel("Max:"));
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

            String[] columns = {"TransactionID", "Amount", "PaymentType", "TicketNumber"};
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
            bottomPanel.add(totalRevenueLabel, BorderLayout.EAST);
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
}
