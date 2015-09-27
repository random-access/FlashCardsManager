package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TransparencyTextField extends JTextField {

    public TransparencyTextField(String text, int cols) {
        super(text, cols);
        setOpaque(false);
    }

    public TransparencyTextField(String text) {
        super(text);
        setOpaque(false);
    }

    public TransparencyTextField() {
        super();
        setOpaque(false);
    }

    public TransparencyTextField(int cols) {
        super(cols);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        Rectangle rect = g.getClip().getBounds();
        g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
        super.paintComponent(g);
    }

}
