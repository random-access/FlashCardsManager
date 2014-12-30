package db;

import java.sql.*;
import java.util.ArrayList;

import core.*;

public class DBExchanger<T extends OrderedItem> {
	// TODO Stream handling (try with resources...)

	private final String driver = "org.apache.derby.jdbc.EmbeddedDriver"; // db-driver
	private final String protocol = "jdbc:derby:"; // database protocol
	private final String dbLocation; // path to database

	private final String projectsTable = "PROJECTS";
	private final String flashcardsTable = "FLASHCARDS";
	private final String labelsTable = "LABELS";
	private final String labelsFlashcardsTable = "LABELS_FLASHCARDS";
	private final String mediaTable = "MEDIA";

	private Connection conn; // connection
	private ProjectsController ctl;

	public DBExchanger(String dbLocation, ProjectsController ctl) throws ClassNotFoundException {
		this.dbLocation = dbLocation;
		this.ctl = ctl;
		Class.forName(driver); // check if driver is reachable
		System.out.println("Created DBExchanger"); // TODO: remove debug output
	}

	// CREATE CONNECTION - Establish connection with database
	public void createConnection() throws SQLException {
		conn = DriverManager.getConnection(protocol + dbLocation + ";create=true");
		conn.setAutoCommit(false);
		if (conn != null) {
			System.out.println("Successfully created Connection to: " + dbLocation);
			// TODO: remove debug output
		}
	}

	// disconnect from database
	public void closeConnection(String pathToDatabase) {
		try {
			conn.commit();
			conn.close();

			boolean gotSQLExc = false;
			try {
				DriverManager.getConnection("jdbc:derby:" + pathToDatabase + ";shutdown=true");
			} catch (SQLException se) {
				if (se.getSQLState().equals("08006")) {
					gotSQLExc = true;
				}
			}
			if (!gotSQLExc) {
				System.out.println("Database did not shut down normally");
			} else {
				System.out.println("Database shut down normally");
			}
			System.gc();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// CREATE TABLES if the don't exist yet
	public void createTablesIfNotExisting() throws SQLException {
		System.out.print("Tabellen werden erstellt ...\n");
		Statement st = conn.createStatement();
		if (!DerbyTools.tableAlreadyExisting(projectsTable, conn)) {
			st.execute("CREATE TABLE " + projectsTable + " (PROJ_ID_PK INT PRIMARY KEY, " + "PROJ_TITLE VARCHAR (100) NOT NULL, "
					+ "NO_OF_STACKS INT NOT NULL)");
			conn.commit();
			System.out.println("--- Projekttabelle erstellt ...\n");
		}
		if (!DerbyTools.tableAlreadyExisting(flashcardsTable, conn)) {
			st.execute("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, "
					+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_FL REFERENCES PROJECTS(PROJ_ID_PK), " + "STACK INT NOT NULL,"
					+ "QUESTION VARCHAR (32672), " + "ANSWER VARCHAR(32672), " + "CUSTOM_WIDTH_Q INT, " + "CUSTOM_WIDTH_A INT)");
			conn.commit();
			System.out.println("--- Lernkartentabelle erstellt ...\n");
		}
		if (!DerbyTools.tableAlreadyExisting(labelsTable, conn)) {
			st.execute("CREATE TABLE " + labelsTable + " (LABEL_ID_PK INT PRIMARY KEY, "
					+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_LA REFERENCES PROJECTS(PROJ_ID_PK), "
					+ "LABEL_NAME VARCHAR (100) NOT NULL)");
			conn.commit();
			System.out.println("--- Label-Tabelle erstellt ... \n");
		}
		if (!DerbyTools.tableAlreadyExisting(labelsFlashcardsTable, conn)) {
			st.execute("CREATE TABLE " + labelsFlashcardsTable + " (LABELS_FLASHCARDS_ID_PK INT PRIMARY KEY, "
					+ "LABEL_ID_FK INT CONSTRAINT LABEL_ID_FK_LF REFERENCES LABELS(LABEL_ID_PK" + "), "
					+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_LF REFERENCES FLASHCARDS(CARD_ID_PK), "
					+ "UNIQUE(LABEL_ID_FK, CARD_ID_FK))");
			conn.commit();
			System.out.println("--- Zuordnungstabelle Label-Lernkarten erstellt ... \n");
		}
		if (!DerbyTools.tableAlreadyExisting(mediaTable, conn)) {
			st.execute("CREATE TABLE " + mediaTable + " (MEDIA_ID_PK INT PRIMARY KEY, "
					+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_ME REFERENCES FLASHCARDS(CARD_ID_PK), "
					+ "PATH_TO_MEDIA VARCHAR(100) NOT NULL, " + "PICTYPE CHAR NOT NULL)");
			conn.commit();
			System.out.println("--- Medientabelle erstellt ... \n");
		}
		st.close();
		System.out.println("...fertig!");
	}

	// ADD PROJECT: insert project into table
	public void addProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("INSERT INTO " + projectsTable + " VALUES (" + project.getId() + ",'" + project.getTitle() + "', "
				+ project.getNumberOfStacks() + ")");
		conn.commit();
		st.close();
		System.out.println("successfully added project " + project.getTitle());
	}

	// UPDATE PROJECT: update project
	public void updateProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("UPDATE " + projectsTable + " SET PROJ_TITLE = '" + project.getTitle() + "', NO_OF_STACKS = "
				+ project.getNumberOfStacks() + " WHERE PROJ_ID_PK = " + project.getId());
		conn.commit();
		st.close();
	}

	public void deleteProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		// TODO delete pics in file system

		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK IN " + "(SELECT LABEL_ID_PK FROM " + labelsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");
		st.execute("DELETE FROM " + labelsTable + " WHERE PROJ_ID_FK = " + project.getId());
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK IN " + "(SELECT CARD_ID_PK FROM " + flashcardsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");
		st.execute("DELETE FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + project.getId());
		st.execute("DELETE FROM " + projectsTable + " WHERE Proj_ID_PK = " + project.getId());
		conn.commit();
	}

	public ArrayList<LearningProject> getAllProjects() throws SQLException {
		ArrayList<LearningProject> projects = new ArrayList<LearningProject>();
		Statement st = conn.createStatement();
		st.execute("SELECT * FROM " + projectsTable);
		ResultSet res = st.getResultSet();
		while (res.next()) {
			LearningProject p = new LearningProject(ctl, res.getInt(1), res.getString(2), res.getInt(3));
			projects.add(p);
		}
		return projects;
	}

	public int getMaxStack(LearningProject p) throws SQLException {
		int maxStack = 0;
		Statement st = conn.createStatement();
		st.execute("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId() + "ORDER BY STACK DESC");
		ResultSet res = st.getResultSet();
		if (res.next()) {
			maxStack = res.getInt(1);
		}
		return maxStack;
	}

	// ADD ROW: insert flashcard into table
	public void addFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("INSERT INTO " + flashcardsTable + " VALUES (" + card.getId() + ", " + card.getProj().getId() + ", "
				+ card.getStack() + ", '" + card.getQuestion() + "', '" + card.getAnswer() + "', " + card.getQuestionWidth()
				+ "," + card.getAnswerWidth() + ")");
		conn.commit();
		setPathToPic(card, PicType.QUESTION);
		setPathToPic(card, PicType.ANSWER);
		st.close();
		System.out.println("successfully added flashcard " + card.getId());
	}

	public void updateFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("UPDATE " + flashcardsTable + " SET PROJ_ID_FK = " + card.getProj().getId() + ", STACK = " + card.getStack()
				+ ", QUESTION = '" + card.getQuestion() + "', ANSWER = '" + card.getAnswer() + "', CUSTOM_WIDTH_Q = "
				+ card.getQuestionWidth() + ", CUSTOM_WIDTH_A = " + card.getAnswerWidth() + " WHERE CARD_ID_PK = " + card.getId());
		conn.commit();
		st.close();
	}

	public void deleteFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + card.getId());
		conn.commit();
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId());
		conn.commit();
		st.execute("DELETE FROM " + flashcardsTable + " WHERE CARD_ID_PK = " + card.getId());
		conn.commit();
	}

	public ArrayList<FlashCard> getAllCards(LearningProject p) throws SQLException {
		ArrayList<FlashCard> cards = new ArrayList<FlashCard>();
		Statement st = conn.createStatement();
		st.execute("SELECT * FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId());
		ResultSet res = st.getResultSet();
		while (res.next()) {
			FlashCard f = new FlashCard(res.getInt(1), p, res.getInt(3), res.getString(4), res.getString(5), null, null,
					res.getInt(6), res.getInt(7));
			f.setPathToQuestionPic(getPathToPic(f, PicType.QUESTION));
			f.setPathToAnswerPic(getPathToPic(f, PicType.ANSWER));
			cards.add(f);
		}
		return cards;
	}

	// GET PATH TO PIC: get picture of flashcard from DB
	public String getPathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SELECT PATH_TO_MEDIA FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '"
				+ type.getShortForm() + "'");
		ResultSet res = st.getResultSet();
		if (res.next()) {
			return res.getString(1);
		}
		return null;
	}

	// SET PATH TO PIC
	public void setPathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("INSERT INTO " + mediaTable + " VALUES (" + nextMediaId() + ", " + f.getId() + ", '"
				+ f.getPathToQuestionPic() + "', '" + type.getShortForm() + "'");
		conn.commit();
		st.close();
	}

	// DELETE PATH TO PIC
	public void deletePathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '" + type.getShortForm()
				+ "'");
		conn.commit();
		st.close();
	}

	// COUNT ROWS: returns the number of rows in a table
	public int countRows(String table) throws SQLException {
		Statement st = conn.createStatement();
		st.executeQuery("SELECT COUNT (*) FROM " + table);
		conn.commit();
		ResultSet rs = st.getResultSet();
		rs.next();
		int i = rs.getInt(1);
		rs.close();
		st.close();
		return i;
	}

	public int countRows(LearningProject proj, int stack) throws SQLException {
		Statement st = conn.createStatement();
		int i = 0;
		st.executeQuery("SELECT COUNT (*) FROM " + flashcardsTable + " where STACK = " + stack + " AND PROJ_ID_FK = "
				+ proj.getId());
		conn.commit();
		ResultSet rs = st.getResultSet();
		if (rs.next()) {
			i = rs.getInt(1);
		}
		rs.close();
		st.close();
		return i;
	}

	private int nextMediaId() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SELECT MEDIA_ID_PK FROM " + mediaTable + " ORDER BY CARD_ID_PK ASC");
		ResultSet res = st.getResultSet();
		int i = 1;
		while (res.next()) {
			int id = res.getInt(1);
			if (id != i) {
				return i;
			}
			i++;
		}
		return i;
	}

	public int nextFlashcardId() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SELECT CARD_ID_PK FROM " + flashcardsTable + " ORDER BY CARD_ID_PK ASC");
		ResultSet res = st.getResultSet();
		int i = 1;
		while (res.next()) {
			int id = res.getInt(1);
			if (id != i) {
				return i;
			}
			i++;
		}
		return i;
	}

	public int nextProjectId() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SELECT PROJ_ID_PK FROM " + projectsTable + " ORDER BY PROJ_ID_PK ASC");
		ResultSet res = st.getResultSet();
		int i = 1;
		while (res.next()) {
			int id = res.getInt(1);
			if (id != i) {
				return i;
			}
			i++;
		}
		return i;
	}

}
