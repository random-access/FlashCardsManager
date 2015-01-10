package gui.editFlashcardsRefactoring;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import core.FlashCard;

@SuppressWarnings("serial")
public class MyTableModel extends AbstractTableModel {

	private ArrayList<TableData> data;
	private String[] columnNames;

	public MyTableModel(ArrayList<TableData> data, String[] columnNames) {
		this.data = data;
		this.columnNames = columnNames;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (data.size() > 0) {
			switch (columnIndex) {
			case 0:
				return data.get(rowIndex).isSelected();
			case 1:
				return data.get(rowIndex).getCard().getId();
			case 2:
				return data.get(rowIndex).getCard().getQuestionInPlainText();
			case 3:
				return data.get(rowIndex).getCard().getStack();
			default:
				return 0;
			}
		}
		return 0;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return data.size() > 0 ? getValueAt(0, column).getClass() : Object.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0 || column == 3;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if (value instanceof Boolean && column == 0) {
			data.get(row).setSelected((boolean) value);
			fireTableCellUpdated(row, column);
		}
		if (value instanceof Integer && column == 3) {
			data.get(row).getCard().setStack((int) value);
			fireTableCellUpdated(row, column);
		}
	}

	public void removeCard(int row) throws SQLException, IOException {
		data.get(row).getCard().delete();
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}
	
	public void updateSelection() {
		
	}
	
	public boolean someCardSelected() {
		for (TableData d : data) {
			if (d.isSelected()) {
				return true;
			}
		}
		return false;
	}
}
