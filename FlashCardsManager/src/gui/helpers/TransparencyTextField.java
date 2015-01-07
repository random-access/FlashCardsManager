package gui.helpers;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JTextField;

public class TransparencyTextField extends JTextField {

	public TransparencyTextField(String text, int cols) {
		super(text, cols);
		setOpaque(false);
	}

	public TransparencyTextField(String text) {
		super(text);
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		Rectangle tBounds = g.getClip().getBounds();
		g.fillRect((int) tBounds.getX(), (int) tBounds.getY(), (int) tBounds.getWidth(), (int) tBounds.getHeight());
		super.paintComponent(g);
	}

}
