package Frontend;

import Components.NavigationBar;
import Interfaces.NavigationListener;
import Utilities.InputValidator;
import Utilities.StyledComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

import DBConnection.DBConnection;
import Exception.*;

public class UpdateProfilePage extends JPanel {
    private final int adminId;
    private final NavigationListener navigationListener;
    private JTextField nameField, emailField, contactField, passwordField;
    private JCheckBox showPasswordCheck;

    public UpdateProfilePage(int adminId, NavigationListener navigationListener) {
        this.adminId = adminId;
        this.navigationListener = navigationListener;
        initializeUI();
        loadAdminData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyledComponents.BACKGROUND);

        NavigationBar navBar = new NavigationBar("Update Profile",
                e -> navigationListener.navigateTo(new ProfilePage(adminId, navigationListener)), 70);
        navBar.setBackground(Color.BLACK);
        navBar.setForeground(Color.WHITE);
        add(navBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(StyledComponents.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel formPanel = StyledComponents.createFormPanel();
        GridBagConstraints gbc = StyledComponents.createGridBagConstraints();

        StyledComponents.addFormField(formPanel, gbc, 0, "Name:", nameField = StyledComponents.createTextField());
        StyledComponents.addFormField(formPanel, gbc, 1, "Email:", emailField = StyledComponents.createTextField());
        StyledComponents.addFormField(formPanel, gbc, 2, "Contact:", contactField = StyledComponents.createTextField());

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = StyledComponents.createLabel("Password:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(StyledComponents.FIELD_FONT);
        formPanel.add(passwordField, gbc);

        // Show password checkbox
        gbc.gridx = 1;
        gbc.gridy = 4;
        showPasswordCheck = StyledComponents.createCheckBox("Show password", e -> {
            JPasswordField pf = (JPasswordField) passwordField;
            pf.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
        });
        formPanel.add(showPasswordCheck, gbc);

        // Buttons panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        JPanel buttonPanel = StyledComponents.createButtonPanel();
        JButton saveBtn = StyledComponents.createStyledButton("Save Changes", this::saveChanges);
        JButton cancelBtn = StyledComponents.createStyledButton("Cancel",
                e -> navigationListener.navigateTo(new ProfilePage(adminId, navigationListener)));
        cancelBtn.setBackground(new Color(200, 200, 200));

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        formPanel.add(buttonPanel, gbc);

        contentPanel.add(formPanel);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void loadAdminData() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "SELECT a.name, a.email, a.contactNumber, ac.password as accountPassword " +
                             "FROM admin a JOIN account ac ON a.adminId = ac.adminId WHERE a.adminId = ?")) {

            pst.setInt(1, adminId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                contactField.setText(rs.getString("contactNumber"));
                passwordField.setText(rs.getString("accountPassword"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    private void saveChanges(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String password = new String(((JPasswordField) passwordField).getPassword());

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            InputValidator.validateEmail(email);
            InputValidator.validatePassword(password);
            InputValidator.validatePasswordStrength(password);
        } catch (InvalidEmailException | InvalidPasswordException | WeakPasswordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement adminStmt = con.prepareStatement(
                    "UPDATE admin SET name=?, email=?, contactNumber=?, password=? WHERE adminId=?")) {
                adminStmt.setString(1, name);
                adminStmt.setString(2, email);
                adminStmt.setString(3, contact);
                adminStmt.setString(4, password);
                adminStmt.setInt(5, adminId);
                adminStmt.executeUpdate();
            }

            try (PreparedStatement accountStmt = con.prepareStatement(
                    "UPDATE account SET email=?, password=? WHERE adminId=?")) {
                accountStmt.setString(1, email);
                accountStmt.setString(2, password);
                accountStmt.setInt(3, adminId);
                accountStmt.executeUpdate();
            }

            con.commit();
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
