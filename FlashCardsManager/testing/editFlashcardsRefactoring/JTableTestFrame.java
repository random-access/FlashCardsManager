package editFlashcardsRefactoring;

import importExport.XMLFlashCard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

import jtabletest.TableData;

@SuppressWarnings("serial")
public class JTableTestFrame extends JFrame {
	private ArrayList<TableData> data;
	private String[] columnNames = { "Auswahl", "ID", "Frage", "Stapel" };
	private TableRowSorter<TableModel> rowSorter;
	
	private JButton btnDelete, btnPrintContent;
	private JTable table;
	private JScrollPane scp;
	private JPanel pnlControls;

	public JTableTestFrame() {
		setTitle("JTable Testklasse");
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
			    for (int i = data.size()-1; i >= 0; --i) {
                    if (data.get(i).isSelected()) {
                        ((MyTableModel) table.getModel()).removeRow(i);
                    }
                }
			}
		});
		
		btnPrintContent.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// int row = table.getModel().get
				int guiRow = table.getSelectedRow();
				System.out.print ("selected gui row: " + guiRow + ", ");
				int modelRow = rowSorter.convertRowIndexToModel(guiRow);
				System.out.println("model row: " + modelRow);
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
		    
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        if (e.getClickCount() == 2) {
		            JTable target = (JTable)e.getSource();
		            int rowAtPoint    = target.rowAtPoint( e.getPoint() );
		            System.out.print("clicked on gui row " + rowAtPoint + ", ");
		            int convertedRowAtPoint = rowSorter.convertRowIndexToModel( rowAtPoint );
		            System.out.println("model row " + convertedRowAtPoint);
		        }
		    }
        });
		
		table.getTableHeader().addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.columnAtPoint(e.getPoint()) == 0) {
                    boolean b = data.get(0).isSelected();
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setSelected(!b);
                        ((MyTableModel) table.getModel()).updateRow(i);
                    }
                }
		    }
		});
	}

	private void constructTable() {
		data = createTestingFlashcardList();
		MyTableModel model = new MyTableModel(data, columnNames);
		table = new JTable(model);
		setCustomWidthAndHeight();
		setCustomAlignment();
		// table.setCellSelectionEnabled(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		enableRowSorting(model);
		addComboBoxToTable();
	}

	private ArrayList<TableData> createTestingFlashcardList() {
		ArrayList<TableData> list = new ArrayList<TableData>();
		for (int i = 0; i < 8; i++) {
			XMLFlashCard f = new XMLFlashCard();
			if (i % 2 == 0) {
				f.setId(i + 1);
			} else {
				f.setId(i - 1);
			}
			if (i % 2 == 0) {
				f.setQuestion("Frage " + (i + 1));
			} else {
				f.setQuestion("Das ist Frage No. " + (i + 1));
					//	+ ", diese Frage ist etwas laenger. Damit kann ich testen wie die Tabelle sich verhaelt.");
			}
			f.setStack(i % 3);
			list.add(new TableData(f));
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
		rowSorter = new TableRowSorter<TableModel>(model);
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
