package gui.helpers;

import core.FlashCard;

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
