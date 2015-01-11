package tests;
import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;

import javax.sql.rowset.serial.SerialBlob;

public class DBExchangerDemo {

   private final String driver = "org.apache.derby.jdbc.EmbeddedDriver"; // db-driver
   private final String protocol = "jdbc:derby:"; // database protocol
   private final String dbLocation; // database location

   private Connection conn; // connection

   public DBExchangerDemo(String dbLocation) throws ClassNotFoundException {
      this.dbLocation = dbLocation;
      Class.forName(driver); // check if driver is reachable
      System.out.println("Created DBExchanger");
   }

   public void connect() throws SQLException {
      conn = DriverManager
            .getConnection(protocol + dbLocation + ";create=true");
      conn.setAutoCommit(false);
      if (conn != null) {
         System.out.println("Successfully created Connection to: " + protocol
               + dbLocation + ";create=true");
      }
   }

   public void disconnect() {
      try {
         conn.commit();
         conn.close();
         boolean gotSQLExc = false;
         try {
            DriverManager.getConnection("jdbc:derby:" + dbLocation
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
         e.printStackTrace();
      }

   }

   public void createTableAndFillWithSampleData() throws SQLException {
      Statement st = conn.createStatement();
      st.execute("CREATE TABLE TESTTABLE (ID INT PRIMARY KEY NOT NULL, TEXT VARCHAR (50))");
      conn.commit();
      st.execute("CREATE TABLE TESTTABLE2 (ID INT PRIMARY KEY NOT NULL, TEXT VARCHAR (50))");
      conn.commit();
      st.execute("CREATE TABLE TESTTABLE3 (ID1 INT CONSTRAINT ID1_FK REFERENCES TESTTABLE(ID), ID INT CONSTRAINT ID_FK REFERENCES TESTTABLE2(ID), TEXT VARCHAR (50), PRIMARY KEY (ID1, ID))");
      conn.commit();
      st.close();
      System.out
            .println("Successfully created testtable and filled it with sample data!");
   }

   // UPDATE PIC: modify pics in flashcard
   public void insertPic(int id, String pathToPic) throws SQLException,
         FileNotFoundException, IOException {
      PreparedStatement prepSt = null;
      String INSERT_PIC;
      FileInputStream fis = null;
      INSERT_PIC = "UPDATE TESTTABLE SET PIC = ? WHERE ID = " + id;
      prepSt = conn.prepareStatement(INSERT_PIC);
      File picFile = new File(pathToPic);
      fis = new FileInputStream(picFile);
      prepSt.setBinaryStream(1, fis, picFile.length());
      prepSt.executeUpdate();
      conn.commit();
      prepSt.close();
      if (fis != null) {
         fis.close();
      }
      System.out.println("Successfully inserted Pic, ID = " + id + "!");
   }

   private byte[] getBlobAsBytes(int id) throws SQLException {
      Blob blob = null;
      ResultSet res = null;
      Statement st = conn.createStatement();
      st.execute("SELECT PIC FROM TESTTABLE WHERE ID = " + id);
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
      System.out.println("Got blob as bytes, ID = " + id);
      return b;
   }

   public void transferBlob(DBExchangerDemo srcDbex, int oldId, int newId)
         throws SQLException {
      String TRANSFER = "UPDATE TESTTABLE SET PIC = ? WHERE ID = "
            + newId;
      PreparedStatement prepSt = conn.prepareStatement(TRANSFER);
      byte[] blobAsBytes = srcDbex.getBlobAsBytes(oldId);
      Blob newBlob = null;
      if (blobAsBytes != null) {
         newBlob = new SerialBlob(blobAsBytes);
         System.out.println("Importing blob...");
      }
      prepSt.setBlob(1, newBlob);
      prepSt.execute();
      conn.commit();
      if (newBlob != null)
         newBlob.free();
      prepSt.close();
   }

   public static void deleteDirectory(String pathToDirectory) {
      Path dir = Paths.get(pathToDirectory);

      try {
         Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                  BasicFileAttributes attrs) throws IOException {
               System.out.println("Deleting file: " + file);
               Files.delete(file);
               return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                  throws IOException {

               System.out.println("Deleting dir: " + dir);
               if (exc == null) {
                  Files.delete(dir);
                  return CONTINUE;
               } else {
                  throw exc;
               }
            }
         });
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) throws ClassNotFoundException,
         SQLException, FileNotFoundException, IOException, InterruptedException {
      DBExchangerDemo dbex1 = new DBExchangerDemo(
            "C:\\Users\\IT-Helpline16\\Desktop\\TestDB");
      dbex1.connect();
      dbex1.createTableAndFillWithSampleData();
     //  dbex1.insertPic(1, "C:\\Users\\IT-Helpline16\\Desktop\\SamplePic.jpg");
    //   dbex1.getBlobAsBytes(1);
      dbex1.disconnect();
      
      
//      DBExchangerDemo dbex2 = new DBExchangerDemo(
//            "C:\\Users\\IT-Helpline16\\Desktop\\TestDB2");
//      dbex2.connect();
//      dbex2.createTableAndFillWithSampleData();
//      dbex2.transferBlob(dbex1, 1, 2);
//      
//      dbex1.disconnect();
//      dbex2.disconnect();
//      
//      deleteDirectory("C:\\Users\\IT-Helpline16\\Desktop\\TestDB");
//      System.out.println("Successfully deleted TestDB!");
//      
//      Thread.sleep(3000);
//      
//      deleteDirectory("C:\\Users\\IT-Helpline16\\Desktop\\TestDB2");
//      System.out.println("Successfully deleted TestDB2!");
//      
//      System.out.println("Finished!");

     
   }
}
