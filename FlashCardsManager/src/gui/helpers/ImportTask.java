package gui.helpers;

import exc.*;
import gui.MainWindow;

import java.awt.Cursor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;

import org.apache.derby.iapi.services.io.ArrayUtil;

import utils.FileUtils;
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
            if (FileUtils.directoryContainsCertainFines(pathToImport,
                    new ArrayList(ArrayUtil.asReadOnlyList(new String[] { "collection.anki2" })))) {
                System.out.println("Recognized ANKI format...");
                ctl.importANKI(pathToImport, this);
            }

            if (FileUtils.directoryContainsCertainFines(pathToImport,
                    new ArrayList(ArrayUtil.asReadOnlyList(new String[] { "flashcards.xml", "media.xml", "projects.xml" })))) {
                System.out.println("Recognized FlashCard (TM) format...");
                ctl.importProjects(pathToImport, this);
            }

        } catch (SQLException sqle) {
            CustomErrorHandling.showDatabaseError(mw, sqle);
            sqle.printStackTrace();
        } catch (IOException ioe) {
            CustomErrorHandling.showInternalError(mw, ioe);
            ioe.printStackTrace();
        } catch (XMLStreamException xse) {
            CustomErrorHandling.showImportError(mw, xse);
            xse.printStackTrace();
        } catch (InvalidValueException | InvalidLengthException exc) {
            CustomErrorHandling.showCorruptDataError(mw, exc);
            exc.printStackTrace();
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
