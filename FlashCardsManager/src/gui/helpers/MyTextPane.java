package gui.helpers;

import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class MyTextPane extends JTextPane {

	private int minimalWidth, minimalHeight;
	private static final int MIN_VALUE = 50;

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
		if (minimalWidth < MIN_VALUE) {
			this.minimalWidth = MIN_VALUE;
		} else {
			this.minimalWidth = minimalWidth;
		}
	}

	public int getMinimalWidth() {
		return minimalWidth;
	}

	public void setMinimalHeight(int minimalHeight) {
		if (minimalHeight < MIN_VALUE) {
			this.minimalHeight = MIN_VALUE;
		} else {
			this.minimalHeight = minimalHeight;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		// makes textpane have a minimum size but with normal resize behaviour
		// when getting larger
		int prefWidth, prefHeight;
		prefWidth = this.minimalWidth;
		if (super.getPreferredSize().height < minimalHeight) {
			prefHeight = minimalHeight;
		} else {
			prefHeight = super.getPreferredSize().height;
		}
		return new Dimension(prefWidth, prefHeight);
	}

}
