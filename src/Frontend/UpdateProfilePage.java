package Frontend;

import Components.NavigationBar;
import DBConnection.DBConnection;
import Interfaces.NavigationListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class UpdateProfilePage extends JPanel {
    private final int adminId;
    private final NavigationListener navigationListener;
    private JTextField nameField, emailField, contactField, passwordField;
    private JCheckBox showPasswordCheck;
    
    // Color scheme
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color ACCENT_COLOR = new Color(0, 122, 204);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public UpdateProfilePage(int adminId, NavigationListener navigationListener) {
        this.adminId = adminId;
        this.navigationListener = navigationListener;
        initializeUI();
        loadAdminData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // Navigation Bar
        NavigationBar navBar = new NavigationBar("Update Profile", 
            e -> navigationListener.navigateTo(new ProfilePage(adminId, navigationListener)), 70);
        navBar.setBackground(Color.BLACK);
        navBar.setForeground(Color.WHITE);
        add(navBar, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        addFormField(formPanel, gbc, 0, "Name:", nameField = createTextField());

        // Email field
        addFormField(formPanel, gbc, 1, "Email:", emailField = createTextField());

        // Contact field
        addFormField(formPanel, gbc, 2, "Contact:", contactField = createTextField());

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField();
        passwordField.setFont(FIELD_FONT);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setColumns(20);
        formPanel.add(passwordField, gbc);

        // Show password checkbox
        gbc.gridx = 1;
        gbc.gridy = 4;
        showPasswordCheck = new JCheckBox("Show password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheck.setBackground(BACKGROUND);
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                ((JPasswordField) passwordField).setEchoChar((char)0); // Show password
            } else {
                ((JPasswordField) passwordField).setEchoChar('•'); // Hide password
            }
        });
        formPanel.add(showPasswordCheck, gbc);

        // Buttons panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND);
        
        JButton saveBtn = createStyledButton("Save Changes");
        saveBtn.addActionListener(this::saveChanges);
        
        JButton cancelBtn = createStyledButton("Cancel");
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.addActionListener(e -> 
            navigationListener.navigateTo(new ProfilePage(adminId, navigationListener)));
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_COLOR);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadAdminData() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(
                 "SELECT a.name, a.email, a.contactNumber, a.password, ac.password as accountPassword " +
                 "FROM admin a JOIN account ac ON a.adminId = ac.adminId WHERE a.adminId = ?")) {
            
            pst.setInt(1, adminId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                contactField.setText(rs.getString("contactNumber"));
                passwordField.setText(rs.getString("accountPassword")); // Using account password
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    private void saveChanges(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String password = new String(((JPasswordField)passwordField).getPassword());

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            // Update admin table
            try (PreparedStatement adminStmt = con.prepareStatement(
                "UPDATE admin SET name=?, email=?, contactNumber=?, password=? WHERE adminId=?")) {
                
                adminStmt.setString(1, name);
                adminStmt.setString(2, email);
                adminStmt.setString(3, contact);
                adminStmt.setString(4, password); // Storing plain text password (not recommended in production)
                adminStmt.setInt(5, adminId);
                adminStmt.executeUpdate();
            }

            // Update account table
            try (PreparedStatement accountStmt = con.prepareStatement(
                "UPDATE account SET email=?, password=? WHERE adminId=?")) {
                
                accountStmt.setString(1, email);
                accountStmt.setString(2, password); // Storing plain text password (not recommended in production)
                accountStmt.setInt(3, adminId);
                accountStmt.executeUpdate();
            }

            con.commit(); // Commit transaction
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            navigationListener.navigateTo(new ProfilePage(adminId, navigationListener));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            try (Connection con = DBConnection.getConnection()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
}