package Frontend;

import Components.HomeNavBar;
import Components.HomeDashboardCard;
import Components.HomePlaceholderPanel;
import Interfaces.NavigationListener;
import Utilities.HomeUIConstants;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {
    private static final String HOME_KEY      = "Home";
    private static final String EMP_KEY       = "EmployeePage";
    private static final String PASS_KEY      = "PassengerPage";
    private static final String REV_KEY       = "RevenuePage";
    private static final String FLIGHT_KEY    = "FlightPage";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContent     = new JPanel(cardLayout);

    public HomePage(int adminId, NavigationListener nav) {
        setLayout(new BorderLayout());
        setBackground(HomeUIConstants.BACKGROUND);

        add(new HomeNavBar(adminId, nav), BorderLayout.NORTH);

        mainContent.setBackground(HomeUIConstants.BACKGROUND);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        mainContent.add(createHomeGrid(adminId, nav), HOME_KEY);
        mainContent.add(new HomePlaceholderPanel(EMP_KEY,   "Employee Details Page"),   EMP_KEY);
        mainContent.add(new HomePlaceholderPanel(PASS_KEY,  "Passenger Details Page"),  PASS_KEY);
        mainContent.add(new HomePlaceholderPanel(REV_KEY,   "Revenue Details Page"),    REV_KEY);
        mainContent.add(new HomePlaceholderPanel(FLIGHT_KEY,"Flight Details Page"),     FLIGHT_KEY);

        add(mainContent, BorderLayout.CENTER);
        cardLayout.show(mainContent, HOME_KEY);
    }

    private JPanel createHomeGrid(int adminId, NavigationListener nav) {
        JPanel grid = new JPanel(new GridLayout(2,2,20,20));
        grid.setBackground(HomeUIConstants.BACKGROUND);
        grid.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        grid.add(new HomeDashboardCard("Employee Details",   EMP_KEY,    adminId, nav));
        grid.add(new HomeDashboardCard("Passenger Details",  PASS_KEY,   adminId, nav));
        grid.add(new HomeDashboardCard("Revenue Details",    REV_KEY,    adminId, nav));
        grid.add(new HomeDashboardCard("Flight Details",     FLIGHT_KEY, adminId, nav));

        return grid;
    }
}