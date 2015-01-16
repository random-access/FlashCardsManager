package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import core.*;
import core.Label;
import events.ProjectDataChangedListener;
import exc.CustomErrorHandling;
import exc.CustomInfoHandling;
import gui.helpers.*;

@SuppressWarnings("serial")
public class FlashcardOverviewDialog extends JDialog implements ProjectDataChangedListener {

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

    private ArrayList<Label> labelData;
    private String[] columnNames = { "Auswahl", "ID", "Frage", "Stapel" };
    private TableRowSorter<FlashcardTableModel> rowSorter;

    // private JButton btnPrintContent;
    private JSplitPane spCenter;
    private JTable tblCards;
    private JTree trProjects;
    private JScrollPane scpCards, scpProjects;
    private JLabel lblEmptyProject;
    private JPanel pnlBottom, pnlControls;
    private JButton btnAddCard, btnEdit, btnDelete;
    private JButton btnClose;
    private JMenuBar mnuBar;
    private JMenu mnuSettings;
    private MyMenuItem mnuSettingsNewCard, mnuSettingsNewLabel, mnuSettingsAddLabelToCards, mnuSettingsRemoveLabelFromCards,
            mnuSettingsDeleteLabel, mnuSettingsTransferCards, mnuSettingsDeleteCards;

    private MainWindow owner;
    private LearningProject project;
    private ProjectPanel projPnl;

    public FlashcardOverviewDialog(ProjectPanel projPnl, ArrayList<FlashCard> cards, LearningProject project) throws SQLException {
        // super(projPnl.getOwner(), true);
        this.owner = projPnl.getOwner();
        this.project = project;
        this.projPnl = projPnl;
        setTitle(project.getTitle() + " - Lernkarten bearbeiten (TESTING)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        constructTable(cards);
        createWidgets();
        addWidgets();
        setListeners();
        owner.getProjectsController().addEventListener(this);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createWidgets() throws SQLException {

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
        mnuSettingsNewLabel = new MyMenuItem("Neues Label hinzuf\u00fcgen..");
        mnuSettingsAddLabelToCards = new MyMenuItem("Label f\u00fcr Auswahl setzen..");
        mnuSettingsRemoveLabelFromCards = new MyMenuItem("Label von Auswahl entfernen..");
        mnuSettingsDeleteLabel = new MyMenuItem("Ausgew\u00e4hltes Label l\u00f6schen..");
        mnuSettingsTransferCards = new MyMenuItem("Ausgew\u00e4hlte Lernkarten verschieben..");
        mnuSettingsDeleteCards = new MyMenuItem("Ausgew\u00e4hlte Lernkarten l\u00f6schen");

        lblEmptyProject = new JLabel(new ImageIcon(imgFlashcardInfo));
        btnAddCard = new JButton(new ImageIcon(imgPlus));
        btnAddCard.setToolTipText("Neue Lernkarte hinzuf\u00fcgen");
        btnEdit = new JButton(new ImageIcon(imgEdit));
        btnEdit.setToolTipText("Lernkarte bearbeiten");
        btnEdit.setEnabled(false);
        btnDelete = new JButton(new ImageIcon(imgDelete));
        btnDelete.setToolTipText("Ausgew\u00e4hlte Lernkarten l\u00f6schen");
        btnDelete.setEnabled(false);
        // btnPrintContent = new JButton("Ausgabe");
        btnClose = new JButton("Schlie\u00dfen");
        scpCards = new JScrollPane();
        selectCardSectionContent();
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
        mnuSettings.add(mnuSettingsNewLabel);
        mnuSettings.add(mnuSettingsAddLabelToCards);
        mnuSettings.add(mnuSettingsRemoveLabelFromCards);
        mnuSettings.add(mnuSettingsDeleteLabel);
        mnuSettings.add(mnuSettingsTransferCards);
        mnuSettings.add(mnuSettingsDeleteCards);

        spCenter.add(scpProjects, JSplitPane.LEFT, 0);
        spCenter.add(scpCards, JSplitPane.RIGHT, 1);

        // pnlBottom.add(btnPrintContent);
        pnlBottom.add(btnClose);
    }

    private void setListeners() {

        // close window when clicking on close button in bottom panel
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FlashcardOverviewDialog.this.dispose();
            }
        });

        btnAddCard.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FlashcardEditorDialog d = new FlashcardEditorDialog(null, FlashcardOverviewDialog.this, project, projPnl);
                    d.setVisible(true);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(FlashcardOverviewDialog.this, ioe);
                }
            }
        });

        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FlashcardEditorDialog d = new FlashcardEditorDialog(FlashcardOverviewDialog.this, project, projPnl,
                            getSelectedCards().get(0));
                    d.setVisible(true);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(FlashcardOverviewDialog.this, ioe);
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(FlashcardOverviewDialog.this, sqle);
                }
            }
        });

        mnuSettingsNewCard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FlashcardEditorDialog d = new FlashcardEditorDialog(null, FlashcardOverviewDialog.this, project, projPnl);
                    d.setVisible(true);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(FlashcardOverviewDialog.this, ioe);
                }
            }
        });

        mnuSettingsNewLabel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddLabelDialog d = new AddLabelDialog(owner, trProjects, project);
                d.setVisible(true);
            }
        });

        // TODO: make it user-friendly!!
        mnuSettingsAddLabelToCards.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) trProjects.getLastSelectedPathComponent();
                if (node.getUserObject() instanceof Label) {
                    ArrayList<FlashCard> selection = getSelectedCards();
                    Label selectedLabel = (Label) node.getUserObject();
                    for (int i = 0; i < selection.size(); i++) {
                        try {
                            selection.get(i).addLabel(selectedLabel);
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        mnuSettingsRemoveLabelFromCards.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) trProjects.getLastSelectedPathComponent();
                if (node.getUserObject() instanceof Label) {
                    ArrayList<FlashCard> selection = getSelectedCards();
                    Label selectedLabel = (Label) node.getUserObject();
                    for (int i = 0; i < selection.size(); i++) {
                        try {
                            selection.get(i).removeLabel(selectedLabel);
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        mnuSettingsDeleteLabel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!trProjects.getSelectionModel().isSelectionEmpty()) {
                    DefaultTreeModel model = (DefaultTreeModel) trProjects.getModel();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) trProjects.getLastSelectedPathComponent();
                    if (node.getUserObject() instanceof Label) {
                        Label l = (Label) node.getUserObject();
                        try {
                            l.delete();
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    model.removeNodeFromParent(node);
                }
            }
        });

        mnuSettingsTransferCards.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedCardsCount() == 0) {
                    CustomInfoHandling.showNoCardsSelectedInfo();
                } else {
                    FlashcardTransferDialog d = new FlashcardTransferDialog(owner.getProjectsController(), owner,
                            FlashcardOverviewDialog.this, project, getSelectedCards());
                    d.setVisible(true);
                }
            }
        });

        mnuSettingsDeleteCards.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = getSelectedCardsCount();
                if (count == 0) {
                    CustomInfoHandling.showNoCardsSelectedInfo();
                } else {
                    deleteSelectedCards(count);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int count = getSelectedCardsCount();
                if (count == 0) {
                    CustomInfoHandling.showNoCardsSelectedInfo();
                } else {
                    deleteSelectedCards(count);
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

        tblCards.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rowAtPoint = tblCards.rowAtPoint(e.getPoint());
                    int convertedRowAtPoint = rowSorter.convertRowIndexToModel(rowAtPoint);
                    try {
                        FlashcardEditorDialog d = new FlashcardEditorDialog(FlashcardOverviewDialog.this, project, projPnl,
                                ((FlashcardTableModel) tblCards.getModel()).getCard(convertedRowAtPoint));
                        d.setVisible(true);
                    } catch (IOException ioe) {
                        CustomErrorHandling.showInternalError(FlashcardOverviewDialog.this, ioe);
                    } catch (SQLException sqle) {
                        CustomErrorHandling.showDatabaseError(FlashcardOverviewDialog.this, sqle);
                    }
                }

                manageButtonActivation();
            }
        });

        // select all checkboxes by double-clicking on table header
        tblCards.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                FlashcardTableModel model = ((FlashcardTableModel) tblCards.getModel());
                if (e.getClickCount() == 2 && model.getRowCount() > 0 && tblCards.columnAtPoint(e.getPoint()) == 0) {
                    boolean b = (boolean) model.getValueAt(0, 0);
                    for (int i = 0; i < model.getRowCount(); i++) {
                        model.setValueAt(!b, i, 0);
                    }
                    manageButtonActivation();
                }
            }
        });

        tblCards.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                tblCards.getSelectionModel().clearSelection();
            }
        });

        tblCards.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                System.out.println("in table changed");
                trProjects.repaint();
                try {
                    projPnl.changeStatus(project.getStatus());
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(FlashcardOverviewDialog.this, sqle);
                }
                manageButtonActivation();
                selectCardSectionContent();
            }
        });

        trProjects.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) trProjects.getLastSelectedPathComponent();
                if (node == null)
                    return;
                if (node.getUserObject() instanceof Label) {
                    rowSorter.setRowFilter(new CustomRowFilter((Label) node.getUserObject()));
                } else {
                    rowSorter.setRowFilter(null);
                }
            }
        });

    }

    public void updateView() {
        System.out.println("in update view");
        try {
            ((FlashcardTableModel) tblCards.getModel()).recreateTable(project, this);
        } catch (SQLException sqle) {
            CustomErrorHandling.showDatabaseError(this, sqle);
        }
        trProjects.repaint();
        selectCardSectionContent();
    }

    private void selectCardSectionContent() {
        if (((FlashcardTableModel) tblCards.getModel()).getRowCount() == 0) {
            scpCards.setViewportView(lblEmptyProject);
        } else {
            scpCards.setViewportView(tblCards);
        }
    }

    private void constructProjectTree() throws SQLException {
        DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(project);
        DefaultTreeModel treeModel = new LabelTreeModel(topNode);

        createTreeNodes(topNode);
        trProjects = new JTree(treeModel);
        // trProjects.setDropMode(DropMode.ON_OR_INSERT);
        trProjects.setEditable(true);
        trProjects.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        trProjects.setRowHeight(25);
        trProjects.setCellRenderer(new LabelTreeCellRenderer());
    }

    private void createTreeNodes(DefaultMutableTreeNode topNode) throws SQLException {
        project.loadLabels();
        labelData = project.getLabels();
        DefaultMutableTreeNode label = null;
        for (int i = 0; i < labelData.size(); i++) {
            label = new DefaultMutableTreeNode(labelData.get(i));
            topNode.add(label);
        }
    }

    private void constructTable(ArrayList<FlashCard> cards) {
        FlashcardTableModel model = new FlashcardTableModel(cards, columnNames);
        tblCards = new JTable(model);
        // tblCards.setDragEnabled(true);
        setCustomWidthAndHeight();
        setCustomAlignment();
        setRowSelectionHandling();
        enableRowSorting(model);
        addComboBoxToTable();
    }

    private void setCustomWidthAndHeight() {
        tblCards.getColumnModel().getColumn(0).setMaxWidth(tblCards.getColumnModel().getColumn(0).getPreferredWidth());
        tblCards.getColumnModel().getColumn(1).setMaxWidth(tblCards.getColumnModel().getColumn(1).getPreferredWidth());
        tblCards.getColumnModel().getColumn(2).setPreferredWidth(350);
        tblCards.getColumnModel().getColumn(3).setMaxWidth(tblCards.getColumnModel().getColumn(3).getPreferredWidth());
        tblCards.setRowHeight(25);
    }

    private void enableRowSorting(FlashcardTableModel model) {
        rowSorter = new TableRowSorter<FlashcardTableModel>(model);
        tblCards.setRowSorter(rowSorter);
    }

    private void setCustomAlignment() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCards.getColumnModel().getColumn(1).setCellRenderer(renderer);
        tblCards.getColumnModel().getColumn(3).setCellRenderer(renderer);
        TableCellRenderer rendererFromHeader = tblCards.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    private void addComboBoxToTable() {
        JComboBox<Integer> cmbStack = new JComboBox<Integer>();
        for (int i = 0; i < project.getNumberOfStacks(); i++) {
            cmbStack.addItem(i + 1);
        }
        tblCards.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbStack));
    }

    private void manageButtonActivation() {
        int count = getSelectedCardsCount();
        btnDelete.setEnabled(count >= 1 ? true : false);
        btnEdit.setEnabled(count == 1 ? true : false);
    }

    private ArrayList<FlashCard> getSelectedCards() {
        FlashcardTableModel model = ((FlashcardTableModel) tblCards.getModel());
        ArrayList<FlashCard> selectedCards = new ArrayList<FlashCard>();
        for (int i = model.getRowCount() - 1; i >= 0; --i) {
            if ((boolean) model.getValueAt(i, 0)) {
                selectedCards.add(model.getCard(i));
            }
        }
        return selectedCards;
    }

    private int getSelectedCardsCount() {
        FlashcardTableModel model = ((FlashcardTableModel) tblCards.getModel());
        int count = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((boolean) model.getValueAt(i, 0)) {
                count++;
            }
        }
        return count;
    }

    public void setRowSelectionHandling() {
        tblCards.setRowSelectionAllowed(true);
        tblCards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // listSelectionModel = tblCards.getSelectionModel();
    }

    public void deleteSelectedCards(int count) {
        final OkOrDisposeDialog d = new OkOrDisposeDialog(owner, 300, 150);
        d.setText("<html>M\u00f6chtest Du wirklich " + count + " Karten l\u00f6schen?</html>");
        d.setTitle("Wirklich l\u00f6schen?");
        d.addOkAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d.dispose();
                FlashcardTableModel model = ((FlashcardTableModel) tblCards.getModel());
                try {
                    for (int i = model.getRowCount() - 1; i >= 0; --i) {
                        if ((boolean) model.getValueAt(i, 0)) {
                            model.removeCard(i);
                        }
                    }
                    CustomInfoHandling.showSuccessfullyDeletedInfo();
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(FlashcardOverviewDialog.this, sqle);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(FlashcardOverviewDialog.this, ioe);
                }
            }
        });
        d.setVisible(true);
    }

    @Override
    public void projectDataChanged(EventObject e) {
        updateView();
    }

}
