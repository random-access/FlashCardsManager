package core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import db.DBExchanger;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import exc.InvalidValueException;
import gui.ChooseProjectsDialog;
import gui.MainWindow;

public class ProjectsManager {

	private final ArrayList<LearningProject> projects;
	private final DBExchanger<OrderedItem> dbex;
	private final String pathToDatabase;

	public ProjectsManager(String pathToDatabase) throws SQLException,
			ClassNotFoundException, EntryAlreadyThereException,
			EntryNotFoundException, IOException {
		this.pathToDatabase = pathToDatabase;
		// create Project Database, establish connection
		dbex = new DBExchanger<OrderedItem>(pathToDatabase);
		dbex.createConnection();
		if (dbex.tableAlreadyExisting()) {
			// read all data from project database
			projects = dbex.readAllData(this);
		} else {
			dbex.createTable();
			projects = new ArrayList<LearningProject>();
		}
	}

	public void addProject(LearningProject proj)
			throws EntryAlreadyThereException, SQLException {
		projects.add(proj);
		dbex.addRow(proj);
	}

	public void deleteProject(LearningProject proj)
			throws EntryNotFoundException, SQLException {
		projects.remove(proj);
		dbex.deleteTable(proj);
		dbex.deleteRow(proj);

	}

	public void updateProject(LearningProject proj)
			throws EntryNotFoundException, SQLException {
		dbex.updateRow(proj);
	}

	public void importProject(String pathToImport, MainWindow.ImportTask t)
			throws ClassNotFoundException, SQLException,
			EntryAlreadyThereException, EntryNotFoundException, IOException,
			InvalidValueException {
		final MainWindow.ImportTask task = t;
		final ProjectsManager importManager = new ProjectsManager(pathToImport);
		task.changeProgress(30);
		
		for (int i = 0; i < importManager.projects.size(); i++) {
			final int projCount = i+1;
			ArrayList<FlashCard> cards = importManager.dbex.readAllData(
					importManager.projects.get(i).getTableName(),
					importManager.projects.get(i));
			LearningProject currentProject = new LearningProject(this,
					importManager.projects.get(i), cards);
			dbex.insertFlashcardArray(cards, currentProject,
					importManager.dbex, importManager.projects.get(i));
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					task.changeProgress (Math.min(100, 30 + projCount * 70 / importManager.projects.size()));
				}	
			});
		}
		System.out.println("Successfully imported selected projects!");
	}

	public void exportProject(ArrayList<LearningProject> p,
			String pathToExport, ChooseProjectsDialog.ExportTask t) throws SQLException, EntryNotFoundException,
			IOException, ClassNotFoundException, EntryAlreadyThereException,
			InvalidValueException {
		final ArrayList<LearningProject> projs = p;
		final ChooseProjectsDialog.ExportTask task = t;
		ProjectsManager exportManager = new ProjectsManager(pathToExport);
		task.changeProgress(30);
		
		for (int i = 0; i < projs.size(); i++) {
			final int projCount = i+1;
			ArrayList<FlashCard> cards = dbex.readAllData(projs.get(i)
					.getTableName(), projs.get(i));
			LearningProject currentProject = new LearningProject(exportManager,
					projs.get(i).getTitle(), projs.get(i).getNumberOfStacks());
			exportManager.dbex.insertFlashcardArray(cards, currentProject,
					dbex, projs.get(i));
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					task.changeProgress(Math.min(100, 30 + projCount * 70 / projs.size()));
				}	
			});
		}
		System.out.println("sucessfully exported selected projects!");
	}

	// NEXT PROJECT ID - GETTER
	public int getNextProjectId() throws SQLException {
		int id = 1;
		while (true) {
			if (!dbex.idAlreadyExisting(id)) {
				return id;
			}
			id++;
		}
	}

	public String getPathToDatabase() {
		return pathToDatabase;
	}

	public ArrayList<LearningProject> getProjects() {
		return projects;
	}
}
