package gui.helpers;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class WrappedLabelWithStackDisplay extends JTextArea {

    WrappedLabelWithStackDisplay (String text, int rows, int cols) {
        super(text, rows, cols);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setFont(getFont().deriveFont(18.0F));
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedSoftBevelBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
    }
}
