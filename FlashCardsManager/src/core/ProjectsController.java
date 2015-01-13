package core;

import importExport.ProjectExporter;
import importExport.ProjectImporter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import storage.DBExchanger;
import storage.MediaExchanger;
import events.*;
import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

public class ProjectsController implements ProjectChangedSource {

    private final DBExchanger dbex;
    private final MediaExchanger mex;
    private ArrayList<LearningProject> projects;
    private String pathToMediaFolder;

    private ArrayList<ProjectDataChangedListener> listeners = new ArrayList<ProjectDataChangedListener>();

    public ProjectsController(String pathToDatabase, String pathToMediaFolder) throws ClassNotFoundException, SQLException {
        dbex = new DBExchanger(pathToDatabase, this);
        mex = new MediaExchanger(pathToMediaFolder);
        this.pathToMediaFolder = pathToMediaFolder;
        dbex.createConnection();
        dbex.createTablesIfNotExisting();
    }

    public void loadProjects() throws SQLException {
        projects = dbex.getAllProjects();
    }

    public DBExchanger getDbex() {
        return dbex;
    }

    public MediaExchanger getMex() {
        return mex;
    }

    public void addProject(LearningProject p) {
        projects.add(p);
    }

    public void removeProject(LearningProject p) {
        projects.remove(p);
    }

    public ArrayList<LearningProject> getProjects() {
        return projects;
    }

    public void importProjects(String pathToImport, IProgressPresenter p) throws XMLStreamException, IOException, SQLException,
            InvalidValueException, InvalidLengthException {
        ProjectImporter importer = new ProjectImporter(pathToImport, pathToMediaFolder, this, p);
        importer.doImport();
    }

    public void exportProject(ArrayList<LearningProject> projects, String pathToExport, IProgressPresenter p)
            throws SQLException, XMLStreamException, IOException {
        ProjectExporter exporter = new ProjectExporter(projects, pathToMediaFolder, pathToExport, p);
        exporter.doExport();
    }

    @Override
    public synchronized void addEventListener(ProjectDataChangedListener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void removeEventListener(ProjectDataChangedListener listener) {
        listeners.remove(listener);
    }

    @Override
    public synchronized void fireProjectDataChangedEvent() {
        System.out.println("In fireProjectDataChangedEvent...");
        ProjectDataChangedEvent event = new ProjectDataChangedEvent(this);
        Iterator<ProjectDataChangedListener> i = listeners.iterator();
        while (i.hasNext()) {
            System.out.println("more listeners...");
            ((ProjectDataChangedListener) i.next()).projectDataChanged(event);
        }
    }

    public void shutdownDatabase() {
        dbex.closeConnection();
    }

}
