// package Utilities;

// import javax.swing.*;
// import java.awt.*;

// public class FormUtils {
//     public static JPanel createFormPanel(String[] labels, JComponent[] fields) {
//         JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
//         for (int i = 0; i < labels.length; i++) {
//             panel.add(new JLabel(labels[i]));
//             panel.add(fields[i]);
//         }
//         return panel;
//     }
    
//     public static void showErrorDialog(Component parent, String message) {
//         JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
//     }
// }