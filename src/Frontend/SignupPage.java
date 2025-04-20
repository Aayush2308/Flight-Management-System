package Frontend;

import Service.UserService;
import Utilities.HashPassword;
import Utilities.InputValidator;
import Components.NavigationBar;
import Exception.InvalidEmailException;
import Exception.InvalidPasswordException;
import Exception.WeakPasswordException;

// Removed import Utilities.PlaceholderUtils; // No longer using PlaceholderUtils
import javax.swing.*;
import Interfaces.NavigationListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.DocumentListener;


public class SignupPage extends JPanel {
    private JTextField nameField, contactField, emailField;
    private JPasswordField passwordField; // Changed from JPasswordField to JTextField
    private NavigationListener navigationListener;
    private UserService userService;
    private NavigationBar navBar;

    // Placeholder text definitions and colors
    private static final String NAME_PLACEHOLDER = "Name";
    private static final String CONTACT_PLACEHOLDER = "Contact Number";
    private static final String EMAIL_PLACEHOLDER = "Email";
    private static final String PASSWORD_PLACEHOLDER = "Password"; // Placeholder text remains
    private Color placeholderColor = Color.GRAY.brighter();
    private Color textColor = Color.WHITE;


    public SignupPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 48));

        navBar = new NavigationBar("Sign Up", this::handleNavActions, 70);
        navBar.setBackground(Color.DARK_GRAY);
        add(navBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        contentPanel.setBackground(new Color(60, 63, 65));

        // Name Field - condensed setup using helper
        nameField = createTextField(NAME_PLACEHOLDER);
        contentPanel.add(nameField); contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Contact Field - condensed setup using helper
        contactField = createTextField(CONTACT_PLACEHOLDER);
        contentPanel.add(contactField); contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email Field - condensed setup using helper
        emailField = createTextField(EMAIL_PLACEHOLDER);
        contentPanel.add(emailField); contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password Field - now a JTextField
        passwordField = new JPasswordField(); // Instantiated as JTextField
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setCaretColor(Color.WHITE);
        addManualPlaceholder(passwordField, PASSWORD_PLACEHOLDER); // Use manual placeholder logic

        contentPanel.add(passwordField); contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));


        // Register Button - condensed setup
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 120, 215)); registerButton.setForeground(Color.WHITE); registerButton.setFocusPainted(false); registerButton.setBorderPainted(false); registerButton.setOpaque(true);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT); registerButton.addActionListener(e -> registerUser());
        contentPanel.add(registerButton);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleNavActions(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();
        if ("←".equals(command) && navigationListener != null) {
             navigationListener.navigateTo("login");
        }
    }

    // createTextField helper remains the same, it works for JTextField
    private JTextField createTextField(String placeholderText) {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 40));
        field.setBackground(new Color(50, 50, 50));
        field.setCaretColor(Color.WHITE);

        addManualPlaceholder(field, placeholderText); // Use manual placeholder

        return field;
    }

     // Manual placeholder logic - adapted for JTextField (removed JPasswordField specific parts)
    private void addManualPlaceholder(JTextField field, String placeholderText) {
        Color currentPlaceholderColor = placeholderColor;
        Color currentTextColor = textColor;

        field.setText(placeholderText);
        field.setForeground(currentPlaceholderColor);
        // Removed JPasswordField specific echo char handling


        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholderText) && field.getForeground().equals(currentPlaceholderColor)) {
                    field.setText("");
                    field.setForeground(currentTextColor);
                    // Removed JPasswordField specific echo char handling
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholderText);
                    field.setForeground(currentPlaceholderColor);
                    // Removed JPasswordField specific echo char handling
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
                 // Removed JPasswordField specific echo char handling
             }
         });
    }


    private void registerUser() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String passwordRaw = String.valueOf(passwordField.getPassword()).trim(); // Get text directly from JTextField
        String password = HashPassword.hashPassword(passwordRaw);

        // Check if fields still contain placeholder text or are empty
        if (name.equals(NAME_PLACEHOLDER) || contact.equals(CONTACT_PLACEHOLDER) || email.equals(EMAIL_PLACEHOLDER) || passwordRaw.equals(PASSWORD_PLACEHOLDER) || name.isEmpty() || contact.isEmpty() || email.isEmpty() || password.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
             return;
         }

         try {
            InputValidator.validateEmail(email);
            InputValidator.validatePassword(passwordRaw);
            InputValidator.validatePasswordStrength(passwordRaw);
        } catch (InvalidEmailException | InvalidPasswordException | WeakPasswordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Pass the visible password string to the service
            if (userService.registerUser(name, contact, email, password)) {
                JOptionPane.showMessageDialog(this, "Signup Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (navigationListener != null) {
                    navigationListener.navigateTo("login");
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}