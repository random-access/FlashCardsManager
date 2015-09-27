package org.random_access.flashcardsmanager_desktop.utils;

import java.awt.Toolkit;

import javax.swing.text.*;

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
			additionalFilter.insertString(fb, offset, text, attr);
		} else {
			Toolkit.getDefaultToolkit().beep(); // TODO: fix it - doesn't work
		}
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
	        throws BadLocationException {
		if(fb.getDocument().getLength() + text.length() - length <= maxChars) {
			additionalFilter.replace(fb, offset, length, text, attr);
		} else {
			Toolkit.getDefaultToolkit().beep(); // TODO: fix it - doesn't work
		}
	    }

}
