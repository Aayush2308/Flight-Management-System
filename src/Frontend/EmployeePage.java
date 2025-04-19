package Frontend;

import DBConnection.DBConnection;
import Interfaces.NavigationListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import Components.NavigationBar;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.*;

public class EmployeePage extends JPanel {
    private JTable employeeTable;
    private JComboBox<String> sortComboBox;
    private JButton sortAscButton, sortDescButton;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private JPanel bottomPanel, controlsPanel;
    private JTextField searchField;
    private Integer currentSearchId = null;

    // --- Define a dark color palette ---
    private static final Color DARK_BACKGROUND = new Color(45, 45, 48);
    private static final Color MEDIUM_BACKGROUND = new Color(60, 63, 65);
    private static final Color LIGHT_TEXT = new Color(187, 187, 187);
    private static final Color DARK_TEXT = new Color(30, 30, 30);
    private static final Color TABLE_ROW_EVEN = new Color(50, 50, 50);
    private static final Color TABLE_ROW_ODD = new Color(55, 55, 55);
    private static final Color TABLE_HEADER_BACKGROUND = new Color(37, 37, 38);
    private static final Color ACCENT_COLOR = new Color(0, 122, 204);
    private static final Color LIGHT_BUTTON_BACKGROUND = new Color(150, 150, 150);

    public EmployeePage(int adminId, NavigationListener navigationListener) {
        setLayout(new BorderLayout());
        setBackground(DARK_BACKGROUND);
        setForeground(LIGHT_TEXT);

        NavigationBar navBar = new NavigationBar("Profile", e -> navigationListener.navigateTo(new HomePage(adminId, navigationListener)), 70);
        add(navBar, BorderLayout.NORTH);

        // --- Controls panel: Sort and CRUD buttons ---
        controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setBackground(MEDIUM_BACKGROUND);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel sortByLabel = new JLabel("Sort by:");
        sortByLabel.setForeground(LIGHT_TEXT);
        sortComboBox = new JComboBox<>(new String[]{"employeeId", "employeeName", "role", "recordId"});
        styleComboBox(sortComboBox);
        sortAscButton = new JButton("Sort ↑");
        styleControlPanelButton(sortAscButton);
        sortDescButton = new JButton("Sort ↓");
        styleControlPanelButton(sortDescButton);

        JLabel searchLabel = new JLabel("Search by ID:");
        searchLabel.setForeground(LIGHT_TEXT);
        searchField = new JTextField(10);
        styleTextField(searchField);
        searchButton = new JButton("Search");
        styleHeaderButton(searchButton);

        controlsPanel.add(sortByLabel);
        controlsPanel.add(sortComboBox);
        controlsPanel.add(sortAscButton);
        controlsPanel.add(sortDescButton);

        
        controlsPanel.add(new JSeparator(SwingConstants.VERTICAL));
        addButton = new JButton("Add");
        styleControlPanelButton(addButton);
        updateButton = new JButton("Update");
        styleControlPanelButton(updateButton);
        deleteButton = new JButton("Delete");
        styleControlPanelButton(deleteButton);
        controlsPanel.add(addButton);
        controlsPanel.add(updateButton);
        controlsPanel.add(deleteButton);
        
        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);
        controlsPanel.add(searchButton);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(navBar, BorderLayout.NORTH);
        northContainer.add(controlsPanel, BorderLayout.SOUTH);
        add(northContainer, BorderLayout.NORTH);

        // --- Center: Employee table ---
        employeeTable = new JTable();
        styleTable(employeeTable);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: status bar ---
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(MEDIUM_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Initial load ---
        fetchAndDisplayEmployees("employeeId", true);

        sortAscButton.addActionListener(e -> fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true));
        sortDescButton.addActionListener(e -> fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), false));
        addButton.addActionListener(e -> showAddDialog());
        updateButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        searchButton.addActionListener(e -> handleSearch());
    }

    // --- Styling Helper Methods ---
    private void styleHeaderButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(DARK_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleControlPanelButton(JButton button) {
        button.setBackground(LIGHT_BUTTON_BACKGROUND);
        button.setForeground(DARK_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(DARK_BACKGROUND);
        textField.setForeground(LIGHT_TEXT);
        textField.setCaretColor(LIGHT_TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MEDIUM_BACKGROUND),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
             public void focusGained(java.awt.event.FocusEvent evt) { textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT_COLOR), BorderFactory.createEmptyBorder(3, 5, 3, 5))); }
             public void focusLost(java.awt.event.FocusEvent evt) { textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MEDIUM_BACKGROUND), BorderFactory.createEmptyBorder(3, 5, 3, 5))); }
         });
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(DARK_BACKGROUND);
        comboBox.setForeground(LIGHT_TEXT);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setForeground(LIGHT_TEXT);
                label.setBackground(isSelected ? ACCENT_COLOR : DARK_BACKGROUND);
                return label;
            }
        });
    }

    private void styleTable(JTable table) {
        table.setBackground(DARK_BACKGROUND);
        table.setForeground(LIGHT_TEXT);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(BorderFactory.createLineBorder(MEDIUM_BACKGROUND));
        table.getTableHeader().setBackground(TABLE_HEADER_BACKGROUND);
        table.getTableHeader().setForeground(LIGHT_TEXT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setOpaque(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? ACCENT_COLOR.darker() : (row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD));
                c.setForeground(LIGHT_TEXT);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return c;
            }
        });
        table.setSelectionBackground(ACCENT_COLOR.darker());
        table.setSelectionForeground(Color.WHITE);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(MEDIUM_BACKGROUND));
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
    }

    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            currentSearchId = null;
        } else {
            try { currentSearchId = Integer.parseInt(searchText); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                currentSearchId = null;
                searchField.setText("");
                fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
                return;
            }
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private void fetchAndDisplayEmployees(String sortColumn, boolean asc) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Role", "RecordID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        try (Connection conn = DBConnection.getConnection()) {
            String baseQuery = "SELECT employeeId, employeeName, role, recordId FROM Employee";
            String whereClause = currentSearchId != null ? " WHERE employeeId = ?" : "";
            String validatedSortColumn = "employeeId";
            for(String validCol : new String[]{"employeeId", "employeeName", "role", "recordId"}) {
                if (validCol.equals(sortColumn)) { validatedSortColumn = sortColumn; break; }
            }
            String orderClause = " ORDER BY " + validatedSortColumn + (asc ? " ASC" : " DESC");

            PreparedStatement stmt = conn.prepareStatement(baseQuery + whereClause + orderClause);
            if (currentSearchId != null) stmt.setInt(1, currentSearchId);

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
            styleTable(employeeTable);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusBar(int count) {
        bottomPanel.removeAll();
        String status = currentSearchId != null ? "Showing results for ID: " + currentSearchId + " (" + count + " found)" : "Total Employees: " + count;
        JLabel statusLabel = new JLabel(status);
        statusLabel.setForeground(LIGHT_TEXT);
        bottomPanel.add(statusLabel);
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField(15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"GroundStaff", "Security", "CabinCrew", "Pilot"});
        JTextField facilityField = new JTextField(5);
        JTextField flightField = new JTextField(5);
        JLabel flightLabel = new JLabel("FlightID:");

        flightLabel.setEnabled(false); flightField.setEnabled(false);
        roleBox.addItemListener(e -> {
            boolean need = e.getStateChange() == ItemEvent.SELECTED && ("CabinCrew".equals(e.getItem()) || "Pilot".equals(e.getItem()));
            flightLabel.setEnabled(need); flightField.setEnabled(need);
            if (!need) flightField.setText("");
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBackground(DARK_BACKGROUND);
        panel.add(styleDialogLabel(new JLabel("Name:"))); panel.add(styleDialogTextField(nameField));
        panel.add(styleDialogLabel(new JLabel("Role:"))); panel.add(styleDialogComboBox(roleBox));
        panel.add(styleDialogLabel(new JLabel("FacilityID:"))); panel.add(styleDialogTextField(facilityField));
        panel.add(styleDialogLabel(flightLabel)); panel.add(styleDialogTextField(flightField));
        styleDialogLabel(flightLabel); styleDialogTextField(flightField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String role = (String) roleBox.getSelectedItem();
        int facilityId;
        try { facilityId = Integer.parseInt(facilityField.getText().trim()); }
        catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid Facility ID.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }
        Integer flightId = null;
        if (flightField.isEnabled()) {
            String ftxt = flightField.getText().trim();
            if (ftxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Flight ID required for " + role, "Input Error", JOptionPane.WARNING_MESSAGE); return; }
            try { flightId = Integer.parseInt(ftxt); }
            catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid Flight ID.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }
        }

        if (flightId != null) {
            try (Connection vconn = DBConnection.getConnection(); PreparedStatement vps = vconn.prepareStatement("SELECT COUNT(*) FROM Flight WHERE flightId=?")) {
                vps.setInt(1, flightId); ResultSet vrs = vps.executeQuery(); vrs.next();
                if (vrs.getInt(1) == 0) { JOptionPane.showMessageDialog(this, "Flight ID not found.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }
            } catch (SQLException sq) { JOptionPane.showMessageDialog(this, "Error validating flight: " + sq.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); return; }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int recordId;
            try (PreparedStatement psRec = conn.prepareStatement("INSERT INTO Records () VALUES()", Statement.RETURN_GENERATED_KEYS)) {
                psRec.executeUpdate(); try (ResultSet gen = psRec.getGeneratedKeys()) { gen.next(); recordId = gen.getInt(1); }
            }
            int empId;
            try (PreparedStatement psEmp = conn.prepareStatement("INSERT INTO Employee (employeeName, role, recordId) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                psEmp.setString(1, name); psEmp.setString(2, role); psEmp.setInt(3, recordId); psEmp.executeUpdate();
                try (ResultSet gen = psEmp.getGeneratedKeys()) { gen.next(); empId = gen.getInt(1); }
            }
            String sql = flightId != null ? "INSERT INTO " + role + " (employeeId, employeeName, facilityId, recordId, flightId) VALUES (?,?,?,?,?)" : "INSERT INTO " + role + " (employeeId, employeeName, facilityId, recordId) VALUES (?,?,?,?)";
            try (PreparedStatement psRole = conn.prepareStatement(sql)) {
                psRole.setInt(1, empId); psRole.setString(2, name);
                psRole.setInt(3, facilityId); psRole.setInt(4, recordId);
                if (flightId != null) psRole.setInt(5, flightId);
                psRole.executeUpdate();
            }
            conn.commit();
            bottomPanel.removeAll(); JLabel statusLabel = new JLabel("Added employee ID: " + empId); statusLabel.setForeground(LIGHT_TEXT); bottomPanel.add(statusLabel); bottomPanel.revalidate(); bottomPanel.repaint();
        } catch (Exception ex) {
            try (Connection conn = DBConnection.getConnection()) { if (conn != null) conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Error adding employee:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private void showUpdateDialog() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an employee to update.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) employeeTable.getValueAt(row, 0);
        String currName = (String) employeeTable.getValueAt(row, 1);
        String currRole = (String) employeeTable.getValueAt(row, 2);

        int recordId;
        Integer currFacilityId = null;
        Integer currFlightId = null;
        try (Connection c = DBConnection.getConnection()) {
            try (PreparedStatement psRec = c.prepareStatement("SELECT recordId FROM Employee WHERE employeeId=?")) {
                psRec.setInt(1, id); ResultSet rsRec = psRec.executeQuery();
                if (!rsRec.next()) { JOptionPane.showMessageDialog(this, "Employee not found in database.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                recordId = rsRec.getInt(1);
            }
            String roleTable = currRole;
            String roleQuery = "SELECT facilityId" + ("CabinCrew".equals(currRole) || "Pilot".equals(currRole) ? ", flightId" : "") + " FROM " + roleTable + " WHERE employeeId = ?";
            try(PreparedStatement psRole = c.prepareStatement(roleQuery)) {
                psRole.setInt(1, id); ResultSet rsRole = psRole.executeQuery();
                if (rsRole.next()) {
                    currFacilityId = rsRole.getInt("facilityId");
                    if ("CabinCrew".equals(currRole) || "Pilot".equals(currRole)) {
                        currFlightId = rsRole.getInt("flightId");
                        if (rsRole.wasNull()) currFlightId = null;
                    }
                }
            }
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error fetching employee details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); return; }

        JTextField nameField = new JTextField(currName, 15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"GroundStaff", "Security", "CabinCrew", "Pilot"});
        roleBox.setSelectedItem(currRole);
        JTextField facilityField = new JTextField(currFacilityId != null ? currFacilityId.toString() : "", 5);
        JTextField flightField = new JTextField(currFlightId != null ? currFlightId.toString() : "", 5);
        JLabel flightLabel = new JLabel("FlightID:");

        boolean initiallyNeedsFlight = "CabinCrew".equals(currRole) || "Pilot".equals(currRole);
        flightLabel.setEnabled(initiallyNeedsFlight); flightField.setEnabled(initiallyNeedsFlight);
        roleBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedRole = (String) e.getItem();
                boolean need = "CabinCrew".equals(selectedRole) || "Pilot".equals(selectedRole);
                flightLabel.setEnabled(need); flightField.setEnabled(need);
                if (!need) flightField.setText("");
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBackground(DARK_BACKGROUND);
        panel.add(styleDialogLabel(new JLabel("Name:"))); panel.add(styleDialogTextField(nameField));
        panel.add(styleDialogLabel(new JLabel("Role:"))); panel.add(styleDialogComboBox(roleBox));
        panel.add(styleDialogLabel(new JLabel("FacilityID:"))); panel.add(styleDialogTextField(facilityField));
        panel.add(styleDialogLabel(flightLabel)); panel.add(styleDialogTextField(flightField));
        styleDialogLabel(flightLabel); styleDialogTextField(flightField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Update Employee ID " + id, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String newName = nameField.getText().trim();
        String newRole = (String) roleBox.getSelectedItem();
        int newFac;
        try { newFac = Integer.parseInt(facilityField.getText().trim()); }
        catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid Facility ID.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }
        Integer newFlight = null;
        if (flightField.isEnabled()) {
            String ftxt = flightField.getText().trim();
            if (ftxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Flight ID required for " + newRole, "Input Error", JOptionPane.WARNING_MESSAGE); return; }
            try { newFlight = Integer.parseInt(ftxt); }
            catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Invalid Flight ID.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }
            try (Connection vconn = DBConnection.getConnection(); PreparedStatement vps = vconn.prepareStatement("SELECT COUNT(*) FROM Flight WHERE flightId=?")) {
                vps.setInt(1, newFlight); ResultSet vrs = vps.executeQuery(); vrs.next();
                if (vrs.getInt(1) == 0) { JOptionPane.showMessageDialog(this, "Flight ID not found.", "Validation Error", JOptionPane.WARNING_MESSAGE); return; }
            } catch (SQLException sq) { JOptionPane.showMessageDialog(this, "Error validating flight: " + sq.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); return; }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEmp = conn.prepareStatement("UPDATE Employee SET employeeName=?, role=? WHERE employeeId=?")) {
                psEmp.setString(1, newName); psEmp.setString(2, newRole); psEmp.setInt(3, id); psEmp.executeUpdate();
            }
            if (!newRole.equals(currRole)) {
                try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM " + currRole + " WHERE employeeId=?")) {
                    psDel.setInt(1, id); psDel.executeUpdate(); }
                String sqlIns = newFlight != null ? "INSERT INTO " + newRole + " (employeeId,employeeName,facilityId,recordId,flightId) VALUES (?,?,?,?,?)" : "INSERT INTO " + newRole + " (employeeId,employeeName,facilityId,recordId) VALUES (?,?,?,?)";
                try (PreparedStatement psIns = conn.prepareStatement(sqlIns)) {
                    psIns.setInt(1,id); psIns.setString(2,newName);
                    psIns.setInt(3,newFac); psIns.setInt(4,recordId);
                    if (newFlight != null) psIns.setInt(5,newFlight);
                    psIns.executeUpdate();
                }
            } else {
                String tbl = currRole;
                String upd = newFlight != null ? "UPDATE " + tbl + " SET employeeName=?,facilityId=?,flightId=? WHERE employeeId=?" : "UPDATE " + tbl + " SET employeeName=?,facilityId=? WHERE employeeId=?";
                try (PreparedStatement psUpd = conn.prepareStatement(upd)) {
                    psUpd.setString(1,newName); psUpd.setInt(2,newFac);
                    if (newFlight != null) { psUpd.setInt(3,newFlight); psUpd.setInt(4,id); }
                    else { psUpd.setInt(3,id); }
                    psUpd.executeUpdate();
                }
            }
            conn.commit();
            bottomPanel.removeAll(); JLabel statusLabel = new JLabel("Updated employee ID: " + id); statusLabel.setForeground(LIGHT_TEXT); bottomPanel.add(statusLabel); bottomPanel.revalidate(); bottomPanel.repaint();
        } catch (SQLException ex) {
            try (Connection conn = DBConnection.getConnection()) { if (conn != null) conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Error updating employee:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private void deleteSelectedEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) employeeTable.getValueAt(row, 0);
        String name = (String) employeeTable.getValueAt(row, 1);

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete employee: " + name + " (ID: " + id + ")?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM Employee WHERE employeeId=?")) {
            ps.setInt(1, id); int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                bottomPanel.removeAll(); JLabel statusLabel = new JLabel("Deleted employee ID: " + id); statusLabel.setForeground(LIGHT_TEXT); bottomPanel.add(statusLabel); bottomPanel.revalidate(); bottomPanel.repaint();
            } else { JOptionPane.showMessageDialog(this, "Employee not found or already deleted.", "Deletion Failed", JOptionPane.WARNING_MESSAGE); }
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error deleting employee:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); }
        fetchAndDisplayEmployees((String) sortComboBox.getSelectedItem(), true);
    }

    private JLabel styleDialogLabel(JLabel label) { label.setForeground(LIGHT_TEXT); return label; }

    private JTextField styleDialogTextField(JTextField textField) {
        textField.setBackground(DARK_BACKGROUND.brighter());
        textField.setForeground(LIGHT_TEXT);
        textField.setCaretColor(LIGHT_TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MEDIUM_BACKGROUND), BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
             public void focusGained(java.awt.event.FocusEvent evt) { textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT_COLOR), BorderFactory.createEmptyBorder(3, 5, 3, 5))); }
             public void focusLost(java.awt.event.FocusEvent evt) { textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MEDIUM_BACKGROUND), BorderFactory.createEmptyBorder(3, 5, 3, 5))); }
         });
        return textField;
    }

     private JComboBox<String> styleDialogComboBox(JComboBox<String> comboBox) {
         comboBox.setBackground(DARK_BACKGROUND.brighter());
         comboBox.setForeground(LIGHT_TEXT);
         comboBox.setRenderer(new DefaultListCellRenderer() {
             @Override
             public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                 JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 label.setForeground(LIGHT_TEXT);
                 label.setBackground(isSelected ? ACCENT_COLOR : DARK_BACKGROUND.brighter());
                 return label;
             }
         });
         return comboBox;
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