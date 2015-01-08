package exc;

import java.awt.Component;

import javax.swing.JOptionPane;

public class CustomInfoHandling {
	
	// prevent instantiation
	private CustomInfoHandling() {}
	
	public static void showSuccessfullyDeletedInfo() {
		JOptionPane.showMessageDialog(null, "Die gew\u00e4hlten Karten wurden erfolgreich gel\u00f6scht", "Fertig", JOptionPane.INFORMATION_MESSAGE);	
	}
	
	public static void showImportSuccessInfo(Component owner) {
		JOptionPane.showMessageDialog(owner, "Import erfolgreich abgeschlossen", "Fertig", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showExportSuccessInfo(Component owner) {
		JOptionPane.showMessageDialog(owner, "Export erfolgreich abgeschlossen", "Fertig", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showSecondInstanceInfo(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Eine Instanz dieser Anwendung ist bereits aktiv. Bitte schlie\u00dfen Sie diese und starten Sie das Programm neu oder wechseln Sie zur offenen Anwendung.",
				"Fehler", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showOldDatabaseInfo() {
		JOptionPane.showMessageDialog(null, "Die Datenbankversion ist nicht mehr aktuell! Bitte aktualisiere die Datenbank", "Datenbankversion..", JOptionPane.WARNING_MESSAGE);	
	}
	
	public static void showNoCardsSelectedInfo() {
		JOptionPane.showMessageDialog(null, "Bitte w\u00e4hle eine oder mehrere Karten aus!", "Nichts ausgew\u00e4hlt", JOptionPane.WARNING_MESSAGE);	
	}
	
	public static void showInvalidCharSequenceInfo(Component owner) {
		JOptionPane.showMessageDialog(owner, "Es wurden ung\u00fcltige Zeichen eingegeben.", "Fehler",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showInvalidLengthInfo(Component owner, int max) {
		JOptionPane.showMessageDialog(owner, "Eingabe ist zu lang! (Maximum: " + max + " Zeichen).", "Fehler",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showInvalidValueInfo(Component owner, int max, int min) {
		JOptionPane.showMessageDialog(owner, "Ung\u00fcltige Eingabe! Bitte gib eine Zahl zwischen " + min + " und " + max 
				+ " ein!", "Fehler",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showNoInputInfo(Component owner) {
		JOptionPane.showMessageDialog(owner, "Es wurden nicht alle Felder ausgef\u00fcllt.",
				"Fehler", JOptionPane.WARNING_MESSAGE); 
	}
	
	public static void showNoPathSelectedInfo(Component owner) {
		JOptionPane.showMessageDialog(owner, "Es wurde kein Pfad ausgew\u00e4hlt", "Fehler!",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showMissingPermissionsInfo(Component owner, String path) {
		JOptionPane.showMessageDialog(owner, "Fehlende Ordnerberechtigungen unter " + path 
				+ ". Bitte w\u00e4hle ein anderes Verzeichnis", "Fehler!", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showUnexpectedFolderStructureInfo(Component owner, String folderName) {
		JOptionPane.showMessageDialog(owner, folderName
				+ " ist kein Ordner, oder in diesem Ordner liegen noch andere Dateien!", "Fehler!",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public static int showOverwriteFileQuestion(Component owner, String folderName, String fileName) {
		return JOptionPane.showConfirmDialog(owner, "Die Datei " + fileName
				+ " existiert bereits in " + folderName + " - soll sie \u00fcberschrieben werden?",
				"Datei \u00fcberschreiben?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	}
	
}
