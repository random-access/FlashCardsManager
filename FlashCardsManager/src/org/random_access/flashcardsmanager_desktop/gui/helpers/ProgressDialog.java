package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;

@SuppressWarnings("serial")
public class ProgressDialog extends JDialog implements PropertyChangeListener {

	private JProgressBar progressBar;
	private JLabel lblInfo;
	private Box progressBox;
	private String info;

	public ProgressDialog(JFrame owner, String text) {
		super(owner, false);
		this.info = text;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Bitte warten..");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
			CustomErrorHandling.showInternalError(null, exc);
		}

		createAndAddWidgets();

		pack();
		setLocationRelativeTo(owner);
	}

	private void createAndAddWidgets() {
		progressBox = Box.createVerticalBox();
		progressBox.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

		this.add(progressBox, BorderLayout.CENTER);
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBox.add(progressBar);
		progressBox.add(Box.createVerticalStrut(10));
		lblInfo = new JLabel(info);
		lblInfo.setPreferredSize(new Dimension(200, lblInfo.getPreferredSize().height));
		lblInfo.setAlignmentX(CENTER_ALIGNMENT);
		progressBox.add(lblInfo);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if ("progress" == event.getPropertyName()) {
			int progress = (Integer) event.getNewValue();
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
		}
	}

	public void changeInfo(String text) {
		lblInfo.setText(text);
		this.revalidate();
	}

}
