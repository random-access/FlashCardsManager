package core;

import importExport.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import storage.*;
import events.ProjectDataChangedEvent;
import events.ProjectDataChangedListener;
import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

public class OfflineProjectsController implements IProjectsController {

	private final IDBExchanger dbex;
	private final IMediaExchanger mex;
	private ArrayList<LearningProject> projects;
	private String pathToMediaFolder;

	private ArrayList<ProjectDataChangedListener> listeners = new ArrayList<ProjectDataChangedListener>();

	public OfflineProjectsController(String pathToDatabase, String pathToMediaFolder) throws ClassNotFoundException, SQLException {
		dbex = new OfflineDBExchanger(pathToDatabase, this);
		mex = new OfflineMediaExchanger(pathToMediaFolder);
		this.pathToMediaFolder = pathToMediaFolder;
		dbex.createConnection();
		dbex.createTablesIfNotExisting();
		loadProjects();
	}

	@Override
	public void loadProjects() throws SQLException {
		projects = dbex.getAllProjects();
	}

	@Override
	public IDBExchanger getDbex() {
		return dbex;
	}

	@Override
	public IMediaExchanger getMex() {
		return mex;
	}

	@Override
	public void addProject(LearningProject p) {
		projects.add(p);
	}

	@Override
	public void removeProject(LearningProject p) {
		projects.remove(p);
	}

	@Override
	public ArrayList<LearningProject> getProjects() {
		return projects;
	}

	@Override
	public void disconnectFromDatabase() {
		dbex.closeConnection();
	}

	public void importProjects(String pathToImport, IProgressPresenter p) throws XMLStreamException, IOException, SQLException,
			InvalidValueException, InvalidLengthException {
		ProjectImporter importer = new ProjectImporter(pathToImport, pathToMediaFolder, this, p);
		importer.doImport();
	}

	public void importANKI(String pathToImport, IProgressPresenter p) throws XMLStreamException, IOException, SQLException,
			InvalidValueException, InvalidLengthException {
		ANKIImporter importer = new ANKIImporter("ANKIImport" + p.toString(), pathToImport, this, p);
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
		ProjectDataChangedEvent event = new ProjectDataChangedEvent(this);
		Iterator<ProjectDataChangedListener> i = listeners.iterator();
		while (i.hasNext()) {
			((ProjectDataChangedListener) i.next()).projectDataChanged(event);
		}
	}

}
