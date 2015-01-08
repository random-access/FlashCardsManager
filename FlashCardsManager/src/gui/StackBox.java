package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class StackBox extends Box {

	private JCheckBox chk;
	private JLabel lblStack, lblNoOfCards;

	StackBox(int stackNo, int noOfCards, ChooseStacksDialog d) {
		super(BoxLayout.X_AXIS);
		chk = new JCheckBox();
		lblStack = new JLabel("Stapel " + stackNo + ": ");
		lblNoOfCards = new JLabel(noOfCards + " Karten");
		this.add(chk);
		this.add(lblStack);
		this.add(Box.createHorizontalGlue());
		this.add(lblNoOfCards);
		this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));
		
		chk.addItemListener(new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				d.controlOkButton();
			}
		});
	}

	public boolean isSelected() {
		return chk.isSelected();
	}

	public void setEnabled(boolean enabled) {
		chk.setEnabled(enabled);
		if (enabled == false) {
			lblStack.setFont(lblStack.getFont().deriveFont(Font.ITALIC));
			lblStack.setForeground(Color.GRAY);
			lblNoOfCards.setFont(lblNoOfCards.getFont().deriveFont(Font.ITALIC));
			lblNoOfCards.setForeground(Color.GRAY);
		}
	}

}
