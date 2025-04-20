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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 70, 70, 70));
        contentPanel.setBackground(new Color(60, 63, 65));
    
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);
    
        // Email Field
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(400, 50));
        emailField.setBackground(new Color(50, 50, 50));
        emailField.setCaretColor(Color.WHITE);
        emailField.setFont(fieldFont);
        addManualPlaceholder(emailField, EMAIL_PLACEHOLDER);
        contentPanel.add(emailField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    
        // Password Field
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(400, 50));
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setFont(fieldFont);
        addManualPlaceholder(passwordField, PASSWORD_PLACEHOLDER);
        contentPanel.add(passwordField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    
        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(30, 144, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(150, 45));
        loginButton.setMaximumSize(new Dimension(200, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> loginUser());
        contentPanel.add(loginButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    
        // Register Label Styled as Link
        JLabel registerLabel = new JLabel("Don't have an account? Register");
        registerLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registerLabel.setForeground(new Color(100, 149, 237));
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (navigationListener != null)
                    navigationListener.navigateTo("signup");
            }
        });
        contentPanel.add(registerLabel);
    
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
                showModernSuccessDialog("Login Successful!"); // 👈 Replaced here
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
    

    private void showModernSuccessDialog(String message) {
        JDialog dialog = new JDialog((Frame) null, "Success", true);
        dialog.setUndecorated(true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
    
        // Panel with rounded border look
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Success Icon
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    
        // Success Message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        messageLabel.setForeground(new Color(34, 139, 34));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);
    
        dialog.add(panel);
    
        // Animate fade-in
        new Thread(() -> {
            for (float opacity = 0.0f; opacity <= 1.0f; opacity += 0.1f) {
                try {
                    Thread.sleep(30);
                    dialog.setOpacity(opacity);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    
        // Auto-close after delay
        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    
        dialog.setOpacity(0f); // Start transparent
        dialog.setVisible(true);
    }
    
}