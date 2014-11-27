package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.text.AbstractDocument;

import utils.SizeFilterExtended;
import utils.DocumentSizeFilter;
import utils.InvalidCharsFilter;
import core.LearningProject;
import core.ProjectsManager;
import exc.EntryNotFoundException;
import exc.NoValueException;

public class ChangeTitleDialog extends JDialog {

	private JPanel pnlCenter, pnlBtns;
	private JLabel lblTitle;
	private JTextField txtTitle;
	private JButton btnOk, btnDiscard;
	private MainWindow owner;
	private ProjectPanel pnl;
	private ProjectsManager prm;
	private LearningProject proj;

	ChangeTitleDialog(ProjectPanel pnl, LearningProject proj, ProjectsManager prm) {
		super(pnl.getOwner(), true);
	    this.owner = pnl.getOwner();
		this.proj = proj;
		this.prm = prm;
		this.pnl = pnl;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Titel \u00e4ndern..");

//		try {
//			UIManager
//					.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
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
					if (txtTitle.getText().equals("")) {
						throw new NoValueException();
					}
					// verarbeite Eingabe in DB
					proj.setTitle(txtTitle.getText());
					prm.updateProject(proj);
					int i = owner.projectPnls.indexOf(pnl);
					owner.projectPnls.remove(pnl);
					owner.projectPnls.add(i, new ProjectPanel(proj, owner, prm));
					owner.pnlCenter.remove(owner.centerBox);
					owner.centerBox = Box.createVerticalBox();
					owner.addProjectsToPanel();
					owner.pnlCenter.add(owner.centerBox, BorderLayout.NORTH);
					owner.updateProjectStatus(proj);
					owner.revalidate();
					ChangeTitleDialog.this.dispose();
				} catch (NoValueException exc) {
					System.out.println("Please fill out the fields!");
					// TODO: error handling
				} catch (EntryNotFoundException e1) {
					// TODO error handling
					System.out.println("Eintrag nicht gefunden - in ChangeTitleDialog");
				} catch (SQLException e1) {
					// TODO error handling
					System.out.println("SQL Fehler - in ChangeTitleDialog");
				}
			}
		});

		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeTitleDialog.this.dispose();
			}
		});

	}

	private void createWidgets() {
		pnlCenter = new JPanel(new FlowLayout (FlowLayout.CENTER));
		pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		lblTitle = new JLabel("Titel:  ");
		if (setWidthInCols()) {
		   txtTitle = new JTextField(pnl.lblText.getText(), 10);
		} else {
		   txtTitle = new JTextField(pnl.lblText.getText());
		}
		txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument) txtTitle.getDocument()).setDocumentFilter(new SizeFilterExtended(new InvalidCharsFilter(this), 50));
		// ((AbstractDocument) txtTitle.getDocument()).setDocumentFilter(new InvalidCharsFilter(this));
		btnOk = new JButton("  OK  ");
		btnDiscard = new JButton(" Abbrechen ");
	}
	
	private boolean setWidthInCols() {
	     JTextField testField = new JTextField (pnl.lblText.getText());
	      double prefWidth = testField.getPreferredSize().getWidth();
	      double defWidth = 122;
	      return (prefWidth < defWidth);
	}

	private void addWidgets() {
		getContentPane().add(pnlCenter, BorderLayout.NORTH);
		getContentPane().add(pnlBtns, BorderLayout.SOUTH);
		pnlCenter.add(lblTitle);
		pnlCenter.add(txtTitle);
		pnlBtns.add(btnDiscard);
		pnlBtns.add(Box.createHorizontalStrut(10));
		pnlBtns.add(btnOk);
		txtTitle.setMinimumSize(new Dimension( (int)lblTitle.getSize().getWidth(), (int)txtTitle.getPreferredSize().getHeight() ));
	}

}
