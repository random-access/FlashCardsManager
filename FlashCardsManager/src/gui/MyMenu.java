package gui;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JMenu;

@SuppressWarnings("serial")
public class MyMenu extends JMenu {

	MyMenu() {
		super();
		customize();
	}

	MyMenu(Action a) {
		super(a);
		customize();
	}

	MyMenu(String s, boolean b) {
		super(s, b);
		customize();
	}

	MyMenu(String s) {
		super(s);
		customize();
	}

	private void customize() {
		this.setMargin(new Insets(3, 11, 3, 5));
	}

}
