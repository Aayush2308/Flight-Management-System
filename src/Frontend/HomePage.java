package Frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Interfaces.NavigationListener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePage extends JPanel {

    private static final Color COLOR_BACKGROUND = new Color(45, 45, 45);
    private static final Color COLOR_NAVBAR = new Color(30, 30, 30);
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_CARD_HOVER = new Color(75, 75, 75);
    private static final Color COLOR_TEXT = new Color(220, 220, 220);
    private static final Color CARD_TEXT = Color.BLACK;
    private static final Color COLOR_BORDER = new Color(80, 80, 80);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_NAV_ICONS = new Font("SansSerif", Font.BOLD, 18);

    private static final String HOME_PANEL = "Home";
    private static final String EMPLOYEE_PANEL = "EmployeePage";
    private static final String PASSENGER_PANEL = "PassengerPage";
    private static final String REVENUE_PANEL = "RevenuePage";
    private static final String FLIGHT_PANEL = "FlightDetailsPage";

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private int adminId;
    private NavigationListener navigationListener;

    public HomePage(int adminId, NavigationListener navigationListener) {
        this.adminId = adminId;
        this.navigationListener = navigationListener;
        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND);

        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(COLOR_BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel homePanel = createHomePanel();
        mainContentPanel.add(homePanel, HOME_PANEL);

        mainContentPanel.add(createPlaceholderPanel(EMPLOYEE_PANEL, "Employee Details Page"), EMPLOYEE_PANEL);
        mainContentPanel.add(createPlaceholderPanel(PASSENGER_PANEL, "Passenger Details Page"), PASSENGER_PANEL);
        mainContentPanel.add(createPlaceholderPanel(REVENUE_PANEL, "Revenue Details Page"), REVENUE_PANEL);
        mainContentPanel.add(createPlaceholderPanel(FLIGHT_PANEL, "Flight Details Page"), FLIGHT_PANEL);

        add(mainContentPanel, BorderLayout.CENTER);

        cardLayout.show(mainContentPanel, HOME_PANEL);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(COLOR_NAVBAR);
        navBar.setPreferredSize(new Dimension(getWidth(), 50));
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // --- Logout Button ---
        JLabel logoutButton = new JLabel("Logout");
        logoutButton.setFont(FONT_NAV_ICONS);
        logoutButton.setForeground(COLOR_TEXT);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(new EmptyBorder(0, 15, 0, 0));
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navigationListener != null) {
                    navigationListener.navigateTo("login"); // Assuming "login" is the identifier for the login page
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setForeground(COLOR_TEXT);
            }
        });
        navBar.add(logoutButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("FMS");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navBar.add(titleLabel, BorderLayout.CENTER);

        JLabel profileIcon = new JLabel(" \uD83D\uDC64 ");
        profileIcon.setFont(FONT_NAV_ICONS);
        profileIcon.setForeground(COLOR_TEXT);
        profileIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileIcon.setBorder(new EmptyBorder(0, 0, 0, 15));
        profileIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(navigationListener != null){
                    navigationListener.navigateTo(new ProfilePage(adminId, navigationListener));
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                profileIcon.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                profileIcon.setForeground(COLOR_TEXT);
            }
        });
        navBar.add(profileIcon, BorderLayout.EAST);

        return navBar;
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new GridLayout(2, 2, 20, 20));
        homePanel.setBackground(COLOR_BACKGROUND);
        homePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        homePanel.add(createCard("Employee Details", EMPLOYEE_PANEL));
        homePanel.add(createCard("Passenger Details", PASSENGER_PANEL));
        homePanel.add(createCard("Revenue Details", REVENUE_PANEL));
        homePanel.add(createCard("Flight Details", FLIGHT_PANEL));

        return homePanel;
    }

    private JPanel createCard(String title, String panelName) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel cardLabel = new JLabel(title);
        cardLabel.setFont(FONT_CARD_TITLE);
        cardLabel.setForeground(CARD_TEXT);
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardLabel.setVerticalAlignment(SwingConstants.CENTER);
        card.add(cardLabel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(COLOR_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_CARD);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (panelName.equals(PASSENGER_PANEL)) {
                    navigationListener.navigateTo(new PassengerPage(adminId, navigationListener));
                } else if (panelName.equals(EMPLOYEE_PANEL)) {
                    navigationListener.navigateTo(new EmployeePage(adminId, navigationListener));
                } else if (panelName.equals(REVENUE_PANEL)) {
                    navigationListener.navigateTo(new RevenuePage(adminId, navigationListener));
                } else if (panelName.equals(FLIGHT_PANEL)) {
                    navigationListener.navigateTo(new FlightPage(adminId, navigationListener));         //
                }
            }
        });

        return card;
    }

    private JPanel createPlaceholderPanel(String name, String displayText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setName(name);
        panel.setBackground(COLOR_BACKGROUND);

        JLabel label = new JLabel(displayText);
        label.setFont(FONT_TITLE);
        label.setForeground(COLOR_TEXT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(label, gbc);
        return panel;
    }
}