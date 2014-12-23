package tests;

import javax.swing.*;

public class JSpinnerTester {
	
	private JSpinner spinner;
	private SpinnerModel spinnerModel;
	
	public JSpinnerTester() {
		spinnerModel = new SpinnerNumberModel(12,6,48,2);
		spinner = new JSpinner(spinnerModel);
		spinner.setActionMap(new ActionMap());
		
	}
	
	
}
