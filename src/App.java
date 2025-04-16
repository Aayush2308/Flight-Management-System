
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Frontend.LoginPage;


public class App {
    public static void main(String[] args) throws Exception {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> new LoginPage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application failed to start: "+e.getMessage());
        }
    }
}
