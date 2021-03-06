package org.random_access.flashcardsmanager_desktop.jtabletest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.dndTest.TestDataExportHandler;

@SuppressWarnings("serial")
public class JTableTestPanel extends JPanel {
	// implements DragGestureListener
	private String[] columnNames = { "ID", "Name" };
	private ArrayList<TableTestData> data;
	private JTable table;

	private JPanel pnlControls;
	private JButton btnDelete;
	private JScrollPane scp;

	public JTableTestPanel() {
		setLayout(new BorderLayout());
		createTestData(5);
		constructTable();

		new DragSource();
		// ds.createDefaultDragGestureRecognizer(table,
		// DnDConstants.ACTION_COPY, this);

		createGUI();
		setListeners();
	}

	private void createGUI() {
		scp = new JScrollPane(table);
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnDelete = new JButton("delete");
		add(scp, BorderLayout.CENTER);
		add(pnlControls, BorderLayout.SOUTH);
		pnlControls.add(btnDelete);
	}

	private void createTestData(int length) {
		data = new ArrayList<TableTestData>();
		for (int i = 1; i <= length; i++) {
			data.add(new TableTestData(i, "Testdaten " + i));
		}
	}

	private void constructTable() {
		MyTableTestModel model = new MyTableTestModel(data, columnNames);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setTransferHandler(new TestDataExportHandler());
		table.setDragEnabled(true);
	}

	private void setListeners() {
		btnDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				while (table.getSelectedRow() != -1) {
					((MyTableTestModel) table.getModel()).removeRow(table.getSelectedRow());
				}
			}
		});
	}

	// @Override
	// public void dragGestureRecognized(DragGestureEvent dge) {
	// Cursor cursor = null;
	// JTable table = (JTable) dge.getComponent();
	//
	// TableTestData data = ((MyTableTestModel)
	// table.getModel()).getRowAt(table.getSelectedRow());
	//
	// if (dge.getDragAction() == DnDConstants.ACTION_COPY) {
	// cursor = DragSource.DefaultCopyDrop;
	// }
	//
	// dge.startDrag(cursor, new TransferableTestdata(data));
	// }

}
