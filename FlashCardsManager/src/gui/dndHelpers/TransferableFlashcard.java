package gui.dndHelpers;
import java.awt.datatransfer.*;
import java.io.IOException;

import core.FlashCard;

public class TransferableFlashcard implements Transferable {

	protected static DataFlavor flashcardFlavor = new DataFlavor(FlashCard.class, "FlashCard");
	protected static DataFlavor[] supportedFlavors = { flashcardFlavor };
	private FlashCard card;

	public TransferableFlashcard(FlashCard card) {
		this.card = card;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(flashcardFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return card;
	}

}
