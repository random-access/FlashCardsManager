package gui;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MyMenuItem extends JMenuItem {

   public MyMenuItem() {
      super();
      customize();
   }

   public MyMenuItem(Action a) {
      super(a);
      customize();
   }

   public MyMenuItem(Icon icon) {
      super(icon);
      customize();
   }

   public MyMenuItem(String text, Icon icon) {
      super(text, icon);
      customize();
   }

   public MyMenuItem(String text, int mnemonic) {
      super(text, mnemonic);
      customize();
   }

   public MyMenuItem(String text) {
      super(text);
      customize();
   }
   
   public void customize() {
      this.setMargin(new Insets(3,0,3,5));
   }
   
   
   
}
