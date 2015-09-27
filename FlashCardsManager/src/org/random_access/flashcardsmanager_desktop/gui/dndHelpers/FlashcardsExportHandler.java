package org.random_access.flashcardsmanager_desktop.gui.dndHelpers;

import java.awt.datatransfer.Transferable;

import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;
import org.random_access.flashcardsmanager_desktop.gui.helpers.FlashcardTableModel;

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
