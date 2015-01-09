package jtabletest;

import importExport.XMLFlashCard;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class MyTableModel extends AbstractTableModel {
	
	private ArrayList<XMLFlashCard> data;
	private ArrayList<Boolean> checkBoxes;
	private String[] columnNames;

	public MyTableModel(ArrayList<XMLFlashCard> data, ArrayList<Boolean> checkBoxes, String[] columnNames) {
		this.data = data;
		this.columnNames = columnNames;
		this.checkBoxes = checkBoxes;
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
		switch (columnIndex) {
		case 0:
			return checkBoxes.get(rowIndex);
		case 1:
			return data.get(rowIndex).getId();
		case 2:
			return data.get(rowIndex).getQuestion();
		case 3:
			return data.get(rowIndex).getStack();
		default:
			return 0;
		}
	}

	@Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }


	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0 || column == 3;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue instanceof Boolean && column == 0) {
			checkBoxes.set(row, (Boolean) aValue);
			fireTableCellUpdated(row, column);
		}
		if (aValue instanceof Integer && column == 3) {
			data.get(row).setStack((int) aValue);
			fireTableCellUpdated(row, column);
		}
	}
}
