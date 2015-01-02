package storage;

import java.sql.*;

import app.StartApp;

public class DerbyTools {

	// prevent instantiation
	private DerbyTools() {
	}

	// TABLE ALREADY EXISTING:
	// check if flashcard table is already in database
	public static boolean tableAlreadyExisting(String name, Connection conn) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		conn.commit();
		ResultSet res = dbmd.getTables(null, "APP", name, null);
		// fetch tables with title <name>
		if (res.next()) { // that means at least 1 result row
			if (StartApp.DEBUG)System.out.println("Table " + res.getString(3) + " already exists!");
			res.close();
			return true;
		} // if we arrive here, the table doesn't exist yet
		res.close();
		if (StartApp.DEBUG) System.out.println("Table " + name + " doesn't exist yet!");
		return false;
	}


	
}
