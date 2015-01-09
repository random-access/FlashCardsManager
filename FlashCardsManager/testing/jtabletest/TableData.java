package jtabletest;

import importExport.XMLFlashCard;

public class TableData {

	private XMLFlashCard card;
	private boolean selected;

	public TableData(XMLFlashCard card) {
		this.card = card;
	}

	public XMLFlashCard getCard() {
		return card;
	}

	public void setCard(XMLFlashCard card) {
		this.card = card;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
