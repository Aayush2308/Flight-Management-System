package Frontend;

import Service.NavigationListener;
import Service.UserService;
import Utilities.PlaceholderUtils;
import javax.swing.*;
import java.awt.*;

public class SignupPage extends JPanel {
    private JTextField nameField, contactField, emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private UserService userService;

    public SignupPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Name Field
        nameField = createTextField("Name");
        add(nameField);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Contact Field
        contactField = createTextField("Contact Number");
        add(contactField);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Email Field
        emailField = createTextField("Email");
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

        // Add placeholder listeners
        PlaceholderUtils.addPlaceholderListeners(nameField, "Name");
        PlaceholderUtils.addPlaceholderListeners(contactField, "Contact Number");
        PlaceholderUtils.addPlaceholderListeners(emailField, "Email");
        PlaceholderUtils.addPlaceholderListeners(passwordField, "Password");

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> registerUser());
        add(registerButton);
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setMaximumSize(new Dimension(300, 40));
        field.setForeground(Color.GRAY);
        PlaceholderUtils.addPlaceholderStyle(field);
        return field;
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (hasEmptyFields(name, contact, email, password)) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            if (userService.registerUser(name, contact, email, password)) {
                JOptionPane.showMessageDialog(this, "Signup Successful");
                if (navigationListener != null) {
                    navigationListener.navigateTo("login");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private boolean hasEmptyFields(String... fields) {
        for (String field : fields) {
            if (field.isEmpty() || field.equals("Name") || 
                field.equals("Contact Number") || 
                field.equals("Email") || 
                field.equals("Password")) {
                return true;
            }
        }
        return false;
    }
}