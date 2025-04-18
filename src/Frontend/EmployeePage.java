package Frontend;

import DBConnection.DBConnection;
import Interfaces.NavigationListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.*;

public class EmployeePage extends JPanel {
    private JTable employeeTable;
    private JComboBox<String> sortComboBox;
    private JButton sortAscButton, sortDescButton;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JPanel topPanel, bottomPanel;
    private JTextField searchField;
    private Integer currentSearchId = null;
    private JButton backButton;

    public EmployeePage(int adminId, NavigationListener navigationListener) {
        setLayout(new BorderLayout());

        // --- Top panel: Search, Sort controls + CRUD buttons ---
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Search components
        searchField = new JTextField(10);
        searchButton = new JButton("Search by ID");
        backButton = new JButton("Back");
        topPanel.add(backButton);
        topPanel.add(new JLabel("Search by ID:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Sort components
        sortComboBox = new JComboBox<>(new String[]{"employeeId", "employeeName", "role", "recordId"});
        sortAscButton = new JButton("Sort ↑");
        sortDescButton = new JButton("Sort ↓");
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortComboBox);
        topPanel.add(sortAscButton);
        topPanel.add(sortDescButton);

        // CRUD buttons
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center: Employee table ---
        employeeTable = new JTable();
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // --- Bottom: status bar ---
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Initial load ---
        fetchAndDisplayEmployees("employeeId", true);

        // --- Listeners ---
        backButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo(new HomePage(adminId, navigationListener));
            }
        });
        sortAscButton.addActionListener(e -> fetchAndDisplayEmployees(
            (String) sortComboBox.getSelectedItem(), true));
        sortDescButton.addActionListener(e -> fetchAndDisplayEmployees(
            (String) sortComboBox.getSelectedItem(), false));
        addButton.addActionListener(e -> showAddDialog());
        updateButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        searchButton.addActionListener(e -> handleSearch());
    }

    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            currentSearchId = null;
        } else {
            try {
                currentSearchId = Integer.parseInt(searchText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID");
                currentSearchId = null;
            }
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private void fetchAndDisplayEmployees(String sortColumn, boolean asc) {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Role", "RecordID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Connection conn = DBConnection.getConnection()) {
            String baseQuery = "SELECT employeeId, employeeName, role, recordId FROM Employee";
            String whereClause = currentSearchId != null ? " WHERE employeeId = ?" : "";
            String orderClause = " ORDER BY " + sortColumn + (asc ? " ASC" : " DESC");
            
            PreparedStatement stmt = conn.prepareStatement(baseQuery + whereClause + orderClause);
            
            if (currentSearchId != null) {
                stmt.setInt(1, currentSearchId);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("employeeId"),
                    rs.getString("employeeName"),
                    rs.getString("role"),
                    rs.getInt("recordId")
                });
            }
            
            employeeTable.setModel(model);
            updateStatusBar(model.getRowCount());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees:\n" + ex.getMessage());
        }
    }

    private void updateStatusBar(int count) {
        bottomPanel.removeAll();
        String status;
        if (currentSearchId != null) {
            status = "Showing results for ID: " + currentSearchId + " (" + count + " found)";
        } else {
            status = "Total Employees: " + count;
        }
        bottomPanel.add(new JLabel(status));
        bottomPanel.revalidate();
    }

    // Rest of the CRUD methods (showAddDialog, showUpdateDialog, deleteSelectedEmployee) 
    // remain unchanged from the original code provided
    // ... [Keep all existing CRUD methods unchanged] ...

    private void showAddDialog() {
        JTextField nameField = new JTextField(15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"GroundStaff","Security","CabinCrew","Pilot"});
        JTextField facilityField = new JTextField(5);
        JTextField flightField = new JTextField(5);
        JLabel flightLabel = new JLabel("FlightID:");

        // Initially disable flight input
        flightLabel.setEnabled(false);
        flightField.setEnabled(false);

        // Toggle flight input based on role
        roleBox.addItemListener(e -> {
            boolean need = e.getStateChange() == ItemEvent.SELECTED && (
                "CabinCrew".equals(e.getItem()) || "Pilot".equals(e.getItem())
            );
            flightLabel.setEnabled(need);
            flightField.setEnabled(need);
            if (!need) flightField.setText("");
        });

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("Name:"));       panel.add(nameField);
        panel.add(new JLabel("Role:"));       panel.add(roleBox);
        panel.add(new JLabel("FacilityID:")); panel.add(facilityField);
        panel.add(flightLabel);                 panel.add(flightField);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Add New Employee", JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String role = (String) roleBox.getSelectedItem();
        int facilityId;
        try {
            facilityId = Integer.parseInt(facilityField.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid Facility ID."); return;
        }
        Integer flightId = null;
        if (flightField.isEnabled()) {
            String ftxt = flightField.getText().trim();
            if (ftxt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Flight ID required for " + role);
                return;
            }
            try {
                flightId = Integer.parseInt(ftxt);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid Flight ID."); return;
            }
        }

        // Validate flight existence
        if (flightId != null) {
            try (Connection vconn = DBConnection.getConnection();
                 PreparedStatement vps = vconn.prepareStatement(
                     "SELECT COUNT(*) FROM Flight WHERE flightId=?")) {
                vps.setInt(1, flightId);
                ResultSet vrs = vps.executeQuery();
                vrs.next();
                if (vrs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Flight ID not found."); return;
                }
            } catch (SQLException sq) {
                JOptionPane.showMessageDialog(this, "Error validating flight: " + sq.getMessage()); return;
            }
        }

        // Proceed with insertion transaction
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int recordId;
            try (PreparedStatement psRec = conn.prepareStatement(
                    "INSERT INTO Records () VALUES()", Statement.RETURN_GENERATED_KEYS
            )) {
                psRec.executeUpdate();
                try (ResultSet gen = psRec.getGeneratedKeys()) {
                    gen.next(); recordId = gen.getInt(1);
                }
            }
            int empId;
            try (PreparedStatement psEmp = conn.prepareStatement(
                    "INSERT INTO Employee (employeeName, role, recordId) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS
            )) {
                psEmp.setString(1, name);
                psEmp.setString(2, role);
                psEmp.setInt(3, recordId);
                psEmp.executeUpdate();
                try (ResultSet gen = psEmp.getGeneratedKeys()) {
                    gen.next(); empId = gen.getInt(1);
                }
            }
            String sql;
            if (flightId != null) {
                sql = "INSERT INTO " + role +
                      " (employeeId, employeeName, facilityId, recordId, flightId) VALUES (?,?,?,?,?)";
            } else {
                sql = "INSERT INTO " + role +
                      " (employeeId, employeeName, facilityId, recordId) VALUES (?,?,?,?)";
            }
            try (PreparedStatement psRole = conn.prepareStatement(sql)) {
                psRole.setInt(1, empId); psRole.setString(2, name);
                psRole.setInt(3, facilityId); psRole.setInt(4, recordId);
                if (flightId != null) psRole.setInt(5, flightId);
                psRole.executeUpdate();
            }
            conn.commit();
            bottomPanel.removeAll();
            bottomPanel.add(new JLabel("Added employee ID: " + empId));
            bottomPanel.revalidate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee:\n" + ex.getMessage());
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private void showUpdateDialog() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an employee to update."); return; }
        int id = (int) employeeTable.getValueAt(row, 0);
        String currName = (String) employeeTable.getValueAt(row, 1);
        String currRole = (String) employeeTable.getValueAt(row, 2);

        // Fetch recordId
        int recordId;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                "SELECT recordId FROM Employee WHERE employeeId=?")) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery(); rs.next(); recordId = rs.getInt(1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching record: " + ex.getMessage()); return;
        }

        JTextField nameField = new JTextField(currName, 15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"GroundStaff","Security","CabinCrew","Pilot"});
        roleBox.setSelectedItem(currRole);
        JTextField facilityField = new JTextField(5);
        JTextField flightField = new JTextField(5);
        JLabel flightLabel = new JLabel("FlightID:");
        flightLabel.setEnabled(false); flightField.setEnabled(false);
        roleBox.addItemListener(e -> {
            boolean need = e.getStateChange()==ItemEvent.SELECTED &&
                ("CabinCrew".equals(e.getItem())||"Pilot".equals(e.getItem()));
            flightLabel.setEnabled(need); flightField.setEnabled(need);
            if (!need) flightField.setText("");
        });

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("Name:"));       panel.add(nameField);
        panel.add(new JLabel("Role:"));       panel.add(roleBox);
        panel.add(new JLabel("FacilityID:")); panel.add(facilityField);
        panel.add(flightLabel);                panel.add(flightField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Update Employee ID " + id,
                                                 JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String newName = nameField.getText().trim();
        String newRole = (String) roleBox.getSelectedItem();
        int newFac;
        try { newFac = Integer.parseInt(facilityField.getText().trim()); }
        catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid Facility ID."); return; }
        Integer newFlight = null;
        if (flightField.isEnabled()) {
            String ftxt = flightField.getText().trim();
            if (ftxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Flight ID required for " + newRole); return; }
            try { newFlight = Integer.parseInt(ftxt); } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid Flight ID."); return; }
            // Validate
            try (Connection vconn = DBConnection.getConnection();
                 PreparedStatement vps = vconn.prepareStatement(
                     "SELECT COUNT(*) FROM Flight WHERE flightId=?")) {
                vps.setInt(1, newFlight); ResultSet vrs = vps.executeQuery(); vrs.next();
                if (vrs.getInt(1)==0) { JOptionPane.showMessageDialog(this, "Flight ID not found."); return; }
            } catch (SQLException sq) { JOptionPane.showMessageDialog(this, "Error validating flight: " + sq.getMessage()); return; }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEmp = conn.prepareStatement(
                    "UPDATE Employee SET employeeName=?, role=? WHERE employeeId=?")) {
                psEmp.setString(1, newName); psEmp.setString(2, newRole); psEmp.setInt(3, id);
                psEmp.executeUpdate();
            }
            if (!newRole.equals(currRole)) {
                try (PreparedStatement psDel = conn.prepareStatement(
                        "DELETE FROM " + currRole + " WHERE employeeId=?")) {
                    psDel.setInt(1, id); psDel.executeUpdate(); }
                String sqlIns = newFlight!=null
                    ? "INSERT INTO "+newRole+" (employeeId,employeeName,facilityId,recordId,flightId) VALUES (?,?,?,?,?)"
                    : "INSERT INTO "+newRole+" (employeeId,employeeName,facilityId,recordId) VALUES (?,?,?,?)";
                try (PreparedStatement psIns = conn.prepareStatement(sqlIns)) {
                    psIns.setInt(1,id); psIns.setString(2,newName);
                    psIns.setInt(3,newFac); psIns.setInt(4,recordId);
                    if (newFlight!=null) psIns.setInt(5,newFlight);
                    psIns.executeUpdate();
                }
            } else {
                String tbl = currRole;
                String upd = newFlight!=null
                    ? "UPDATE "+tbl+" SET employeeName=?,facilityId=?,flightId=? WHERE employeeId=?"
                    : "UPDATE "+tbl+" SET employeeName=?,facilityId=? WHERE employeeId=?";
                try (PreparedStatement psUpd = conn.prepareStatement(upd)) {
                    psUpd.setString(1,newName); psUpd.setInt(2,newFac);
                    if (newFlight!=null) { psUpd.setInt(3,newFlight); psUpd.setInt(4,id); }
                    else { psUpd.setInt(3,id); }
                    psUpd.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee:\n" + ex.getMessage());
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private void deleteSelectedEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an employee to delete."); return; }
        int id = (int) employeeTable.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Really delete employee ID " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM Employee WHERE employeeId=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting employee:\n" + ex.getMessage());
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }
}

//Ignore this code, It was for modularity, not needed yet.

// package Frontend;

// import Models.Employee;
// import Service.EmployeeService;
// import Components.EmployeeTablePanel;
// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.util.List;

// public class EmployeePage extends JPanel {
//     private EmployeeTablePanel tablePanel;
//     private JComboBox<String> sortComboBox;
//     private JButton sortAscButton, sortDescButton;
//     private JButton addButton, updateButton, deleteButton, searchButton;
//     private JPanel topPanel, bottomPanel;
//     private JTextField searchField;
//     private Integer currentSearchId = null;
//     private JButton backButton;
//     private EmployeeService employeeService;

//     public EmployeePage() {
//         this.employeeService = new EmployeeService();
//         initializeUI();
//     }

//     private void initializeUI() {
//         setLayout(new BorderLayout());
        
//         // Initialize components
//         tablePanel = new EmployeeTablePanel();
//         initializeTopPanel();
//         initializeBottomPanel();
        
//         // Add components to layout
//         add(topPanel, BorderLayout.NORTH);
//         add(tablePanel, BorderLayout.CENTER);
//         add(bottomPanel, BorderLayout.SOUTH);
        
//         // Initial data load
//         refreshEmployeeData("employeeId", true);
//     }
    
//     private void initializeTopPanel() {
//         topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         // Search components
//         searchField = new JTextField(10);
//         searchButton = new JButton("Search by ID");
//         backButton = new JButton("Back");
//         topPanel.add(backButton);
//         topPanel.add(new JLabel("Search by ID:"));
//         topPanel.add(searchField);
//         topPanel.add(searchButton);

//         // Sort components
//         sortComboBox = new JComboBox<>(new String[]{"employeeId", "employeeName", "role", "recordId"});
//         sortAscButton = new JButton("Sort ↑");
//         sortDescButton = new JButton("Sort ↓");
//         topPanel.add(new JLabel("Sort by:"));
//         topPanel.add(sortComboBox);
//         topPanel.add(sortAscButton);
//         topPanel.add(sortDescButton);

//         // CRUD buttons
//         topPanel.add(new JSeparator(SwingConstants.VERTICAL));
//         addButton = new JButton("Add");
//         updateButton = new JButton("Update");
//         deleteButton = new JButton("Delete");
//         topPanel.add(addButton);
//         topPanel.add(updateButton);
//         topPanel.add(deleteButton);
        
//         // Add listeners
//         addButton.addActionListener(this::handleAdd);
//         updateButton.addActionListener(this::handleUpdate);
//         deleteButton.addActionListener(this::handleDelete);
//         searchButton.addActionListener(this::handleSearch);
//         backButton.addActionListener(this::handleBack);
//         sortAscButton.addActionListener(e -> refreshEmployeeData(
//             (String) sortComboBox.getSelectedItem(), true));
//         sortDescButton.addActionListener(e -> refreshEmployeeData(
//             (String) sortComboBox.getSelectedItem(), false));
//     }
    
//     private void initializeBottomPanel() {
//         bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//     }
    
//     private void refreshEmployeeData(String sortColumn, boolean asc) {
//         try {
//             List<Employee> employees = employeeService.getEmployees(
//                 sortColumn, asc, currentSearchId);
//             tablePanel.updateTable(employees);
//             updateStatusBar(employees.size());
//         } catch (SQLException ex) {
//             JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage());
//         }
//     }
    
//     private void updateStatusBar(int count) {
//         bottomPanel.removeAll();
//         String status = currentSearchId != null ?
//             "Showing results for ID: " + currentSearchId + " (" + count + " found)" :
//             "Total Employees: " + count;
//         bottomPanel.add(new JLabel(status));
//         bottomPanel.revalidate();
//     }
    
//     // Event handlers
//     private void handleSearch(ActionEvent e) {
//         String searchText = searchField.getText().trim();
//         if (searchText.isEmpty()) {
//             currentSearchId = null;
//         } else {
//             try {
//                 currentSearchId = Integer.parseInt(searchText);
//             } catch (NumberFormatException ex) {
//                 JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID");
//                 currentSearchId = null;
//             }
//         }
//         refreshEmployeeData((String) sortComboBox.getSelectedItem(), true);
//     }
    
//     private void handleBack(ActionEvent e) {
//         currentSearchId = null;
//         searchField.setText("");
//         refreshEmployeeData((String) sortComboBox.getSelectedItem(), true);
//     }
    
//     private void handleAdd(ActionEvent e) {
//         // Implementation using EmployeeService
//     }
    
//     private void handleUpdate(ActionEvent e) {
//         // Implementation using EmployeeService
//     }
    
//     private void handleDelete(ActionEvent e) {
//         // Implementation using EmployeeService
//     }
// }