package gui;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class MyButton extends JButton {
   public MyButton(String text, Icon icon) {
      super(text,icon);
      setVerticalTextPosition(SwingConstants.BOTTOM);
      setHorizontalTextPosition(SwingConstants.CENTER);
   }
}
