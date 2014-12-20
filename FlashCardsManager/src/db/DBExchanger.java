package db;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;

import core.*;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;

public class DBExchanger<T extends OrderedItem> {
// TODO Stream handling (try with resources...)

   private final String driver = "org.apache.derby.jdbc.EmbeddedDriver"; // db-driver
   private final String protocol = "jdbc:derby:"; // database protocol
   private final String dbURL; // database location
   private final String projectsTableTitle = "PROJECTS"; // Title of project
   // table
   private Connection conn; // connection

   private static final int DEFAULT_QUESTION_LENGTH = 20;
   private static final int DEFAULT_ANSWER_LENGTH = 30;

   public DBExchanger(String dbLocation) throws ClassNotFoundException {
      Class.forName(driver); // check if driver is reachable
      this.dbURL = protocol + dbLocation + ";create=true";
      System.out.println("Created DBExchanger"); // TODO: remove debug output
   }

   // DB-URL - Getter
   public String getDbURL() {
      return dbURL;
   }

   // CREATE CONNECTION - Establish connection with database
   public void createConnection() throws SQLException {
      conn = DriverManager.getConnection(dbURL);
      conn.setAutoCommit(false);
      if (conn != null) {
         System.out.println("Successfully created Connection to: " + dbURL);
         // TODO: remove debug output
      }
   }

   public void closeConnection(String pathToDatabase) {
      try {
         conn.commit();
         conn.close();

         boolean gotSQLExc = false;
         try {
            DriverManager.getConnection("jdbc:derby:" + pathToDatabase
                  + ";shutdown=true");
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

   // CREATE PROJECTS TABLE, Columns: ID, PROJ_TITLE, NEXT_CARD_ID,
   // NO_OF_STACKS, VARCHARS_QUESTION, VARCHARS_ANSWER
   public void createTable() throws SQLException, EntryAlreadyThereException {
      Statement st = conn.createStatement();
      st.execute("CREATE TABLE "
            + projectsTableTitle
            + " (ID INT PRIMARY KEY, PROJ_TITLE VARCHAR (50), NO_OF_STACKS INT, "
            + "VARCHARS_QUESTION INT, VARCHARS_ANSWER INT)");
      conn.commit();
      st.close();
      System.out.println("Successfully created table: " + projectsTableTitle);
      // TODO: remove debug output
   }

   // CREATE QUESTION TABLE, Columns: ID, STACK, QUESTION, ANSWER
   public void createTable(String name) throws SQLException,
         EntryAlreadyThereException {
      if (!tableAlreadyExisting(name)) {
         Statement st = conn.createStatement();
         st.execute("CREATE TABLE " + name
               + " (ID INT PRIMARY KEY, STACK INT, QUESTION VARCHAR("
               + DEFAULT_QUESTION_LENGTH + "), ANSWER VARCHAR ("
               + DEFAULT_ANSWER_LENGTH + "), QUESTIONPIC BLOB, ANSWERPIC BLOB)");
         conn.commit();
         st.close();
         System.out.println("Successfully created table: " + name);
      } else {
         throw new EntryAlreadyThereException();
      }
   }

   // ADD ROW: insert project into table
   public void addRow(LearningProject project)
         throws EntryAlreadyThereException, SQLException {
      if (!idAlreadyExisting(project.getId())) {
         Statement st = conn.createStatement();
         st.executeUpdate("INSERT INTO " + projectsTableTitle + " VALUES ("
               + project.getId() + ",'" + project.getTitle() + "',"
               + project.getNumberOfStacks() + "," + DEFAULT_QUESTION_LENGTH
               + "," + DEFAULT_ANSWER_LENGTH + ")");
         conn.commit();
         st.close();
         System.out.println("Insert values: INSERT INTO " + projectsTableTitle
               + " VALUES (" + project.getId() + ",'" + project.getTitle()
               + "'," + project.getNumberOfStacks() + ","
               + DEFAULT_QUESTION_LENGTH + "," + DEFAULT_ANSWER_LENGTH + ")");
         // TODO: remove debug output
      } else {
         throw new EntryAlreadyThereException();
      }
   }

   // ADD ROW: insert flashcard into table
   public void addRow(FlashCard card, LearningProject proj,
         String pathToQuestionPic, String pathToAnswerPic) throws SQLException,
         EntryNotFoundException, IOException {
      controlVarcharLength(card, proj);

      String INSERT_CARD = "insert into " + proj.getTableName()
            + " (ID, STACK, QUESTION, ANSWER, QUESTIONPIC, ANSWERPIC) "
            + "values (?, ?, ?, ?, ?, ?)";
      PreparedStatement prepSt = conn.prepareStatement(INSERT_CARD);
      prepSt.setInt(1, card.getId());
      prepSt.setInt(2, card.getStack());
      prepSt.setString(3, card.getQuestion());
      prepSt.setString(4, card.getAnswer());
      // insert pictures as blobs in database if there are paths in flashcard,
      // else null
      FileInputStream inputStreamQuestion = null;
      FileInputStream inputStreamAnswer = null;
      if (card.hasQuestionPic() == false) {
         prepSt.setBinaryStream(5, null);
      } else {
         File qPicFile = new File(pathToQuestionPic);
         System.out.println(pathToQuestionPic);
         inputStreamQuestion = new FileInputStream(qPicFile);
         prepSt.setBinaryStream(5, inputStreamQuestion, qPicFile.length());
      }
      if (card.hasAnswerPic() == false) {
         prepSt.setBinaryStream(6, null);
      } else {
         File aPicFile = new File(pathToAnswerPic);
         System.out.println(pathToAnswerPic);
         inputStreamAnswer = new FileInputStream(aPicFile);
         prepSt.setBinaryStream(6, inputStreamAnswer, aPicFile.length());
      }
      prepSt.executeUpdate();
      conn.commit();
      prepSt.close();
      if (inputStreamQuestion != null) {
         inputStreamQuestion.close();
      }
      if (inputStreamAnswer != null) {
         inputStreamAnswer.close();
      }
      System.out.println("Successfully inserted values from card "
            + card.getId() + " into database!");
      // TODO: remove debug output
   }

   // ADD ARRAY
   public void insertFlashcardArray(ArrayList<FlashCard> allCards,
         LearningProject destProj, DBExchanger<OrderedItem> srcDbex,
         LearningProject srcProj) throws SQLException, EntryNotFoundException {
      String INSERT_TEXT = "insert into " + destProj.getTableName()
            + " (ID, STACK, QUESTION, ANSWER, QUESTIONPIC, ANSWERPIC) "
            + "values (?, ?, ?, ?, ?, ?)";
      ListIterator<FlashCard> lit = allCards.listIterator();
      while (lit.hasNext()) {

         FlashCard currentCard = lit.next();
         controlVarcharLength(currentCard, destProj);
         PreparedStatement prepSt = conn.prepareStatement(INSERT_TEXT);
         prepSt.setInt(1, currentCard.getId());
         prepSt.setInt(2, currentCard.getStack());
         prepSt.setString(3, currentCard.getQuestion());
         prepSt.setString(4, currentCard.getAnswer());
         prepSt.setBlob(5, null, 0);
         prepSt.setBlob(6, null, 0);
         prepSt.execute();
         conn.commit();
         prepSt.close();
         System.out.println("successfully exported card texts"
               + currentCard.getId() + "...");
      }

      ListIterator<FlashCard> litQ = allCards.listIterator();
      while (lit.hasNext()) {
         FlashCard currentCard = litQ.next();
         String TRANSFERQ = "UPDATE " + destProj.getTableName()
               + " SET QUESTIONPIC = ? WHERE ID = " + currentCard.getId();
         PreparedStatement prepSt = conn.prepareStatement(TRANSFERQ);
         byte[] questionBytes = srcDbex.getBlobAsBytes(PicType.QUESTION,
               currentCard, srcProj);
         Blob questionBlob = null;
         if (questionBytes != null) {
            questionBlob = new SerialBlob(questionBytes);
            System.out.println("card " + currentCard.getId()
                  + " has question-pic...");
         }
         prepSt.setBlob(1, questionBlob);
         prepSt.execute();
         conn.commit();
         if (questionBlob != null) {
            questionBlob.free();
         }
      }

      ListIterator<FlashCard> litA = allCards.listIterator();
      while (lit.hasNext()) {
         FlashCard currentCard = litA.next();
         String TRANSFERA = "UPDATE " + destProj.getTableName()
               + " SET ANSWERPIC = ? WHERE ID = " + currentCard.getId();
         PreparedStatement prepSt = conn.prepareStatement(TRANSFERA);
         byte[] answerBytes = srcDbex.getBlobAsBytes(PicType.ANSWER,
               currentCard, srcProj);
         Blob answerBlob = null;
         if (answerBytes != null) {
            answerBlob = new SerialBlob(answerBytes);
            System.out.println("card " + currentCard.getId()
                  + " has answer-pic...");
         }
         prepSt.setBlob(1, answerBlob);
         prepSt.execute();
         conn.commit();
         if (answerBlob != null) {
            answerBlob.free();
         }
      }

   }

   // UPDATE ROW: modify existing project
   public void updateRow(LearningProject project)
         throws EntryNotFoundException, SQLException {
      if (idAlreadyExisting(project.getId())) {
         Statement st = conn.createStatement();
         st.executeUpdate("UPDATE " + projectsTableTitle
               + " SET PROJ_TITLE = '" + project.getTitle()
               + "', NO_OF_STACKS = " + project.getNumberOfStacks()
               + " WHERE ID = " + project.getId());
         conn.commit();
         st.close();
         System.out.println("Update values: UPDATE " + projectsTableTitle
               + " SET PROJ_TITLE = '" + project.getTitle()
               + "', NO_OF_STACKS = " + project.getNumberOfStacks()
               + " WHERE ID = " + project.getId());
         // TODO: remove debug output
      } else {
         throw new EntryNotFoundException();
      }
   }

   // UPDATE ROW: modify existing flashcard
   public void updateRow(FlashCard card, LearningProject proj)
         throws EntryNotFoundException, SQLException {
      if (idAlreadyExisting(card.getId(), proj.getTableName())) {
         controlVarcharLength(card, proj);
         ;
         Statement st = conn.createStatement();
         st.executeUpdate("UPDATE " + proj.getTableName() + " SET STACK = "
               + card.getStack() + ", QUESTION = '" + card.getQuestion()
               + "', ANSWER = '" + card.getAnswer() + "' WHERE ID = "
               + card.getId());
         conn.commit();
         st.close();
         System.out.println("Update values: UPDATE " + proj.getTableName()
               + " SET STACK = " + card.getStack() + ", QUESTION = '"
               + card.getQuestion() + "', ANSWER = '" + card.getAnswer()
               + "' WHERE ID = " + card.getId());
         // TODO: remove debug output
      } else {
         throw new EntryNotFoundException();
      }
   }

   // GET PIC AS BLOB:
   public byte[] getBlobAsBytes(PicType type, FlashCard card,
         LearningProject proj) throws SQLException {
      Blob blob = null;
      ResultSet res = null;
      Statement st = conn.createStatement();
      switch (type) {
      case QUESTION:
         st.execute("SELECT QUESTIONPIC FROM " + proj.getTableName()
               + " WHERE ID = " + card.getId());
         break;
      case ANSWER:
         st.executeQuery("SELECT ANSWERPIC FROM " + proj.getTableName()
               + " WHERE ID = " + card.getId());
         break;
      }
      conn.commit();
      res = st.getResultSet();

      if (res.next()) {
         blob = res.getBlob(1);
      }
      res.close();
      st.close();
      byte[] b = null;
      if (blob != null) {
         b = blob.getBytes(1, (int) blob.length());
         blob.free();
      }
      return b;
   }

   // GET PIC: get picture of flashcard from DB
   public BufferedImage getPic(PicType type, FlashCard card,
         LearningProject proj) throws SQLException, IOException {
      ResultSet result = null;
      Statement st = null;
      BufferedImage img = null;
      ByteArrayInputStream in = null;
      st = conn.createStatement();
      switch (type) {
      case QUESTION:
         st.executeQuery("SELECT QUESTIONPIC FROM " + proj.getTableName()
               + " WHERE ID = " + card.getId());
         break;
      case ANSWER:
         st.executeQuery("SELECT ANSWERPIC FROM " + proj.getTableName()
               + " WHERE ID = " + card.getId());
         break;
      }
      conn.commit();
      result = st.getResultSet();
      byte[] image = null;
      if (result.next()) {
         image = result.getBytes(1);
      }
      in = new ByteArrayInputStream(image);
      img = ImageIO.read(in);
      if (in != null) {
         in.close();
      }
      result.close();
      st.close();
      return img;
   }

   // UPDATE PIC: modify pics in flashcard
   public void updatePic(PicType type, FlashCard card, LearningProject proj,
         String pathToPic) throws SQLException, FileNotFoundException,
         IOException {
      PreparedStatement prepSt = null;
      String MODIFY_PIC;
      FileInputStream inputStreamQuestion = null;
      FileInputStream inputStreamAnswer = null;
      switch (type) {
      case QUESTION:
         MODIFY_PIC = "UPDATE " + proj.getTableName()
               + " SET QUESTIONPIC = ? WHERE ID = " + card.getId();
         prepSt = conn.prepareStatement(MODIFY_PIC);
         File questionPicture = new File(pathToPic);
         inputStreamQuestion = new FileInputStream(questionPicture);
         prepSt.setBinaryStream(1, inputStreamQuestion,
               questionPicture.length());
         break;
      case ANSWER:
         MODIFY_PIC = "UPDATE " + proj.getTableName()
               + " SET ANSWERPIC = ? WHERE ID = " + card.getId();
         prepSt = conn.prepareStatement(MODIFY_PIC);
         File answerPicture = new File(pathToPic);
         inputStreamAnswer = new FileInputStream(answerPicture);
         prepSt.setBinaryStream(1, inputStreamAnswer, answerPicture.length());
         break;
      }
      prepSt.executeUpdate();
      conn.commit();
      prepSt.close();
      if (inputStreamQuestion != null) {
         inputStreamQuestion.close();
      }
      if (inputStreamAnswer != null) {
         inputStreamAnswer.close();
      }
      System.out.println("Successfully modified " + type.toString()
            + "-Pic from card " + card.getId() + "!");
      // TODO: remove debug output
   }

   // DELETE PIC
   public void deletePic(PicType type, FlashCard card, LearningProject proj)
         throws SQLException {
      PreparedStatement prepSt = null;
      String DELETE_PIC;
      switch (type) {
      case QUESTION:
         DELETE_PIC = "UPDATE " + proj.getTableName()
               + " SET QUESTIONPIC = ? WHERE ID = " + card.getId();
         prepSt = conn.prepareStatement(DELETE_PIC);
         prepSt.setBinaryStream(1, null);
         break;
      case ANSWER:
         DELETE_PIC = "UPDATE " + proj.getTableName()
               + " SET ANSWERPIC = ? WHERE ID = " + card.getId();
         prepSt = conn.prepareStatement(DELETE_PIC);
         prepSt.setBinaryStream(1, null);
         break;
      }
      prepSt.executeUpdate();
      conn.commit();
      prepSt.close();
      System.out.println("Successfully deleted " + type.toString()
            + "-Pic from card " + card.getId() + "!");
      // TODO: remove debug output
   }

   // READ ALL DATA into Learning Project Array
   public ArrayList<LearningProject> readAllData(ProjectsManager projMgr)
         throws SQLException, ClassNotFoundException, EntryNotFoundException,
         IOException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT * FROM " + projectsTableTitle);
      conn.commit();
      ResultSet res = st.getResultSet();
      ArrayList<LearningProject> data = new ArrayList<LearningProject>();
      while (res.next()) {
         LearningProject proj = new LearningProject(projMgr, res.getInt(1),
               res.getString(2), res.getInt(3), res.getInt(4), res.getInt(5));
         data.add(proj);
         System.out.println("Synchronized from Database -> Project: "
               + proj.getId());
         // TODO: remove debug output
      }
      res.close();
      st.close();
      return data;
   }

   // READ ALL DATA into Flashcard Array
   public ArrayList<FlashCard> readAllData(String table, LearningProject proj)
         throws SQLException, EntryNotFoundException, IOException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT * FROM " + table);
      conn.commit();
      ResultSet res = st.getResultSet();
      ArrayList<FlashCard> data = new ArrayList<FlashCard>();
      while (res.next()) {
         boolean hasQuestionPic = (res.getBlob(5) != null);
         boolean hasAnswerPic = (res.getBlob(6) != null);
         FlashCard f = new FlashCard(proj, res.getInt(1), res.getInt(2),
               res.getString(3), res.getString(4), hasQuestionPic, hasAnswerPic);
         data.add(f);
         System.out.println("Synchronized from Database -> FlashCard: "
               + f.getId()); // TODO: remove debug output
      }
      res.close();
      st.close();
      return data;
   }

   // DELETE ROW:
   public void deleteRow(LearningProject proj) throws EntryNotFoundException,
         SQLException {
      if (idAlreadyExisting(proj.getId())) {
         Statement st = conn.createStatement();
         st.executeUpdate("DELETE FROM " + projectsTableTitle + " WHERE ID = "
               + proj.getId());
         conn.commit();
         st.close();
         System.out.println("Delete row: DELETE FROM " + projectsTableTitle
               + " WHERE ID = " + proj.getId());
         // TODO: remove debug output

      } else {
         throw new EntryNotFoundException();
      }
   }

   public void deleteRow(FlashCard f, LearningProject proj)
         throws EntryNotFoundException, SQLException {
      if (idAlreadyExisting(f.getId(), proj.getTableName())) {
         Statement st = conn.createStatement();
         st.executeUpdate("DELETE FROM " + proj.getTableName() + " WHERE ID = "
               + f.getId());
         conn.commit();
         st.close();
         System.out.println("Delete row: DELETE FROM " + proj.getTableName()
               + " WHERE ID = " + f.getId());
         // TODO: remove debug output

      } else {
         throw new EntryNotFoundException();
      }
   }

   // DELETE TABLE
   public void deleteTable(LearningProject project) throws SQLException,
         EntryNotFoundException {
      if (idAlreadyExisting(project.getId())) {
         Statement st = conn.createStatement();
         st.execute("DROP TABLE " + project.getTableName());
         conn.commit();
         st.close();
         System.out.println("Deleted table: " + "DROP TABLE "
               + project.getTableName());
      } else {
         throw new EntryNotFoundException();
      }
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

   public int countRows(String table, int stack) throws SQLException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT COUNT (*) FROM " + table + " where STACK = "
            + stack);
      conn.commit();
      ResultSet rs = st.getResultSet();
      rs.next();
      int i = rs.getInt(1);
      rs.close();
      st.close();
      return i;
   }

   // TABLE ALREADY EXISTING:
   // check if flashcard table is already in database
   public boolean tableAlreadyExisting(String name) throws SQLException {
      DatabaseMetaData dbmd = conn.getMetaData();
      conn.commit();
      ResultSet rs = dbmd.getTables(null, "APP", name, null);
      // fetch tables with title <name>
      System.out.print("Check if table exists already: ");
      // TODO: remove debug output
      if (rs.next()) { // that means at least 1 result row
         System.out.println("Table " + rs.getString(3) + " already exists!");
         rs.close();
         // TODO: remove debug output
         return true;
      } // if we arrive here, the table doesn't exist yet
      rs.close();
      System.out.println("Table " + name + " doesn't exist yet!");
      // TODO: remove debug output
      return false;
   }

   // check if project table is already in database
   public boolean tableAlreadyExisting() throws SQLException {
      DatabaseMetaData dbmd = conn.getMetaData();
      conn.commit();
      ResultSet rs = dbmd.getTables(null, "APP", projectsTableTitle, null);
      // fetch tables with title <name>
      System.out.print("Check if table exists already: ");
      // TODO: remove debug output
      if (rs.next()) { // that means at least 1 result row
         System.out.println("Table " + rs.getString(3) + " already exists!");
         rs.close();
         // TODO: remove debug output
         return true;
      } // if we arrive here, the table doesn't exist yet
      rs.close();
      System.out.println("Table " + projectsTableTitle + " doesn't exist yet!");
      // TODO: remove debug output
      return false;
   }

   // ID ALREADY EXISTING: check if there's a row in the table with same ID
   // project
   public boolean idAlreadyExisting(int id) throws SQLException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT ID FROM " + this.projectsTableTitle
            + " WHERE ID = " + id);
      conn.commit();
      System.out.println("Search for ID: SELECT ID FROM " + projectsTableTitle
            + " WHERE ID = " + id); // TODO: remove
      // debug output
      ResultSet res = st.getResultSet();
      if (res.next()) {
         System.out.println("ID " + id + " already exists");
         res.close();
         st.close();
         // TODO: remove debug output
         return true;
      } // if we arrive here, the row doesn't exist yet
      res.close();
      st.close();
      System.out.println("ID " + id + " doesn't exist yet!");
      // TODO: remove debug output
      return false;
   }

   // flashcard
   public boolean idAlreadyExisting(int id, String table) throws SQLException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT ID FROM " + table + " WHERE ID = " + id);
      conn.commit();
      System.out.println("Search for ID: SELECT ID FROM " + table
            + " WHERE ID = " + id); // TODO: remove debug output
      ResultSet res = st.getResultSet();
      if (res.next()) {
         System.out.println("ID " + id + " already exists");
         res.close();
         st.close();
         // TODO: remove debug output
         return true;
      } // if we arrive here, the row doesn't exist yet
      res.close();
      st.close();
      System.out.println("ID " + id + " doesn't exist yet!");
      // TODO: remove debug output
      return false;
   }

   // TITLE ALREADY EXISTING: check if project title is already in use
   public boolean titleAlreadyExisting(String projTitle) throws SQLException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT ID FROM " + projectsTableTitle
            + " WHERE PROJ_TITLE = '" + projTitle + "'");
      conn.commit();
      System.out.println("Check for duplicate: " + "SELECT ID FROM "
            + projectsTableTitle + " WHERE PROJ_TITLE = " + projTitle);
      ResultSet res = st.getResultSet();
      if (res.next()) {
         System.out.println("Duplicate!");
         res.close();
         st.close();
         return true;
      }
      res.close();
      st.close();
      return false;
   }

   // methods to find out if VARCHAR size has to be increased
   private int getVarcharsLength(LearningProject proj, String column)
         throws SQLException, EntryNotFoundException {
      Statement st = conn.createStatement();
      st.executeQuery("SELECT " + column + " FROM " + projectsTableTitle
            + " where ID = " + proj.getId());
      conn.commit();
      ResultSet res = st.getResultSet();
      if (res.next()) {
         int i = res.getInt(1);
         res.close();
         st.close();
         return i;
      } else {
         res.close();
         st.close();
         throw new EntryNotFoundException();
      }
   }

   // ALTER VARCHAR LENGTH: increases the size of varchar columns to be able to
   // store longer values
   public void controlVarcharLength(FlashCard card, LearningProject proj)
         throws SQLException, EntryNotFoundException {
      Statement st = conn.createStatement();
      int questionSize = getVarcharsLength(proj, "VARCHARS_QUESTION");
      int answerSize = getVarcharsLength(proj, "VARCHARS_ANSWER");
      if (card.getQuestionLength() > questionSize) {
         questionSize = card.getQuestionLength();
         st.executeUpdate("ALTER TABLE " + proj.getTableName()
               + " ALTER QUESTION" + " SET DATA TYPE VARCHAR (" + questionSize
               + ")");
         conn.commit();
         System.out.println("Changed Varchar length: ALTER QUESTION"
               + " SET DATA TYPE VARCHAR (" + questionSize + ")");
         st.executeUpdate("UPDATE " + projectsTableTitle
               + " SET VARCHARS_QUESTION = " + questionSize + " where ID = "
               + proj.getId());
         conn.commit();
         proj.setMaxCharsQuestion(questionSize);
      }
      if (card.getAnswerLength() > answerSize) {
         answerSize = card.getAnswerLength();
         st.executeUpdate("ALTER TABLE " + proj.getTableName()
               + " ALTER ANSWER" + " SET DATA TYPE VARCHAR (" + answerSize
               + ")");
         conn.commit();
         System.out.println("Changed Varchar length: ALTER TABLE "
               + proj.getTableName() + " ALTER ANSWER"
               + " SET DATA TYPE VARCHAR (" + answerSize + ")");
         st.executeUpdate("UPDATE " + projectsTableTitle
               + " SET VARCHARS_ANSWER = " + answerSize + " where ID = "
               + proj.getId());
         conn.commit();
         proj.setMaxCharsAnswer(answerSize);
      }
      st.close();
   }

}
