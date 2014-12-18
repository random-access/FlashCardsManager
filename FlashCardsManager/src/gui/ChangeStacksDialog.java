package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.LearningProject;
import core.ProjectsManager;
import exc.EntryNotFoundException;
import exc.InvalidValueException;
import exc.NoValueException;

@SuppressWarnings("serial")
public class ChangeStacksDialog extends JDialog {

	private JPanel pnlCenter, pnlBtns;
	private JLabel lblNoOfStacks;
	private JTextField txtNoOfStacks;
	private JButton btnOk, btnDiscard;
	private MainWindow owner;
	private ProjectPanel pnl;
	private LearningProject project;
	private ProjectsManager prm;

	ChangeStacksDialog(ProjectPanel pnl,
			LearningProject project, ProjectsManager prm) {
		super(pnl.getOwner(), true);
		this.owner = pnl.getOwner();
		this.prm = prm;
		this.project = project;	
		this.pnl = pnl;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Anzahl Durchl\u00e4ufe \u00e4ndern..");

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

	private void setListeners() {
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// pruefe ob vollstaendige Eingabe
				try {
					if (txtNoOfStacks.getText().equals("")) {
						throw new NoValueException();
					}
					// verarbeite Eingabe in DB
					int nr = Integer.parseInt(txtNoOfStacks.getText());
					pnl.noOfStacks = nr;
					project.setNumberOfStacks(nr);
					prm.updateProject(project);
					owner.updateProjectStatus(project);
					ChangeStacksDialog.this.dispose();
				} catch (NoValueException exc) {
					System.out.println("Please fill out the fields!");
					// TODO: error message
				} catch (NumberFormatException exc) {
					System.out.println("Please enter a valid number!");
					// TODO: error message
				} catch (EntryNotFoundException e1) {
					// TODO error handling
					System.out.println("Eintrag nicht gefunden - in ChangeStacksDialog");
				} catch (SQLException e1) {
					// TODO error handling
					System.out.println("SQL Fehler - in ChangeTitleDialog");
				} catch (InvalidValueException e1) {
					System.out.println("Number of stack must be a positive value!");
					// TODO error handling
				} 
			}
		});

		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeStacksDialog.this.dispose();
			}
		});
	}

	private void createWidgets() {
		pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		lblNoOfStacks = new JLabel("Anzahl Durchl\u00e4ufe:  ");
		txtNoOfStacks = new JTextField(String.valueOf(pnl.noOfStacks), 5);
		txtNoOfStacks.setHorizontalAlignment(SwingConstants.CENTER);
		btnOk = new JButton("  OK  ");
		btnDiscard = new JButton(" Abbrechen ");
	}

	private void addWidgets() {
		getContentPane().add(pnlCenter, BorderLayout.NORTH);
		getContentPane().add(pnlBtns, BorderLayout.SOUTH);
		pnlCenter.add(lblNoOfStacks);
		pnlCenter.add(txtNoOfStacks);
		pnlBtns.add(btnDiscard);
		pnlBtns.add(Box.createHorizontalStrut(10));
		pnlBtns.add(btnOk);
	}

}
