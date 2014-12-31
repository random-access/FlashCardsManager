package utils;

import java.awt.Toolkit;

import javax.swing.text.*;

public class DocumentSizeFilter extends DocumentFilter {
	private int maxChars;
	boolean DEBUG = false;
	
	public DocumentSizeFilter (int maxChars) {
		this.maxChars = maxChars;
	}
	
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		if (DEBUG) {
			System.out.println("in DocumentSizeFilter's insertString method");
		}
		if (fb.getDocument().getLength() + string.length() <= maxChars) {
			super.insertString(fb, offset, string, attr);
		} else {
			Toolkit.getDefaultToolkit().beep(); // TODO: fix it - doesn't work
		}
	}
	
	@Override
	public void replace (FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
		if (DEBUG) {
			System.out.println("in DocumentSizeFilter's replace method");
		}
		if(fb.getDocument().getLength() + text.length() - length <= maxChars) {
			super.replace(fb, offset, length, text, attr);
		} else {
			Toolkit.getDefaultToolkit().beep(); // TODO: fix it - doesn't work
		}
	}
	
}
