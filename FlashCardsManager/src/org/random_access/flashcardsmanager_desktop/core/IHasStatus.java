package org.random_access.flashcardsmanager_desktop.core;

import java.sql.SQLException;

public interface IHasStatus {

    public Status getStatus() throws SQLException;

}
