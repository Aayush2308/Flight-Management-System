package Frontend;

import Models.Account;
import Service.AuthService;
import Utilities.PlaceholderUtils;
import javax.swing.*;
import Components.LoginNavigationBar;
import Interfaces.NavigationListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private AuthService authService;
    private LoginNavigationBar navBar;

    public LoginPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.authService = new AuthService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
    
        navBar = new LoginNavigationBar("Login", this::handleNavActions, 70);
        add(navBar, BorderLayout.NORTH);
    
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
    
        // Email Field
        emailField = new JTextField("Email");
        emailField.setMaximumSize(new Dimension(300, 40));
        emailField.setForeground(Color.GRAY);
        PlaceholderUtils.addPlaceholderStyle(emailField);
        contentPanel.add(emailField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    
        // Password Field
        passwordField = new JPasswordField("Password");
        passwordField.setEchoChar((char) 0);
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setForeground(Color.GRAY);
        PlaceholderUtils.addPlaceholderStyle(passwordField);
        contentPanel.add(passwordField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    
        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> loginUser());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(loginButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        JButton registerButton = new JButton("Register An Account");
        registerButton.setContentAreaFilled(false); 
        registerButton.setBorderPainted(false);     
        registerButton.setFocusPainted(false); 
        registerButton.setOpaque(false);        
        registerButton.setForeground(Color.BLUE);  
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> {
            if (navigationListener != null) {
                navigationListener.navigateTo("signup");
            }
        });
        contentPanel.add(registerButton);
        add(contentPanel, BorderLayout.CENTER);
    
        // Placeholder handlers
        PlaceholderUtils.addPlaceholderListeners(emailField, "Email");
        PlaceholderUtils.addPlaceholderListeners(passwordField, "Password");
    }

    private void handleNavActions(ActionEvent e) {
        String command = ((JButton)e.getSource()).getText();
        if ("←".equals(command)) {
            if (navigationListener != null) {
                navigationListener.navigateTo("welcome"); // or wherever you want to go
            }
        }
    }
    

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (email.equals("Email") || password.equals("Password") || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Account account = authService.authenticate(email, password);
            if (account != null) {
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