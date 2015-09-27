package org.random_access.flashcardsmanager_desktop.core.interfaces;

import java.sql.SQLException;

import org.random_access.flashcardsmanager_desktop.core.LearningProject;
import org.random_access.flashcardsmanager_desktop.core.Status;
import org.random_access.flashcardsmanager_desktop.exc.InvalidLengthException;

public interface ILabel {
    // OPS
    public void store() throws SQLException, InvalidLengthException;

    public void update() throws SQLException;

    public void delete() throws SQLException;

    // Getter & setter
    public int getId();

    public void setId(int id);

    public String getName();

    public void setName(String name);

    public LearningProject getProject();

    public void setProject(LearningProject project);

    public Status getStatus() throws SQLException;

    public int getNumberOfCards() throws SQLException;
}
