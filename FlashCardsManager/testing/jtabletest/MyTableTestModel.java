package jtabletest;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class MyTableTestModel extends AbstractTableModel {

    private ArrayList<TableTestData> data;
    private String[] columnNames;

    public MyTableTestModel(ArrayList<TableTestData> data, String[] columnNames) {
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
            return data.get(rowIndex).getId();
        case 1:
            return data.get(rowIndex).getTitle();
        default:
            return null;
        }
    }

    public TableTestData getRowAt(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        switch (column) {
        case 0:
            data.get(row).setId((int) value);
            break;
        case 1:
            data.get(row).setTitle((String) value);
            break;
        }
    }

    public void removeRow(int row) {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
