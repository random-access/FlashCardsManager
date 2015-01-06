package exc;

import java.awt.Component;

import javax.swing.JOptionPane;

import utils.Logger;
import app.StartApp;

public class CustomErrorHandling {
	
	// prevent instantiation
	private CustomErrorHandling(){}
	
	public static void showDatabaseError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
		
	}
	
	public static void showInternalError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Ein interner Fehler ist aufgetreten", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
	}
	
	public static void showReadSettingsError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Es gibt ein Problem beim Einlesen der Einstellungen.", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
	}
	
	public static void showOtherError(Component owner, Throwable t) {
		JOptionPane.showMessageDialog(owner, "Ein unerwarteter Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
		output(t);
	}

	public static void showSecondInstanceError(Component owner, Exception e) {
		JOptionPane
		.showMessageDialog(
				owner,
				"Eine Instanz dieser Anwendung ist bereits aktiv. Bitte schlie\u00dfen Sie diese und starten Sie das Programm neu oder wechseln Sie zur offenen Anwendung.",
				"Fehler", JOptionPane.ERROR_MESSAGE);
		debugOutput(e);
	}
	
	public static void showOldDatabaseInfo() {
		JOptionPane.showMessageDialog(null, "Die Datenbankversion ist nicht mehr aktuell! Bitte aktualisiere die Datenbank", "Datenbankversion..", JOptionPane.INFORMATION_MESSAGE);	
	}
	
	private static void debugOutput(Throwable t) {
		if (StartApp.DEBUG) t.printStackTrace();
	}
	
	private static void output(Throwable t) {
		if (StartApp.DEBUG) t.printStackTrace();
		else Logger.log(t);
	}
	
	private static void output(Exception e) {
		if (StartApp.DEBUG) e.printStackTrace();
		else Logger.log(e);
	}
}
