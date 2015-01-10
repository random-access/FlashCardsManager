package gui.editFlashcardsRefactoring;

import exc.CustomErrorHandling;
import exc.CustomInfoHandling;
import gui.AddFlashcardDialog;
import gui.ChooseTargetProjectDialog;
import gui.MainWindow;
import gui.OkOrDisposeDialog;
import gui.ProjectPanel;
import gui.helpers.MyMenuItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import core.FlashCard;
import core.LearningProject;

@SuppressWarnings("serial")
public class EditFlashcardsDialog extends JDialog {

	private static BufferedImage imgSettings, imgPlus, imgFlashcardInfo, imgEdit, imgDelete;
	{
		try {
			imgSettings = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgSettings_28x28.png"));
			imgPlus = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgPlus_16x16.png"));
			imgEdit = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgEdit_16x16.png"));
			imgDelete = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgDelete_16x16.png"));
			imgFlashcardInfo = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
					"img/AddFlashcardInfo_450x338.png"));
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(null, ioe);
		}
	}

	private ArrayList<TableData> cardData;
	private ArrayList<Label> labelData;
	private String[] columnNames = { "Auswahl", "ID", "Frage", "Stapel" };
	private TableRowSorter<TableModel> rowSorter;

	// private JButton btnPrintContent;
	private JSplitPane spCenter;
	private JTable tblCards;
	private JTree trProjects;
	private JScrollPane scpCards, scpProjects;
	private JPanel pnlBottom, pnlControls;
	private JButton btnAddCard, btnEdit, btnDelete;
	private JButton btnClose;
	private JMenuBar mnuBar;
	private JMenu mnuSettings;
	private MyMenuItem mnuSettingsNewCard;
	private MyMenuItem mnuSettingsTransferCards;
	private MyMenuItem mnuSettingsDeleteCards;

	private MainWindow owner;
	private LearningProject project;
	private ProjectPanel projPnl;
	private ArrayList<FlashCard> cards;

	public EditFlashcardsDialog(ProjectPanel projPnl, ArrayList<FlashCard> cards, LearningProject project) throws SQLException {
		// super(projPnl.getOwner(), true);
		this.owner = projPnl.getOwner();
		this.project = project;
		this.projPnl = projPnl;
		System.out.println("***********************************" + project.getStatus());
		this.cards = cards;
		setTitle(project.getTitle() + " - Lernkarten bearbeiten (TESTING)");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		createWidgets();
		addWidgets();
		setListeners();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void createWidgets() {
		constructTable();
		pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlControls.setBorder(BorderFactory.createLineBorder(getContentPane().getBackground(), 10));
		pnlControls.setOpaque(true);
		pnlControls.setBackground(Color.DARK_GRAY);

		pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));

		mnuBar = new JMenuBar();
		mnuSettings = new JMenu("");
		mnuSettings.setToolTipText("Einstellungen..");
		mnuSettings.setIcon(new ImageIcon(imgSettings));
		mnuSettings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		mnuSettingsNewCard = new MyMenuItem("Neue Lernkarte hinzuf\u00fcgen..");
		mnuSettingsTransferCards = new MyMenuItem("Ausgew\u00e4hlte Lernkarten verschieben..");
		mnuSettingsDeleteCards = new MyMenuItem("Ausgew\u00e4hlte Lernkarten l\u00f6schen");

		btnAddCard = new JButton(new ImageIcon(imgPlus));
		btnAddCard.setToolTipText("Neue Lernkarte hinzuf\u00fcgen");
		btnEdit = new JButton(new ImageIcon(imgEdit));
		btnEdit.setToolTipText("Lernkarte bearbeiten");
		btnDelete = new JButton(new ImageIcon(imgDelete));
		btnDelete.setToolTipText("Ausgewaehlte Lernkarten loeschen"); // TODO Umlaute
		// btnPrintContent = new JButton("Ausgabe");
		btnClose = new JButton("Schlie\u00dfen");

		scpCards = new JScrollPane(tblCards);
		scpCards.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		constructProjectTree();
		scpProjects = new JScrollPane(trProjects);
		scpProjects.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredSoftBevelBorder(),
				BorderFactory.createLineBorder(Color.WHITE, 5)));
		spCenter = new JSplitPane();
		spCenter.setBackground(Color.WHITE);
	}

	private void addWidgets() {
		this.add(pnlControls, BorderLayout.NORTH);
		add(spCenter, BorderLayout.CENTER);
		add(pnlBottom, BorderLayout.SOUTH);
		
		pnlControls.add(btnAddCard);
		pnlControls.add(Box.createHorizontalStrut(4));
		pnlControls.add(btnEdit);
		pnlControls.add(Box.createHorizontalStrut(4));
		pnlControls.add(btnDelete);
		pnlControls.add(Box.createHorizontalStrut(4));
		pnlControls.add(mnuBar);
		pnlControls.add(Box.createHorizontalStrut(2));

		mnuBar.add(mnuSettings);
		mnuSettings.add(mnuSettingsNewCard);
		mnuSettings.add(mnuSettingsTransferCards);
		mnuSettings.add(mnuSettingsDeleteCards);

		spCenter.add(scpProjects, JSplitPane.LEFT, 0);
		spCenter.add(scpCards, JSplitPane.RIGHT, 1);

		// pnlBottom.add(btnPrintContent);
		pnlBottom.add(btnClose);
	}

	private void setListeners() {

		// btnEdit.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// try {
		// AddFlashcardDialog d = new AddFlashcardDialog(JTableTestFrame.this,
		// project, projectPnl, getSelectedCard());
		// d.setVisible(true);
		// } catch (IOException ioe) {
		// CustomErrorHandling.showInternalError(JTableTestFrame.this, ioe);
		// } catch (SQLException sqle) {
		// CustomErrorHandling.showDatabaseError(JTableTestFrame.this, sqle);
		// }
		// }
		// });

		// close window when clicking on close button in bottom panel
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditFlashcardsDialog.this.dispose();
			}
		});

		btnAddCard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AddFlashcardDialog d = new AddFlashcardDialog(null, EditFlashcardsDialog.this, project, projPnl);
					d.setVisible(true);
				} catch (IOException ioe) {
					CustomErrorHandling.showInternalError(EditFlashcardsDialog.this, ioe);
				}
			}
		});

		mnuSettingsNewCard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AddFlashcardDialog d = new AddFlashcardDialog(null, EditFlashcardsDialog.this, project, projPnl);
					d.setVisible(true);
				} catch (IOException ioe) {
					CustomErrorHandling.showInternalError(EditFlashcardsDialog.this, ioe);
				}
			}
		});

		mnuSettingsTransferCards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<FlashCard> transferCards = getSelectedCards();
				if (transferCards.size() == 0) {
					CustomInfoHandling.showNoCardsSelectedInfo();
				} else {
					ChooseTargetProjectDialog d = new ChooseTargetProjectDialog(owner.getProjectsController(), owner,
							EditFlashcardsDialog.this, project, getSelectedCards());
					d.setVisible(true);
				}
			}
		});

		// TODO delete selected cards by selecting delete option in menu
		mnuSettingsDeleteCards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int count = getSelectedCards().size();
				if (count == 0) {
					CustomInfoHandling.showNoCardsSelectedInfo();
				} else {
					OkOrDisposeDialog d = new OkOrDisposeDialog(owner, 300, 150);
					d.setText("<html>M\u00f6chtest Du wirklich " + count + " Karten l\u00f6schen?</html>");
					d.setTitle("Wirklich l\u00f6schen?");
					d.addOkAction(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							d.dispose();
							try {
								for (int i = cardData.size() - 1; i >= 0; --i) {
									if (cardData.get(i).isSelected()) {
										((MyTableModel) tblCards.getModel()).removeCard(i);
									}
								}
								CustomInfoHandling.showSuccessfullyDeletedInfo();
							} catch (SQLException sqle) {
								CustomErrorHandling.showDatabaseError(EditFlashcardsDialog.this, sqle);
							} catch (IOException ioe) {
								CustomErrorHandling.showInternalError(EditFlashcardsDialog.this, ioe);
							}
						}
					});
					d.setVisible(true);
				}
			}
		});

		// btnPrintContent.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// int guiRow = tblCards.getSelectedRow();
		// System.out.print("selected gui row: " + guiRow + ", ");
		// int modelRow = rowSorter.convertRowIndexToModel(guiRow);
		// System.out.println("model row: " + modelRow);
		// }
		// });

		// TODO: open flashcard by double clicking on table row
		tblCards.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int rowAtPoint = target.rowAtPoint(e.getPoint());
					System.out.print("clicked on gui row " + rowAtPoint + ", ");
					int convertedRowAtPoint = rowSorter.convertRowIndexToModel(rowAtPoint);
					System.out.println("model row " + convertedRowAtPoint);
				}
			}
		});

		// select all checkboxes by double-clicking on table header
		tblCards.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && cardData.size() > 0 && tblCards.columnAtPoint(e.getPoint()) == 0) {
					boolean b = cardData.get(0).isSelected();
					for (int i = 0; i < cardData.size(); i++) {
						cardData.get(i).setSelected(!b);
						((MyTableModel) tblCards.getModel()).updateRow(i);
					}
				}
			}
		});
	}

	public void updateCards() {
		cardData.clear();
		for (int i = 0; i < cards.size(); i++) {
			cardData.add(new TableData(cards.get(i)));
		}
		((MyTableModel) tblCards.getModel()).fireTableDataChanged();
		// this.revalidate();
	}

	private ArrayList<FlashCard> getSelectedCards() {
		ArrayList<FlashCard> selectedCards = new ArrayList<FlashCard>();
		for (int i = cardData.size() - 1; i >= 0; --i) {
			if (cardData.get(i).isSelected()) {
				selectedCards.add(cardData.get(i).getCard());
			}
		}
		return selectedCards;
	}

	private void constructProjectTree() {
		DefaultMutableTreeNode topNode = new DefaultMutableTreeNode("Test-Projekt");
		createTreeNodes(topNode);
		trProjects = new JTree(topNode);
		trProjects.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		trProjects.setRowHeight(25);
		trProjects.setCellRenderer(new MyTreeCellRenderer(project));
	}

	private void createTreeNodes(DefaultMutableTreeNode topNode) {
		DefaultMutableTreeNode label = null;
		labelData = createTestingLabelsList();
		for (int i = 0; i < labelData.size(); i++) {
			label = new DefaultMutableTreeNode(labelData.get(i));
			topNode.add(label);
		}
	}

	private void constructTable() {
		cardData = createFlashcardList();
		MyTableModel model = new MyTableModel(cardData, columnNames);
		tblCards = new JTable(model);
		setCustomWidthAndHeight();
		setCustomAlignment();
		// table.setCellSelectionEnabled(false);
		tblCards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		enableRowSorting(model);
		addComboBoxToTable();
	}

	private ArrayList<Label> createTestingLabelsList() {
		ArrayList<Label> list = new ArrayList<Label>();
		for (int i = 0; i < 8; i++) {
			Label l = new Label(i + 1, "Label " + (i + 1));
			list.add(l);
		}
		return list;
	}

	private ArrayList<TableData> createFlashcardList() {
		ArrayList<TableData> list = new ArrayList<TableData>();
		for (int i = 0; i < cards.size(); i++) {
			list.add(new TableData(cards.get(i)));
		}
		return list;
	}

	private void setCustomWidthAndHeight() {
		tblCards.getColumnModel().getColumn(0).setMaxWidth(tblCards.getColumnModel().getColumn(0).getPreferredWidth());
		tblCards.getColumnModel().getColumn(1).setMaxWidth(tblCards.getColumnModel().getColumn(1).getPreferredWidth());
		tblCards.getColumnModel().getColumn(2).setPreferredWidth(350);
		tblCards.getColumnModel().getColumn(3).setMaxWidth(tblCards.getColumnModel().getColumn(3).getPreferredWidth());
		tblCards.setRowHeight(25);
	}

	private void enableRowSorting(TableModel model) {
		rowSorter = new TableRowSorter<TableModel>(model);
		tblCards.setRowSorter(rowSorter);
	}

	private void setCustomAlignment() {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		tblCards.getColumnModel().getColumn(1).setCellRenderer(renderer);
		tblCards.getColumnModel().getColumn(3).setCellRenderer(renderer);
	}

	private void addComboBoxToTable() {
		JComboBox<Integer> cmbStack = new JComboBox<Integer>();
		for (int i = 0; i < 3; i++) {
			cmbStack.addItem(i + 1);
		}
		tblCards.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbStack));
	}

}
