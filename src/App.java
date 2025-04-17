
import Frontend.RevenuePage;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
        
public class App {
    public static void main(String[] args) throws Exception {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> new RevenuePage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application failed to start: "+e.getMessage());
        }
    }
}
