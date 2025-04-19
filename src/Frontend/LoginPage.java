package Frontend;

import Models.Account;
import Service.AuthService;
import Utilities.PlaceholderUtils; // Assuming this utility class exists
import javax.swing.*;
import Components.LoginNavigationBar; // Assuming this component exists
import Interfaces.NavigationListener; // Assuming this interface exists
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private AuthService authService;
    private LoginNavigationBar navBar; // Kept for the top bar

    public LoginPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.authService = new AuthService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Apply a dark background to the main panel areas not covered by sub-panels
        setBackground(new Color(45, 45, 48)); // A dark grey

        // Navigation Bar - apply dark theme colors
        navBar = new LoginNavigationBar("Login", this::handleNavActions, 70);
        navBar.setBackground(Color.DARK_GRAY); // Dark background for the nav bar
        // Note: Styling the text/title inside LoginNavigationBar depends on its implementation.
        // You might need to modify LoginNavigationBar itself to change title color if this line isn't enough.
        // Example (assuming LoginNavigationBar allows setting foreground):
        // navBar.setForeground(Color.WHITE); // Set text color if applicable

        add(navBar, BorderLayout.NORTH);

        // Content Panel (holds the form elements)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        // Set a background for the content panel, slightly lighter dark grey perhaps
        contentPanel.setBackground(new Color(60, 63, 65)); // Lighter dark grey
        // Ensure components added below are visible on this background

        // Email Field
        emailField = new JTextField("Email");
        emailField.setMaximumSize(new Dimension(300, 40));
        // Apply dark theme colors to the text field
        emailField.setBackground(new Color(50, 50, 50)); // Darker field background
        emailField.setForeground(Color.WHITE); // Light text color
        emailField.setCaretColor(Color.WHITE); // White cursor for visibility on dark background
        PlaceholderUtils.addPlaceholderStyle(emailField); // Apply placeholder style

        // Password Field
        passwordField = new JPasswordField("Password");
        passwordField.setEchoChar((char) 0); // Show initial placeholder text
        passwordField.setMaximumSize(new Dimension(300, 40));
        // Apply dark theme colors to the password field
        passwordField.setBackground(new Color(50, 50, 50)); // Darker field background
        passwordField.setForeground(Color.WHITE); // Light text color
        passwordField.setCaretColor(Color.WHITE); // White cursor
        PlaceholderUtils.addPlaceholderStyle(passwordField); // Apply placeholder style

        // Add fields to the content panel with spacing
        contentPanel.add(emailField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        JButton loginButton = new JButton("Login");
        // Apply dark theme button styling - use an accent color from the image
        loginButton.setBackground(new Color(0, 120, 215)); // A shade of blue
        loginButton.setForeground(Color.WHITE); // White text on the button
        loginButton.setFocusPainted(false); // Remove focus border
        loginButton.setBorderPainted(false); // Remove default border
        loginButton.setOpaque(true); // Make sure background is painted
        loginButton.addActionListener(e -> loginUser());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button horizontally
        contentPanel.add(loginButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register Button (styled as a link)
        JButton registerButton = new JButton("Register An Account");
        // Keep as a link, ensure color is visible on the dark background
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setOpaque(false);
        registerButton.setForeground(Color.BLUE.brighter()); // Make blue slightly brighter for visibility
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button horizontally
        registerButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo("signup");
            }
        });
        contentPanel.add(registerButton);

        // Add the content panel to the center of the main panel
        add(contentPanel, BorderLayout.CENTER);

        // Apply placeholder focus listeners (assuming PlaceholderUtils handles colors correctly)
        PlaceholderUtils.addPlaceholderListeners(emailField, "Email");
        PlaceholderUtils.addPlaceholderListeners(passwordField, "Password");

        // Note: You might need to adjust the PlaceholderUtils class
        // to ensure the placeholder text color is appropriate for the dark backgrounds.
        // It should probably use a light grey color when the field is empty and not focused.
    }

    // Navigation action handler (kept from original)
    private void handleNavActions(ActionEvent e) {
        String command = ((JButton)e.getSource()).getText();
        // Assuming the navBar button text is "←"
        if ("←".equals(command)) {
            if (navigationListener != null) {
                navigationListener.navigateTo("welcome"); // or wherever you want to go
            }
        }
         // If LoginNavigationBar has a title label and you want to style it:
         // Example: if (navBar.getTitleLabel() != null) navBar.getTitleLabel().setForeground(Color.WHITE);
    }

    // Login functionality (kept from original)
    private void loginUser() {
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (email.equals("Email") || password.equals("Password") || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Account account = authService.authenticate(email, password);
            if (account != null) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (navigationListener != null) {
                    // Navigate to HomePage, passing the adminId
                    navigationListener.navigateTo(new HomePage(account.getAdminId(), navigationListener));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // Log the exception in a real application
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}