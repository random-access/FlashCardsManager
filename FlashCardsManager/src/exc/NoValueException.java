package exc;

@SuppressWarnings("serial")
public class NoValueException extends Exception {

   public NoValueException() {
   }

   public NoValueException(String arg0) {
      super(arg0);
   }

   public NoValueException(Throwable arg0) {
      super(arg0);
   }

   public NoValueException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public NoValueException(String arg0, Throwable arg1, boolean arg2,
         boolean arg3) {
      super(arg0, arg1, arg2, arg3);
   }

}
