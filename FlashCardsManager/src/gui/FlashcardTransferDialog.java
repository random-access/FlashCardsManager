package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.FlashCard;
import core.LearningProject;
import core.ProjectsController;
import events.ProjectDataChangedListener;
import exc.CustomErrorHandling;
import gui.helpers.MyComboBoxModel;

@SuppressWarnings("serial")
public class FlashcardTransferDialog extends JDialog {
	private ArrayList<FlashCard> cardsToTransfer;
	private LearningProject srcProj;
	private ProjectsController ctl;
	private MainWindow owner;

	private JPanel pnlBottom, pnlCenter, pnlGrid;
	private JButton btnDiscard, btnOk;
	private JLabel lblSourceProject, lblSourceProjectName, lblTargetProject;
	private JComboBox<LearningProject> cmbChooseProject;
	private JCheckBox chkKeepProgress;
	
	public FlashcardTransferDialog(ProjectsController ctl, MainWindow owner, FlashcardOverviewDialog editDialog, LearningProject srcProj, ArrayList<FlashCard> cardsToTransfer) {
		super(owner, true);
		setTitle("Lernkarten verschieben...");
		this.cardsToTransfer = cardsToTransfer;
		this.srcProj = srcProj;
		this.ctl = ctl;
		this.owner = owner;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
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

	private void addWidgets() {
		add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.add(btnDiscard);
		pnlBottom.add(btnOk);
		add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.add(pnlGrid);
		pnlGrid.add(lblSourceProject);
		pnlGrid.add(lblTargetProject);
		pnlGrid.add(lblSourceProjectName);
		pnlGrid.add(cmbChooseProject);
		pnlGrid.add(chkKeepProgress);
	}

	private void createWidgets() {
		pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnDiscard = new JButton("abbrechen");
		btnOk = new JButton("ok");
		pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlGrid = new JPanel(new GridLayout(0, 2, 40, 10));
		pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		MyComboBoxModel model = new MyComboBoxModel(getPossibleTargetProjects());
		cmbChooseProject = new JComboBox<LearningProject>(model);
		cmbChooseProject.setSelectedItem("<Projekt ausw\u00e4hlen>");
		lblTargetProject = new JLabel("Zielprojekt:");
		lblTargetProject.setAlignmentX(LEFT_ALIGNMENT);
		lblSourceProject = new JLabel("Derzeitiges Projekt:");
		lblSourceProjectName = new JLabel(srcProj.getTitle());
		lblSourceProject.setFont(getFont().deriveFont(Font.BOLD));
		lblTargetProject.setFont(getFont().deriveFont(Font.BOLD));

		chkKeepProgress = new JCheckBox("Lernfortschritt mitnehmen");

	}

	private ArrayList<LearningProject> getPossibleTargetProjects() {
		ArrayList<LearningProject> projects = new ArrayList<LearningProject>(); 
		for (LearningProject p : ctl.getProjects()) {
			if (!p.equals(srcProj)) {
				projects.add(p);
			}
		}
		return projects;
	}

	private void setListeners() {
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LearningProject targetProject = (LearningProject) cmbChooseProject.getModel().getSelectedItem();
				try {
					for (FlashCard f : cardsToTransfer) {
						f.transferTo(targetProject, chkKeepProgress.isSelected());
					}
					ctl.fireProjectDataChangedEvent();
					// editDialog.updateCardsView(srcProj.getAllCards());
					owner.updateProjectList();
				} catch (SQLException sqle) {
					CustomErrorHandling.showDatabaseError(owner, sqle);
				} catch (IOException ioe) {
					CustomErrorHandling.showInternalError(owner, ioe);
				} finally {
					FlashcardTransferDialog.this.dispose();
				}
			}
		});

		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FlashcardTransferDialog.this.dispose();
			}
		});
	}

}
