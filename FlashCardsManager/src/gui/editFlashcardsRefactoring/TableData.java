package gui.editFlashcardsRefactoring;

import core.FlashCard;

public class TableData {

	private FlashCard card;
	private boolean selected;

	public TableData(FlashCard card) {
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
