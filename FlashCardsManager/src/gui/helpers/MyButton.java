package gui.helpers;

import javax.swing.*;

@SuppressWarnings("serial")
public class MyButton extends JButton {

	public MyButton(String text, Icon icon) {
		super(text, icon);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}
	
	public MyButton(String text) {
		super(text);
	}
	
	public MyButton(Icon icon) {
		super(icon);
	}

	public MyButton(Action action, Icon icon) {
		super(action);
		setText(null);
		setIcon(icon);
//		 setVerticalTextPosition(SwingConstants.BOTTOM);
//		 setHorizontalTextPosition(SwingConstants.CENTER);
	}
	
	public MyButton(Action action, String text) {
		super(action);
		setText(text);
	}

}
