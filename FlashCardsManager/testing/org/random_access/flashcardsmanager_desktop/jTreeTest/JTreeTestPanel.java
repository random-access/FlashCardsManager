package org.random_access.flashcardsmanager_desktop.jTreeTest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.tree.*;

import org.random_access.flashcardsmanager_desktop.core.IHasStatus;
import org.random_access.flashcardsmanager_desktop.core.Status;
import org.random_access.flashcardsmanager_desktop.dndTest.TestdataDropTargetListener;
import org.random_access.flashcardsmanager_desktop.tests.JComponentTestFrame;

public class JTreeTestPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private TestLearningProject proj;
	private JTree tree;
	private TreeModel model;
	private ArrayList<TestLabel> labels;
	private JButton btnAddLabel, btnRemoveLabel;
	private JPanel pnlControls;

	public JTreeTestPanel() {
		labels = createTestingLabelsList();
		proj = new TestLearningProject(1, "TestProjekt", Status.YELLOW);
		DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(proj);
		createTreeNodes(topNode);
		model = new MyTreeModel(topNode);
		tree = new JTree(model);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(25);
		tree.setCellRenderer(new MyTreeCellRenderer());
		tree.setDropMode(DropMode.ON);
		this.setLayout(new BorderLayout());
		this.add(tree, BorderLayout.CENTER);

		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnAddLabel = new JButton("Label hinzufuegen");
		btnRemoveLabel = new JButton("Label entfernen");
		setListeners();
		this.add(pnlControls, BorderLayout.NORTH);
		pnlControls.add(btnAddLabel);
		pnlControls.add(btnRemoveLabel);
	}

	private void setListeners() {
		new TestdataDropTargetListener(tree);
	}

	private void createTreeNodes(DefaultMutableTreeNode topNode) {
		DefaultMutableTreeNode label = null;
		for (int i = 0; i < labels.size(); i++) {
			label = new DefaultMutableTreeNode(labels.get(i));
			topNode.add(label);
		}
	}

	private ArrayList<TestLabel> createTestingLabelsList() {
		ArrayList<TestLabel> list = new ArrayList<TestLabel>();
		for (int i = 0; i < 8; i++) {
			Status s = null;
			switch (i % 3) {
			case 0:
				s = Status.RED;
				break;
			case 1:
				s = Status.YELLOW;
				break;
			case 2:
				s = Status.GREEN;
				break;
			}
			TestLabel l = new TestLabel(i + 1, "Label " + (i + 1) + " (SAMPLE)", s);
			list.add(l);
		}
		return list;
	}

	public static void main(String[] args) {
		TestLabel l = new TestLabel(1, "Test", Status.RED);
		System.out.println(l instanceof IHasStatus);
		new JComponentTestFrame(new JTreeTestPanel());
	}

}
