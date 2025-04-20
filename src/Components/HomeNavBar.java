package Components;

import Frontend.LoginPage;
import Frontend.ProfilePage;
import Interfaces.NavigationListener;
import Utilities.HomeUIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomeNavBar extends JPanel {
    public HomeNavBar(int adminId, NavigationListener nav) {
        setLayout(new BorderLayout());
        setBackground(HomeUIConstants.NAVBAR_BG);
        setPreferredSize(new Dimension(0, 50));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, HomeUIConstants.BORDER_COLOR));

        JLabel logout = new JLabel("Logout");
        logout.setFont(HomeUIConstants.NAV_ICON_FONT);
        logout.setForeground(HomeUIConstants.TEXT_COLOR);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setBorder(new EmptyBorder(0, 15, 0, 0));
        logout.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                nav.navigateTo(new LoginPage(nav));
            }
            public void mouseEntered(MouseEvent e) { logout.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { logout.setForeground(HomeUIConstants.TEXT_COLOR); }
        });
        add(logout, BorderLayout.WEST);

        JLabel title = new JLabel("FMS", SwingConstants.CENTER);
        title.setFont(HomeUIConstants.TITLE_FONT);
        title.setForeground(HomeUIConstants.TEXT_COLOR);
        add(title, BorderLayout.CENTER);

        JLabel profile = new JLabel("\uD83D\uDC64");
        profile.setFont(HomeUIConstants.NAV_ICON_FONT);
        profile.setForeground(HomeUIConstants.TEXT_COLOR);
        profile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profile.setBorder(new EmptyBorder(0, 0, 0, 15));
        profile.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                nav.navigateTo(new ProfilePage(adminId, nav));
            }
            public void mouseEntered(MouseEvent e) { profile.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { profile.setForeground(HomeUIConstants.TEXT_COLOR); }
        });
        add(profile, BorderLayout.EAST);
    }
}