package Components;

import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.awt.*;

public class ShadowBorder {
    public static Border create() {
        return BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                new EmptyBorder(10, 15, 10, 15)
            )
        );
    }
}