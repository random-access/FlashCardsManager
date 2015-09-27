package org.random_access.flashcardsmanager_desktop.gui;

import javax.swing.*;

@SuppressWarnings("serial")
public class ProjectBox extends Box {
	
	private JCheckBox chk;
	private JLabel lblProject;

	ProjectBox(String projectName) {
		super(BoxLayout.X_AXIS );
		this.setAlignmentX(LEFT_ALIGNMENT);
		chk = new JCheckBox();
		lblProject = new JLabel(projectName);
		this.add(chk);
		this.add(lblProject);
		this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));
	}
	
	boolean isSelected() {
		return chk.isSelected();
	}

}
