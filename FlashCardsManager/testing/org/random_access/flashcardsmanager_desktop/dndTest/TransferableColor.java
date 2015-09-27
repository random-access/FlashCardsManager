package org.random_access.flashcardsmanager_desktop.dndTest;

import java.awt.Color;
import java.awt.datatransfer.*;

public class TransferableColor implements Transferable {

    protected static DataFlavor colorFlavor = new DataFlavor(Color.class, "A Color Object");

    protected static DataFlavor[] supportedFlavors = { colorFlavor, DataFlavor.stringFlavor, };

    Color color;

    public TransferableColor(Color color) {
        this.color = color;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(colorFlavor) || flavor.equals(DataFlavor.stringFlavor))
            return true;
        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(colorFlavor))
            return color;
        else if (flavor.equals(DataFlavor.stringFlavor))
            return color.toString();
        else
            throw new UnsupportedFlavorException(flavor);
    }
}
