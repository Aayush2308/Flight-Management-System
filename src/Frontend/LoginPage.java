package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import javax.swing.border.AbstractBorder;

public class LoginPage extends JFrame {

    public LoginPage() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set to full screen size initially
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(600, 400));
        setLayout(new BorderLayout());

        // Custom wavy top
        WavyHeaderPanel header = new WavyHeaderPanel();
        header.setPreferredSize(new Dimension(0, 250));
        add(header, BorderLayout.NORTH);

        // Center panel with login form
        JPanel centerPanel = new JPanel(new GridBagLayout()); // centers content
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(350, 300));
        formPanel.setOpaque(false);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Sign In");

        Dimension fieldSize = new Dimension(320, 40);
        emailField.setMaximumSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        loginButton.setMaximumSize(new Dimension(320, 45));

        loginButton.setBackground(new Color(0, 150, 136));
        loginButton.setForeground(Color.WHITE);

        formPanel.add(new JLabel("Company Email"));
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(new JLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(loginButton);

        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}

class WavyHeaderPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 150, 136));

        int width = getWidth();
        int height = getHeight();

        GeneralPath wave = new GeneralPath();
        wave.moveTo(0, 0);
        wave.lineTo(0, height - 50);
        wave.quadTo(width / 4.0, height + 30, width / 2.0, height - 40);
        wave.quadTo(width * 3.0 / 4.0, height - 110, width, height - 30);
        wave.lineTo(width, 0);
        wave.closePath();

        g2.fill(wave);
    }
}

class RoundedBorder extends AbstractBorder {
    private final int radius;
    public RoundedBorder(int radius){
        this.radius = radius;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
    }
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = radius + 1;
        return insets;
    }
}

