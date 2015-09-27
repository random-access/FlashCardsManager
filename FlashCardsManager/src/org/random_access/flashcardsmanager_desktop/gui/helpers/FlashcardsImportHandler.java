package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JTree;
import javax.swing.TransferHandler;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;

@SuppressWarnings("serial")
public class FlashcardsImportHandler extends TransferHandler {

	public boolean canImport(TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		if (!support.isDataFlavorSupported(new DataFlavor(FlashCard.class, "FlashCard"))) {
			System.out.println("wrong data");
			return false;
		}
		boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
		if (copySupported) {
			support.setDropAction(COPY);
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int i = dl.getChildIndex();

		Transferable t = support.getTransferable();
		System.out.println("drop of " + t.getClass() + " to " + i);
		return true;
	}
}
