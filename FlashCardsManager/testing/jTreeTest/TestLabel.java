package jTreeTest;

import core.IHasStatus;
import core.Status;

public class TestLabel implements IHasStatus {
    private int id;
    private String title;
    private Status status;

    public TestLabel(int id, String title, Status status) {
        super();
        this.id = id;
        this.title = title;
        this.status = status;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return title;
    }

}
