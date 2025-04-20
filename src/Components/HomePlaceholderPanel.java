package Components;

import Utilities.HomeUIConstants;

import javax.swing.*;
import java.awt.*;

public class HomePlaceholderPanel extends JPanel {
    public HomePlaceholderPanel(String name, String text) {
        setName(name);
        setBackground(HomeUIConstants.BACKGROUND);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel(text);
        label.setFont(HomeUIConstants.TITLE_FONT);
        label.setForeground(HomeUIConstants.TEXT_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1; gbc.weighty = 1;
        add(label, gbc);
    }
}