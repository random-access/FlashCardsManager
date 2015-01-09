package tests;

import importExport.XMLFlashCard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import jtabletest.MyTableModel;

@SuppressWarnings("serial")
public class JTableTestFrame extends JFrame {
	private ArrayList<XMLFlashCard> data;
	private ArrayList<Boolean> checkBoxes;
	private String[] columnNames = { "Auswahl", "ID", "Frage", "Stapel" };
	
	private JButton btnDelete, btnPrintContent;
	private JTable table;
	private JScrollPane scp;
	private JPanel pnlControls;

	public JTableTestFrame() {
		setTitle("JComponent Testklasse");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		constructTable();
		scp = new JScrollPane(table);
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnDelete = new JButton("Auswahl loeschen");
		btnPrintContent = new JButton("Daten zeigen");
		add(scp, BorderLayout.CENTER);
		add(pnlControls, BorderLayout.SOUTH);
		pnlControls.add(btnDelete);
		pnlControls.add(btnPrintContent);
		setListeners();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void setListeners() {
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < checkBoxes.size(); i++) {
					if (checkBoxes.get(i)) {
						// table.getModel().get
						data.remove(i);
						checkBoxes.remove(i);
						
					}
				}
				revalidate();
			}
		});
	}

	private void constructTable() {
		data = createTestingFlashcardList();
		checkBoxes = createCheckboxes(data.size());
		TableModel model = new MyTableModel(data, checkBoxes, columnNames);
		table = new JTable(model);
		setCustomWidthAndHeight();
		setCustomAlignment();
		enableRowSorting(model);
		addComboBoxToTable();
	}
	
	private ArrayList<Boolean> createCheckboxes(int size) {
		ArrayList<Boolean> boxes = new ArrayList<Boolean>();
		for (int i = 0; i < size; i++) {
			boxes.add(new Boolean(false));
		}
		return boxes;
	}

	private ArrayList<XMLFlashCard> createTestingFlashcardList() {
		ArrayList<XMLFlashCard> list = new ArrayList<XMLFlashCard>();
		for (int i = 0; i < 30; i++) {
			XMLFlashCard f = new XMLFlashCard();
			if (i % 2 == 0) {
				f.setId(i + 1);
			} else {
				f.setId(i - 1);
			}
			if (i % 2 == 0) {
				f.setQuestion("Frage " + (i + 1));
			} else {
				f.setQuestion("Das ist Frage No. " + (i + 1)
						+ ", diese Frage ist etwas laenger. Damit kann ich testen wie die Tabelle sich verhaelt.");
			}
			f.setStack(i % 3);
			list.add(f);
		}
		return list;
	}

	private void setCustomWidthAndHeight() {
		table.getColumnModel().getColumn(0).setMaxWidth(table.getColumnModel().getColumn(0).getPreferredWidth());
		table.getColumnModel().getColumn(1).setMaxWidth(table.getColumnModel().getColumn(1).getPreferredWidth());
		table.getColumnModel().getColumn(2).setPreferredWidth(350);
		table.getColumnModel().getColumn(3).setMaxWidth(table.getColumnModel().getColumn(3).getPreferredWidth());
		table.setRowHeight(25);
	}

	private void enableRowSorting(TableModel model) {
		final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(rowSorter);
	}

	private void setCustomAlignment() {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(3).setCellRenderer(renderer);
	}

	private void addComboBoxToTable() {
		JComboBox<Integer> cmbStack = new JComboBox<Integer>();
		for (int i = 0; i < 3; i++) {
			cmbStack.addItem(i + 1);
		}
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbStack));

	}

	public static void main(String[] args) {

		new JTableTestFrame();
	}

}
