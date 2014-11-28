package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.FlashCard;
import core.LearningProject;
import core.ProjectsManager;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import exc.InvalidValueException;

public class MainWindow extends JFrame {

	static BufferedImage imgSettings, imgPlus, imgAddProjectInfo;

	static {
		try {
			imgSettings = ImageIO.read(ProjectPanel.class.getClassLoader()
					.getResourceAsStream("img/ImgSettings_28x28.png"));
			imgPlus = ImageIO.read(ProjectPanel.class.getClassLoader()
					.getResourceAsStream("img/ImgPlus_16x16.png"));
			imgAddProjectInfo = ImageIO.read(MainWindow.class.getClassLoader()
					.getResourceAsStream("img/AddProjectInfo_450x338.png"));
		} catch (IOException e) {
			System.out.println("Picture not found");
			// TODO: JDialog mit ErrorMsg
		}
	}

	/* */
	private final int majorVersion, minorVersion, patchLevel;
	private JMenuBar mnuBar;
	private JMenu mnuSettings;
	private JMenu mnuSettingsNew, mnuSettingsImport, mnuSettingsExport;
	private JMenuItem mnuSettingsView, mnuSettingsPrint, mnuSettingsStatistic,
			mnuSettingsHelp, mnuSettingsAbout, mnuSettingsNewProject,
			mnuSettingsImportProject, mnuSettingsExportProject;
	/* */
	JPanel pnlControls, pnlCenter;
	JLabel lblAddProjectInfo;
	Box centerBox;
	JScrollPane scpCenter;
	ArrayList<ProjectPanel> projectPnls;
	private JButton btnAddProject;
	private ProjectsManager prm;

	public MainWindow(ProjectsManager prm, int majorVersion, int minorVersion, int patchLevel) {
		this.prm = prm;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchLevel = patchLevel;
		setTitle("FlashCards Manager");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// try {
		// UIManager
		// .setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

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
		computeProjectPanels();
		addWidgets();
		setListeners();

		setSize(500, 450);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	void computeProjectPanels() {
		projectPnls = new ArrayList<ProjectPanel>();
		for (int i = 0; i < prm.getProjects().size(); i++) {
			ProjectPanel pnl = new ProjectPanel(prm.getProjects().get(i), this,
					prm);
			pnl.changeStatus(calculateStatus(prm.getProjects().get(i)));
			projectPnls.add(pnl);
		}
	}

	private Status calculateStatus(LearningProject proj) {
		ArrayList<FlashCard> flashcards = proj.getAllCards();
		Status s = Status.RED;
		System.out.println("status red..");
		for (int i = 0; i < flashcards.size(); i++) {
			if (flashcards.get(i).getStack() > 1) { // at least one card in
													// stack >
													// 1
				s = Status.GREEN;
				System.out.println("status green");
				for (int j = 0; j < flashcards.size(); j++) {
					if (flashcards.get(j).getStack() < proj.getNumberOfStacks()) {
						// at least 1 card in stack < maxStack
						s = Status.YELLOW;
						System.out.println("status yellow");
						break;
					}
				}
				break;
			}
		}
		return s;
	}

	public void updateProjectStatus(LearningProject proj) {
		Status s = calculateStatus(proj);
		ProjectPanel p; // search for right project in project panels & update
						// status
		for (int i = 0; i < projectPnls.size(); i++) {
			if (projectPnls.get(i).getProject() == proj) {
				p = projectPnls.get(i);
				p.changeStatus(s);
				System.out.println("finally: status " + s.toString());
				p.repaint();
				p.revalidate();
				break;
			}
		}
	}

	private void createWidgets() {
		/* */
		mnuBar = new JMenuBar();
		mnuSettings = new JMenu("");
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
		// mnuSettingsExportTable.setEnabled(false);
		/* */
		pnlControls = new JPanel();
		pnlControls.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlControls.setBorder(BorderFactory.createLineBorder(getContentPane()
				.getBackground(), 8));
		pnlControls.setOpaque(true);
		pnlControls.setBackground(Color.DARK_GRAY);
		pnlCenter = new JPanel(new BorderLayout());
		centerBox = Box.createVerticalBox();
		lblAddProjectInfo = new JLabel(new ImageIcon(imgAddProjectInfo));
		scpCenter = new JScrollPane(pnlCenter);
		btnAddProject = new JButton(new ImageIcon(imgPlus));
		btnAddProject
				.setFont(btnAddProject.getFont().deriveFont(Font.BOLD, 16));
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
			System.out.println("projectpnls.size() == 0");
			centerBox.add(lblAddProjectInfo);
		} else {
			for (int i = 0; i < projectPnls.size(); i++) {
				centerBox.add(projectPnls.get(i));
			}
			centerBox.add(Box.createVerticalGlue());
		}
	}

	public void setListeners() {
		btnAddProject.addActionListener(new AddProjectListener());
		mnuSettingsNewProject.addActionListener(new AddProjectListener());
		mnuSettingsAbout.addActionListener(new AboutProjectListener());
		mnuSettingsExportProject.addActionListener(new ExportProjectListener());
		mnuSettingsImportProject.addActionListener(new ImportProjectListener());
	}

	class AboutProjectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final OkOrDisposeDialog d = new OkOrDisposeDialog(MainWindow.this,
					450, 250);
			d.setText("<html><center><b>Lernkarten - ein OpenSource Lernprogramm </b><br><br>Version: "
					+ majorVersion
					+ "." + minorVersion
					+ "." + patchLevel
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

	class ExportProjectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ChooseProjectsDialog d = new ChooseProjectsDialog(MainWindow.this,
					prm);
			d.setVisible(true);
		}
	}

	class ImportProjectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			doAction();
		}

		private void doTask(String pathToImport) {
			ProgressDialog dialog = new ProgressDialog(MainWindow.this,
					"... importieren ...");
			dialog.setVisible(true);
			ImportTask task = new ImportTask(pathToImport, dialog);
			task.addPropertyChangeListener(dialog);
			task.execute();
		}

		private void doAction() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showOpenDialog(MainWindow.this);
			String pathToImport = null;
			if (fileChooser.getSelectedFile() != null) { // prevent NullPointerExc when no path selected
				pathToImport = fileChooser.getSelectedFile().getAbsolutePath();
			}
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (pathToImport == null) { // no path selected
					JOptionPane.showMessageDialog(MainWindow.this,
							"Es wurde kein Pfad ausgewählt", "Fehler!",
							JOptionPane.WARNING_MESSAGE);
					doAction();
				} else { // some path selected
					File f = new File(pathToImport);
					if (!f.canWrite()) { // can't read -> error message
						JOptionPane.showMessageDialog(MainWindow.this,
								"Fehlende Ordnerberechtigungen unter " + f
										+ ". ", "Fehlende Berechtigung!",
								JOptionPane.WARNING_MESSAGE);
						doAction();
					} else { // it's possible to overwrite -> ask user
						doTask(pathToImport);
					}
				}

			}
			
		}
	}

	class AddProjectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			AddProjectDialog p = new AddProjectDialog(MainWindow.this, prm);
			p.setVisible(true);
		}
	}

	public class ImportTask extends SwingWorker<Void, Void> {
		String pathToImport;
		ProgressDialog dialog;

		ImportTask(String pathToImport, ProgressDialog dialog) {
			this.pathToImport = pathToImport;
			this.dialog = dialog;
		}

		public void changeProgress(int progress) {
			super.setProgress(progress);
		}

		@Override
		protected Void doInBackground() throws Exception {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					MainWindow.this.getRootPane().setCursor(
							Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			});

			setProgress(0);
			try {
				prm.importProject(pathToImport, this);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (EntryAlreadyThereException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (EntryNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidValueException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setProgress(100);
			Thread.sleep(1000);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dialog.dispose();
					centerBox.removeAll();
					computeProjectPanels();
					addProjectsToPanel();
					centerBox.revalidate();
					centerBox.repaint();
					MainWindow.this.getRootPane().setCursor(
							Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(MainWindow.this,
							"Import erfolgreich abgeschlossen", "Fertig",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
			return null;
		}

	}

}
