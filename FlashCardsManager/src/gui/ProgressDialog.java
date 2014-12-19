package gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.Logger;

@SuppressWarnings("serial")
public class ProgressDialog extends JDialog implements PropertyChangeListener{
	private JProgressBar progressBar;
	private JLabel lblInfo;
	private Box progressBox;
	private String text;
	
	public ProgressDialog(JFrame owner, String text) {
		super (owner, false);
		this.text = text;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Bitte warten..");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      }
		
		createAndAddWidgets();
		
		pack();
		setLocationRelativeTo(owner);
	}
	
	private void createAndAddWidgets() {
		progressBox = Box.createVerticalBox();
		progressBox.setBorder(BorderFactory.createEmptyBorder(40,60,40,60));
		
		this.add(progressBox, BorderLayout.CENTER);
		progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBox.add(progressBar);
		progressBox.add(Box.createVerticalStrut(10));
		lblInfo = new JLabel(text);
		lblInfo.setAlignmentX(CENTER_ALIGNMENT);
		progressBox.add(lblInfo);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
        }
	}
	
}
