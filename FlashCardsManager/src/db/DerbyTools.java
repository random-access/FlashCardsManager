package db;

import java.sql.*;
import java.util.ArrayList;

import tools.*;

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
		System.out.print("Check if table exists already: ");
		if (res.next()) { // that means at least 1 result row
			System.out.println("Table " + res.getString(3) + " already exists!");
			res.close();
			return true;
		} // if we arrive here, the table doesn't exist yet
		res.close();
		System.out.println("Table " + name + " doesn't exist yet!");
		return false;
	}

	// flashcard
	public static boolean idAlreadyExisting(Connection conn, String table, String colName, int value) throws SQLException {
		Statement st = conn.createStatement();
		st.executeQuery("SELECT " + colName + " FROM " + table + " WHERE " + colName + " = " + value);
		conn.commit();
		System.out.println("Search for ID: SELECT ID FROM " + table + " WHERE ID = " + value);
		ResultSet res = st.getResultSet();
		if (res.next()) {
			System.out.println("ID " + value + " already exists");
			res.close();
			st.close();
			return true;
		} // if we arrive here, the row doesn't exist yet
		res.close();
		st.close();
		System.out.println("ID " + value + " doesn't exist yet!");
		return false;
	}
	
}
