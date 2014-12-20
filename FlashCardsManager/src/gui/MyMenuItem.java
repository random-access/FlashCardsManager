package gui;

import java.awt.Insets;

import javax.swing.*;

@SuppressWarnings("serial")
public class MyMenuItem extends JMenuItem {

   MyMenuItem() {
      super();
      customize();
   }

   MyMenuItem(Action a) {
      super(a);
      customize();
   }

   MyMenuItem(Icon icon) {
      super(icon);
      customize();
   }

   MyMenuItem(String text, Icon icon) {
      super(text, icon);
      customize();
   }

   MyMenuItem(String text, int mnemonic) {
      super(text, mnemonic);
      customize();
   }

   MyMenuItem(String text) {
      super(text);
      customize();
   }
   
   private void customize() {
      this.setMargin(new Insets(3,0,3,5));
   }
   
   
   
}
