package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JMenu;

@SuppressWarnings("serial")
public class MyMenu extends JMenu {

	public MyMenu() {
		super();
		customize();
	}

	public MyMenu(Action a) {
		super(a);
		customize();
	}

	public MyMenu(String s, boolean b) {
		super(s, b);
		customize();
	}

	public MyMenu(String s) {
		super(s);
		customize();
	}

	private void customize() {
		this.setMargin(new Insets(3, 11, 3, 5));
	}

}
