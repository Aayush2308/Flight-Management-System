import Frontend.*;
import Interfaces.NavigationListener;
import javax.swing.*;
import java.awt.*;
import Utilities.AppNavigator;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("My App");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(850, 600);
                frame.setLocationRelativeTo(null);

                Container contentPane = frame.getContentPane();

                // Use the new AppNavigator class
                NavigationListener listener = new AppNavigator(contentPane);

                // Set the initial screen
                contentPane.add(new LoginPage(listener));

                frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application failed to start: " + e.getMessage());
        }
    }
}
