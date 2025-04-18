package Frontend;

import Service.UserService;
import Components.NavigationBar;
import Utilities.PlaceholderUtils;
import javax.swing.*;
import Interfaces.NavigationListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupPage extends JPanel {
    private JTextField nameField, contactField, emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private UserService userService;
    private NavigationBar navBar;

    public SignupPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        // Use BorderLayout for the main panel
        setLayout(new BorderLayout());

        // Add Navigation Bar to the top (North)
        navBar = new NavigationBar("Sign Up", this::handleNavActions, 70);
        add(navBar, BorderLayout.NORTH);

        // Content Panel for form fields with BoxLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        // Name Field
        nameField = createTextField("Name");
        contentPanel.add(nameField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Contact Field
        contactField = createTextField("Contact Number");
        contentPanel.add(contactField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email Field
        emailField = createTextField("Email");
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

        // Add placeholder listeners
        PlaceholderUtils.addPlaceholderListeners(nameField, "Name");
        PlaceholderUtils.addPlaceholderListeners(contactField, "Contact Number");
        PlaceholderUtils.addPlaceholderListeners(emailField, "Email");
        PlaceholderUtils.addPlaceholderListeners(passwordField, "Password");

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> registerUser());
        contentPanel.add(registerButton);

        // Add content panel to CENTER
        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleNavActions(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();
        if ("←".equals(command)) {
            if (navigationListener != null) {
                navigationListener.navigateTo("login");
            }
        }
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
