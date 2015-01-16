package core;

import java.sql.SQLException;

import storage.DBExchanger;
import storage.TableType;
import exc.InvalidLengthException;

public class Label implements IHasStatus {
    private DBExchanger dbex; // TODO final

    private int id;
    private String title;
    private LearningProject project;

    // Add new label
    public Label(String title, LearningProject project) throws SQLException {
        dbex = project.getDBEX();
        this.id = dbex.nextId(TableType.LABELS);
        this.title = title;
        this.project = project;
    }

    // Restore a label from database
    public Label(int id, String title, LearningProject project) {
        dbex = project.getDBEX();
        this.id = id;
        this.title = title;
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
        dbex.deleteLabelFromProject(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return this.title;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

}
