package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;

import xml.Settings;
import xml.XMLExchanger;
import core.ProjectsManager;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import gui.MainWindow;

public class StartApp {

	static final String APP_FOLDER = appDirectory();
	static final String DEFAULT_SETTINGS_PATH = APP_FOLDER + "/settings.xml";
	static final String DEFAULT_DATABASE_PATH = APP_FOLDER + "/database_1";

	public static void main(String[] args) {

		try {
			final Settings settings = initializeSettings();
			final ProjectsManager prm = new ProjectsManager(
					settings.getPathToDatabase());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new MainWindow(prm, settings.getMajorVersion(), settings
							.getMinorVersion());
				}
			});
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntryAlreadyThereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Settings initializeSettings() throws NumberFormatException,
			FileNotFoundException, XMLStreamException {
		Settings settings = null;
		if (new File(DEFAULT_SETTINGS_PATH).isFile()) {
			System.out.println("Settings already in user folder");
			// settings already in user folder -> read from settings
			settings = XMLExchanger.readConfig(DEFAULT_SETTINGS_PATH);
			if (settings.getPathToDatabase().equals("null")
					|| !(new File(settings.getPathToDatabase()).isDirectory())) {
				System.out
						.println("Database not where it was expected or not there");
				// database deleted -> create new DB on default path
				settings.setPathToDatabase(DEFAULT_DATABASE_PATH);
				XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, settings);
			}
			if (settings.getDatabaseVersion() == 0) { /* correct XML -> right now everybody has DBv1 
				& no individual stuff is in XML */
				settings.setDatabaseVersion(1);
				XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, settings);
				System.out.println("Correcting XML-Settings (database version was null)...");
			}
		} else {
			// first install -> copy default settings.xml into user folder
			System.out
					.println("XML Config not in user folder -> copy into user folder");
			settings = XMLExchanger.readConfig("settings.xml");
			System.out.println("read config: settings.xml");
			createDirectory(APP_FOLDER);
			settings.setPathToDatabase(DEFAULT_DATABASE_PATH);
			System.out.println(DEFAULT_SETTINGS_PATH);
			XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, settings);
		}
		return settings;

	}

	private static String appDirectory() {
		String OS = System.getProperty("os.name").toUpperCase();
		// Windows:
		if (OS.contains("WIN")) {
			System.out.println("On Windows:" + System.getenv("APPDATA")
					+ "/Lernkarten");
			return System.getenv("APPDATA") + "/Lernkarten";
		}
		// Mac:
		else if (OS.contains("MAC")) {
			System.out.println("On Mac:" + System.getProperty("user.home")
					+ "/Library" + "/Lernkarten");
			return System.getProperty("user.home") + "/Library" + "/Lernkarten";
		}
		// Linux:
		else if (OS.contains("NUX")) {
			System.out.println("On Linux:" + System.getProperty("user.home")
					+ "/.Lernkarten");
			return System.getProperty("user.home") + "/.Lernkarten";
		} else {
			System.out.println("Elsewhere:" + System.getProperty("user.dir")
					+ "/.Lernkarten");
			return System.getProperty("user.dir") + "/.Lernkarten";
		}
	}

	static void createDirectory(String name) {
		File lernkarten = new File(name);
		if (!lernkarten.exists()) {
			System.out.println("creating directory: " + name + "...");
			boolean result = false;
			try {
				lernkarten.mkdir();
				result = true;
			} catch (SecurityException se) {
				se.printStackTrace();
			}
			if (result) {
				System.out.println(name + " successfully created!");
			}
		}
	}
}
