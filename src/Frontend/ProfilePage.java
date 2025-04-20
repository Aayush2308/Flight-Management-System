package Frontend;

import Components.NavigationBar;
import Interfaces.NavigationListener;
import Models.Admin;
import Service.AdminService;

import javax.swing.*;
import java.awt.*;

public class ProfilePage extends JPanel {
    private JLabel nameLabel, emailLabel, contactLabel, passwordLabel;
    private final int adminId;
    private final NavigationListener navigationListener;
    private boolean passwordVisible = false;
    
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

        // Password field with toggle button
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordTitle = new JLabel("Password:");
        passwordTitle.setFont(LABEL_FONT);
        passwordTitle.setForeground(TEXT_COLOR);
        profileCard.add(passwordTitle, gbc);

        gbc.gridx = 1;
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(CARD_BACKGROUND);
        
        passwordLabel = new JLabel();
        passwordLabel.setFont(VALUE_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        passwordPanel.add(passwordLabel, BorderLayout.CENTER);
        
        JButton togglePasswordBtn = new JButton("Show");
        styleToggleButton(togglePasswordBtn);
        togglePasswordBtn.addActionListener(e -> togglePasswordVisibility());
        passwordPanel.add(togglePasswordBtn, BorderLayout.EAST);
        
        profileCard.add(passwordPanel, gbc);

        // Edit button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);
        
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

    private void styleToggleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(new Color(220, 220, 220));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        fetchAdminDetails(adminId); // Refresh to show/hide password
    }

    private void showEditDialog() {
        // Implementation for edit dialog
        if(navigationListener != null){
            navigationListener.navigateTo(new UpdateProfilePage(adminId, navigationListener));
        }
    }

        private void fetchAdminDetails(int adminId) {
        AdminService service = new AdminService();
        Admin admin = service.getAdminById(adminId);

        if (admin != null) {
            nameLabel.setText(admin.getName());
            emailLabel.setText(admin.getEmail());
            contactLabel.setText(admin.getContactNumber());

            String password = admin.getPassword();
            if (password != null) {
                passwordLabel.setText(passwordVisible ? password : "••••••••");
            } else {
                passwordLabel.setText("Not set");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error loading profile.");
        }
    }
}