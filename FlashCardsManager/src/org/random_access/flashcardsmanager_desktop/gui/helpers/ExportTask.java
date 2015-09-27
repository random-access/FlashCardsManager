package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.awt.Cursor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import org.random_access.flashcardsmanager_desktop.core.*;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.exc.CustomInfoHandling;
import org.random_access.flashcardsmanager_desktop.gui.MainWindow;

public class ExportTask extends SwingWorker<Void, Void> implements IProgressPresenter {
	String pathToExport;
	ProgressDialog dialog;
	MainWindow mw;
	IProjectsController ctl;
	ArrayList<LearningProject> projects;

	public ExportTask(String pathToExport, ArrayList<LearningProject> projects, ProgressDialog dialog, MainWindow mw,
			IProjectsController ctl) {
		this.pathToExport = pathToExport;
		this.projects = projects;
		this.dialog = dialog;
		this.mw = mw;
		this.ctl = ctl;
	}

	public void changeProgress(int progress) {
		setProgress(progress);
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
		// --> export project to selected location and show progress
		try {
			((OfflineProjectsController) ctl).exportProject(projects, pathToExport, this);
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(mw, sqle);
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(mw, ioe);
		} catch (XMLStreamException xse) {
			CustomErrorHandling.showExportError(mw, xse);
		}
		setProgress(100);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.dispose();
				mw.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				CustomInfoHandling.showExportSuccessInfo(mw);
			}
		});
		return null;
	}

}