package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.FileUtils;
import core.LearningProject;
import core.ProjectsManager;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import exc.InvalidValueException;

public class ChooseProjectsDialog extends JDialog {
   MainWindow owner;
   ArrayList<LearningProject> allProjects;

   private JPanel pnlControls;
   private JLabel lblInfo;
   private Box centerBox;
   private ProjectBox[] boxes;
   private JButton btnOk, btnDiscard;
   private ProjectsManager prm;
   private boolean delete;

   public ChooseProjectsDialog(MainWindow owner, ProjectsManager prm) {
      super(owner, false);
      this.owner = owner;
      this.prm = prm;
      this.allProjects = prm.getProjects();
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setTitle("Projektauswahl..");
      setLayout(new BorderLayout());

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
      lblInfo = new JLabel(
            "<html>Bitte w\u00e4hle die Projekte aus,<br>die exportiert werden sollen:</html>");
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

   private ArrayList<LearningProject> getSelectedProjects() {
      ArrayList<LearningProject> selectedProjects = new ArrayList<LearningProject>();
      for (int i = 0; i < boxes.length; i++) {
         if (boxes[i].isSelected()) {
            selectedProjects.add(allProjects.get(i));
         }
      }
      return selectedProjects;
   }

   class ExportProjectListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent e) {
         doAction();
      }

      private void doTask(String pathToExport) {
         ProgressDialog dialog = new ProgressDialog(owner,
               "... exportieren ...");
         dialog.setVisible(true);
         ExportTask task = new ExportTask(pathToExport, dialog);
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
               JOptionPane.showMessageDialog(ChooseProjectsDialog.this,
                     "Es wurde kein Pfad ausgewaehlt", "Fehler!",
                     JOptionPane.WARNING_MESSAGE);
               doAction();
            } else { // some path selected
               File f = new File(pathToExport);
               if (f.exists()) { // file already there
                  if (!f.canWrite()) { // can't overwrite -> error message
                     JOptionPane.showMessageDialog(
                           owner,
                           "Fehlende Ordnerberechtigungen unter "
                                 + f.getParent() + ".", "Fehler!",
                           JOptionPane.WARNING_MESSAGE);
                     doAction();
                  } else { // it's possible to overwrite -> ask user
                     int dialogResult = JOptionPane.showConfirmDialog(owner,
                           "Die Datei " + f.getName()
                                 + " existiert bereits in " + f.getParent()
                                 + " - soll sie überschrieben werden?",
                           "Datei überschreiben?", JOptionPane.YES_NO_OPTION,
                           JOptionPane.WARNING_MESSAGE);
                     if (dialogResult == JOptionPane.YES_OPTION) {
                        // user wants to overwrite -> delete existing
                        // directory and start export
                        if (directoryContainsOnlyDatabase(pathToExport)) {
                           delete = true;
                           doTask(pathToExport);
                        } else {
                           JOptionPane
                                 .showMessageDialog(
                                       owner,
                                       f.getName()
                                             + " ist kein Ordner, oder in diesem Ordner liegen noch andere Dateien!",
                                       "Fehler!", JOptionPane.WARNING_MESSAGE);
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
                     JOptionPane.showMessageDialog(
                           owner,
                           "Fehler - keine Schreibberechtigung unter "
                                 + f.getParent()
                                 + ". Bitte waehle ein anderes Verzeichnis! ",
                           "Fehlende Berechtigung!",
                           JOptionPane.WARNING_MESSAGE);
                     doAction();
                  } else { // writing is possible -> start export
                     doTask(pathToExport);
                     System.out.println("New location -- do task...");
                  }
               }
            }

         }
      }

   }

   private boolean directoryContainsOnlyDatabase(String pathToDirectory) {
      File f = new File(pathToDirectory);
      if (!f.isDirectory()) {
         return false;
      } else {
         File[] files = f.listFiles();
         for (File fi : files) {
            if (!(fi.getName().equals("log") || fi.getName().equals("seg0") || fi
                  .getName().equals("service.properties"))) {
               return false;
            }
         }
         return true;
      }
   }

   public class ExportTask extends SwingWorker<Void, Void> {
      String pathToExport;
      ProgressDialog dialog;

      ExportTask(String pathToExport, ProgressDialog dialog) {
         this.pathToExport = pathToExport;
         this.dialog = dialog;
      }

      public void changeProgress(int progress) {
         setProgress(progress);
      }

      @Override
      protected Void doInBackground() throws Exception {
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               owner.getRootPane().setCursor(
                     Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
         });
         setProgress(0);
         try {
            if (delete) {
               FileUtils.deleteDirectory(pathToExport);
            }
            // --> export project to selected location and show progress
            prm.exportProject(getSelectedProjects(), pathToExport, this);
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
               owner.getRootPane().setCursor(
                     Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
               JOptionPane.showMessageDialog(owner,
                     "Export erfolgreich abgeschlossen", "Fertig",
                     JOptionPane.INFORMATION_MESSAGE);
            }
         });
         return null;
      }

   }

}
