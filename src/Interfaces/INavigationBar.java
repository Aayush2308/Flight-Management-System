package Interfaces;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public interface INavigationBar {
    void setTitle(String title);
    void setNavigationListener(ActionListener listener);
    void addCustomButton(JButton button);
    JPanel getNavBarPanel();
}
