package gui;

import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class MyTextPane extends JTextPane {
	
	private int minimalWidth, minimalHeight;

	public MyTextPane(int minimalWidth, int minimalHeight) {
		super();
		this.minimalWidth = minimalWidth;
		this.minimalHeight = minimalHeight;
	}

	public MyTextPane(StyledDocument doc, int minimalWidth, int minimalHeight) {
		super(doc);
		this.minimalWidth = minimalWidth;
		this.minimalHeight = minimalHeight;
	}
	
	public void setMinimalWidth(int minimalWidth) {
		this.minimalWidth = minimalWidth;
	}
	
	public void setMinimalHeight(int minimalHeight) {
		this.minimalHeight = minimalHeight;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (this.getWidth() > minimalWidth && this.getHeight() > minimalHeight) {
			return super.getPreferredSize();
		} else {
			if (this.getHeight() > minimalHeight) {
				return new Dimension(minimalWidth, super.getPreferredSize().height);
			} else if (this.getWidth() > minimalWidth) {
				return new Dimension(super.getPreferredSize().width, minimalHeight);
			} else {
				return new Dimension(minimalWidth, minimalHeight);
			}
		}
	}
	
//	@Override
//	public Dimension getPreferredScrollableViewportSize() {
//		if (getPreferredScrollableViewportSize().width > 450) {
//			return super.getPreferredSize();
//		} else {
//			return new Dimension(450, super.getPreferredScrollableViewportSize().height);
//		}
//	}
	
	
}
