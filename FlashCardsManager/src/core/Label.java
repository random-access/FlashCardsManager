package core;

public class Label {
    private int id;
    private String title;
    private LearningProject project;

    public Label(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Label(int id, String title, LearningProject project) {
        this.id = id;
        this.title = title;
        this.project = project;
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

    public String toString() {
        return this.title;
    }

    public LearningProject getProject() {
        return project;
    }

    public void setProject(LearningProject project) {
        this.project = project;
    }

}
