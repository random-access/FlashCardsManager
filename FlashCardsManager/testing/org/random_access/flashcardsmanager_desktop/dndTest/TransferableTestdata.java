package org.random_access.flashcardsmanager_desktop.dndTest;

import java.awt.datatransfer.*;
import java.io.IOException;

import org.random_access.flashcardsmanager_desktop.jtabletest.TableTestData;

public class TransferableTestdata implements Transferable {

	protected static DataFlavor testFlavor;

	static {
		try {
			testFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + TableTestData[].class.getName()
					+ "\"");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static DataFlavor[] supportedFlavors = { testFlavor };
	private TableTestData[] data;

	public TransferableTestdata(TableTestData[] data) {
		this.data = data;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(testFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(testFlavor))
			return data;
		else
			throw new UnsupportedFlavorException(flavor);
	}
}
