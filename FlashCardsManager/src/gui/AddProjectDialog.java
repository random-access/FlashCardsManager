package gui;

import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.CustomColor;
import gui.helpers.TransparencyTextField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import utils.*;
import core.LearningProject;
import core.ProjectsController;

@SuppressWarnings("serial")
public class AddProjectDialog extends JDialog {

	private JPanel pnlCenter, pnlBtns;
	private JLabel lblTitle, lblNoOfStacks;
	private JTextField txtTitle, txtNoOfStacks;
	private JButton btnOk, btnDiscard;
	private MainWindow owner;
	private ProjectsController ctl;

	AddProjectDialog(MainWindow owner, ProjectsController ctl) {
		super(owner, true);
		this.owner = owner;
		this.ctl = ctl;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Projekt hinzuf\u00fcgen..");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
			Logger.log(e);
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
				// pruefe ob vollstaendige Eingabe
				try {
					if (hasSomewhereNoInput()) {
						if (txtTitle.getText().equals("")) {
							txtTitle.setBackground(CustomColor.BACKGROUND_ERROR_RED);
						}
						if (txtNoOfStacks.getText().equals("")) {
							txtNoOfStacks.setBackground(CustomColor.BACKGROUND_ERROR_RED);
						}
						JOptionPane.showMessageDialog(AddProjectDialog.this, "Es wurden nicht alle Felder ausgef\u00fcllt.",
								"Fehler", JOptionPane.ERROR_MESSAGE); 
					} else {
						int noOfStacks = Integer.parseInt(txtNoOfStacks.getText());
						String title = txtTitle.getText();
						// verarbeite Eingabe in DB
						LearningProject newProject = new LearningProject(ctl, title, noOfStacks);
						newProject.store();
						owner.updateProjectList();
						AddProjectDialog.this.dispose();
					}
				} catch (SQLException exc) {
					JOptionPane.showMessageDialog(AddProjectDialog.this, "Ein interner Datenbankfehler ist aufgetreten.",
							"Fehler", JOptionPane.ERROR_MESSAGE);
					Logger.log(exc);
				} catch (NumberFormatException exc) {
					JOptionPane.showMessageDialog(AddProjectDialog.this, "Ung\u00fcltige Zeichenfolge.", "Fehler",
							JOptionPane.ERROR_MESSAGE);
					txtNoOfStacks.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				} catch (InvalidLengthException e1) {
					JOptionPane.showMessageDialog(AddProjectDialog.this, "Zu langer Titel (max. 150 Zeichen).", "Fehler",
							JOptionPane.ERROR_MESSAGE);
					txtTitle.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				} catch (InvalidValueException e1) {
					JOptionPane.showMessageDialog(AddProjectDialog.this, "Ung\u00fcltige Stapelanzahl", "Fehler",
							JOptionPane.ERROR_MESSAGE);
					txtNoOfStacks.setForeground(CustomColor.FOREGROUND_ERROR_RED);
				}
			}

			private boolean hasSomewhereNoInput() {
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
