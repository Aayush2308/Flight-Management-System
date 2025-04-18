package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {
    private Color color;
    
    public StyledButton(String text, Color color) {
        super(text);
        this.color = color;
        setPreferredSize(new Dimension(100, 35));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                setBackground(color);
            }
            public void mousePressed(MouseEvent e) {
                setBackground(color.darker());
            }
            public void mouseReleased(MouseEvent e) {
                setBackground(color.brighter());
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(getText(),
            (getWidth() - fm.stringWidth(getText())) / 2,
            (getHeight() + fm.getAscent() - fm.getDescent()) / 2
        );
        g2d.dispose();
    }
}
