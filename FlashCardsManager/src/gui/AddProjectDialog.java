package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.SizeFilterExtended;
import utils.DocumentSizeFilter;
import utils.InvalidCharsFilter;
import core.LearningProject;
import core.ProjectsManager;
import exc.EntryAlreadyThereException;
import exc.InvalidValueException;
import exc.NoValueException;

public class AddProjectDialog extends JDialog {

	private JPanel pnlCenter, pnlBtns;
	private JLabel lblTitle, lblNoOfStacks;
	private JTextField txtTitle, txtNoOfStacks;
	private JButton btnOk, btnDiscard;
	private MainWindow owner;
	private ProjectsManager prm;

	AddProjectDialog(MainWindow owner, ProjectsManager prm) {
		super(owner, true);
		this.owner = owner;
		this.prm = prm;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Projekt hinzuf\u00fcgen..");
		
		try {
	         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	      } catch (ClassNotFoundException e) {
	         e.printStackTrace();
	      } catch (InstantiationException e) {
	         e.printStackTrace();
	      } catch (IllegalAccessException e) {
	         e.printStackTrace();
	      } catch (UnsupportedLookAndFeelException e) {
	         e.printStackTrace();
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
		txtTitle = new JTextField();
		txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument) txtTitle.getDocument()).setDocumentFilter(new SizeFilterExtended(new InvalidCharsFilter(this), 50));
		txtNoOfStacks = new JTextField();
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
					if (txtTitle.getText().equals("")
							|| txtNoOfStacks.getText().equals("")) {
						throw new NoValueException();
					}
					int noOfStacks = Integer.parseInt(txtNoOfStacks.getText());
					String title = txtTitle.getText();
					// verarbeite Eingabe in DB
					LearningProject newProject = new LearningProject(prm,
							title, noOfStacks);
					owner.projectPnls.add(new ProjectPanel(newProject, owner,
							prm));
					owner.pnlCenter.remove(owner.centerBox);
					owner.centerBox = Box.createVerticalBox();
					System.out.println(owner.projectPnls.size());
					owner.addProjectsToPanel();
					owner.pnlCenter.add(owner.centerBox, BorderLayout.NORTH);
					owner.pnlCenter.repaint();
					owner.revalidate();
					AddProjectDialog.this.dispose();
				} catch (ClassNotFoundException exc) {
					// TODO error handling
					System.out.println("Problem mit dem Datenbanktreiber - in AddProjectDialog!");
				} catch (EntryAlreadyThereException exc) {
					// TODO error handling
					System.out.println("Eintrag bereits vorhanden - in AddProjectDialog!");
				} catch (SQLException exc) {
					// TODO error handling
					System.out.println("SQL-Fehler - in AddProjectDialog!");
				} catch (NumberFormatException exc) {
					System.out.println("Please enter a valid number!");
					// TODO: error message
				} catch (NoValueException exc) {
					System.out.println("Please fill out the fields!");
					// TODO: error message
				} catch (InvalidValueException e1) {
					System.out.println("Number of stack must be a positive value!");
					// TODO: error message
				}
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
