package tools;

// TODO: Auto-generated Javadoc
/**
 * The Class Pair.
 *
 * @param <T> the generic type
 * @param <U> the generic type
 */
public class Pair <T, U> {
   
   /** The value1. */
   private T value1;
   
   /** The value2. */
   private U value2;
   
   /**
    * Instantiates a new pair.
    *
    * @param value1 the value1
    * @param value2 the value2
    */
   public Pair(T value1, U value2) {
      this.value1 = value1;
      this.value2 = value2;
   }
   
   /**
    * Gets the value1.
    *
    * @return the value1
    */
   public T getValue1() {
      return value1;
   }

   /**
    * Sets the value1.
    *
    * @param value1 the new value1
    */
   public void setValue1(T value1) {
      this.value1 = value1;
   }

   /**
    * Gets the value2.
    *
    * @return the value2
    */
   public U getValue2() {
      return value2;
   }

   /**
    * Sets the value2.
    *
    * @param value2 the new value2
    */
   public void setValue2(U value2) {
      this.value2 = value2;
   }
}
