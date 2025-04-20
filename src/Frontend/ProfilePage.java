package Frontend;

import Components.NavigationBar;
import Exception.InvalidPasswordException;
import Exception.WeakPasswordException;
import Interfaces.NavigationListener;
import Models.Admin;
import Service.AdminService;
import Utilities.HashPassword;
import Utilities.InputValidator;

import javax.swing.*;
import java.awt.*;

public class ProfilePage extends JPanel {
    private JLabel nameLabel, emailLabel, contactLabel;
    private final int adminId;
    private final NavigationListener navigationListener;
    
    // Color scheme
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color ACCENT_COLOR = new Color(0, 122, 204);
    private static final Color CARD_BACKGROUND = new Color(245, 245, 245);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font VALUE_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public ProfilePage(int adminId, NavigationListener navigationListener) {
        this.adminId = adminId;
        this.navigationListener = navigationListener;
        initializeUI();
        fetchAdminDetails(adminId);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // Navigation Bar
        NavigationBar navBar = new NavigationBar("Profile", 
            e -> navigationListener.navigateTo(new HomePage(adminId, navigationListener)), 70);
        navBar.setBackground(Color.BLACK);
        navBar.setForeground(Color.WHITE);
        add(navBar, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Profile card
        JPanel profileCard = new JPanel(new GridBagLayout());
        profileCard.setBackground(CARD_BACKGROUND);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        addFormField(profileCard, gbc, 0, "Name:", nameLabel = new JLabel());

        // Email field
        addFormField(profileCard, gbc, 1, "Email:", emailLabel = new JLabel());

        // Contact field
        addFormField(profileCard, gbc, 2, "Contact:", contactLabel = new JLabel());

        // Update Password button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);
        
        JButton updatePasswordBtn = createStyledButton("Update Password");
        updatePasswordBtn.addActionListener(e -> showUpdatePasswordDialog());
        profileCard.add(updatePasswordBtn, gbc);

        // Edit button
        gbc.gridy = 4;
        JButton editBtn = createStyledButton("Edit Profile");
        editBtn.addActionListener(e -> showEditDialog());
        profileCard.add(editBtn, gbc);

        // Add profile card to center
        contentPanel.add(profileCard);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        valueLabel.setFont(VALUE_FONT);
        valueLabel.setForeground(TEXT_COLOR);
        panel.add(valueLabel, gbc);
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

    private void showEditDialog() {
        // Implementation for edit dialog
        if(navigationListener != null){
            navigationListener.navigateTo(new UpdateProfilePage(adminId, navigationListener));
        }
    }

    private void showUpdatePasswordDialog() {
        JDialog passwordDialog = new JDialog((Frame) null, "Update Password", true);
        passwordDialog.setLayout(new BorderLayout());
        passwordDialog.setSize(400, 300);

        // Create the dialog content panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Old Password
        // JLabel oldPasswordLabel = new JLabel("Old Password:");
        // oldPasswordLabel.setFont(LABEL_FONT);
        // oldPasswordLabel.setForeground(TEXT_COLOR);
        // JPasswordField oldPasswordField = new JPasswordField();
        // oldPasswordField.setFont(VALUE_FONT);

        // New Password
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(LABEL_FONT);
        newPasswordLabel.setForeground(TEXT_COLOR);
        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setFont(VALUE_FONT);
        newPasswordField.setPreferredSize(new Dimension(200, 30));

        // Show password toggle
        // JButton showPasswordBtn = new JButton("Show Password");
        // showPasswordBtn.addActionListener(e -> togglePasswordVisibility(oldPasswordField, newPasswordField));

        // Add components to dialog
        // gbc.gridx = 0;
        // gbc.gridy = 0;
        // panel.add(oldPasswordLabel, gbc);
        // gbc.gridx = 1;
        // panel.add(oldPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(newPasswordLabel, gbc);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);
        

        // gbc.gridx = 0;
        // gbc.gridy = 2;
        // panel.add(showPasswordBtn, gbc);

        // Update button
        JButton updateBtn = new JButton("Update");
        updateBtn.setFont(LABEL_FONT);
        updateBtn.addActionListener(e -> handleUpdatePassword(newPasswordField.getPassword()));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(updateBtn, gbc);

        passwordDialog.add(panel, BorderLayout.CENTER);
        passwordDialog.setVisible(true);
    }

    // private void togglePasswordVisibility(JPasswordField oldPasswordField, JPasswordField newPasswordField) {
    //     char currentEchoChar = oldPasswordField.getEchoChar();
    //     char newEchoChar = newPasswordField.getEchoChar();
    //     if (currentEchoChar == '*') {
    //         oldPasswordField.setEchoChar((char) 0);
    //         newPasswordField.setEchoChar((char) 0);
    //     } else {
    //         oldPasswordField.setEchoChar('*');
    //         newPasswordField.setEchoChar('*');
    //     }
    // }

    private void handleUpdatePassword(char[] newPassword) {
        String newPasswordStr = new String(newPassword);

        try {
            // System.out.println(password);
            InputValidator.validatePassword(newPasswordStr);
            InputValidator.validatePasswordStrength(newPasswordStr);
        } catch (InvalidPasswordException | WeakPasswordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AdminService service = new AdminService();
        service.updatePassword(adminId, HashPassword.hashPassword(newPasswordStr));

    }

    private void fetchAdminDetails(int adminId) {
        AdminService service = new AdminService();
        Admin admin = service.getAdminById(adminId);

        if (admin != null) {
            nameLabel.setText(admin.getName());
            emailLabel.setText(admin.getEmail());
            contactLabel.setText(admin.getContactNumber());
        } else {
            JOptionPane.showMessageDialog(this, "Error loading profile.");
        }
    }
}
