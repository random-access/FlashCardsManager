package org.random_access.flashcardsmanager_desktop.storage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

import org.random_access.flashcardsmanager_desktop.app.StartApp;
import org.random_access.flashcardsmanager_desktop.core.*;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.exc.InvalidLengthException;
import org.random_access.flashcardsmanager_desktop.gui.helpers.IProgressPresenter;
import org.random_access.flashcardsmanager_desktop.importExport.*;

public class OfflineDBExchanger implements IDBExchanger, XMLDBExchanger {

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
	private OfflineProjectsController ctl;

	public OfflineDBExchanger(String dbLocation, OfflineProjectsController ctl) throws ClassNotFoundException {
		this.dbLocation = dbLocation;
		this.ctl = ctl;
		Class.forName(driver); // check if driver is reachable
	}

	@Override
	// CREATE CONNECTION - Establish connection with database
	public void createConnection() throws SQLException {
		conn = DriverManager.getConnection(protocol + dbLocation + ";create=true");
		conn.setAutoCommit(false);
		if (StartApp.DEBUG) {
			if (conn != null) {
				System.out.println("Successfully created connection to: " + dbLocation);
			} else {
				System.out.println("Could't create connection to: " + dbLocation);
			}
		}
	}

	@Override
	// disconnect from database
	public void closeConnection() {
		try {
			conn.commit();
			conn.close();

			boolean gotSQLExc = false;
			try {
				DriverManager.getConnection("jdbc:derby:" + dbLocation + ";shutdown=true");
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
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(null, sqle);
		}
	}

	@Override
	// CREATE TABLES if they don't exist yet
	public void createTablesIfNotExisting() throws SQLException {
		Statement st = conn.createStatement();
		if (!DerbyTools.tableAlreadyExisting(projectsTable, conn)) {
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + projectsTable + " (PROJ_ID_PK INT PRIMARY KEY, " + "PROJ_TITLE VARCHAR ("
						+ maxShortString + ") NOT NULL, " + "NO_OF_STACKS INT NOT NULL)");
			st.execute("CREATE TABLE " + projectsTable + " (PROJ_ID_PK INT PRIMARY KEY, " + "PROJ_TITLE VARCHAR ("
					+ maxShortString + ") NOT NULL, " + "NO_OF_STACKS INT NOT NULL)");
			conn.commit();
		}
		if (!DerbyTools.tableAlreadyExisting(flashcardsTable, conn)) {
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, "
						+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_FL REFERENCES PROJECTS(PROJ_ID_PK), " + "STACK INT NOT NULL,"
						+ "QUESTION VARCHAR (" + maxVarcharLength + "), " + "ANSWER VARCHAR(" + maxVarcharLength + "), "
						+ "CUSTOM_WIDTH_Q INT, " + "CUSTOM_WIDTH_A INT)");
			st.execute("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, "
					+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_FL REFERENCES PROJECTS(PROJ_ID_PK), " + "STACK INT NOT NULL, "
					+ "QUESTION VARCHAR (" + maxVarcharLength + "), " + "ANSWER VARCHAR(" + maxVarcharLength + "), "
					+ "CUSTOM_WIDTH_Q INT, " + "CUSTOM_WIDTH_A INT)");
			conn.commit();
		}
		if (!DerbyTools.tableAlreadyExisting(labelsTable, conn)) {
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + labelsTable + " (LABEL_ID_PK INT PRIMARY KEY, "
						+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_LA REFERENCES PROJECTS(PROJ_ID_PK), " + "LABEL_NAME VARCHAR ("
						+ maxShortString + ") NOT NULL)");
			st.execute("CREATE TABLE " + labelsTable + " (LABEL_ID_PK INT PRIMARY KEY, "
					+ "PROJ_ID_FK INT CONSTRAINT PROJ_ID_FK_LA REFERENCES PROJECTS(PROJ_ID_PK), " + "LABEL_NAME VARCHAR ("
					+ maxShortString + ") NOT NULL)");
			conn.commit();
		}
		if (!DerbyTools.tableAlreadyExisting(labelsFlashcardsTable, conn)) {
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + labelsFlashcardsTable + " (LABELS_FLASHCARDS_ID_PK INT PRIMARY KEY, "
						+ "LABEL_ID_FK INT CONSTRAINT LABEL_ID_FK_LF REFERENCES LABELS(LABEL_ID_PK" + "), "
						+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_LF REFERENCES FLASHCARDS(CARD_ID_PK), "
						+ "UNIQUE(LABEL_ID_FK, CARD_ID_FK))");
			st.execute("CREATE TABLE " + labelsFlashcardsTable + " (LABELS_FLASHCARDS_ID_PK INT PRIMARY KEY, "
					+ "LABEL_ID_FK INT CONSTRAINT LABEL_ID_FK_LF REFERENCES LABELS(LABEL_ID_PK" + "), "
					+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_LF REFERENCES FLASHCARDS(CARD_ID_PK), "
					+ "UNIQUE(LABEL_ID_FK, CARD_ID_FK))");
			conn.commit();
		}
		if (!DerbyTools.tableAlreadyExisting(mediaTable, conn)) {
			if (StartApp.DEBUG)
				System.out.println("CREATE TABLE " + mediaTable + " (MEDIA_ID_PK INT PRIMARY KEY, "
						+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_ME REFERENCES FLASHCARDS(CARD_ID_PK), "
						+ "PATH_TO_MEDIA VARCHAR(" + maxShortString + ") NOT NULL, " + "PICTYPE CHAR NOT NULL)");
			st.execute("CREATE TABLE " + mediaTable + " (MEDIA_ID_PK INT PRIMARY KEY, "
					+ "CARD_ID_FK INT CONSTRAINT CARD_ID_FK_ME REFERENCES FLASHCARDS(CARD_ID_PK), " + "PATH_TO_MEDIA VARCHAR("
					+ maxShortString + ") NOT NULL, " + "PICTYPE CHAR NOT NULL)");
			conn.commit();
		}
		st.close();
	}

	/*********************************** PROJECT QUERIES *********************************************/

	@Override
	// ADD PROJECT: insert project into table
	public void addProject(LearningProject project) throws SQLException, InvalidLengthException {
		if (project.getTitle().length() > maxShortString / 5) {
			throw new InvalidLengthException();
		}
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("INSERT INTO " + projectsTable + " VALUES (" + project.getId() + ",'" + project.getTitle() + "', "
					+ project.getNumberOfStacks() + ")");
		st.execute("INSERT INTO " + projectsTable + " VALUES (" + project.getId() + ",'" + project.getTitle() + "', "
				+ project.getNumberOfStacks() + ")");
		conn.commit();
		st.close();
	}

	@Override
	// UPDATE PROJECT: update project
	public void updateProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("UPDATE " + projectsTable + " SET PROJ_TITLE = '" + project.getTitle() + "', NO_OF_STACKS = "
					+ project.getNumberOfStacks() + " WHERE PROJ_ID_PK = " + project.getId());
		st.execute("UPDATE " + projectsTable + " SET PROJ_TITLE = '" + project.getTitle() + "', NO_OF_STACKS = "
				+ project.getNumberOfStacks() + " WHERE PROJ_ID_PK = " + project.getId());
		conn.commit();
		st.close();
	}

	@Override
	public void deleteProject(LearningProject project) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK IN " + "(SELECT LABEL_ID_PK FROM "
					+ labelsTable + " WHERE PROJ_ID_FK = " + project.getId() + ")");
		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK IN " + "(SELECT LABEL_ID_PK FROM " + labelsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");

		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + labelsTable + " WHERE PROJ_ID_FK = " + project.getId());
		st.execute("DELETE FROM " + labelsTable + " WHERE PROJ_ID_FK = " + project.getId());

		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK IN " + "(SELECT CARD_ID_PK FROM "
					+ flashcardsTable + " WHERE PROJ_ID_FK = " + project.getId() + ")");
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK IN " + "(SELECT CARD_ID_PK FROM " + flashcardsTable
				+ " WHERE PROJ_ID_FK = " + project.getId() + ")");

		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + project.getId());
		st.execute("DELETE FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + project.getId());

		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + projectsTable + " WHERE Proj_ID_PK = " + project.getId());
		st.execute("DELETE FROM " + projectsTable + " WHERE Proj_ID_PK = " + project.getId());
		conn.commit();
	}

	@Override
	public ArrayList<LearningProject> getAllProjects() throws SQLException {
		ArrayList<LearningProject> projects = new ArrayList<LearningProject>();
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT * FROM " + projectsTable);
		st.execute("SELECT * FROM " + projectsTable);
		conn.commit();
		ResultSet res = st.getResultSet();
		while (res.next()) {
			LearningProject p = new LearningProject(ctl, res.getInt(1), res.getString(2), res.getInt(3));
			projects.add(p);
		}
		res.close();
		return projects;
	}

	@Override
	public int getMaxStack(LearningProject p) throws SQLException {
		int maxStack = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId()
					+ " ORDER BY STACK DESC");
		st.execute("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId() + " ORDER BY STACK DESC");
		conn.commit();
		ResultSet res = st.getResultSet();
		if (res.next()) {
			maxStack = res.getInt(1);
		}
		res.close();
		return maxStack;
	}

	@Override
	public int getMinStack(LearningProject p) throws SQLException {
		int minStack = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId()
					+ " ORDER BY STACK ASC");
		st.execute("SELECT STACK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId() + " ORDER BY STACK ASC");
		conn.commit();
		ResultSet res = st.getResultSet();
		if (res.next()) {
			minStack = res.getInt(1);
		}
		res.close();
		return minStack;
	}

	/************************************* LABEL QUERIES **********************************************/

	@Override
	public ArrayList<Label> getAllLabels(LearningProject p) throws SQLException {
		ArrayList<Label> labels = new ArrayList<Label>();
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT LABEL_ID_PK, LABEL_NAME FROM " + labelsTable + " WHERE PROJ_ID_FK = " + p.getId()
					+ "ORDER BY LABEL_NAME ASC");
		st.execute("SELECT LABEL_ID_PK, LABEL_NAME FROM " + labelsTable + " WHERE PROJ_ID_FK = " + p.getId()
				+ "ORDER BY LABEL_NAME ASC");
		conn.commit();
		ResultSet res = st.getResultSet();
		while (res.next()) {
			Label l = new Label(res.getInt(1), res.getString(2), p);
			labels.add(l);
		}
		res.close();
		return labels;
	}

	@Override
	public ArrayList<Label> getAllLabels(FlashCard f) throws SQLException {
		ArrayList<Label> labels = new ArrayList<Label>();
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT LABEL_ID_PK, LABEL_NAME FROM " + labelsTable + " INNER JOIN " + labelsFlashcardsTable
					+ " ON LABEL_ID_PK = LABEL_ID_FK AND CARD_ID_FK = " + f.getId());
		st.execute("SELECT LABEL_ID_PK, LABEL_NAME FROM " + labelsTable + " INNER JOIN " + labelsFlashcardsTable
				+ " ON LABEL_ID_PK = LABEL_ID_FK AND CARD_ID_FK = " + f.getId());
		conn.commit();
		ResultSet res = st.getResultSet();
		while (res.next()) {
			for (Label l : f.getProj().getLabels()) {
				if (l.getId() == res.getInt(1)) {
					labels.add(l);
				}
			}
		}
		res.close();
		return labels;
	}

	@Override
	public void addLabelToProject(Label l) throws SQLException, InvalidLengthException {
		if (l.getName().length() > maxShortString / 5) {
			throw new InvalidLengthException();
		}
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("INSERT INTO " + labelsTable + " VALUES (" + l.getId() + ", " + l.getProject().getId() + ", '"
					+ l.getName() + "')");
		st.execute("INSERT INTO " + labelsTable + " VALUES (" + l.getId() + ", " + l.getProject().getId() + ", '" + l.getName()
				+ "')");
		conn.commit();
		st.close();
	}

	@Override
	public void addLabelToFlashcard(Label l, FlashCard f) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("INSERT INTO " + labelsFlashcardsTable + " VALUES (" + nextId(TableType.LABELS_FLASHCARDS) + ", "
					+ l.getId() + ", " + f.getId() + ")");
		st.execute("INSERT INTO " + labelsFlashcardsTable + " VALUES (" + nextId(TableType.LABELS_FLASHCARDS) + ", " + l.getId()
				+ ", " + f.getId() + ")");
		conn.commit();
		st.close();
	}

	@Override
	public void updateLabelFromProject(Label l) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("UPDATE " + labelsTable + " SET LABEL_NAME = '" + l.getName() + "', PROJ_ID_PK = "
					+ l.getProject().getId() + " WHERE LABEL_ID_PK = " + l.getId());
		st.execute("UPDATE " + labelsTable + " SET LABEL_NAME = '" + l.getName() + "', PROJ_ID_FK = " + l.getProject().getId()
				+ " WHERE LABEL_ID_PK = " + l.getId());
		conn.commit();
		st.close();
	}

	@Override
	public void deleteLabelFromFlashCard(Label l, FlashCard f) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK = " + l.getId()
					+ " AND CARD_ID_FK = " + f.getId());
		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK = " + l.getId() + " AND CARD_ID_FK = "
				+ f.getId());
		conn.commit();
		st.close();
	}

	@Override
	public void deleteLabelFromProject(Label l) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK = " + l.getId());
		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE LABEL_ID_FK = " + l.getId());
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + labelsTable + " WHERE LABEL_ID_PK = " + l.getId());
		st.execute("DELETE FROM " + labelsTable + " WHERE LABEL_ID_PK = " + l.getId());
		conn.commit();
		st.close();
	}

	@Override
	public int getMaxStack(Label l) throws SQLException {
		int maxStack = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT STACK FROM FLASHCARDS INNER JOIN LABELS_FLASHCARDS ON CARD_ID_PK = CARD_ID_FK "
					+ "INNER JOIN LABELS ON LABEL_ID_FK = LABEL_ID_PK " + "AND LABEL_ID_FK = " + l.getId()
					+ " ORDER BY STACK DESC");
		st.execute("SELECT STACK FROM FLASHCARDS INNER JOIN LABELS_FLASHCARDS ON CARD_ID_PK = CARD_ID_FK "
				+ "INNER JOIN LABELS ON LABEL_ID_FK = LABEL_ID_PK " + "AND LABEL_ID_FK = " + l.getId() + " ORDER BY STACK DESC");
		conn.commit();
		ResultSet res = st.getResultSet();
		if (res.next()) {
			maxStack = res.getInt(1);
		}
		res.close();
		return maxStack;
	}

	@Override
	public int getMinStack(Label l) throws SQLException {
		int minStack = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT STACK FROM FLASHCARDS INNER JOIN LABELS_FLASHCARDS ON CARD_ID_PK = CARD_ID_FK "
					+ "INNER JOIN LABELS ON LABEL_ID_FK = LABEL_ID_PK " + "AND LABEL_ID_FK = " + l.getId()
					+ " ORDER BY STACK ASC");
		st.execute("SELECT STACK FROM FLASHCARDS INNER JOIN LABELS_FLASHCARDS ON CARD_ID_PK = CARD_ID_FK "
				+ "INNER JOIN LABELS ON LABEL_ID_FK = LABEL_ID_PK " + "AND LABEL_ID_FK = " + l.getId() + " ORDER BY STACK ASC");
		conn.commit();
		ResultSet res = st.getResultSet();
		if (res.next()) {
			minStack = res.getInt(1);
		}
		res.close();
		return minStack;
	}

	@Override
	public ArrayList<XMLLabelFlashcardRelation> getXMLLfRelations(FlashCard f) throws SQLException {
		ArrayList<XMLLabelFlashcardRelation> lfrelations = new ArrayList<XMLLabelFlashcardRelation>();
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			st.execute("SELECT * FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + f.getId());
		st.execute("SELECT * FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + f.getId());
		conn.commit();
		ResultSet res = st.getResultSet();
		while (res.next()) {
			XMLLabelFlashcardRelation lfrel = new XMLLabelFlashcardRelation();
			lfrel.setId(res.getInt(1));
			lfrel.setLabelId(res.getInt(2));
			lfrel.setCardId(res.getInt(3));
			lfrelations.add(lfrel);
		}
		res.close();
		return lfrelations;
	}

	/************************************* FLASHCARD QUERIES **********************************************/
	@Override
	// ADD FLASHCARD: insert flashcard into table
	public void addFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("INSERT INTO " + flashcardsTable + " VALUES (" + card.getId() + ", " + card.getProj().getId()
					+ ", " + card.getStack() + ", '" + card.getQuestion().replaceAll("\'", "&apos;") + "', '"
					+ card.getAnswer().replaceAll("\'", "&apos;") + "', " + card.getQuestionWidth() + "," + card.getAnswerWidth()
					+ ")");
		st.execute("INSERT INTO " + flashcardsTable + " VALUES (" + card.getId() + ", " + card.getProj().getId() + ", "
				+ card.getStack() + ", '" + card.getQuestion().replaceAll("\'", "&apos;") + "', '"
				+ card.getAnswer().replaceAll("\'", "&apos;") + "', " + card.getQuestionWidth() + "," + card.getAnswerWidth()
				+ ")");
		conn.commit();
		st.close();
		updatePathToPics(card);
	}

	@Override
	// UPDATE FLASHCARD: insert flashcard into table
	public void updateFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("UPDATE " + flashcardsTable + " SET PROJ_ID_FK = " + card.getProj().getId() + ", STACK = "
					+ card.getStack() + ", QUESTION = '" + card.getQuestion().replaceAll("\'", "&apos;") + "', ANSWER = '"
					+ card.getAnswer().replaceAll("\'", "&apos;") + "', CUSTOM_WIDTH_Q = " + card.getQuestionWidth()
					+ ", CUSTOM_WIDTH_A = " + card.getAnswerWidth() + " WHERE CARD_ID_PK = " + card.getId());
		st.execute("UPDATE " + flashcardsTable + " SET PROJ_ID_FK = " + card.getProj().getId() + ", STACK = " + card.getStack()
				+ ", QUESTION = '" + card.getQuestion().replaceAll("\'", "&apos;") + "', ANSWER = '"
				+ card.getAnswer().replaceAll("\'", "&apos;") + "', CUSTOM_WIDTH_Q = " + card.getQuestionWidth()
				+ ", CUSTOM_WIDTH_A = " + card.getAnswerWidth() + " WHERE CARD_ID_PK = " + card.getId());
		conn.commit();
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

	@Override
	public void deleteFlashcard(FlashCard card) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + card.getId());
		st.execute("DELETE FROM " + labelsFlashcardsTable + " WHERE CARD_ID_FK = " + card.getId());
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId());
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId());
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + flashcardsTable + " WHERE CARD_ID_PK = " + card.getId());
		st.execute("DELETE FROM " + flashcardsTable + " WHERE CARD_ID_PK = " + card.getId());
		conn.commit();
	}

	@Override
	public ArrayList<FlashCard> getAllCards(LearningProject proj, IProgressPresenter p) throws SQLException {
		ArrayList<FlashCard> cards = new ArrayList<FlashCard>();
		int noOfCards = this.countRows(proj);
		if (noOfCards == 0) {
			return cards;
		}
		if (p != null)
			p.changeProgress(Math.min(p.getProgress() + 100 / noOfCards, 100));
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT * FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + proj.getId());
		st.execute("SELECT * FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + proj.getId());
		conn.commit();
		ResultSet res = st.getResultSet();
		int cardCount = 0;
		while (res.next()) {
			FlashCard f = new FlashCard(res.getInt(1), proj, res.getInt(3), res.getString(4).replaceAll("&apos;", "\'"), res
					.getString(5).replaceAll("&apos;", "\'"), null, null, res.getInt(6), res.getInt(7));
			f.setPathToQuestionPic(getPathToPic(f, PicType.QUESTION));
			f.setPathToAnswerPic(getPathToPic(f, PicType.ANSWER));
			cards.add(f);
			if (p != null) {
				cardCount++;
				p.changeProgress((int) Math.min(((double) cardCount / noOfCards) * 100, 100));
			}
		}
		res.close();
		return cards;
	}

	@Override
	// GET PIC as XMLMedia
	public XMLMedia getPic(FlashCard card, PicType type) throws SQLException {
		XMLMedia media = new XMLMedia();
		if (StartApp.DEBUG)
			System.out.println("SELECT * FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId() + " AND PICTYPE = '"
					+ type.getShortForm() + "'");
		Statement st = conn.createStatement();
		st.executeQuery("SELECT * FROM " + mediaTable + " WHERE CARD_ID_FK = " + card.getId() + " AND PICTYPE = '"
				+ type.getShortForm() + "'");
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

	@Override
	// GET PATH TO PIC: get picture of flashcard from DB
	public String getPathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT PATH_TO_MEDIA FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId()
					+ " AND PICTYPE = '" + type.getShortForm() + "'");
		st.execute("SELECT PATH_TO_MEDIA FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '"
				+ type.getShortForm() + "'");
		conn.commit();

		ResultSet res = st.getResultSet();
		String path = null;
		if (res.next()) {
			path = res.getString(1);
		}
		res.close();
		return path;
	}

	@Override
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
		if (StartApp.DEBUG)
			System.out.println("INSERT INTO " + mediaTable + " VALUES (" + nextId(TableType.MEDIA) + ", " + f.getId() + ", '"
					+ newPath + "', '" + type.getShortForm() + "')");
		st.execute("INSERT INTO " + mediaTable + " VALUES (" + nextId(TableType.MEDIA) + ", " + f.getId() + ", '" + newPath
				+ "', '" + type.getShortForm() + "')");
		conn.commit();
		st.close();
	}

	@Override
	// DELETE PATH TO PIC
	public void deletePathToPic(FlashCard f, PicType type) throws SQLException {
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '"
					+ type.getShortForm() + "'");
		st.execute("DELETE FROM " + mediaTable + " WHERE CARD_ID_FK = " + f.getId() + " AND PICTYPE = '" + type.getShortForm()
				+ "'");
		conn.commit();
		st.close();
	}

	@Override
	// COUNT ROWS: returns the number of flashcards belonging to 1 project
	public int countRows(LearningProject p) throws SQLException {
		int count = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT COUNT (*) FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + p.getId());
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

	@Override
	// COUNT ROWS: returns the number of flashcards belonging to 1 project
	// and 1 stack
	public int countRows(LearningProject proj, int stack) throws SQLException {
		int count = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT COUNT (*) FROM " + flashcardsTable + " where STACK = " + stack + " AND PROJ_ID_FK = "
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

	@Override
	// COUNT ROWS: returns the number of flashcards belonging to 1 project
	// an 1 stack
	public int countRows(Label l) throws SQLException {
		int count = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT COUNT (*) FROM " + labelsFlashcardsTable + " where LABEL_ID_FK = " + l.getId());
		st.executeQuery("SELECT COUNT (*) FROM " + labelsFlashcardsTable + " where LABEL_ID_FK = " + l.getId());
		conn.commit();
		ResultSet rs = st.getResultSet();
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		st.close();
		return count;
	}

	@Override
	public int getCardNumberInProject(FlashCard f) throws SQLException {
		int number = 0;
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT CARD_ID_PK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + f.getProj().getId());
		st.execute("SELECT CARD_ID_PK FROM " + flashcardsTable + " WHERE PROJ_ID_FK = " + f.getProj().getId());
		// + " ORDER BY CARD_ID_PK ASC");
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

	/************************************ OTHER QUERIES ********************************************/

	@Override
	public int nextId(TableType type) throws SQLException {
		String table = getTableName(type);
		String primaryKey = getPrimaryKey(type);
		Statement st = conn.createStatement();
		if (StartApp.DEBUG)
			System.out.println("SELECT " + primaryKey + " FROM " + table + " ORDER BY " + primaryKey + " ASC");
		st.execute("SELECT " + primaryKey + " FROM " + table + " ORDER BY " + primaryKey + " ASC");
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

	private String getPrimaryKey(TableType type) {
		switch (type) {
		case PROJECTS:
			return "PROJ_ID_PK";
		case FLASHCARDS:
			return "CARD_ID_PK";
		case MEDIA:
			return "MEDIA_ID_PK";
		case LABELS:
			return "LABEL_ID_PK";
		case LABELS_FLASHCARDS:
			return "LABELS_FLASHCARDS_ID_PK";
		default:
			return "";
		}
	}

	private String getTableName(TableType type) {
		switch (type) {
		case PROJECTS:
			return projectsTable;
		case FLASHCARDS:
			return flashcardsTable;
		case MEDIA:
			return mediaTable;
		case LABELS:
			return labelsTable;
		case LABELS_FLASHCARDS:
			return labelsFlashcardsTable;
		default:
			return "";
		}
	}

}
