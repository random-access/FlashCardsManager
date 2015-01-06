package gui.helpers;

import gui.MainWindow;

import java.awt.Cursor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import utils.Logger;
import core.LearningProject;
import core.ProjectsController;

public class ExportTask extends SwingWorker<Void, Void> implements IProgressPresenter{
    String pathToExport;
    ProgressDialog dialog;
    MainWindow mw;
    ProjectsController ctl;
    ArrayList<LearningProject> projects;

    public ExportTask(String pathToExport, ArrayList<LearningProject> projects, ProgressDialog dialog, MainWindow mw, ProjectsController ctl) {
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
             mw.getRootPane().setCursor(
                   Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          }
       });
       setProgress(0);
          // --> export project to selected location and show progress
       try {
           ctl.exportProject(projects, pathToExport, this);
       } catch (SQLException exc) {
          JOptionPane.showMessageDialog(null,
                "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
                JOptionPane.ERROR_MESSAGE);
          Logger.log(exc);
       } catch (IOException exc) {
          JOptionPane.showMessageDialog(null,
                "Ein interner Fehler ist aufgetreten", "Fehler",
                JOptionPane.ERROR_MESSAGE);
          Logger.log(exc);
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
             mw.getRootPane().setCursor(
                   Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
             JOptionPane.showMessageDialog(mw,
                   "Export erfolgreich abgeschlossen", "Fertig",
                   JOptionPane.INFORMATION_MESSAGE);
          }
       });
       return null;
    }

 }