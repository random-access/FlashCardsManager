package core;

import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;
import importExport.ProjectExporter;
import importExport.ProjectImporter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import storage.DBExchanger;
import storage.MediaExchanger;

public class ProjectsController {

	private final DBExchanger dbex;
	private final MediaExchanger mex;
	private ArrayList<LearningProject> projects;
	private String pathToMediaFolder;

	public ProjectsController(String pathToDatabase, String pathToMediaFolder)
			throws ClassNotFoundException, SQLException {
		dbex = new DBExchanger(pathToDatabase, this);
		mex = new MediaExchanger(pathToMediaFolder);
		this.pathToMediaFolder = pathToMediaFolder;
		dbex.createConnection();
		dbex.createTablesIfNotExisting();
		loadProjects();
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

	public void importProjects(String pathToImport, IProgressPresenter p)
			throws XMLStreamException, IOException, SQLException,
			InvalidValueException, InvalidLengthException {
		ProjectImporter importer = new ProjectImporter(pathToImport,
				pathToMediaFolder, this, p);
		importer.doImport();
	}

	public void exportProject(ArrayList<LearningProject> projects,
			String pathToExport, IProgressPresenter p) throws SQLException,
			XMLStreamException, IOException {
		ProjectExporter exporter = new ProjectExporter(projects,
				pathToMediaFolder, pathToExport, p);
		exporter.doExport();
	}

}
