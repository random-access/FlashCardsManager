package gui.helpers;

import java.awt.Component;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;

import core.FlashCard;
import core.LearningProject;
import exc.CustomErrorHandling;
import gui.ProjectPanel;
import gui.ProjectPanel.DialogType;

@SuppressWarnings("serial")
public class FlashcardTableModel extends AbstractTableModel {

	private ArrayList<FlashcardTableData> data;
	private String[] columnNames;

	public FlashcardTableModel(ArrayList<FlashCard> cards, String[] columnNames) {
		this.data = createFlashcardList(cards);
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
				try {
                    return data.get(rowIndex).getCard().getNumberInProj();
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(null, sqle);
                    return -1;
                }
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
	
	public FlashCard getCard(int row) {
		return data.get(row).getCard();
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
		    FlashCard c = data.get(row).getCard();
			c.setStack((int) value);
			try {
                c.update();
            } catch (SQLException sqle) {
                CustomErrorHandling.showDatabaseError(null, sqle);
            } catch (IOException ioe) {
                CustomErrorHandling.showInternalError(null, ioe);
            }
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
	
	public boolean someCardSelected() {
		for (FlashcardTableData d : data) {
			if (d.isSelected()) {
				return true;
			}
		}
		return false;
	}
	
	public void recreateTable(LearningProject p, Component owner) throws SQLException {
        data.clear();
        p.loadFlashcards(null);
        data = createFlashcardList(p.getAllCards());
        fireTableDataChanged();
    }
	
    private ArrayList<FlashcardTableData> createFlashcardList(ArrayList<FlashCard> cards) {
        ArrayList<FlashcardTableData> list = new ArrayList<FlashcardTableData>();
        for (int i = 0; i < cards.size(); i++) {
            list.add(new FlashcardTableData(cards.get(i)));
        }
        return list;
    }
}
