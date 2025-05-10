package Utilities;

import Interfaces.NavigationListener;
import javax.swing.*;
import java.awt.*;
import Frontend.*;

public class AppNavigator implements NavigationListener {
    private final Container contentPane;

    public AppNavigator(Container contentPane) {
        this.contentPane = contentPane;
    }

    @Override
    public void navigateTo(String pageName) {
        contentPane.removeAll();
        switch (pageName.toLowerCase()) {
            case "login":
                contentPane.add(new LoginPage(this));
                break;
            case "signup":
                contentPane.add(new SignupPage(this));
                break;
            case "employee":
                contentPane.add(new EmployeePage(0, this));
                break;
            case "revenue":
                contentPane.add(new RevenuePage(0, this));
                break;
            case "passenger":
                contentPane.add(new PassengerPage(0, this));
                break;
            case "flight":
                contentPane.add(new FlightPage(0, this));
                break;
            case "home":
                contentPane.add(new HomePage(0, this));
                break;
            case "update":
                contentPane.add(new UpdateProfilePage(0, this));
                break;
            default:
                contentPane.add(new JLabel("Page not found: " + pageName));
        }
        contentPane.revalidate();
        contentPane.repaint();
    }

    @Override
    public void navigateTo(JPanel panel) {
        contentPane.removeAll();
        contentPane.add(panel);
        contentPane.revalidate();
        contentPane.repaint();
    }
}
