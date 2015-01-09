package editFlashcardsRefactoring;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import jtabletest.TableData;

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
		switch (columnIndex) {
		case 0:
			return data.get(rowIndex).isSelected();
		case 1:
			return data.get(rowIndex).getCard().getId();
		case 2:
			return data.get(rowIndex).getCard().getQuestion();
		case 3:
			return data.get(rowIndex).getCard().getStack();
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

	public void removeRow(int row) {
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}

    public void updateRow(int row) {
        fireTableRowsUpdated(row, row); 
    }
}
