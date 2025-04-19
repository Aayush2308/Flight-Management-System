package Frontend;

import Service.UserService;
import Components.NavigationBar; // Assuming this component exists
import Utilities.PlaceholderUtils; // Assuming this utility class exists
import javax.swing.*;
import Interfaces.NavigationListener; // Assuming this interface exists
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupPage extends JPanel {
    private JTextField nameField, contactField, emailField;
    private JPasswordField passwordField;
    private NavigationListener navigationListener;
    private UserService userService; // Assuming this service exists
    private NavigationBar navBar; // Assuming this component exists

    public SignupPage(NavigationListener listener) {
        this.navigationListener = listener;
        this.userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        // Use BorderLayout for the main panel
        setLayout(new BorderLayout());

        // Apply a dark background to the main panel areas not covered by sub-panels
        setBackground(new Color(45, 45, 48)); // Dark grey background

        // Add Navigation Bar to the top (North)
        navBar = new NavigationBar("Sign Up", this::handleNavActions, 70);
        // Apply dark theme to nav bar
        navBar.setBackground(Color.DARK_GRAY); // Dark background for the nav bar
        // Note: Styling the text/title inside NavigationBar depends on its implementation.
        // You might need to modify NavigationBar itself to change title color if this line isn't enough.
        // Example (assuming NavigationBar allows setting foreground):
        // navBar.setForeground(Color.WHITE); // Set text color if applicable

        add(navBar, BorderLayout.NORTH);

        // Content Panel for form fields with BoxLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        // Apply a dark theme background to the content panel
        contentPanel.setBackground(new Color(60, 63, 65)); // Lighter dark grey

        // Name Field
        nameField = createTextField("Name"); // Uses updated createTextField
        contentPanel.add(nameField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Contact Field
        contactField = createTextField("Contact Number"); // Uses updated createTextField
        contentPanel.add(contactField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email Field
        emailField = createTextField("Email"); // Uses updated createTextField
        contentPanel.add(emailField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password Field
        passwordField = new JPasswordField("Password");
        passwordField.setEchoChar((char) 0); // Show initial placeholder text
        passwordField.setMaximumSize(new Dimension(300, 40));
        // Apply dark theme colors
        passwordField.setBackground(new Color(50, 50, 50)); // Dark background
        passwordField.setForeground(Color.WHITE); // Light text color
        passwordField.setCaretColor(Color.WHITE); // White cursor
        PlaceholderUtils.addPlaceholderStyle(passwordField); // Apply placeholder style
        contentPanel.add(passwordField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add placeholder listeners (assuming PlaceholderUtils handles colors correctly)
        PlaceholderUtils.addPlaceholderListeners(nameField, "Name");
        PlaceholderUtils.addPlaceholderListeners(contactField, "Contact Number");
        PlaceholderUtils.addPlaceholderListeners(emailField, "Email");
        PlaceholderUtils.addPlaceholderListeners(passwordField, "Password");

        // Register Button
        JButton registerButton = new JButton("Register");
        // Apply dark theme button styling
        registerButton.setBackground(new Color(0, 120, 215)); // Blue accent
        registerButton.setForeground(Color.WHITE); // White text
        registerButton.setFocusPainted(false); // Remove focus border
        registerButton.setBorderPainted(false); // Remove default border
        registerButton.setOpaque(true); // Make sure background is painted
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center button horizontally
        registerButton.addActionListener(e -> registerUser());
        contentPanel.add(registerButton);

        // Add content panel to CENTER
        add(contentPanel, BorderLayout.CENTER);
    }

    private void handleNavActions(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();
        // Assuming the navBar button text is "←"
        if ("←".equals(command)) {
            if (navigationListener != null) {
                navigationListener.navigateTo("login"); // Navigate back to login page
            }
        }
         // If NavigationBar has a title label and you want to style it:
         // Example: if (navBar.getTitleLabel() != null) navBar.getTitleLabel().setForeground(Color.WHITE);
    }

    // Updated createTextField to include dark theme colors
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setMaximumSize(new Dimension(300, 40));
        // Apply dark theme colors
        field.setBackground(new Color(50, 50, 50)); // Dark background
        field.setForeground(Color.WHITE); // Light text color
        field.setCaretColor(Color.WHITE); // White cursor for visibility
        // PlaceholderUtils will handle setting the placeholder text color when empty/not focused.
        // Ensure PlaceholderUtils is configured to use a light color (like light grey) for placeholders on dark fields.
        PlaceholderUtils.addPlaceholderStyle(field);
        return field;
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (hasEmptyFields(name, contact, email, password)) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Assuming registerUser returns boolean indicating success
            if (userService.registerUser(name, contact, email, password)) {
                JOptionPane.showMessageDialog(this, "Signup Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (navigationListener != null) {
                    navigationListener.navigateTo("login"); // Navigate to login after successful signup
                }
            } else {
                 // Handle registration failure (e.g., email already exists) if userService.registerUser returns false
                 JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Log the exception in a real application
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to check for empty or placeholder fields
    private boolean hasEmptyFields(String... fields) {
        for (String field : fields) {
            // Check if the field is empty after trimming or still contains placeholder text
            if (field.isEmpty() ||
                (emailField.getText().trim().equals("Email") && field.equals(emailField.getText().trim())) ||
                (nameField.getText().trim().equals("Name") && field.equals(nameField.getText().trim())) ||
                (contactField.getText().trim().equals("Contact Number") && field.equals(contactField.getText().trim())) ||
                (String.valueOf(passwordField.getPassword()).trim().equals("Password") && field.equals(String.valueOf(passwordField.getPassword()).trim())) // Handle password field placeholder
               )
            {
                return true;
            }
        }
        return false;
    }
}