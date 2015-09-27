package org.random_access.flashcardsmanager_desktop.importExport;

import java.sql.SQLException;

import org.random_access.flashcardsmanager_desktop.core.LearningProject;
import org.random_access.flashcardsmanager_desktop.core.OfflineProjectsController;
import org.random_access.flashcardsmanager_desktop.exc.InvalidValueException;

public class XMLLearningProject {

    private int projId;
    private String projTitle;
    private int noOfStacks;

    public int getProjId() {
        return projId;
    }

    public void setProjId(int projId) {
        this.projId = projId;
    }

    public String getProjTitle() {
        return projTitle;
    }

    public void setProjTitle(String projTitle) {
        this.projTitle = projTitle;
    }

    public int getNoOfStacks() {
        return noOfStacks;
    }

    public void setNoOfStacks(int noOfStacks) {
        this.noOfStacks = noOfStacks;
    }

    public LearningProject toLearningProject(OfflineProjectsController ctl) throws SQLException, InvalidValueException {
        return new LearningProject(ctl, projTitle, noOfStacks);
    }

    @Override
    public String toString() {
        return "XMLLearningProject [projId=" + projId + ", projTitle=" + projTitle + ", noOfStacks=" + noOfStacks + "]";
    }
}
