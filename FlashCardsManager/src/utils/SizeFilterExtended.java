package utils;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class SizeFilterExtended extends DocumentFilter {

	private final DocumentFilter additionalFilter;
	private int maxChars;

	public SizeFilterExtended(DocumentFilter additionalFilter, int maxChars) {
		this.additionalFilter = additionalFilter;
		this.maxChars = maxChars;
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String text,
			AttributeSet attr) throws BadLocationException {
		if (fb.getDocument().getLength() + text.length() <= maxChars) {
			System.out.println("Länge: " + fb.getDocument().getLength() + text.length());
			additionalFilter.insertString(fb, offset, text, attr);
		} else {
			Toolkit.getDefaultToolkit().beep(); // TODO: fix it - doesn't work
		}
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
	        throws BadLocationException {
		if(fb.getDocument().getLength() + text.length() - length <= maxChars) {
			System.out.println("Länge: " + fb.getDocument().getLength() + text.length());
			additionalFilter.replace(fb, offset, length, text, attr);
		} else {
			Toolkit.getDefaultToolkit().beep(); // TODO: fix it - doesn't work
		}
	    }

}
