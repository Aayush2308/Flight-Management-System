package Components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

public class ModernHeaderRenderer extends DefaultTableCellRenderer {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    
    public ModernHeaderRenderer() {
        setHorizontalAlignment(JLabel.LEFT);
        setFont(HEADER_FONT);
        setBackground(PRIMARY_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
