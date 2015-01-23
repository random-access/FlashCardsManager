package storage;

import java.sql.*;

import app.StartApp;
import core.ProjectsController;

public class MySQLDBExchanger extends DerbyDBExchanger {

    private String user;
    private String pwd;

    public MySQLDBExchanger(String dbLocation, String user, String pwd, ProjectsController ctl) throws ClassNotFoundException {
        super(dbLocation, ctl);
        this.user = user;
        this.pwd = pwd;
    }

    protected String getDriver() {
        return "com.mysql.jdbc.Driver";
    }

    protected String getProtocol() {
        return "jdbc:mysql://";
    }

    // CREATE CONNECTION - Establish connection with database
    public void createConnection() throws SQLException {
        conn = DriverManager.getConnection(protocol + dbLocation + "?create=true", user, pwd);
        conn.setAutoCommit(false);
        if (StartApp.DEBUG) {
            if (conn != null) {
                System.out.println("Successfully created connection to: " + dbLocation);
            } else {
                System.out.println("Could't create connection to: " + dbLocation);
            }
        }
    }

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
                System.out.println("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, " + "PROJ_ID_FK INT, "
                        + "STACK INT NOT NULL," + "QUESTION TEXT (" + maxVarcharLength + "), " + "ANSWER TEXT ("
                        + maxVarcharLength + "), " + "CUSTOM_WIDTH_Q INT, "
                        + "CUSTOM_WIDTH_A INT, FOREIGN KEY (PROJ_ID_FK) REFERENCES PROJECTS(PROJ_ID_PK))");
            st.execute("CREATE TABLE " + flashcardsTable + " (CARD_ID_PK INT PRIMARY KEY, " + "PROJ_ID_FK INT, "
                    + "STACK INT NOT NULL," + "QUESTION TEXT (" + maxVarcharLength + "), " + "ANSWER TEXT (" + maxVarcharLength
                    + "), " + "CUSTOM_WIDTH_Q INT, "
                    + "CUSTOM_WIDTH_A INT, FOREIGN KEY (PROJ_ID_FK) REFERENCES PROJECTS(PROJ_ID_PK))");
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

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        MySQLDBExchanger mdbex = new MySQLDBExchanger("212.227.103.70:3306/flashcards_db", "moni", "testing123", null);
        mdbex.createConnection();
        System.out.println("success!");
        mdbex.createTablesIfNotExisting();
        System.out.println("created tables!");
    }
}
