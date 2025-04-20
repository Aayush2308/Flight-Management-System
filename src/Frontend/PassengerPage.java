// Frontend/PassengerPage.java
package Frontend;

import Components.NavigationBar;
import Models.Passenger;
import Service.PassengerService;
import Interfaces.NavigationListener;
import Exception.InvalidPhoneNumberException;
import Utilities.InputValidator;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List;
import javax.swing.event.*;

public class PassengerPage extends JPanel {
    private PassengerService passengerService;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JPanel listPanel, detailsPanel;
    private JLabel detailName, detailTicket, detailPassport, detailContact, detailLuggage;

    private final Color darkBg = new Color(30, 32, 34);
    private final Color darkPanel = new Color(40, 42, 45);
    private final Color primary = new Color(0, 150, 136);
    private final Font font = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font titleFont = new Font("Segoe UI Semibold", Font.BOLD, 24);

    public PassengerPage(int adminId, NavigationListener navigationListener) {
        this.passengerService = new PassengerService();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1100, 650));

        NavigationBar navBar = new NavigationBar("Passenger", e -> 
            navigationListener.navigateTo(new HomePage(adminId, navigationListener)), 70);
        add(navBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(darkBg);
        add(mainPanel, BorderLayout.CENTER);

        initListPanel();
        initDetailsPanel();

        mainPanel.add(listPanel, "ListView");
        mainPanel.add(detailsPanel, "DetailView");

        loadPassengers();
        cardLayout.show(mainPanel, "ListView");
    }

    private void initListPanel() {
        listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(darkBg);
        listPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkBg);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("✈ Passenger List");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(title, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(darkBg);
        searchPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(font);
        searchLabel.setForeground(Color.WHITE);
        searchPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(font);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setBackground(darkPanel);
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setToolTipText("Search by name, ticket, passport, or contact");
        searchPanel.add(searchField, BorderLayout.CENTER);

        headerPanel.add(searchPanel, BorderLayout.EAST);
        listPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Name", "Ticket Number", "Passport", "Contact", "Actions"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? JButton.class : String.class;
            }
        };
        
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
        };
        
        table.setFont(font);
        table.setRowHeight(35);
        table.setForeground(Color.WHITE);
        table.setBackground(darkPanel);
        table.setGridColor(new Color(60, 60, 60));
        table.setSelectionBackground(new Color(70, 70, 70));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Custom header renderer
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(50, 52, 55));
        header.setForeground(Color.BLACK);
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
        
        JButton addBtn = createButton("➕ Add Passenger", new Color(0, 150, 136));
        JButton deleteBtn = createButton("🗑 Delete Passenger", new Color(239, 83, 80));
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

        addBtn.addActionListener(e -> {
            JTextField nameField = new JTextField(20);
            JTextField passportField = new JTextField(20);
            JTextField contactField = new JTextField(20);
        
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Enter Name (Only letters):"));
            panel.add(nameField);
            panel.add(new JLabel("Enter Passport Number:"));
            panel.add(passportField);
            panel.add(new JLabel("Enter 10-digit Contact Number:"));
            panel.add(contactField);
        
            int result = JOptionPane.showConfirmDialog(null, panel, "Add New Passenger",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String passport = passportField.getText().trim();
                String contact = contactField.getText().trim();

                if (!name.matches("[a-zA-Z ]{1,100}")) {
                    JOptionPane.showMessageDialog(PassengerPage.this, "Invalid name.");
                    return;
                }

                if (!passport.matches(".{1,50}")) {
                    JOptionPane.showMessageDialog(PassengerPage.this, "Invalid passport.");
                    return;
                }

                try {
                    contact = InputValidator.validatePhoneNumber(contact);
                } catch (InvalidPhoneNumberException ex) {
                    JOptionPane.showMessageDialog(PassengerPage.this, ex.getMessage());
                    return;
                }
        
                try {
                    passengerService.addPassenger(passport, name, contact);
                    JOptionPane.showMessageDialog(PassengerPage.this, "Passenger added successfully.");
                    refreshPassengerList();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PassengerPage.this, "Error adding passenger.");
                }
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ticketNumber = JOptionPane.showInputDialog("Enter Ticket Number to Delete:");
                if (ticketNumber != null && !ticketNumber.trim().isEmpty()) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel(); // 🔧 this line resolves the model error
                    boolean found = false;
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (model.getValueAt(i, 1).toString().equalsIgnoreCase(ticketNumber.trim())) {
                            model.removeRow(i);
                            JOptionPane.showMessageDialog(null, "Passenger deleted successfully.");
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        JOptionPane.showMessageDialog(null, "Ticket number not found.");
                    }
                }
            }
        });
        
    }

    private void initDetailsPanel() {
        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(darkBg);
        detailsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(darkBg);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("👤 Passenger Details");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(title, BorderLayout.WEST);

        JButton backButton = createButton("← Back to List", new Color(100, 100, 100));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ListView"));
        headerPanel.add(backButton, BorderLayout.EAST);

        detailsPanel.add(headerPanel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        infoPanel.setBorder(new EmptyBorder(30, 150, 30, 150));
        infoPanel.setBackground(darkPanel);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        detailName = createDetailLabel();
        detailTicket = createDetailLabel();
        detailPassport = createDetailLabel();
        detailContact = createDetailLabel();
        detailLuggage = createDetailLabel();

        infoPanel.add(detailName);
        infoPanel.add(detailTicket);
        infoPanel.add(detailPassport);
        infoPanel.add(detailContact);
        infoPanel.add(detailLuggage);

        detailsPanel.add(infoPanel, BorderLayout.CENTER);
    }

    private JLabel createDetailLabel() {
        JLabel lbl = new JLabel();
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return lbl;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(Color.BLACK);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });
        
        return btn;
    }

    private void loadPassengers() {
        tableModel.setRowCount(0);
        try {
            List<Passenger> passengers = passengerService.getAllPassengers();
            for (Passenger passenger : passengers) {
                Vector<Object> row = new Vector<>();
                row.add(passenger.getName());
                row.add(passenger.getTicketNumber());
                row.add(passenger.getPassportNumber());
                row.add(passenger.getContactNumber());
                row.add("View Details");
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(PassengerPage.this, "Error loading passengers.");
        }
    }

    private void refreshPassengerList() {
        loadPassengers();
    }
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(Color.WHITE);  // White background
            setForeground(Color.BLACK);  // Black text
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),  // Black border
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
        private String ticket;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = createButton("View Details", primary);
            button.addActionListener(e -> showDetails(ticket));
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                   boolean isSelected, int row, int column) {
            ticket = table.getValueAt(row, 1).toString();
            return button;
        }

        private void showDetails(String ticketNumber) {
            try {
                Passenger passenger = passengerService.getPassengerDetails(ticketNumber);
                float luggageWeight = passengerService.getLuggageWeight(ticketNumber);
        
                if (passenger != null) {
                    detailName.setText("Name: " + passenger.getName());
                    detailTicket.setText("Ticket #: " + passenger.getTicketNumber());
                    detailPassport.setText("Passport #: " + passenger.getPassportNumber());
                    detailContact.setText("Contact #: " + passenger.getContactNumber());
                }
        
                if (luggageWeight >= 0) {
                    detailLuggage.setText("Luggage Weight: " + luggageWeight + " kg");
                } else {
                    detailLuggage.setText("Luggage: Not Available");
                }
        
                cardLayout.show(mainPanel, "DetailView");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(PassengerPage.this, "Error retrieving passenger details.");
            }
        }
        
    }
}