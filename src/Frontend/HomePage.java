package Frontend;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Interfaces.NavigationListener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePage extends JPanel {

    private static final Color COLOR_BACKGROUND = new Color(45, 45, 45); // Dark gray background
    private static final Color COLOR_NAVBAR = new Color(30, 30, 30);      // Slightly darker navbar
    private static final Color COLOR_CARD = new Color(60, 60, 60);        // Card background
    private static final Color COLOR_CARD_HOVER = new Color(75, 75, 75);  // Card background on hover
    private static final Color COLOR_TEXT = new Color(220, 220, 220);      // Light gray text
    private static final Color COLOR_BORDER = new Color(80, 80, 80);      // Border color
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_NAV_ICONS = new Font("SansSerif", Font.BOLD, 18);

    // --- Card Names for CardLayout ---
    private static final String HOME_PANEL = "Home";
    private static final String EMPLOYEE_PANEL = "EmployeePage";
    private static final String PASSENGER_PANEL = "PassengerPage"; // Matched filename
    private static final String REVENUE_PANEL = "RevenuePage";
    private static final String FLIGHT_PANEL = "FlightDetailsPage"; // Matched filename

    // --- Components ---
    private JPanel mainContentPanel; // Panel holding the different views (cards or detail pages)
    private CardLayout cardLayout;   // Layout manager to switch between panels
    private int adminId;
    private NavigationListener navigationListener;

    /**
     * Constructor for the HomePage. Sets up the JPanel and initializes components.
     */
    public HomePage(int adminId, NavigationListener navigationListener) {
        this.adminId = adminId;
        this.navigationListener = navigationListener;
        setLayout(new BorderLayout()); // Use BorderLayout for the main panel
        setBackground(COLOR_BACKGROUND); // Set background for the content pane

        // --- Create Navbar ---
        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.NORTH); // Add navbar to the top

        // --- Create Main Content Area with CardLayout ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(COLOR_BACKGROUND); // Ensure background consistency
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

        // --- Create Home Panel (with cards) ---
        JPanel homePanel = createHomePanel();
        mainContentPanel.add(homePanel, HOME_PANEL);

        // --- Create Placeholder Detail Panels ---
        mainContentPanel.add(createPlaceholderPanel(EMPLOYEE_PANEL, "Employee Details Page"), EMPLOYEE_PANEL);
        mainContentPanel.add(createPlaceholderPanel(PASSENGER_PANEL, "Passenger Details Page"), PASSENGER_PANEL);
        mainContentPanel.add(createPlaceholderPanel(REVENUE_PANEL, "Revenue Details Page"), REVENUE_PANEL);
        mainContentPanel.add(createPlaceholderPanel(FLIGHT_PANEL, "Flight Details Page"), FLIGHT_PANEL);

        add(mainContentPanel, BorderLayout.CENTER); // Add main content area to the center

        // --- Show the initial panel ---
        cardLayout.show(mainContentPanel, HOME_PANEL);
    }

    /**
     * Creates the top navigation bar panel.
     * @return JPanel representing the navigation bar.
     */
    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout()); // Use BorderLayout for icon/title positioning
        navBar.setBackground(COLOR_NAVBAR);
        navBar.setPreferredSize(new Dimension(getWidth(), 50)); // Set preferred height
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER)); // Bottom border

        // --- Home Icon (Placeholder) ---
        // Using a clickable JLabel as a simple button
        JLabel homeIcon = new JLabel(" \u2302 "); // Unicode House symbol (simple placeholder)
        homeIcon.setFont(FONT_NAV_ICONS);
        homeIcon.setForeground(COLOR_TEXT);
        homeIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeIcon.setBorder(new EmptyBorder(0, 15, 0, 0)); // Padding left
        homeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navigate back to the home screen (show the cards)
                cardLayout.show(mainContentPanel, HOME_PANEL);
            }
             @Override
            public void mouseEntered(MouseEvent e) {
                homeIcon.setForeground(Color.WHITE); // Highlight on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                homeIcon.setForeground(COLOR_TEXT);
            }
        });
        navBar.add(homeIcon, BorderLayout.WEST);

        // --- Title ---
        JLabel titleLabel = new JLabel("FMS");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the title
        navBar.add(titleLabel, BorderLayout.CENTER);

        // --- Profile Icon (Placeholder) ---
        // Using a clickable JLabel as a simple button
        JLabel profileIcon = new JLabel(" \uD83D\uDC64 "); // Unicode Bust in Silhouette (simple placeholder)
        profileIcon.setFont(FONT_NAV_ICONS);
        profileIcon.setForeground(COLOR_TEXT);
        profileIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileIcon.setBorder(new EmptyBorder(0, 0, 0, 15)); // Padding right
         profileIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Placeholder action for profile click
                JOptionPane.showMessageDialog(HomePage.this,
                        "Profile Icon Clicked!",
                        "Profile Action",
                        JOptionPane.INFORMATION_MESSAGE);
            }
             @Override
            public void mouseEntered(MouseEvent e) {
                profileIcon.setForeground(Color.WHITE); // Highlight on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                profileIcon.setForeground(COLOR_TEXT);
            }
        });
        navBar.add(profileIcon, BorderLayout.EAST);

        return navBar;
    }

     /**
     * Creates the main home panel containing the four navigation cards.
     * @return JPanel representing the home screen with cards.
     */
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new GridLayout(2, 2, 20, 20)); // 2x2 grid with gaps
        homePanel.setBackground(COLOR_BACKGROUND); // Match main background
        homePanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding around the grid

        // --- Create and Add Cards ---
        homePanel.add(createCard("Employee Details", EMPLOYEE_PANEL));
        homePanel.add(createCard("Passenger Details", PASSENGER_PANEL));
        homePanel.add(createCard("Revenue Details", REVENUE_PANEL));
        homePanel.add(createCard("Flight Details", FLIGHT_PANEL));

        return homePanel;
    }

    private JPanel createCard(String title, String panelName) {
        JPanel card = new JPanel(new BorderLayout()); // Use BorderLayout to center text
        card.setBackground(COLOR_CARD);
        card.setBorder(new LineBorder(COLOR_BORDER, 1, true)); // Rounded corners not standard in basic Swing borders
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel cardLabel = new JLabel(title);
        cardLabel.setFont(FONT_CARD_TITLE);
        cardLabel.setForeground(COLOR_TEXT);
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center text horizontally
        cardLabel.setVerticalAlignment(SwingConstants.CENTER);     // Center text vertically
        card.add(cardLabel, BorderLayout.CENTER);

        // --- Hover Effect ---
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(COLOR_CARD_HOVER); // Darken slightly on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_CARD); // Restore original color
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (panelName.equals(PASSENGER_PANEL)) {
                    navigationListener.navigateTo(new PassengerPage());
                } else if (panelName.equals(EMPLOYEE_PANEL)) {
                    navigationListener.navigateTo(new EmployeePage());
                } else if (panelName.equals(REVENUE_PANEL)) {
                    navigationListener.navigateTo(new EmployeePage());
                } else if (panelName.equals(FLIGHT_PANEL)) {
                    navigationListener.navigateTo("flight");
                }
            }
        });

        return card;
    }

    private JPanel createPlaceholderPanel(String name, String displayText) {
        JPanel panel = new JPanel(new GridBagLayout()); // Use GridBagLayout to center content
        panel.setName(name); // Set the name for identification if needed later
        panel.setBackground(COLOR_BACKGROUND);

        JLabel label = new JLabel(displayText);
        label.setFont(FONT_TITLE); // Use title font for emphasis
        label.setForeground(COLOR_TEXT);

        // GridBagConstraints to center the label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Take up horizontal space
        gbc.weighty = 1.0; // Take up vertical space
        gbc.anchor = GridBagConstraints.CENTER; // Center component

        panel.add(label, gbc);
        return panel;
    }
}