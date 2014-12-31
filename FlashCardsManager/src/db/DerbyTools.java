package db;

import java.sql.*;

public class DerbyTools {

	// prevent instantiation
	private DerbyTools() {
	}

	// TABLE ALREADY EXISTING:
	// check if flashcard table is already in database
	public static boolean tableAlreadyExisting(String name, Connection conn, boolean debug) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		conn.commit();
		ResultSet res = dbmd.getTables(null, "APP", name, null);
		// fetch tables with title <name>
		if (res.next()) { // that means at least 1 result row
			if (debug)System.out.println("Table " + res.getString(3) + " already exists!");
			res.close();
			return true;
		} // if we arrive here, the table doesn't exist yet
		res.close();
		if (debug) System.out.println("Table " + name + " doesn't exist yet!");
		return false;
	}


	
}
