package Frontend;

import DBConnection.DBConnection;
import Interfaces.NavigationListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class FlightDetails extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private int adminId;
    private NavigationListener navigationListener;

    private JPanel listPanel, detailsPanel;
    private JLabel detailFlightId, detailRunway, detailIada, detailSource, detailDestination, detailDeparture, detailArrival;

    // Updated color scheme
    private final Color darkBg = new Color(30, 32, 34);
    private final Color darkPanel = new Color(40, 42, 45);
    private final Color white = Color.WHITE;
    private final Color black = Color.BLACK;
    private final Color greyHover = new Color(200, 200, 200);
    private final Font font = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font titleFont = new Font("Segoe UI Semibold", Font.BOLD, 24);

    public FlightDetails(int adminId, NavigationListener navigationListener) {
        this.adminId = adminId;
        this.navigationListener = navigationListener;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1100, 650));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(darkBg);
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JButton backButton = createBackButton();
        topPanel.add(backButton, BorderLayout.WEST);
        
        JLabel title = new JLabel("Flight Management");
        title.setFont(titleFont);
        title.setForeground(white);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(title, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(darkBg);
        add(mainPanel, BorderLayout.CENTER);

        initListPanel();
        initDetailsPanel();

        mainPanel.add(listPanel, "ListView");
        mainPanel.add(detailsPanel, "DetailView");

        loadFlights();
        cardLayout.show(mainPanel, "ListView");
    }

    private JButton createBackButton() {
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(white);
        backButton.setForeground(black);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(greyHover);
            }
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(white);
            }
        });
        
        backButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo(new HomePage(adminId, navigationListener));
            }
        });
        
        return backButton;
    }

    private void initListPanel() {
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(darkBg);
        listPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkBg);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("✈ Flight List");
        title.setFont(titleFont);
        title.setForeground(white);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(title, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(darkBg);
        searchPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(font);
        searchLabel.setForeground(white);
        searchPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(font);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setBackground(darkPanel);
        searchField.setForeground(white);
        searchField.setCaretColor(white);
        searchField.setToolTipText("Search by flight ID, source, destination, or IADA number");
        searchPanel.add(searchField, BorderLayout.CENTER);

        headerPanel.add(searchPanel, BorderLayout.EAST);
        listPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Flight ID", "Runway", "IADA", "Source", "Destination", "Actions"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 5 ? JButton.class : String.class;
            }
        };
        
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };
        
        table.setFont(font);
        table.setRowHeight(35);
        table.setForeground(white);
        table.setBackground(darkPanel);
        table.setGridColor(new Color(60, 60, 60));
        table.setSelectionBackground(new Color(70, 70, 70));
        table.setSelectionForeground(white);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Custom header renderer - Changed to make headers visible
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(220, 220, 220)); // Light gray background
        header.setForeground(black); // Black text for headers
        header.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Center-align all columns except Actions
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(darkBg);
        
        JButton addBtn = createButton("➕ Add Flight", white);
        JButton deleteBtn = createButton("🗑 Delete Flight", white);
        
        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        listPanel.add(btnPanel, BorderLayout.SOUTH);

        // Search Functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }

            private void filterTable() {
                String query = searchField.getText().toLowerCase();
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
            }
        });

        // Add Flight
        addBtn.addActionListener(e -> showAddFlightForm());

        // Delete Flight
        deleteBtn.addActionListener(e -> {
            String flightId = JOptionPane.showInputDialog(FlightDetails.this, "Enter Flight ID to Delete:");
            if (flightId == null || flightId.isEmpty()) return;

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM flight WHERE flightId = ?")) {
                ps.setInt(1, Integer.parseInt(flightId));
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(FlightDetails.this, "Flight deleted successfully.");
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(FlightDetails.this, "No flight found with that ID.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(FlightDetails.this, "Error deleting flight.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(FlightDetails.this, "Please enter a valid Flight ID number.");
            }
        });
    }

    private void showAddFlightForm() {
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(darkPanel);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField flightIdField = new JTextField();
        JTextField runwayField = new JTextField();
        JTextField iadaField = new JTextField();
        JTextField sourceField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField arrivalField = new JTextField();

        formPanel.add(createFormLabel("Flight ID:"));
        formPanel.add(flightIdField);
        formPanel.add(createFormLabel("Runway Number:"));
        formPanel.add(runwayField);
        formPanel.add(createFormLabel("IADA Number:"));
        formPanel.add(iadaField);
        formPanel.add(createFormLabel("Source:"));
        formPanel.add(sourceField);
        formPanel.add(createFormLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(createFormLabel("Departure Time:"));
        formPanel.add(departureField);
        formPanel.add(createFormLabel("Arrival Time:"));
        formPanel.add(arrivalField);

        JButton submitBtn = createButton("Submit", white);
        JButton cancelBtn = createButton("Cancel", white);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(darkPanel);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);

        // Create a container panel for the form
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(darkBg);
        containerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        containerPanel.add(formPanel, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Show the form in a dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Flight");
        dialog.setModal(true);
        dialog.setContentPane(containerPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        cancelBtn.addActionListener(e -> dialog.dispose());

        submitBtn.addActionListener(e -> {
            try {
                int flightId = Integer.parseInt(flightIdField.getText());
                int runway = Integer.parseInt(runwayField.getText());
                String iada = iadaField.getText();
                String source = sourceField.getText();
                String destination = destinationField.getText();
                String departure = departureField.getText();
                String arrival = arrivalField.getText();

                if (iada.isEmpty() || source.isEmpty() || destination.isEmpty() || 
                    departure.isEmpty() || arrival.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled");
                }

                try (Connection con = DBConnection.getConnection()) {
                    // Insert into flight table
                    try (PreparedStatement psFlight = con.prepareStatement(
                            "INSERT INTO flight VALUES (?, ?, ?, ?, ?)")) {
                        psFlight.setInt(1, flightId);
                        psFlight.setInt(2, runway);
                        psFlight.setString(3, iada);
                        psFlight.setString(4, source);
                        psFlight.setString(5, destination);
                        psFlight.executeUpdate();
                    }

                    // Insert into flightSchedule table
                    try (PreparedStatement psSchedule = con.prepareStatement(
                            "INSERT INTO flightSchedule (flightId, departureTime, arrivalTime) VALUES (?, ?, ?)")) {
                        psSchedule.setInt(1, flightId);
                        psSchedule.setString(2, departure);
                        psSchedule.setString(3, arrival);
                        psSchedule.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(FlightDetails.this, "Flight added successfully!");
                    loadFlights();
                    dialog.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(FlightDetails.this, 
                            "Error adding flight: " + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(FlightDetails.this, 
                        "Flight ID and Runway must be numbers", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(FlightDetails.this, 
                        ex.getMessage(), 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(white);
        return label;
    }

    private void initDetailsPanel() {
        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(darkBg);
        detailsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkBg);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("✈ Flight Details");
        title.setFont(titleFont);
        title.setForeground(white);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(title, BorderLayout.WEST);

        JButton backButton = createButton("← Back to List", white);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ListView"));
        headerPanel.add(backButton, BorderLayout.EAST);

        detailsPanel.add(headerPanel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(7, 1, 15, 15));
        infoPanel.setBorder(new EmptyBorder(30, 150, 30, 150));
        infoPanel.setBackground(darkPanel);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        detailFlightId = createDetailLabel();
        detailRunway = createDetailLabel();
        detailIada = createDetailLabel();
        detailSource = createDetailLabel();
        detailDestination = createDetailLabel();
        detailDeparture = createDetailLabel();
        detailArrival = createDetailLabel();

        infoPanel.add(detailFlightId);
        infoPanel.add(detailRunway);
        infoPanel.add(detailIada);
        infoPanel.add(detailSource);
        infoPanel.add(detailDestination);
        infoPanel.add(detailDeparture);
        infoPanel.add(detailArrival);

        detailsPanel.add(infoPanel, BorderLayout.CENTER);
    }

    private JLabel createDetailLabel() {
        JLabel lbl = new JLabel();
        lbl.setForeground(white);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return lbl;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(black); // Black text
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(greyHover); // Grey hover effect
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private void loadFlights() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT flightId, runwayNumber, iadaNumber, source, destination FROM flight")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("flightId"));
                row.add(rs.getInt("runwayNumber"));
                row.add(rs.getString("iadaNumber"));
                row.add(rs.getString("source"));
                row.add(rs.getString("destination"));
                row.add("View Details");
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flights.");
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(white); // White background
            setForeground(black); // Black text
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus, int row, int column) {
            setText("View Details");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int flightId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = createButton("View Details", white);
            button.addActionListener(e -> showFlightDetails(flightId));
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                   boolean isSelected, int row, int column) {
            flightId = (int) table.getValueAt(row, 0);
            return button;
        }

        private void showFlightDetails(int flightId) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     "SELECT f.*, s.departureTime, s.arrivalTime FROM flight f " +
                     "JOIN flightSchedule s ON f.flightId = s.flightId WHERE f.flightId = ?")) {
                
                ps.setInt(1, flightId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    detailFlightId.setText("Flight ID: " + rs.getInt("flightId"));
                    detailRunway.setText("Runway: " + rs.getInt("runwayNumber"));
                    detailIada.setText("IADA: " + rs.getString("iadaNumber"));
                    detailSource.setText("Source: " + rs.getString("source"));
                    detailDestination.setText("Destination: " + rs.getString("destination"));
                    detailDeparture.setText("Departure: " + rs.getString("departureTime"));
                    detailArrival.setText("Arrival: " + rs.getString("arrivalTime"));
                }

                cardLayout.show(mainPanel, "DetailView");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(FlightDetails.this, "Error retrieving flight details.");
            }
        }
    }
}