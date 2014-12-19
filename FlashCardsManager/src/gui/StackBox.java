package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class StackBox extends Box {
	
	private JCheckBox chk;
	private JLabel lblStack, lblNoOfCards;

	public StackBox(int stackNo, int noOfCards) {
		super(BoxLayout.X_AXIS);
		chk = new JCheckBox();
		lblStack = new JLabel("Stapel " + stackNo + ": ");
		lblNoOfCards = new JLabel(noOfCards + " Karten");
		this.add(chk);
		this.add(lblStack);
		this.add(Box.createHorizontalGlue());
		this.add(lblNoOfCards);
		this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));
	}
	
	public boolean isSelected() {
		return chk.isSelected();
	}

}
