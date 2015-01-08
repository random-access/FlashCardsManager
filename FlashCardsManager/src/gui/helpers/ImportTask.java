package gui.helpers;

import exc.*;
import gui.MainWindow;

import java.awt.Cursor;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import core.ProjectsController;

public class ImportTask extends SwingWorker<Void, Void> implements IProgressPresenter {
	String pathToImport;
	ProgressDialog dialog;
	MainWindow mw;
	ProjectsController ctl;

	public ImportTask(String pathToImport, ProgressDialog dialog, MainWindow mw, ProjectsController ctl) {
		this.pathToImport = pathToImport;
		this.dialog = dialog;
		this.mw = mw;
		this.ctl = ctl;
	}

	public void changeProgress(int progress) {
		super.setProgress(progress);
	}

	@Override
	public void changeInfo(String text) {
		dialog.changeInfo(text);
	}

	@Override
	protected Void doInBackground() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mw.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		});
		setProgress(0);
		try {
			ctl.importProjects(pathToImport, this);

		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(mw, sqle);
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(mw, ioe);
		} catch (XMLStreamException xse) {
			CustomErrorHandling.showImportError(mw, xse);
		} catch (InvalidValueException |InvalidLengthException exc) {
			CustomErrorHandling.showCorruptDataError(mw, exc);
		}
		setProgress(100);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.dispose();
				try {
					mw.updateProjectList();
					CustomInfoHandling.showImportSuccessInfo(mw);
				} catch (SQLException sqle) {
					CustomErrorHandling.showDatabaseError(mw, sqle);
				}
				mw.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
			}
		});
		return null;
	}
}
