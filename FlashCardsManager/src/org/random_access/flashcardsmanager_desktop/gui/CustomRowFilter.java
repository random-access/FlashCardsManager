package org.random_access.flashcardsmanager_desktop.gui;

import javax.swing.JTextField;
import javax.swing.RowFilter;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;
import org.random_access.flashcardsmanager_desktop.core.Label;
import org.random_access.flashcardsmanager_desktop.gui.helpers.FlashcardTableModel;

public class CustomRowFilter extends RowFilter<FlashcardTableModel, Integer> {

	private Label label;
	private JTextField txt;

	public CustomRowFilter(Label label) {
		this.label = label;
	}

	public CustomRowFilter(JTextField txt) {
		this.txt = txt;
	}

	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends FlashcardTableModel, ? extends Integer> entry) {
		FlashcardTableModel model = entry.getModel();
		FlashCard card = model.getCard(entry.getIdentifier());
		if (label != null) {
			return card.getLabels().contains(label);
		}
		return card.getQuestion().toLowerCase().contains(txt.getText().toLowerCase());
	}

}
