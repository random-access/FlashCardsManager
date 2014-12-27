package tools;

// TODO: Auto-generated Javadoc
/**
 * The Enum MyDerbyDataTypes.
 */
public enum MyDerbyDataTypes {
   
   /** The varchar. */
   VARCHAR("String"), 
 /** The integer. */
 INTEGER("int"), 
 /** The blob. */
 BLOB("Blob");
    
   /** The java type. */
   String javaType;
   
   /**
    * Instantiates a new my derby data types.
    *
    * @param javaType the java type
    */
   MyDerbyDataTypes(String javaType) {
      this.javaType = javaType;
   }
   
   
   
}
