package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginNavigationBar extends JPanel {
    private JLabel titleLabel;
    private ActionListener navListener;
    private final int height;

    public LoginNavigationBar(String title, ActionListener listener, int height) {
        this.navListener = listener;
        this.height = height;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Integer.MAX_VALUE, height));
        setOpaque(false);

        // Create gradient background panel
        JPanel gradientPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        gradientPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, height));

        // Navigation buttons
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, height);
            }
        };
        navButtons.setOpaque(false);

        // Title
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        int titleFontSize = Math.max(12, height*32/70);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, titleFontSize));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        gradientPanel.add(navButtons, BorderLayout.WEST);
        gradientPanel.add(titleLabel, BorderLayout.CENTER);
        add(gradientPanel, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text) {
        int buttonSize = height * 45 / 70;
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(231, 76, 60).darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(231, 76, 60));
                } else {
                    g2d.setColor(new Color(255, 255, 255, 80));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(text, 
                    (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2
                );
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(buttonSize, buttonSize));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(navListener);
        return button;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}