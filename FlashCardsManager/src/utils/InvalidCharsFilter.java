package utils;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.text.*;

public class InvalidCharsFilter extends DocumentFilter {
	private static final char[] INVALID_CHARS = { '\'' };
	boolean DEBUG = false;
	Component owner;
	
	public InvalidCharsFilter(Component owner) {
		this.owner = owner;
	}

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset,
			String string, AttributeSet attr) throws BadLocationException {

		StringBuffer buffer = new StringBuffer(string);
		for (int i = buffer.length() - 1; i >= 0; i--) {
			char ch = buffer.charAt(i);
			for (int j = 0; j < INVALID_CHARS.length; j++) {
				if (ch == INVALID_CHARS[j]) {
					buffer.deleteCharAt(i);
					JOptionPane.showMessageDialog(owner,
							"Unerlaubtes Zeichen: " + INVALID_CHARS[j],
							"Fehler", JOptionPane.ERROR_MESSAGE);
					if (DEBUG) {
						System.out.println("deleted ' !");
					}
				}
			}
		}
		super.insertString(fb, offset, buffer.toString(), attr);
	}

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
			String string, AttributeSet attr) throws BadLocationException {
		if (length > 0)
			fb.remove(offset, length);
		insertString(fb, offset, string, attr);
	}

}
