package gui.helpers;

import gui.MainWindow;
import gui.ProgressDialog;

import java.awt.Cursor;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import utils.Logger;
import core.ProjectsController;

public class ImportTask extends SwingWorker<Void, Void> {
	String pathToImport;
	ProgressDialog dialog;
	MainWindow mw;
	ProjectsController ctl;

	public ImportTask(String pathToImport, ProgressDialog dialog, MainWindow mw, ProjectsController ctl) {
		this.pathToImport = pathToImport;
		this.dialog = dialog;
		this.mw = mw;
		this.ctl =ctl;
	}

	public void changeProgress(int progress) {
		super.setProgress(progress);
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

		} catch (SQLException exc) {
			JOptionPane.showMessageDialog(mw, "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			Logger.log(exc);
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(mw, "Ein interner Fehler ist aufgetreten", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			Logger.log(exc);
		} catch (NumberFormatException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (XMLStreamException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
		setProgress(100);
		try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

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
