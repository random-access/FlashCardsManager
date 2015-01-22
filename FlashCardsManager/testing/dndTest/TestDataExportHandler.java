package dndTest;

import java.awt.datatransfer.Transferable;

import javax.swing.*;

import jtabletest.MyTableTestModel;
import jtabletest.TableTestData;

@SuppressWarnings("serial")
public class TestDataExportHandler extends TransferHandler {

	@Override
	public int getSourceActions(JComponent component) {
		return COPY;
	}

	public Transferable createTransferable(JComponent c) {
		JTable table = (JTable) c;
		TableTestData[] data = new TableTestData[table.getSelectedRowCount()];
		for (int i = 0; i < table.getSelectedRowCount(); i++) {
			data[i] = ((MyTableTestModel) table.getModel()).getRowAt(table.getSelectedRows()[i]);
		}
		return new TransferableTestdata(data);
	}

	public void exportDone(JComponent c, Transferable t, int action) {

	}
}
