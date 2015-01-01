package gui.helpers;

import gui.MainWindow;
import gui.ProgressDialog;

import java.awt.Cursor;

import javax.swing.*;

import core.ProjectsController;

public class ImportTask extends SwingWorker<Void, Void> {
	String pathToImport;
	ProgressDialog dialog;
	MainWindow mw;
	ProjectsController ctl;

	ImportTask(String pathToImport, ProgressDialog dialog, MainWindow mw, ProjectsController ctl) {
		this.pathToImport = pathToImport;
		this.dialog = dialog;
		this.mw = mw;
		this.ctl =ctl;
	}

	public void changeProgress(int progress) {
		super.setProgress(progress);
	}

	@Override
	protected Void doInBackground() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mw.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		});

		setProgress(0);
//		try {
			ctl.importProjects(pathToImport, this);

//		} catch (SQLException | EntryAlreadyThereException | EntryNotFoundException exc) {
//			JOptionPane.showMessageDialog(MainWindow.this, "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
//					JOptionPane.ERROR_MESSAGE);
//			Logger.log(exc);
//		} catch (IOException | ClassNotFoundException exc) {
//			JOptionPane.showMessageDialog(MainWindow.this, "Ein interner Fehler ist aufgetreten", "Fehler",
//					JOptionPane.ERROR_MESSAGE);
//			Logger.log(exc);
//		}
		setProgress(100);
		Thread.sleep(1000);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.dispose();
				mw.updateProjectList();	
				mw.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				JOptionPane.showMessageDialog(mw, "Import erfolgreich abgeschlossen", "Fertig",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		return null;
	}
}
