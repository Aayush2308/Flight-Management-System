package Components;

import Frontend.EmployeePage;
import Frontend.PassengerPage;
import Frontend.RevenuePage;
import Frontend.FlightPage;
import Interfaces.NavigationListener;
import Utilities.HomeUIConstants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomeDashboardCard extends JPanel {
    public HomeDashboardCard(String text, String panelKey, int adminId, NavigationListener nav) {
        setLayout(new BorderLayout());
        setBackground(HomeUIConstants.CARD_BG);
        setBorder(new LineBorder(HomeUIConstants.BORDER_COLOR, 1, true));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(HomeUIConstants.CARD_TITLE_FONT);
        lbl.setForeground(HomeUIConstants.CARD_TEXT_COLOR);
        add(lbl, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { setBackground(HomeUIConstants.CARD_HOVER_BG); }
            public void mouseExited(MouseEvent e)  { setBackground(HomeUIConstants.CARD_BG); }
            public void mouseClicked(MouseEvent e) {
                switch (panelKey) {
                  case "EmployeePage":   nav.navigateTo(new EmployeePage(adminId, nav)); break;
                  case "PassengerPage":  nav.navigateTo(new PassengerPage(adminId, nav)); break;
                  case "RevenuePage":    nav.navigateTo(new RevenuePage(adminId, nav)); break;
                  case "FlightPage":     nav.navigateTo(new FlightPage(adminId, nav)); break;
                }
            }
        });
    }
}