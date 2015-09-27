package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.core.*;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.exc.CustomInfoHandling;
import org.random_access.flashcardsmanager_desktop.gui.helpers.*;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

    private static BufferedImage imgIcon36x36, imgIcon24x24, imgIcon16x16, imgIcon12x12, imgSettings, imgPlus, imgAddProjectInfo;

    static {
        try {
            imgIcon36x36 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
                    "org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_36x36.png"));
            imgIcon24x24 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
                    "org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_24x24.png"));
            imgIcon16x16 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
                    "org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_16x16.png"));
            imgIcon12x12 = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream(
                    "org.random_access.flashcardsmanager_desktop.img/Label_LearningCards_blue_12x12.png"));
            imgSettings = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgSettings_28x28.png"));

            imgPlus = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgPlus_16x16.png"));
            imgAddProjectInfo = ImageIO.read(MainWindow.class.getClassLoader().getResourceAsStream(
                    "org.random_access.flashcardsmanager_desktop.img/AddProjectInfo_450x338.png"));
        } catch (IOException ioe) {
            CustomErrorHandling.showInternalError(null, ioe);
        }
    }

    private LinkedList<Image> icons;
    private final int majorVersion, minorVersion, patchLevel;
    private JMenuBar mnuBar;
    private JMenu mnuSettings;
    private JMenu mnuSettingsNew, mnuSettingsImport, mnuSettingsExport;
    private JMenuItem mnuSettingsView, mnuSettingsPrint, mnuSettingsStatistic, mnuSettingsHelp, mnuSettingsAbout,
            mnuSettingsNewProject, mnuSettingsImportProject, mnuSettingsExportProject;
    private JPanel pnlControls, pnlCenter;
    private Box centerBox;
    private ArrayList<ProjectPanel> projectPnls;
    private JLabel lblAddProjectInfo;
    private JScrollPane scpCenter;
    private JButton btnAddProject;
    private IProjectsController ctl;

    public MainWindow(IProjectsController ctl, int majorVersion, int minorVersion, int patchLevel) {
        this.ctl = ctl;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchLevel = patchLevel;
        setTitle("FlashCards Manager");
        icons = new LinkedList<Image>();
        icons.add(imgIcon12x12);
        icons.add(imgIcon16x16);
        icons.add(imgIcon24x24);
        icons.add(imgIcon36x36);
        setIconImages(icons);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
            CustomErrorHandling.showInternalError(null, exc);
        }

        createWidgets();
        try {
            computeProjectPanels();

            addWidgets();
            setListeners();

            setSize(getPreferredSize().width + 20, 450);
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (SQLException sqle) {
            CustomErrorHandling.showDatabaseError(this, sqle);
        }
    }

    public IProjectsController getProjectsController() {
        return this.ctl;
    }

    public LinkedList<Image> getIcons() {
        return icons;
    }

    void computeProjectPanels() throws SQLException {
        ctl.loadProjects();
        projectPnls = new ArrayList<ProjectPanel>();
        for (int i = 0; i < ctl.getProjects().size(); i++) {
            ProjectPanel pnl = new ProjectPanel(ctl.getProjects().get(i), this);
            pnl.changeStatus(ctl.getProjects().get(i).getStatus());
            projectPnls.add(pnl);
        }
    }

    void updateProjectStatus(LearningProject proj) throws SQLException {
        Status s = proj.getStatus();
        ProjectPanel p; // search for right project in project panels & update
                        // status
        for (int i = 0; i < projectPnls.size(); i++) {
            if (projectPnls.get(i).getProject() == proj) {
                p = projectPnls.get(i);
                p.changeStatus(s);
                p.repaint();
                p.revalidate();
                break;
            }
        }
    }

    public void updateProjectList() throws SQLException {
        computeProjectPanels();
        centerBox.removeAll();
        addProjectsToPanel();
        centerBox.revalidate();
        centerBox.repaint();
    }

    private void createWidgets() {
        /* */
        mnuBar = new JMenuBar();
        mnuSettings = new JMenu("");
        mnuSettings.setToolTipText("Einstellungen..");
        mnuSettings.setIcon(new ImageIcon(imgSettings));
        mnuSettings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        mnuSettingsNew = new MyMenu("Neu");
        mnuSettingsImport = new MyMenu("Importieren");
        mnuSettingsExport = new MyMenu("Exportieren");

        mnuSettingsView = new MyMenuItem("Style");
        mnuSettingsView.setEnabled(false);
        mnuSettingsPrint = new MyMenuItem("Karten drucken..");
        mnuSettingsPrint.setEnabled(false);
        mnuSettingsStatistic = new MyMenuItem("Statistiken..");
        mnuSettingsStatistic.setEnabled(false);
        mnuSettingsHelp = new MyMenuItem("Hilfe..");
        mnuSettingsHelp.setEnabled(false);
        mnuSettingsAbout = new MyMenuItem("\u00dcber..");
        /* */
        mnuSettingsNewProject = new MyMenuItem("Projekt");
        mnuSettingsImportProject = new MyMenuItem("Projekte importieren..");
        mnuSettingsExportProject = new MyMenuItem("Projekte exportieren..");
        /* */
        pnlControls = new JPanel();
        pnlControls.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlControls.setBorder(BorderFactory.createLineBorder(getContentPane().getBackground(), 8));
        pnlControls.setOpaque(true);
        pnlControls.setBackground(Color.DARK_GRAY);
        pnlCenter = new JPanel(new BorderLayout());
        centerBox = Box.createVerticalBox();
        lblAddProjectInfo = new JLabel(new ImageIcon(imgAddProjectInfo));
        scpCenter = new JScrollPane(pnlCenter);
        btnAddProject = new JButton(new ImageIcon(imgPlus));
        btnAddProject.setFont(btnAddProject.getFont().deriveFont(Font.BOLD, 16));
        btnAddProject.setToolTipText("Neues Lernprojekt hinzuf\u00fcgen");
    }

    private void addWidgets() {
        getContentPane().add(pnlControls, BorderLayout.NORTH);
        getContentPane().add(scpCenter, BorderLayout.CENTER);

        pnlControls.add(btnAddProject);
        pnlControls.add(Box.createHorizontalStrut(4));
        pnlControls.add(mnuBar);
        pnlControls.add(Box.createHorizontalStrut(2));

        mnuBar.add(mnuSettings);
        mnuSettings.add(mnuSettingsNew);
        mnuSettings.add(mnuSettingsImport);
        mnuSettings.add(mnuSettingsExport);
        mnuSettings.add(mnuSettingsView);
        mnuSettings.add(mnuSettingsPrint);
        mnuSettings.add(mnuSettingsStatistic);
        mnuSettings.add(mnuSettingsHelp);
        mnuSettings.add(mnuSettingsAbout);

        mnuSettingsNew.add(mnuSettingsNewProject);
        mnuSettingsImport.add(mnuSettingsImportProject);
        mnuSettingsExport.add(mnuSettingsExportProject);

        addProjectsToPanel();
        pnlCenter.add(centerBox, BorderLayout.NORTH);

    }

    void addProjectsToPanel() {
        if (projectPnls.size() == 0) {
            centerBox.add(lblAddProjectInfo);
        } else {
            for (int i = 0; i < projectPnls.size(); i++) {
                centerBox.add(projectPnls.get(i));
            }
            centerBox.add(Box.createVerticalGlue());
        }
    }

    private void setListeners() {
        btnAddProject.addActionListener(new AddProjectListener());
        mnuSettingsNewProject.addActionListener(new AddProjectListener());
        mnuSettingsAbout.addActionListener(new AboutProjectListener());
        mnuSettingsExportProject.addActionListener(new ExportProjectListener());
        mnuSettingsImportProject.addActionListener(new ImportProjectListener());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                ctl.disconnectFromDatabase();
            }

        });
    }

    private class AboutProjectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final OkOrDisposeDialog d = new OkOrDisposeDialog(MainWindow.this, 450, 250);
            d.setText("<html><center><b>Lernkarten - ein OpenSource Lernprogramm </b><br><br>Version: "
                    + majorVersion
                    + "."
                    + minorVersion
                    + "."
                    + patchLevel
                    + "<br><br>Feedback bitte an: <a href=\"mailto:software@random-access.org\">software@random-access.org</a><br><br>\u00a9 Monika Schrenk, 2014</center></html>");
            d.addOkAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    d.dispose();
                }
            });
            d.setVisible(true);
        }

    }

    private class ExportProjectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            PrepareExportDialog d = new PrepareExportDialog(MainWindow.this, ctl);
            d.setVisible(true);
        }
    }

    private class ImportProjectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doAction();
        }

        private void doTask(String pathToImport) {
            ProgressDialog dialog = new ProgressDialog(MainWindow.this, "Vorbereiten...");
            dialog.setVisible(true);
            ImportTask task = new ImportTask(pathToImport, dialog, MainWindow.this, ctl);
            task.addPropertyChangeListener(dialog);
            task.execute();
        }

        private void doAction() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(MainWindow.this);
            String pathToImport = null;
            if (fileChooser.getSelectedFile() != null) {
                // prevent NullPointerExc when no path selected
                pathToImport = fileChooser.getSelectedFile().getAbsolutePath();
            }
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (pathToImport == null) { // no path selected
                    CustomInfoHandling.showNoPathSelectedInfo(MainWindow.this);
                    doAction();
                } else { // some path selected
                    File f = new File(pathToImport);
                    if (!f.canWrite()) { // can't read -> error message
                        CustomInfoHandling.showMissingPermissionsInfo(MainWindow.this, f.toString());
                        doAction();
                    } else { // it's possible to overwrite -> ask user
                        doTask(pathToImport);
                    }
                }

            }
        }
    }

    private class AddProjectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddProjectDialog p = new AddProjectDialog(MainWindow.this, ctl);
            p.setVisible(true);
        }
    }

}
