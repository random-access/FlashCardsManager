package org.random_access.flashcardsmanager_desktop.exc;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.random_access.flashcardsmanager_desktop.app.StartApp;
import org.random_access.flashcardsmanager_desktop.utils.Logger;

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
	
	public static void showExportError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Beim Exportieren sind Fehler aufgetreten.", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
	}
	

	public static void showImportError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Beim Importieren sind Fehler aufgetreten.", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
	}
	
	public static void showCorruptDataError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Fehlerhafte Import-Datei!", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
	}

	public static void showParseError(Component owner, Exception e) {
		JOptionPane.showMessageDialog(owner, "Es sind Probleme beim Einlesen aufgetreten.", "Fehler",
				JOptionPane.ERROR_MESSAGE);
		output(e);
	}
	
	public static void showOtherError(Component owner, Throwable t) {
		JOptionPane.showMessageDialog(owner, "Ein unerwarteter Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
		output(t);
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
