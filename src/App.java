
import Frontend.RevenuePage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
        
public class App {
    public static void main(String[] args) throws Exception {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Revenue Page");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 600);
            frame.add(new RevenuePage());
            frame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application failed to start: "+e.getMessage());
        }
    }
}
