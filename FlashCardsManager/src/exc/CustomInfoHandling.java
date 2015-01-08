package exc;

import javax.swing.JOptionPane;

public class CustomInfoHandling {
	
	// prevent instantiation
	private CustomInfoHandling() {}
	
	public static void showSuccessfullyDeletedInfo() {
		JOptionPane.showMessageDialog(null, "Die gew\u00e4hlten Karten wurden erfolgreich gel\u00f6scht", "Fertig", JOptionPane.INFORMATION_MESSAGE);	
	}

}
