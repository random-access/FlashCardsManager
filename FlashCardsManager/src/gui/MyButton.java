package gui;

import javax.swing.*;

@SuppressWarnings("serial")
public class MyButton extends JButton {
	
   MyButton(String text, Icon icon) {
      super(text,icon);
      setVerticalTextPosition(SwingConstants.BOTTOM);
      setHorizontalTextPosition(SwingConstants.CENTER);
   }
   
}
