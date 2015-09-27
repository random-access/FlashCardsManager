package org.random_access.flashcardsmanager_desktop.core;

import java.sql.SQLException;
import java.util.ArrayList;

import org.random_access.flashcardsmanager_desktop.events.ProjectChangedSource;
import org.random_access.flashcardsmanager_desktop.storage.IDBExchanger;
import org.random_access.flashcardsmanager_desktop.storage.IMediaExchanger;

public interface IProjectsController extends ProjectChangedSource {

	public void loadProjects() throws SQLException;

	public IDBExchanger getDbex();

	public IMediaExchanger getMex();

	public void addProject(LearningProject p);

	public void removeProject(LearningProject p);

	public ArrayList<LearningProject> getProjects();

	public void disconnectFromDatabase();

}
