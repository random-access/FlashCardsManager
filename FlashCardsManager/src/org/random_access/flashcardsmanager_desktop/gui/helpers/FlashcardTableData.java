package org.random_access.flashcardsmanager_desktop.gui.helpers;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;

public class FlashcardTableData {

	private FlashCard card;
	private boolean selected;

	public FlashcardTableData(FlashCard card) {
		this.card = card;
	}

	public FlashCard getCard() {
		return card;
	}

	public void setCard(FlashCard card) {
		this.card = card;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
