import Frontend.*;
import Interfaces.NavigationListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
        
public class App {
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                
                JFrame frame = new JFrame("My App");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(850, 600);
                frame.setLocationRelativeTo(null);

                final NavigationListener[] listener = new NavigationListener[1];

                listener[0] = pageName -> {
                    frame.getContentPane().removeAll();
                    switch(pageName){
                        case "login":
                            frame.add(new LoginPage(listener[0]));
                            break;
                        case "signup":
                            frame.add(new SignupPage(listener[0]));
                            break;
                        case "employee":
                            frame.add(new EmployeePage());
                            break;
                        case "revenue":
                            frame.add(new RevenuePage());
                            break;
                    }
                    frame.revalidate();
                    frame.repaint();
                };
                frame.add(new LoginPage(listener[0]));
                frame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application failed to start: " + e.getMessage());
        }
    }
}