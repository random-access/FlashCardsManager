package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import utils.InvalidCharsFilter;
import utils.SizeFilterExtended;
import core.LearningProject;
import core.IProjectsController;
import exc.*;
import gui.helpers.CustomColor;
import gui.helpers.TransparencyTextField;

@SuppressWarnings("serial")
public class AddProjectDialog extends JDialog {

	private JPanel pnlCenter, pnlBtns;
	private JLabel lblTitle, lblNoOfStacks;
	private JTextField txtTitle, txtNoOfStacks;
	private JButton btnOk, btnDiscard;
	private MainWindow owner;
	private IProjectsController ctl;

	AddProjectDialog(MainWindow owner, IProjectsController ctl) {
		super(owner, true);
		this.owner = owner;
		this.ctl = ctl;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Projekt hinzuf\u00fcgen..");

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
		pnlCenter = new JPanel(new GridLayout(2, 2, 20, 10));
		pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		lblTitle = new JLabel("Titel:");
		lblNoOfStacks = new JLabel("Anzahl Durchl\u00e4ufe:");
		txtTitle = new TransparencyTextField();
		txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument) txtTitle.getDocument()).setDocumentFilter(new SizeFilterExtended(new InvalidCharsFilter(this), 50));
		txtNoOfStacks = new TransparencyTextField();
		txtNoOfStacks.setHorizontalAlignment(SwingConstants.CENTER);
		btnOk = new JButton("  OK  ");
		btnDiscard = new JButton(" Abbrechen ");

	}

	private void addWidgets() {
		getContentPane().add(pnlCenter, BorderLayout.NORTH);
		getContentPane().add(pnlBtns, BorderLayout.SOUTH);
		pnlCenter.add(lblTitle);
		pnlCenter.add(txtTitle);
		pnlCenter.add(lblNoOfStacks);
		pnlCenter.add(txtNoOfStacks);
		pnlBtns.add(btnDiscard);
		pnlBtns.add(Box.createHorizontalStrut(10));
		pnlBtns.add(btnOk);
	}

	private void setListeners() {
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (missingInput()) {
						if (txtTitle.getText().equals("")) {
							txtTitle.setBackground(CustomColor.BACKGROUND_ERROR_RED);
						}
						if (txtNoOfStacks.getText().equals("")) {
							txtNoOfStacks.setBackground(CustomColor.BACKGROUND_ERROR_RED);
						}
						CustomInfoHandling.showNoInputInfo(AddProjectDialog.this);
					} else {
						int noOfStacks = Integer.parseInt(txtNoOfStacks.getText());
						String title = txtTitle.getText();
						LearningProject newProject = new LearningProject(ctl, title, noOfStacks);
						newProject.store();
						owner.updateProjectList();
						AddProjectDialog.this.dispose();
					}
				} catch (SQLException sqle) {
					CustomErrorHandling.showDatabaseError(AddProjectDialog.this, sqle);
				} catch (NumberFormatException nfe) {
					CustomInfoHandling.showInvalidCharSequenceInfo(AddProjectDialog.this);
					txtNoOfStacks.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				} catch (InvalidLengthException ile) {
					CustomInfoHandling.showInvalidLengthInfo(AddProjectDialog.this, 150); // TODO
																							// global
																							// max
					txtTitle.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				} catch (InvalidValueException ive) {
					CustomInfoHandling.showInvalidValueInfo(AddProjectDialog.this, 99, 0); // TODO
																							// global
																							// max
					txtNoOfStacks.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				}
			}

			private boolean missingInput() {
				return txtTitle.getText().equals("") || txtNoOfStacks.getText().equals("");
			}
		});

		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddProjectDialog.this.dispose();
			}
		});

	}

}
