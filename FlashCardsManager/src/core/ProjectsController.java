package core;

import java.sql.SQLException;
import java.util.ArrayList;

import db.DBExchanger;

public class ProjectsController {

	private final DBExchanger<OrderedItem> dbex;

	private ArrayList<LearningProject> projects;

	public ProjectsController(String pathToDatabase) throws ClassNotFoundException, SQLException {
		dbex = new DBExchanger<OrderedItem>(pathToDatabase, this);
		dbex.createConnection();
		dbex.createTablesIfNotExisting();
	}
	
	public void loadProjects() throws SQLException {
		projects = dbex.getAllProjects();
	}

	public DBExchanger<OrderedItem> getDbex() {
		return dbex;
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
	
}
