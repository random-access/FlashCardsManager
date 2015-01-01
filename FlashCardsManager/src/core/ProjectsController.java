package core;

import gui.helpers.ExportTask;
import gui.helpers.ImportTask;

import java.sql.SQLException;
import java.util.ArrayList;

import storage.DBExchanger;
import storage.MediaExchanger;

public class ProjectsController {
	
	private final DBExchanger<OrderedItem> dbex;
	private final MediaExchanger mex;
	private ArrayList<LearningProject> projects;

	public ProjectsController(String pathToDatabase, String pathToMediaFolder, boolean debug) throws ClassNotFoundException, SQLException {
		dbex = new DBExchanger<OrderedItem>(pathToDatabase, this, debug);
		mex = new MediaExchanger(pathToMediaFolder, debug);
		dbex.createConnection();
		dbex.createTablesIfNotExisting();
	}
	
	public void loadProjects() throws SQLException {
		projects = dbex.getAllProjects();
	}

	public DBExchanger<OrderedItem> getDbex() {
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

	public void importProjects(String pathToFiles, ImportTask task) {
		// TODO
	}

	public void exportProject(ArrayList<LearningProject> projects2, String pathToExport, ExportTask exportTask) {
		// TODO Auto-generated method stub
		
	}
	
}
