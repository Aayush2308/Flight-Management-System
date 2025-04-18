package Frontend;

import DBConnection.DBConnection;
import Models.Account;
import Service.AuthService;
import Service.NavigationListener;
import Utilities.PlaceholderUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private AuthService authService;

    public LoginPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.authService = new AuthService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Email Field
        emailField = new JTextField("Email");
        emailField.setMaximumSize(new Dimension(300, 40));
        emailField.setForeground(Color.GRAY);
        PlaceholderUtils.addPlaceholderStyle(emailField);
        add(emailField);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Password Field
        passwordField = new JPasswordField("Password");
        passwordField.setEchoChar((char) 0);
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setForeground(Color.GRAY);
        PlaceholderUtils.addPlaceholderStyle(passwordField);
        add(passwordField);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> loginUser());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(loginButton);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Register Label
        JLabel registerLabel = new JLabel("<HTML><U>Register an account</U></HTML>");
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigationListener.navigateTo("signup");
            }
        });
        
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLabel.setPreferredSize(new Dimension(100, 20));
        add(registerLabel);

        PlaceholderUtils.addPlaceholderListeners(emailField, "Email");
        PlaceholderUtils.addPlaceholderListeners(passwordField, "Password");
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (email.equals("Email") || password.equals("Password") || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Account user = authService.authenticate(email, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                // navigationListener.navigateTo("dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}