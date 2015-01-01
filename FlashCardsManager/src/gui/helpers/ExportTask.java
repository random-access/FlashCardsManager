package gui.helpers;

import gui.MainWindow;
import gui.ProgressDialog;

import java.awt.Cursor;
import java.util.ArrayList;

import javax.swing.*;

import core.LearningProject;
import core.ProjectsController;

public class ExportTask extends SwingWorker<Void, Void> {
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
    protected Void doInBackground() throws Exception {
       SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
             mw.getRootPane().setCursor(
                   Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          }
       });
       setProgress(0);
          // --> export project to selected location and show progress
           ctl.exportProject(projects, pathToExport, this);
//       } catch (SQLException | EntryAlreadyThereException | EntryNotFoundException exc) {
//          JOptionPane.showMessageDialog(null,
//                "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
//                JOptionPane.ERROR_MESSAGE);
//          Logger.log(exc);
//       } catch (IOException | ClassNotFoundException exc) {
//          JOptionPane.showMessageDialog(null,
//                "Ein interner Fehler ist aufgetreten", "Fehler",
//                JOptionPane.ERROR_MESSAGE);
//          Logger.log(exc);
//       }
       setProgress(100);
       Thread.sleep(1000);

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