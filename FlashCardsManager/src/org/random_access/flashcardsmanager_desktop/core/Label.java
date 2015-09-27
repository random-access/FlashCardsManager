package org.random_access.flashcardsmanager_desktop.core;

import java.sql.SQLException;

import org.random_access.flashcardsmanager_desktop.app.StartApp;
import org.random_access.flashcardsmanager_desktop.exc.InvalidLengthException;
import org.random_access.flashcardsmanager_desktop.importExport.XMLLabel;
import org.random_access.flashcardsmanager_desktop.storage.IDBExchanger;
import org.random_access.flashcardsmanager_desktop.storage.TableType;

public class Label implements IHasStatus {
	private final IDBExchanger dbex;

	private int id;
	private String name;
	private LearningProject project;

	// Add new label
	public Label(String name, LearningProject project) throws SQLException {
		dbex = project.getDBEX();
		this.id = dbex.nextId(TableType.LABELS);
		this.name = name;
		this.project = project;
	}

	// Restore a label from database
	public Label(int id, String name, LearningProject project) {
		dbex = project.getDBEX();
		this.id = id;
		this.name = name;
		this.project = project;
	}

	public void store() throws SQLException, InvalidLengthException {
		project.addLabel(this);
		dbex.addLabelToProject(this);
	}

	public void update() throws SQLException {
		dbex.updateLabelFromProject(this);
	}

	public void delete() throws SQLException {
		project.removeLabel(this);
		for (int i = project.getAllCards().size() - 1; i >= 0; i--) {
			if (project.getAllCards().get(i).getLabels().contains(this)) {
				project.getAllCards().get(i).getLabels().remove(this);
			}
		}
		dbex.deleteLabelFromProject(this);
	}

	public XMLLabel toXMLLabel() {
		XMLLabel xmlLabel = new XMLLabel();
		xmlLabel.setId(id);
		xmlLabel.setProjId(project.getId());
		xmlLabel.setName(name);
		return xmlLabel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LearningProject getProject() {
		return project;
	}

	public void setProject(LearningProject project) {
		this.project = project;
	}

	public Status getStatus() throws SQLException {
		int maxStack = dbex.getMaxStack(this);
		int minStack = dbex.getMinStack(this);
		Status s;
		if (maxStack == 1 || maxStack == 0) {
			s = Status.RED;
		} else if (maxStack == project.getNumberOfStacks() && maxStack == minStack) {
			s = Status.GREEN;
		} else {
			s = Status.YELLOW;
		}
		return s;
	}

	public String toString() {
		try {
			return this.name + " (" + this.getNumberOfCards() + ")";
		} catch (SQLException e) {
			if (StartApp.DEBUG)
				e.printStackTrace();
			return this.name;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Label other = (Label) obj;
		if (id != other.id)
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public int getNumberOfCards() throws SQLException {
		return dbex.countRows(this);
	}

}
