package org.random_access.flashcardsmanager_desktop.importExport;

import java.sql.SQLException;

import org.random_access.flashcardsmanager_desktop.core.Label;
import org.random_access.flashcardsmanager_desktop.core.LearningProject;

public class XMLLabel {

	private int id;
	private int projId;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProjId() {
		return projId;
	}

	public void setProjId(int projId) {
		this.projId = projId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Label toLabel(LearningProject proj) throws SQLException {
		return new Label(name, proj);
	}

	@Override
	public String toString() {
		return "XMLLabel [id=" + id + ", projId=" + projId + ", name=" + name + "]";
	}

}
