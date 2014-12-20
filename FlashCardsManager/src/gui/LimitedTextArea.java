package gui;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

import utils.DocumentSizeFilter;

@SuppressWarnings("serial")
public class LimitedTextArea extends JPanel {
	
	private JTextArea txt;
	private JLabel lblRemainingChars;
	private DefaultStyledDocument doc;
	private int maxChars;
	
	LimitedTextArea(int rows, int cols, int maxChars) {
		this.maxChars = maxChars;
		txt = new JTextArea (rows, cols);
		txt.setLineWrap(true);
		txt.setWrapStyleWord(true);
		lblRemainingChars = new JLabel(maxChars + " / " + maxChars + " Zeichen");
		
		doc = new DefaultStyledDocument();
		doc.setDocumentFilter(new DocumentSizeFilter(maxChars));
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCount();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateCount();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateCount();
			}

		});
		txt.setDocument(doc);
		this.setLayout(new BorderLayout());
		this.add(txt, BorderLayout.CENTER);
		this.add(lblRemainingChars, BorderLayout.SOUTH);
	}

	private void updateCount() {
		lblRemainingChars.setText(maxChars - doc.getLength() + " / " + maxChars + " Zeichen");
	}
	
	void setText(String text) {
		txt.setText(text);
	}
	
	String getText() {
		return txt.getText();
	}

}
