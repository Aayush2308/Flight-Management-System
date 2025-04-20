import Frontend.*;
import Interfaces.NavigationListener;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("My App");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(850, 600);
                frame.setLocationRelativeTo(null);

                // Panel to hold dynamic content
                Container contentPane = frame.getContentPane();

                final NavigationListener[] listener = new NavigationListener[1];

                // Define the NavigationListener logic
                listener[0] = new NavigationListener() {
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
                                contentPane.add(new FlightDetails(0, this));
                                break;
                            case "home":
                                contentPane.add(new HomePage(0, this)); // default homepage
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
                };

                // Initial screen
                contentPane.add(new LoginPage(listener[0]));

                frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application failed to start: " + e.getMessage());
        }
    }
}
