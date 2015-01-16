package core;

import java.sql.SQLException;

public interface IHasStatus {

    public Status getStatus() throws SQLException;

}
