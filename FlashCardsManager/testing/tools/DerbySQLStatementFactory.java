package tools;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Derby SQL Statement Strings.
 * Currently only the data types VARCHAR and INTEGER are supported, other datatypes can be added in @see MyDerbyDataTypes but also 
 * must be controlled in @see validateInsertData
 */
public class DerbySQLStatementFactory {
   
   /**
    * Builds the create table command, this will look like:
    * CREATE TABLE TableName (Column1 DataType1, Column2 DataType2, [...])
    * @param tableName the table name
    * @param columns the columns
    * @return SQL create table statement as String
    */
   public static String buildCreateTableCommand(String tableName, ArrayList<Pair<String, MyDerbyDataTypes>> columns) {
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

   /**
    * Builds the insert command, this will look like: 
    * INSERT INTO TableName (Column1, Column2, [...]) VALUES (Value1, Value2, [...])
    * @param tableName the table name
    * @param insertData the insert data
    * @return SQL insert statement as String
    * @throws UnsupportedDataException the unsupported data exception
    */
   public static String buildInsertCommand(String tableName, ArrayList<Triple<String, String, MyDerbyDataTypes>> insertData)
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

   /**
    * Validate insert data.
    *
    * @param dataElement the data element
    * @throws UnsupportedDataException the unsupported data exception
    */
   private static void validateInsertData(Triple<String, String, MyDerbyDataTypes> dataElement)
         throws UnsupportedDataException {
         if (dataElement.getValue3().equals(MyDerbyDataTypes.VARCHAR) && (!dataElement.getValue2().startsWith("\'") || !dataElement.getValue2().endsWith("\'"))) {
            dataElement.setValue2("\'" + dataElement.getValue2() + "\'");
         }
         if (dataElement.getValue3().equals(MyDerbyDataTypes.BLOB)) {
            throw new UnsupportedDataException("Blob is not a simple data type!");
         }
   }
   
   /**
    * Builds the update command, this will look like:
    * UPDATE TableName SET Column1 = Value1, Column2 = Value2, [...] WHERE PrimaryKey =  Value
    *
    * @param tableName the table name
    * @param fields the fields
    * @param identifier the identifier
    * @return SQL update statement as String
    * @throws UnsupportedDataException the unsupported data exception
    */
   public static String buildUpdateCommand(String tableName, ArrayList<Triple<String, String, MyDerbyDataTypes>> fields,
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
}
