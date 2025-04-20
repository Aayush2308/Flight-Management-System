package Frontend;

import Models.Account;
import Service.LoginService;
import Utilities.HashPassword;

import javax.swing.*;
import Components.LoginNavigationBar;
import Interfaces.NavigationListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.event.DocumentListener; // Added import

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private LoginService authService;
    private LoginNavigationBar navBar;

    // Placeholder text definitions and colors
    private static final String EMAIL_PLACEHOLDER = "Email";
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private Color placeholderColor = Color.GRAY.brighter();
    private Color textColor = Color.WHITE;

    public LoginPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.authService = new LoginService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        navBar = new LoginNavigationBar("Login", this::handleNavActions, 70);
        navBar.setBackground(Color.BLACK);
        add(navBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        contentPanel.setBackground(new Color(60, 63, 65));

        // Email Field - condensed setup
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(300, 40)); emailField.setBackground(new Color(50, 50, 50)); emailField.setCaretColor(Color.WHITE);
        addManualPlaceholder(emailField, EMAIL_PLACEHOLDER); // Add manual placeholder logic
        contentPanel.add(emailField); contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password Field - condensed setup
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 40)); passwordField.setBackground(new Color(50, 50, 50)); passwordField.setCaretColor(Color.WHITE);
        addManualPlaceholder(passwordField, PASSWORD_PLACEHOLDER); // Add manual placeholder logic
        contentPanel.add(passwordField); contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button - condensed setup
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 120, 215)); loginButton.setForeground(Color.WHITE); loginButton.setFocusPainted(false); loginButton.setBorderPainted(false); loginButton.setOpaque(true);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); loginButton.addActionListener(e -> loginUser());
        contentPanel.add(loginButton); contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register Button - condensed setup
        JButton registerButton = new JButton("Register An Account");
        registerButton.setContentAreaFilled(false); registerButton.setBorderPainted(false); registerButton.setFocusPainted(false); registerButton.setOpaque(false);
        registerButton.setForeground(Color.BLUE.brighter()); registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT); registerButton.addActionListener(e -> { if (navigationListener != null) navigationListener.navigateTo("signup"); });
        contentPanel.add(registerButton);

        add(contentPanel, BorderLayout.CENTER);
    }

    // Manual placeholder logic - kept separate for clarity and reuse
    private void addManualPlaceholder(JTextField field, String placeholderText) {
        Color currentPlaceholderColor = placeholderColor;
        Color currentTextColor = textColor;

        field.setText(placeholderText);
        field.setForeground(currentPlaceholderColor);
        if (field instanceof JPasswordField) {
            ((JPasswordField)field).setEchoChar((char)0);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholderText) && field.getForeground().equals(currentPlaceholderColor)) {
                    field.setText("");
                    field.setForeground(currentTextColor);
                     if (field instanceof JPasswordField) {
                        ((JPasswordField)field).setEchoChar('*');
                     }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholderText);
                    field.setForeground(currentPlaceholderColor);
                     if (field instanceof JPasswordField) {
                        ((JPasswordField)field).setEchoChar((char)0);
                     }
                }
            }
        });

        field.getDocument().addDocumentListener(new DocumentListener() {
             public void changedUpdate(javax.swing.event.DocumentEvent e) { updateColor(); }
             public void removeUpdate(javax.swing.event.DocumentEvent e) { updateColor(); }
             public void insertUpdate(javax.swing.event.DocumentEvent e) { updateColor(); }
             private void updateColor() {
                 if (!field.getText().equals(placeholderText) && field.getForeground().equals(currentPlaceholderColor)) {
                     field.setForeground(currentTextColor);
                 }
                  if (field instanceof JPasswordField) {
                       if (!new String(((JPasswordField)field).getPassword()).isEmpty() && ((JPasswordField)field).getEchoChar() == (char)0) {
                           ((JPasswordField)field).setEchoChar('*');
                       }
                  }
             }
         });
    }


    private void handleNavActions(ActionEvent e) {
        String command = ((JButton)e.getSource()).getText();
        if ("←".equals(command) && navigationListener != null) {
             navigationListener.navigateTo("welcome");
        }
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String passwordRaw = String.valueOf(passwordField.getPassword()).trim();
        String password = HashPassword.hashPassword(passwordRaw);

        if (email.equals(EMAIL_PLACEHOLDER) || password.equals(PASSWORD_PLACEHOLDER) || email.isEmpty() || password.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
             return;
         }

        try {
            Account account = authService.authenticate(email, password);
            if (account != null) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (navigationListener != null) {
                    navigationListener.navigateTo(new HomePage(account.getAdminId(), navigationListener));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}