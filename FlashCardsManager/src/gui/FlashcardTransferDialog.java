package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

import core.*;
import exc.CustomErrorHandling;
import gui.helpers.MyComboBoxModel;

@SuppressWarnings("serial")
public class FlashcardTransferDialog extends JDialog {
    private ArrayList<FlashCard> cardsToTransfer;
    private LearningProject srcProj;
    private IProjectsController ctl;

    private JPanel pnlBottom, pnlCenter, pnlGrid, pnlPlaceHolder;
    private JButton btnDiscard, btnOk;
    private JLabel lblSourceProject, lblSourceProjectName, lblTargetProject;
    private JComboBox<LearningProject> cmbChooseProject;
    private JCheckBox chkKeepProgress, chkKeepLabels;
    private FlashcardOverviewFrame editFrame;

    public FlashcardTransferDialog(IProjectsController ctl, FlashcardOverviewFrame editFrame, LearningProject srcProj,
            ArrayList<FlashCard> cardsToTransfer) {
        super(editFrame, true);
        setTitle("Lernkarten verschieben...");
        this.cardsToTransfer = cardsToTransfer;
        this.srcProj = srcProj;
        this.ctl = ctl;
        this.editFrame = editFrame;
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
        setLocationRelativeTo(editFrame);
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
        pnlGrid.add(pnlPlaceHolder);
        pnlGrid.add(chkKeepLabels);
    }

    private void createWidgets() {
        pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnDiscard = new JButton("abbrechen");
        btnOk = new JButton("ok");
        pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlGrid = new JPanel(new GridLayout(4, 2, 40, 10));
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

        chkKeepProgress = new JCheckBox("Lernfortschritt mitnehmen", true);
        chkKeepLabels = new JCheckBox("Labels mitnehmen (wenn n\u00f6tig hinzuf\u00fcgen)", true);
        pnlPlaceHolder = new JPanel();
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
                        f.transferTo(targetProject, chkKeepProgress.isSelected(), chkKeepLabels.isSelected());
                    }
                    ctl.fireProjectDataChangedEvent();
                    // editDialog.updateCardsView(srcProj.getAllCards());
                    editFrame.getMainFrame().updateProjectList();
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(editFrame, sqle);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(editFrame, ioe);
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
