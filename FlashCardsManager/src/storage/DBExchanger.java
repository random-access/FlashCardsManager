package storage;

import gui.helpers.IProgressPresenter;
import importExport.XMLMedia;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import app.StartApp;
import core.*;

public class DBExchanger {
	// TODO Stream handling (try with resources...)

	private final String driver = "org.apache.derby.jdbc.EmbeddedDriver"; // db-driver
	private final String protocol = "jdbc:derby:"; // database protocol
	private final String dbLocation; // path to database

	private final String projectsTable = "PROJECTS";
	private final String flashcardsTable = "FLASHCARDS";
	private final String labelsTable = "LABELS";
	private final String labelsFlashcardsTable = "LABELS_FLASHCARDS";
	private final String mediaTable = "MEDIA";
	private final int maxShortString = 1000;
	private final int maxVarcharLength = 32672;

	private Connection conn; // connection
	private ProjectsController ctl;

	public DBExchanger(String dbLocation, ProjectsController ctl) throws ClassNotFoundException {
		this.dbLocation = dbLocation;
		this.ctl = ctl;
		Class.forName(driver); // check if driver is reachable
	}

	// CREATE CONNECTION - Establish connection with database
	public void createConnection() throws SQLException {
		conn = DriverManager.getConnection(protocol + dbLocation + ";create=true");
		conn.setAutoCommit(false);
		if (StartApp.DEBUG) {
			if (conn != null) {
				System.out.println("Successfully created connection to: " + dbLocation);
				// TODO: remove debug output
			} else {
				System.out.println("Could't create connection to: " + dbLocation);
			}
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
			if (StartApp.DEBUG) {
				if (!gotSQLExc) {
					System.out.println("Database did not shut down normally");
				} else {
					System.out.println("Database shut down normally");
				}
			}
			System.gc();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// CREATE TABLES if the don't exist yet
	public void createTablesIfNotExisting() throws SQLException {
		Statement st = conn.createStatement();
		if (!DerbyTools.tableAlreadyExisting(projectsTable, conn)) {
			st.execute("CREATE TABLE " + projectsTable + " (PROJ_ID_PK INT PRIMARY KEY, " + "PROJ_TITLE VARCHAR (" + maxShortString + ") NOT NULL, "
					+ "NO_OF_STACKS INT NOT NULL)");
			conn.commit();
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + projectsTable + " (PROJ_ID_PK INT PRIMARY KEY, "
						+ "PROJ_TITLE VARCHAR (" + maxShortString + ") NOT NULL, " + "NO_OF_STACKS INT NOT NULL)");
		}
		if (!DerbyTools.tableAlreadyExisting(flashcardsTable, conn)) {
			st.execute("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, "
					+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_FL REFERENCES PROJECTS(PROJ_ID_PK), " + "STACK INT NOT NULL,"
					+ "QUESTION VARCHAR (" + maxVarcharLength + "), " + "ANSWER VARCHAR(" + maxVarcharLength + "), " + "CUSTOM_WIDTH_Q INT, " + "CUSTOM_WIDTH_A INT)");
			conn.commit();
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, "
						+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_FL REFERENCES PROJECTS(PROJ_ID_PK), " + "STACK INT NOT NULL,"
						+ "QUESTION VARCHAR (" + maxVarcharLength + "), " + "ANSWER VARCHAR(" + maxVarcharLength + "), " + "CUSTOM_WIDTH_Q INT, "
						+ "CUSTOM_WIDTH_A INT)");
		}
		if (!DerbyTools.tableAlreadyExisting(labelsTable, conn)) {
			st.execute("CREATE TABLE " + labelsTable + " (LABEL_ID_PK INT PRIMARY KEY, "
					+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_LA REFERENCES PROJECTS(PROJ_ID_PK), "
					+ "LABEL_NAME VARCHAR (" + maxShortString + ") NOT NULL)");
			conn.commit();
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + labelsTable + " (LABEL_ID_PK INT PRIMARY KEY, "
						+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_LA REFERENCES PROJECTS(PROJ_ID_PK), "
						+ "LABEL_NAME VARCHAR (" + maxShortString + ") NOT NULL)");
		}
		if (!DerbyTools.tableAlreadyExisting(labelsFlashcardsTable, conn)) {
			st.execute("CREATE TABLE " + labelsFlashcardsTable + " (LABELS_FLASHCARDS_ID_PK INT PRIMARY KEY, "
					+ "LABEL_ID_FK INT CONSTRAINT LABEL_ID_FK_LF REFERENCES LABELS(LABEL_ID_PK" + "), "
					+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_LF REFERENCES FLASHCARDS(CARD_ID_PK), "
					+ "UNIQUE(LABEL_ID_FK, CARD_ID_FK))");
			conn.commit();
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + labelsFlashcardsTable + " (LABELS_FLASHCARDS_ID_PK INT PRIMARY KEY, "
						+ "LABEL_ID_FK INT CONSTRAINT LABEL_ID_FK_LF REFERENCES LABELS(LABEL_ID_PK" + "), "
						+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_LF REFERENCES FLASHCARDS(CARD_ID_PK), "
						+ "UNIQUE(LABEL_ID_FK, CARD_ID_FK))");
		}
		if (!DerbyTools.tableAlreadyExisting(mediaTable, conn)) {
			st.execute("CREATE TABLE " + mediaTable + " (MEDIA_ID_PK INT PRIMARY KEY, "
					+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_ME REFERENCES FLASHCARDS(CARD_ID_PK), "
					+ "PATH_TO_MEDIA VARCHAR(" + maxShortString + ") NOT NULL, " + "PICTYPE CHAR NOT NULL)");
			conn.commit();
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + mediaTable + " (MEDIA_ID_PK INT PRIMARY KEY, "
						+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_ME REFERENCES FLASHCARDS(CARD_ID_PK), "
						+ "PATH_TO_MEDIA VARCHAR(" + maxShortString + ") NOT NULL, " + "PICTYPE CHAR NOT NULL)");
		}
		st.close();
	}

	// ADD PROJECT: insert project into table
	public void addProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("INSERT INTO " + projectsTable + " VALUES (" + project.getId() + ",'" + project.getTitle() + "', "
				+ project.getNumberOfStacks() + ")");
		conn.commit();
		if (StartApp.DEBUG)
			System.out.println("INSERT INTO " + projectsTable + " VALUES (" + project.getId() + ",'" + project.getTitle() + "', "
					+ project.getNumberOfStacks() + ")");
		st.close();
	}

	// UPDATE PROJECT: update project
	public void updateProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("UPDATE " + projectsTable + " SET PROJ_TITLE = '" + project.getTitle() + "', NO_OF_STACKS = "
				+ project.getNumberOfStacks() + " WHERE PROJ_ID_PK = " + project.getId());
		conn.commit();
		if (StartApp.DEBUG)
			System.out.println("UPDATE " + projectsTable + " SET PROJ_TITLE = '" + project.getTitle() + "', NO_OF_STACKS = "
					+ project.getNumberOfStacks() + " WHERE PROJ_ID_PK = " + project.getId());
		st.close();
	}

	public void deleteProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		// TODO delete pics in file system

		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK IN " + "(SELECT LABEL_ID_PK FROM " + labelsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK IN " + "(SELECT LABEL_ID_PK FROM " + labelsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");
		
		st.execute("DELETE FROM " + labelsTable + " WHERE PROJ_ID_FK = " + project.getId());
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + labelsTable + " WHERE PROJ_ID_FK = " + project.getId());
		
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK IN " + "(SELECT CARD_ID_PK FROM " + flashcardsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK IN " + "(SELECT CARD_ID_PK FROM " + flashcardsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");
		
		st.execute("DELETE FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + project.getId());
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + project.getId());
		
		st.execute("DELETE FROM " + projectsTable + " WHERE Proj_ID_PK = " + project.getId());
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + projectsTable + " WHERE Proj_ID_PK = " + project.getId());
		
		conn.commit();
	}

	public ArrayList<LearningProject> getAllProjects() throws SQLException {
		ArrayList<LearningProject> projects = new ArrayList<LearningProject>();
		Statement st = conn.createStatement();
		st.execute("SELECT * FROM " + projectsTable);
		conn.commit();
		if (StartApp.DEBUG) System.out.println("SELECT * FROM " + projectsTable);
		ResultSet res = st.getResultSet();
		while (res.next()) {
			LearningProject p = new LearningProject(ctl, res.getInt(1), res.getString(2), res.getInt(3));
			projects.add(p);
		}
		res.close();
		return projects;
	}

	public int getMaxStack(LearningProject p) throws SQLException {
		int maxStack = 0;
		Statement st = conn.createStatement();
		st.execute("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId() + " ORDER BY STACK DESC");
		conn.commit();
		if (StartApp.DEBUG) System.out.println("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId() + " ORDER BY STACK DESC");
		ResultSet res = st.getResultSet();
		if (res.next()) {
			maxStack = res.getInt(1);
		}
		res.close();
		return maxStack;
	}

	// ADD ROW: insert flashcard into table
	public void addFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("INSERT INTO " + flashcardsTable + " VALUES (" + card.getId() + ", " + card.getProj().getId() + ", "
				+ card.getStack() + ", '" + card.getQuestion() + "', '" + card.getAnswer() + "', " + card.getQuestionWidth()
				+ "," + card.getAnswerWidth() + ")");
		conn.commit();
		if (StartApp.DEBUG) System.out.println("INSERT INTO " + flashcardsTable + " VALUES (" + card.getId() + ", " + card.getProj().getId() + ", "
				+ card.getStack() + ", '" + card.getQuestion() + "', '" + card.getAnswer() + "', " + card.getQuestionWidth()
				+ "," + card.getAnswerWidth() + ")");
		st.close();
		updatePathToPics(card);
	}

	public void updateFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("UPDATE " + flashcardsTable + " SET PROJ_ID_FK = " + card.getProj().getId() + ", STACK = " + card.getStack()
				+ ", QUESTION = '" + card.getQuestion() + "', ANSWER = '" + card.getAnswer() + "', CUSTOM_WIDTH_Q = "
				+ card.getQuestionWidth() + ", CUSTOM_WIDTH_A = " + card.getAnswerWidth() + " WHERE CARD_ID_PK = " + card.getId());
		conn.commit();
		if (StartApp.DEBUG) System.out.println("UPDATE " + flashcardsTable + " SET PROJ_ID_FK = " + card.getProj().getId() + ", STACK = " + card.getStack()
				+ ", QUESTION = '" + card.getQuestion() + "', ANSWER = '" + card.getAnswer() + "', CUSTOM_WIDTH_Q = "
				+ card.getQuestionWidth() + ", CUSTOM_WIDTH_A = " + card.getAnswerWidth() + " WHERE CARD_ID_PK = " + card.getId());
		st.close();
		updatePathToPics(card);
	}

	private void updatePathToPics(FlashCard card) throws SQLException {
		if (card.getPathToQuestionPic() == null) {
			deletePathToPic(card, PicType.QUESTION);
		} else {
			setPathToPic(card, PicType.QUESTION);
		}
		if (card.getPathToAnswerPic() == null) {
			deletePathToPic(card, PicType.ANSWER);
		} else {
			setPathToPic(card, PicType.ANSWER);
		}
	}

	public void deleteFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + card.getId());
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + card.getId());
		
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId());
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId());
		
		st.execute("DELETE FROM " + flashcardsTable + " WHERE CARD_ID_PK = " + card.getId());
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + flashcardsTable + " WHERE CARD_ID_PK = " + card.getId());
		conn.commit();
	}

	public ArrayList<FlashCard> getAllCards(LearningProject proj, IProgressPresenter p) throws SQLException {
		int noOfCards = this.countRows(proj);
		p.changeProgress(Math.min(p.getProgress() + 100/noOfCards, 100));
		ArrayList<FlashCard> cards = new ArrayList<FlashCard>();
		Statement st = conn.createStatement();
		st.execute("SELECT * FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + proj.getId());
		conn.commit();
		if (StartApp.DEBUG) System.out.println("SELECT * FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + proj.getId());
		ResultSet res = st.getResultSet();
		while (res.next()) {
			FlashCard f = new FlashCard(res.getInt(1), proj, res.getInt(3), res.getString(4), res.getString(5), null, null,
					res.getInt(6), res.getInt(7));
			f.setPathToQuestionPic(getPathToPic(f, PicType.QUESTION));
			f.setPathToAnswerPic(getPathToPic(f, PicType.ANSWER));
			cards.add(f);
			p.changeProgress(Math.min(p.getProgress() + 100/noOfCards, 100));
		}
		res.close();
		return cards;
	}
	
	// GET PIC as XMLMedia
	public XMLMedia getPic(FlashCard card, PicType type) throws SQLException {
	   XMLMedia media = new XMLMedia();
	   Statement st = conn.createStatement();
	   st.executeQuery("SELECT * FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId() 
	         + " AND PICTYPE = '"+ type.getShortForm() + "'");
	   ResultSet res = st.getResultSet();

	   if (res.next()) {
	      media.setMediaId(res.getInt(1));
	      media.setCardId(res.getInt(2));
	      media.setPathToMedia(new File(res.getString(3)).getName());
	      media.setPicType(type.getShortForm());
	      return media;
	   }
	   return null;
	}
	

	// GET PATH TO PIC: get picture of flashcard from DB
	public String getPathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SELECT PATH_TO_MEDIA FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '"
				+ type.getShortForm() + "'");
		conn.commit();
		if (StartApp.DEBUG) System.out.println("SELECT PATH_TO_MEDIA FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '"
				+ type.getShortForm() + "'");
		ResultSet res = st.getResultSet();
		String path = null;
		if (res.next()) {
			path = res.getString(1);
		}
		res.close();
		return path;
	}

	// SET PATH TO PIC
	public void setPathToPic(FlashCard f, PicType type) throws SQLException {
		deletePathToPic(f, type);
		String newPath = null;
		Statement st = conn.createStatement();
		switch (type) {
		case QUESTION:
			newPath = f.getPathToQuestionPic();
			break;
		case ANSWER:
			newPath = f.getPathToAnswerPic();
			break;
		}
		if (StartApp.DEBUG) System.out.println("INSERT INTO " + mediaTable + " VALUES (" + nextMediaId() + ", " + f.getId() + ", '" + newPath
				+ "', '" + type.getShortForm() + "')");
		st.execute("INSERT INTO " + mediaTable + " VALUES (" + nextMediaId() + ", " + f.getId() + ", '" + newPath + "', '"
				+ type.getShortForm() + "')");
		conn.commit();
		st.close();
	}

	// DELETE PATH TO PIC
	public void deletePathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG) System.out.println("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '"
				+ type.getShortForm() + "'");
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '" + type.getShortForm()
				+ "'");
		conn.commit();
		st.close();
	}

	// COUNT ROWS: returns the number of rows in a table
	public int countRows(LearningProject p) throws SQLException {
		int count = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG) System.out.println("SELECT COUNT (*) FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId());
		st.executeQuery("SELECT COUNT (*) FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId());
		conn.commit();
		ResultSet rs = st.getResultSet();
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		return count;
	}

	public int countRows(LearningProject proj, int stack) throws SQLException {
		int count = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG) System.out.println("SELECT COUNT (*) FROM " + flashcardsTable + " where STACK = " + stack + " AND PROJ_ID_FK = "
				+ proj.getId());
		st.executeQuery("SELECT COUNT (*) FROM " + flashcardsTable + " where STACK = " + stack + " AND PROJ_ID_FK = "
				+ proj.getId());
		conn.commit();
		ResultSet rs = st.getResultSet();
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		return count;
	}
	
	public int getCardNumberInProject(FlashCard f) throws SQLException {
		int number = 0;
		Statement st = conn.createStatement();
		st.execute("SELECT CARD_ID_PK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " 
				+ f.getProj().getId() + " ORDER BY CARD_ID_PK ASC");
		ResultSet res = st.getResultSet();
		while (res.next()) {
			number++;
			int id = res.getInt(1);
			if (id == f.getId()) {
				return number;
			}
		}
		return number;
	}

	private int nextMediaId() throws SQLException {
		int count = 1;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG) System.out.println("SELECT MEDIA_ID_PK FROM " + mediaTable + " ORDER BY MEDIA_ID_PK ASC");
		st.execute("SELECT MEDIA_ID_PK FROM " + mediaTable + " ORDER BY MEDIA_ID_PK ASC");
		ResultSet res = st.getResultSet();
		while (res.next()) {
			int id = res.getInt(1);
			if (id != count) {
				return count;
			}
			count++;
		}
		return count;
	}

	public int nextFlashcardId() throws SQLException {
		int count = 1;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG) System.out.println("SELECT CARD_ID_PK FROM " + flashcardsTable + " ORDER BY CARD_ID_PK ASC");
		st.execute("SELECT CARD_ID_PK FROM " + flashcardsTable + " ORDER BY CARD_ID_PK ASC");
		ResultSet res = st.getResultSet();
		while (res.next()) {
			int id = res.getInt(1);
			if (id != count) {
				return count;
			}
			count++;
		}
		return count;
	}

	public int nextProjectId() throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG) System.out.println("SELECT PROJ_ID_PK FROM " + projectsTable + " ORDER BY PROJ_ID_PK ASC");
		st.execute("SELECT PROJ_ID_PK FROM " + projectsTable + " ORDER BY PROJ_ID_PK ASC");
		ResultSet res = st.getResultSet();
		int count = 1;
		while (res.next()) {
			int id = res.getInt(1);
			if (id != count) {
				return count;
			}
			count++;
		}
		return count;
	}

}
