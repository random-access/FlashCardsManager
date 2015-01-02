package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import tools.MyDerbyDataTypes;
import tools.Pair;
import tools.Triple;
import tools.UnsupportedDataException;

public class BatchTransferTest {
   private final String driver = "org.apache.derby.jdbc.EmbeddedDriver"; // db-driver
   private final String protocol = "jdbc:derby:"; // database protocol
   private final String dbLocation; // database location

   private Connection conn; // connection

   public BatchTransferTest() {
      dbLocation = "Test";
   }

   public BatchTransferTest(String dbLocation) throws ClassNotFoundException {
      this.dbLocation = dbLocation;
      Class.forName(driver); // check if driver is reachable
      System.out.println("Created DBExchanger");
   }

   public void connect() throws SQLException {
      conn = DriverManager.getConnection(protocol + dbLocation + ";create=true");
      conn.setAutoCommit(false);
      if (conn != null) {
         System.out.println("Successfully created Connection to: " + protocol + dbLocation + ";create=true");
      }
   }

   public void disconnect() {
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
   
   // CREATE TABLE <TableName> (<Column1> <DataType1>, <Column2> <DataType2>, [...])
   public String buildCreateTableCommand(String tableName, ArrayList<Pair<String, MyDerbyDataTypes>> columns) {
      StringBuilder sbCreateTableCommand = new StringBuilder();
      sbCreateTableCommand.append("CREATE TABLE ");
      sbCreateTableCommand.append(tableName);
      sbCreateTableCommand.append(" (");
      for (Pair<String, MyDerbyDataTypes> p : columns) {
         sbCreateTableCommand.append(p.getValue1()).append(" ").append(p.getValue2()).append(", ");
      }
      sbCreateTableCommand.replace(sbCreateTableCommand.length() - 2, sbCreateTableCommand.length(), ")");
      return sbCreateTableCommand.toString();
   }

   // INSERT INTO <TableName> (<Column1>, <Column2>, [...]) VALUES (<Value1>, <Value2>, [...])
   public String buildInsertCommand(String tableName, ArrayList<Triple<String, String, MyDerbyDataTypes>> insertData)
         throws UnsupportedDataException {
      StringBuilder sbInsertCommand = new StringBuilder();
      sbInsertCommand.append("INSERT INTO ");
      sbInsertCommand.append(tableName);
      sbInsertCommand.append(" (");
      for (Triple<String, String, MyDerbyDataTypes> t : insertData) {
         sbInsertCommand.append(t.getValue1()).append(", ");
      }
      sbInsertCommand.replace(sbInsertCommand.length() - 2, sbInsertCommand.length(), ")");
      sbInsertCommand.append(" VALUES (");
      for (Triple<String, String, MyDerbyDataTypes> t : insertData) {
         validateInsertData(t);
         sbInsertCommand.append(t.getValue2()).append(", ");
      }
      sbInsertCommand.replace(sbInsertCommand.length() - 2, sbInsertCommand.length(), ")");
      return sbInsertCommand.toString();
   }

   private void validateInsertData(Triple<String, String, MyDerbyDataTypes> dataElement)
         throws UnsupportedDataException {
         if (dataElement.getValue3().equals(MyDerbyDataTypes.VARCHAR) && (!dataElement.getValue2().startsWith("\'") || !dataElement.getValue2().endsWith("\'"))) {
            dataElement.setValue2("\'" + dataElement.getValue2() + "\'");
         }
         if (dataElement.getValue3().equals(MyDerbyDataTypes.BLOB)) {
            throw new UnsupportedDataException("Blob is not a simple data type!");
         }
   }
   
   // UPDATE <TableName> SET <Column1> = <Value1>, <Column2> = <Value2>, [...] WHERE <PrimaryKey> = <Value>
   public String buildUpdateCommand(String tableName, ArrayList<Triple<String, String, MyDerbyDataTypes>> fields,
         Triple<String, String, MyDerbyDataTypes> identifier) throws UnsupportedDataException {
      StringBuilder sbInsertCommand = new StringBuilder();
      sbInsertCommand.append("UPDATE ");
      sbInsertCommand.append(tableName);
      sbInsertCommand.append(" SET ");
      for (Triple<String, String, MyDerbyDataTypes> t : fields) {
         validateInsertData(t);
         sbInsertCommand.append(t.getValue1()).append(" = ").append(t.getValue2()).append(", ");
      }
      sbInsertCommand.replace(sbInsertCommand.length() - 2, sbInsertCommand.length(), "");
      sbInsertCommand.append(" WHERE ");
      validateInsertData(identifier);
      sbInsertCommand.append(identifier.getValue1()).append(" = ").append(identifier.getValue2());
      return sbInsertCommand.toString();
   }

   public static void main(String[] args) throws UnsupportedDataException {
      ArrayList<Pair<String, MyDerbyDataTypes>> list = new ArrayList<Pair<String, MyDerbyDataTypes>>();
      list.add(new Pair<String, MyDerbyDataTypes>("Name1", MyDerbyDataTypes.INTEGER));
      list.add(new Pair<String, MyDerbyDataTypes>("Name2", MyDerbyDataTypes.VARCHAR));
      list.add(new Pair<String, MyDerbyDataTypes>("Name3", MyDerbyDataTypes.INTEGER));
      list.add(new Pair<String, MyDerbyDataTypes>("Name4", MyDerbyDataTypes.BLOB));
      BatchTransferTest test = new BatchTransferTest();
      System.out.println(test.buildCreateTableCommand("TestTable", list));

      ArrayList<Triple<String, String, MyDerbyDataTypes>> list2 = new ArrayList<Triple<String, String, MyDerbyDataTypes>>();
      list2.add(new Triple<String, String, MyDerbyDataTypes>("Name1", "value1", MyDerbyDataTypes.INTEGER));
      list2.add(new Triple<String, String, MyDerbyDataTypes>("Name2", "value2", MyDerbyDataTypes.VARCHAR));
      list2.add(new Triple<String, String, MyDerbyDataTypes>("Name3", "value3", MyDerbyDataTypes.INTEGER));
      list2.add(new Triple<String, String, MyDerbyDataTypes>("Name4", "value4", MyDerbyDataTypes.VARCHAR));
      System.out.println(test.buildInsertCommand("TestTable", list2));
      
      list2.remove(0);
      System.out.println(test.buildUpdateCommand("TestTable", list2, new Triple<String, String, MyDerbyDataTypes> ("Name1", "value1", MyDerbyDataTypes.INTEGER)));
   }

}
