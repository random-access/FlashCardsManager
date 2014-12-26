package gui.helpers;

import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class MyTextPane extends JTextPane {

   private int minimalWidth, minimalHeight;

   public MyTextPane(int minimalWidth, int minimalHeight) {
      super();
      this.minimalWidth = minimalWidth;
      this.minimalHeight = minimalHeight;
   }

   public MyTextPane(StyledDocument doc, int minimalWidth, int minimalHeight) {
      super(doc);
      this.minimalWidth = minimalWidth;
      this.minimalHeight = minimalHeight;
   }

   public void setMinimalWidth(int minimalWidth) {
      this.minimalWidth = minimalWidth;
   }
   
   public int getMinimalWidth() {
      return minimalWidth;
   }

   public void setMinimalHeight(int minimalHeight) {
      this.minimalHeight = minimalHeight;
   }
   
   private int getMinimalHeight() {
      return minimalHeight;
   }
   
   @Override
   public Dimension getPreferredSize() {
      // makes textpane have a minimum size but with normal resize behaviour when getting larger
      int prefWidth, prefHeight;
      prefWidth = this.minimalWidth;
      System.out.println("MinimalWidth: " + this.minimalWidth);
      System.out.println("PrefWidth: " + super.getPreferredSize().width);
      if (super.getPreferredSize().height < minimalHeight) {
         prefHeight = minimalHeight;
      } else {
         prefHeight = super.getPreferredSize().height;
      }
      return new Dimension(prefWidth, prefHeight);
   }


//   @Override
//   public Dimension getPreferredSize() {
//      // makes textpane have a minimum size but with normal resize behaviour when getting larger
//      
//      if (this.getWidth() > minimalWidth && this.getHeight() > minimalHeight) {
//         return super.getPreferredSize();
//      } else {
//         if (super.getPreferredSize().height > minimalHeight) {
//            return new Dimension(minimalWidth, super.getPreferredSize().height);
//         } else if (super.getPreferredSize().width > minimalWidth) {
//            return new Dimension(super.getPreferredSize().width, minimalHeight);
//         } else {
//            return new Dimension(minimalWidth, minimalHeight);
//         }
//      }
//   }

}
