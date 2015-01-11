package app;

import exc.CustomErrorHandling;
import exc.CustomInfoHandling;
import gui.IntroPanel;
import gui.MainWindow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.UIManager;
import javax.xml.stream.XMLStreamException;

import utils.FileUtils;
import utils.Logger;
import xml.Settings;
import xml.XMLSettingsExchanger;
import core.ProjectsController;

public class StartApp {

	public static final boolean DEBUG = false;

	private static final String APP_FOLDER = FileUtils.appDirectory("Lernkarten");
	private static final String DEFAULT_LOG_PATH = APP_FOLDER + "/logs";
	private static final String DEFAULT_SETTINGS_PATH = APP_FOLDER + "/settings.xml";
	private static final String DEFAULT_DATABASE_PATH = APP_FOLDER + "/database_2";
	private static final String PATH_TO_MEDIA = APP_FOLDER + "/media";

	private static InputStream defaultSettings = StartApp.class.getClassLoader().getResourceAsStream("xml/settings.xml");
	private static Settings currentSettings;
	private static Settings newSettings;

	public static void main(String[] args) {
		final IntroPanel intro = new IntroPanel();

		Properties p = System.getProperties();
		p.setProperty("derby.system.home", APP_FOLDER);
		try {
			FileUtils.createDirectory(APP_FOLDER);
			FileUtils.createDirectory(PATH_TO_MEDIA);
			Logger.setPathToLog(DEFAULT_LOG_PATH, "errors", "log");
			Logger.init(5);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			initializeSettings();
			final ProjectsController ctl = new ProjectsController(currentSettings.getPathToDatabase(), PATH_TO_MEDIA);
			new MainWindow(ctl, currentSettings.getMajorVersion(), currentSettings.getMinorVersion(),
					currentSettings.getPatchLevel());

		} catch (SQLException exc) {
			if (exc.getSQLState().equals("XJ040")) {
				CustomInfoHandling.showSecondInstanceInfo(null, exc);
			} else {
				CustomErrorHandling.showDatabaseError(null, exc);
			}
		} catch (XMLStreamException | IOException exc) {
			CustomErrorHandling.showReadSettingsError(null, exc);
		} catch (Exception exc) {
			CustomErrorHandling.showOtherError(null, exc);
		} catch (Throwable t) {
			CustomErrorHandling.showOtherError(null, t);
		} finally {
			intro.dispose();
		}
	}

	// check if version number has changed and update version
	private static boolean updatedVersion() throws NumberFormatException, XMLStreamException, IOException {
		boolean updated = false;
		if (currentSettings.getPatchLevel() != newSettings.getPatchLevel()) {
			currentSettings.setPatchLevel(newSettings.getPatchLevel());
			updated = true;
		}
		if (currentSettings.getMinorVersion() != newSettings.getMinorVersion()) {
			currentSettings.setMinorVersion(newSettings.getMinorVersion());
			updated = true;
		}
		if (currentSettings.getMajorVersion() != newSettings.getMajorVersion()) {
			currentSettings.setMajorVersion(newSettings.getMajorVersion());
			updated = true;
		}
		return updated;
	}

	private static void initializeSettings() throws XMLStreamException, NumberFormatException, IOException {
		newSettings = XMLSettingsExchanger.readConfig(defaultSettings);
		if (new File(DEFAULT_SETTINGS_PATH).isFile()) {
			if (DEBUG)
				System.out.println("Settings already in user folder");
			// settings already in user folder -> read from settings
			currentSettings = XMLSettingsExchanger.readConfig(DEFAULT_SETTINGS_PATH);

			// database v1 still in settings
			if (currentSettings.getDatabaseVersion() == 1 && newSettings.getDatabaseVersion() == 2) {
				// database v2 doesn't exist yet
				if (!(new File(DEFAULT_DATABASE_PATH).exists())) {
					CustomInfoHandling.showOldDatabaseInfo();
					System.exit(0);
				} else {
					currentSettings.setDatabaseVersion(2);
					currentSettings.setPathToDatabase(DEFAULT_DATABASE_PATH);
					XMLSettingsExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
					if (DEBUG)
						System.out.println("Updated database version.");
				}
			}
			if (!currentSettings.getPathToDatabase().endsWith("2")
					|| !(new File(currentSettings.getPathToDatabase()).isDirectory())) {
				if (DEBUG)
					System.out.println("Database not where it was expected or not there");
				// database deleted -> create new DB on default path
				currentSettings.setPathToDatabase(DEFAULT_DATABASE_PATH);
				XMLSettingsExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
			}

			if (StartApp.updatedVersion()) {
				if (DEBUG)
					System.out.println("was updated");
				XMLSettingsExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
			}

		} else {
			// first install -> copy default settings.xml into user folder
			if (DEBUG)
				System.out.println("XML Config not in user folder -> copy into user folder");
			currentSettings = newSettings;
			currentSettings.setPathToDatabase(DEFAULT_DATABASE_PATH);
			XMLSettingsExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
		}

	}
}
