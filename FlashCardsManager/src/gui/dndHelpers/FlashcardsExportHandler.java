package gui.dndHelpers;

import gui.helpers.FlashcardTableModel;

import java.awt.datatransfer.Transferable;

import javax.swing.*;

import core.FlashCard;

@SuppressWarnings("serial")
public class FlashcardsExportHandler extends TransferHandler {

	@Override
	public int getSourceActions(JComponent component) {
		return COPY;
	}

	public Transferable createTransferable(JComponent c) {
		JTable table = (JTable) c;
		FlashCard[] cards = new FlashCard[table.getSelectedRowCount()];
		for (int i = 0; i < table.getSelectedRowCount(); i++) {
			cards[i] = ((FlashcardTableModel) table.getModel()).getCard(table.getSelectedRows()[i]);
		}
		return new TransferableFlashcards(cards);
	}

	public void exportDone(JComponent c, Transferable t, int action) {

	}
}
