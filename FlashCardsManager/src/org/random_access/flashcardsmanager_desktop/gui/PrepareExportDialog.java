package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.core.IProjectsController;
import org.random_access.flashcardsmanager_desktop.core.LearningProject;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.exc.CustomInfoHandling;
import org.random_access.flashcardsmanager_desktop.gui.helpers.ExportTask;
import org.random_access.flashcardsmanager_desktop.gui.helpers.ProgressDialog;
import org.random_access.flashcardsmanager_desktop.importExport.XMLFiles;
import org.random_access.flashcardsmanager_desktop.utils.FileUtils;

@SuppressWarnings("serial")
public class PrepareExportDialog extends JDialog {
    private IProjectsController ctl;
    private MainWindow owner;
    private ArrayList<LearningProject> allProjects;

    private JPanel pnlControls;
    private JLabel lblInfo;
    private Box centerBox;
    private ProjectBox[] boxes;
    private JButton btnOk, btnDiscard;

    // private boolean delete = false;

    PrepareExportDialog(MainWindow owner, IProjectsController ctl) {
        super(owner, false);
        this.owner = owner;
        this.ctl = ctl;
        this.allProjects = ctl.getProjects();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Projektauswahl..");
        setLayout(new BorderLayout());

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
        pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centerBox = Box.createVerticalBox();
        boxes = new ProjectBox[allProjects.size()];
        for (int i = 0; i < allProjects.size(); i++) {
            boxes[i] = new ProjectBox(allProjects.get(i).getTitle());
        }
        lblInfo = new JLabel("<html>Bitte w\u00e4hle die Projekte aus,<br>die exportiert werden sollen:</html>");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        btnOk = new JButton("Ok");
        btnDiscard = new JButton("Abbrechen");

    }

    private void addWidgets() {
        this.add(centerBox, BorderLayout.CENTER);
        this.add(pnlControls, BorderLayout.SOUTH);
        centerBox.add(lblInfo);
        for (int i = 0; i < allProjects.size(); i++) {
            centerBox.add(boxes[i]);
        }
        pnlControls.add(btnDiscard);
        pnlControls.add(btnOk);
    }

    private void setListeners() {
        btnDiscard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrepareExportDialog.this.dispose();
            }
        });

        btnOk.addActionListener(new ExportProjectListener());
    }

    ArrayList<LearningProject> getSelectedProjects() throws SQLException {
        ArrayList<LearningProject> selectedProjects = new ArrayList<LearningProject>();
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i].isSelected()) {
                selectedProjects.add(allProjects.get(i));
            }
        }
        return selectedProjects;
    }

    private class ExportProjectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                doAction();
            } catch (SQLException sqle) {
                CustomErrorHandling.showDatabaseError(PrepareExportDialog.this, sqle);
            } catch (IOException ioe) {
                CustomErrorHandling.showInternalError(PrepareExportDialog.this, ioe);
            }
        }

        private void doTask(String pathToExport) throws SQLException {
            ProgressDialog dialog = new ProgressDialog(owner, "Vorbereiten...");
            dialog.setVisible(true);
            ExportTask task = new ExportTask(pathToExport, getSelectedProjects(), dialog, owner, ctl);
            task.addPropertyChangeListener(dialog);
            task.execute();
        }

        private void doAction() throws SQLException, IOException {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showSaveDialog(PrepareExportDialog.this);
            PrepareExportDialog.this.dispose();
            String pathToExport = null;
            if (fileChooser.getSelectedFile() != null) {
                // prevent NullPointerExc when no path selected
                pathToExport = fileChooser.getSelectedFile().getAbsolutePath();
            }
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (pathToExport == null) { // no path selected
                    CustomInfoHandling.showNoPathSelectedInfo(owner);
                    doAction();
                } else { // some path selected
                    File f = new File(pathToExport);
                    if (f.exists()) { // file already there
                        if (!f.canWrite()) { // can't overwrite -> error message
                            CustomInfoHandling.showMissingPermissionsInfo(owner, f.getParent());
                            doAction();
                        } else { // it's possible to overwrite -> ask user
                            int dialogResult = CustomInfoHandling.showOverwriteFileQuestion(owner, f.getParent(), f.getName());
                            if (dialogResult == JOptionPane.YES_OPTION) {
                                // user wants to overwrite -> delete existing
                                // directory and start export
                                if (FileUtils.directoryContainsOnlyCertainFiles(pathToExport, XMLFiles.getAllNames())) {
                                    FileUtils.deleteDirectory(pathToExport);
                                    doTask(pathToExport);
                                } else {
                                    CustomInfoHandling.showUnexpectedFolderStructureInfo(owner, pathToExport);
                                    doAction();
                                }
                            } else if (dialogResult == JOptionPane.NO_OPTION) {
                                // user doesn't want to overwrite -> show file
                                // chooser again
                                doAction();
                            }
                        }
                    } else { // no file with that name yet
                        if (!f.getParentFile().canWrite()) { // missing
                                                             // permissions
                            CustomInfoHandling.showMissingPermissionsInfo(owner, f.getParent());
                            doAction();
                        } else { // writing is possible -> start export
                            doTask(pathToExport);
                        }
                    }
                }
            }
        }
    }
}
