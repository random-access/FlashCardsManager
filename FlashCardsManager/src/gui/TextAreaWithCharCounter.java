package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

public class TextAreaWithCharCounter extends JTextArea{
    private JTextArea txt;
    private JLabel lblCurrentCharCount;
    private DefaultStyledDocument doc;

    public TextAreaWithCharCounter() {
        txt = new JTextArea();
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        doc = new DefaultStyledDocument();
        doc.setDocumentFilter(null);
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
        lblCurrentCharCount = new JLabel(doc.getLength() + " Zeichen");
        txt.setDocument(doc);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.add(txt, BorderLayout.CENTER);
        this.add(lblCurrentCharCount, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(300, 150));
    }

    private void updateCount() {
        lblCurrentCharCount.setText(doc.getLength() + " Zeichen");
    }

    @Override
    public void setText(String text) {
        txt.setText(text);
    }

    @Override
    public String getText() {
        return txt.getText();
    }
}
