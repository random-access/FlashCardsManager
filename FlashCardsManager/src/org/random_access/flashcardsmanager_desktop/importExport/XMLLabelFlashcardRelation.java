package org.random_access.flashcardsmanager_desktop.importExport;

public class XMLLabelFlashcardRelation {

	private int id;
	private int labelId;
	private int cardId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLabelId() {
		return labelId;
	}

	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	@Override
	public String toString() {
		return "XMLLabelFlashcardRelation [id=" + id + ", labelId=" + labelId + ", cardId=" + cardId + "]";
	}

}
