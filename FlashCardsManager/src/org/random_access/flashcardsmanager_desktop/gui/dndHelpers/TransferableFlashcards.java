package org.random_access.flashcardsmanager_desktop.gui.dndHelpers;

import java.awt.datatransfer.*;
import java.io.IOException;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;

public class TransferableFlashcards implements Transferable {

	protected static DataFlavor flashcardsFlavor;

	static {
		try {
			flashcardsFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + FlashCard[].class.getName()
					+ "\"");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static DataFlavor[] supportedFlavors = { flashcardsFlavor };
	private FlashCard[] cards;

	public TransferableFlashcards(FlashCard[] cards) {
		this.cards = cards;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(flashcardsFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return cards;
	}

}
