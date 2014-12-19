package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ProjectBox extends Box {
	
	private JCheckBox chk;
	private JLabel lblProject;

	public ProjectBox(String projectName) {
		super(BoxLayout.X_AXIS );
		this.setAlignmentX(LEFT_ALIGNMENT);
		chk = new JCheckBox();
		lblProject = new JLabel(projectName);
		this.add(chk);
		this.add(lblProject);
		this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));
	}
	
	public boolean isSelected() {
		return chk.isSelected();
	}

}
