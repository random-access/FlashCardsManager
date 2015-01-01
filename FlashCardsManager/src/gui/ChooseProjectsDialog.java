package gui;

import gui.helpers.ExportTask;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;

import utils.FileUtils;
import utils.Logger;
import core.LearningProject;
import core.ProjectsController;

@SuppressWarnings("serial")
public class ChooseProjectsDialog extends JDialog {
	private ProjectsController ctl;
	private MainWindow owner;
	private ArrayList<LearningProject> allProjects;

	private JPanel pnlControls;
	private JLabel lblInfo;
	private Box centerBox;
	private ProjectBox[] boxes;
	private JButton btnOk, btnDiscard;
	// private boolean delete = false;
	private static final String[] DATABASE_FILES = { "log", "seg0", "service.properties" };

	ChooseProjectsDialog(MainWindow owner, ProjectsController ctl) {
		super(owner, false);
		this.owner = owner;
		this.ctl = ctl;
		this.allProjects = ctl.getProjects();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Projektauswahl..");
		setLayout(new BorderLayout());

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
				ChooseProjectsDialog.this.dispose();
			}
		});

		btnOk.addActionListener(new ExportProjectListener());
	}

	ArrayList<LearningProject> getSelectedProjects() {
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
			doAction();
		}

		private void doTask(String pathToExport) {
			ProgressDialog dialog = new ProgressDialog(owner, "... exportieren ...");
			dialog.setVisible(true);
			ExportTask task = new ExportTask(pathToExport, getSelectedProjects(), dialog, owner, ctl);
			task.addPropertyChangeListener(dialog);
			task.execute();
		}

		private void doAction() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showSaveDialog(ChooseProjectsDialog.this);
			ChooseProjectsDialog.this.dispose();
			String pathToExport = null;
			if (fileChooser.getSelectedFile() != null) { // prevent
				// NullPointerExc
				// when no path
				// selected
				pathToExport = fileChooser.getSelectedFile().getAbsolutePath();
			}
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (pathToExport == null) { // no path selected
					JOptionPane.showMessageDialog(ChooseProjectsDialog.this, "Es wurde kein Pfad ausgew\u00e4hlt", "Fehler!",
							JOptionPane.WARNING_MESSAGE);
					doAction();
				} else { // some path selected
					File f = new File(pathToExport);
					if (f.exists()) { // file already there
						if (!f.canWrite()) { // can't overwrite -> error message
							JOptionPane.showMessageDialog(owner, "Fehlende Ordnerberechtigungen unter " + f.getParent() + ".",
									"Fehler!", JOptionPane.WARNING_MESSAGE);
							doAction();
						} else { // it's possible to overwrite -> ask user
							int dialogResult = JOptionPane.showConfirmDialog(owner, "Die Datei " + f.getName()
									+ " existiert bereits in " + f.getParent() + " - soll sie \u00fcberschrieben werden?",
									"Datei \u00fcberschreiben?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if (dialogResult == JOptionPane.YES_OPTION) {
								// user wants to overwrite -> delete existing
								// directory and start export
								if (FileUtils.directoryContainsOnlyCertainFiles(pathToExport, DATABASE_FILES)) {
									// delete = true;
									doTask(pathToExport);
								} else {
									JOptionPane.showMessageDialog(owner, f.getName()
											+ " ist kein Ordner, oder in diesem Ordner liegen noch andere Dateien!", "Fehler!",
											JOptionPane.WARNING_MESSAGE);
									doAction();
								}
							} else if (dialogResult == JOptionPane.NO_OPTION) {
								// user doesn't want to overwrite -> show file
								// chooser
								doAction();
							}

						}
					} else { // no file with that name yet
						if (!f.getParentFile().canWrite()) { // can't write ->
							// error message
							JOptionPane.showMessageDialog(owner, "Fehler - keine Schreibberechtigung unter " + f.getParent()
									+ ". Bitte w\u00e4hle ein anderes Verzeichnis! ", "Fehlende Berechtigung!",
									JOptionPane.WARNING_MESSAGE);
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
