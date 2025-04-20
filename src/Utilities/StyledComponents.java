package Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StyledComponents {
    public static final Color BACKGROUND = Color.WHITE;
    public static final Color TEXT_COLOR = Color.BLACK;
    public static final Color ACCENT_COLOR = new Color(0, 122, 204);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_COLOR);
        return field;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JCheckBox createCheckBox(String text, ActionListener action) {
        JCheckBox check = new JCheckBox(text);
        check.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        check.setBackground(BACKGROUND);
        check.addActionListener(action);
        return check;
    }

    public static JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }

    public static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(BACKGROUND);
        return panel;
    }

    public static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public static JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        return formPanel;
    }

    public static void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(createLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
