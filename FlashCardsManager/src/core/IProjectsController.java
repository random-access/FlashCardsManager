package core;

import java.sql.SQLException;
import java.util.ArrayList;

import storage.IDBExchanger;
import storage.IMediaExchanger;
import events.ProjectChangedSource;

public interface IProjectsController extends ProjectChangedSource {

	public void loadProjects() throws SQLException;

	public IDBExchanger getDbex();

	public IMediaExchanger getMex();

	public void addProject(LearningProject p);

	public void removeProject(LearningProject p);

	public ArrayList<LearningProject> getProjects();

	public void disconnectFromDatabase();

}
