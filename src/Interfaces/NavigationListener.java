package Interfaces;
import javax.swing.JPanel;

public interface NavigationListener {
    void navigateTo(String pageName);
    void navigateTo(JPanel pageComponent);
}
