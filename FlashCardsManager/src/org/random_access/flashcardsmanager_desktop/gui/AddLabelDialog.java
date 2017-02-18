package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.tree.*;

import org.random_access.flashcardsmanager_desktop.core.Label;
import org.random_access.flashcardsmanager_desktop.core.LearningProject;
import org.random_access.flashcardsmanager_desktop.exc.*;
import org.random_access.flashcardsmanager_desktop.gui.helpers.CustomColor;
import org.random_access.flashcardsmanager_desktop.gui.helpers.DefaultMutableTreeNodeSorter;
import org.random_access.flashcardsmanager_desktop.gui.helpers.TransparencyTextField;
import org.random_access.flashcardsmanager_desktop.utils.InvalidCharsFilter;
import org.random_access.flashcardsmanager_desktop.utils.SizeFilterExtended;

@SuppressWarnings("serial")
public class AddLabelDialog extends JDialog {

	private JPanel pnlCenter, pnlBtns;
	private JLabel lblTitle;
	private JTextField txtTitle;
	private JButton btnOk, btnDiscard;

	private LearningProject project;
	private JTree tree;

	AddLabelDialog(JFrame owner, JTree tree, LearningProject project) {
		super(owner, true);
		this.project = project;
		this.tree = tree;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Label hinzuf\u00fcgen..");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
			CustomErrorHandling.showInternalError(null, exc);
		}

		createWidgets();
		addWidgets();
		setListeners();

		pack();
		setLocationRelativeTo(owner);
	}

	private void createWidgets() {
		pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		lblTitle = new JLabel("Name:");
		txtTitle = new TransparencyTextField(15);
		txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument) txtTitle.getDocument()).setDocumentFilter(new SizeFilterExtended(new InvalidCharsFilter(this), 50));
		btnOk = new JButton("  OK  ");
		btnDiscard = new JButton(" Abbrechen ");

	}

	private void addWidgets() {
		getContentPane().add(pnlCenter, BorderLayout.NORTH);
		getContentPane().add(pnlBtns, BorderLayout.SOUTH);
		pnlCenter.add(lblTitle);
		pnlCenter.add(Box.createHorizontalStrut(10));
		pnlCenter.add(txtTitle);
		pnlBtns.add(btnDiscard);
		pnlBtns.add(Box.createHorizontalStrut(10));
		pnlBtns.add(btnOk);
	}

	private void setListeners() {
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					if (txtTitle.getText().equals("")) {
						txtTitle.setBackground(CustomColor.BACKGROUND_ERROR_RED);
						CustomInfoHandling.showNoInputInfo(AddLabelDialog.this);
					} else {
						String title = txtTitle.getText();
						Label l = new Label(title, project);
						l.store();
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						model.insertNodeInto(new DefaultMutableTreeNode(l), (MutableTreeNode) model.getRoot(),
								model.getChildCount(model.getRoot()));
						tree.expandPath(tree.getPathForRow(0));
						DefaultMutableTreeNodeSorter.sortTreeNodes((MutableTreeNode) model.getRoot());
						model.nodeStructureChanged((MutableTreeNode) model.getRoot());
						AddLabelDialog.this.dispose();
					}
				} catch (SQLException sqle) {
					CustomErrorHandling.showDatabaseError(AddLabelDialog.this, sqle);
				} catch (InvalidLengthException ile) {
					CustomInfoHandling.showInvalidLengthInfo(AddLabelDialog.this, 150);
					// TODO global max
					txtTitle.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				}
			}
		});

		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddLabelDialog.this.dispose();
			}
		});

	}

}
