package app;

import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import gui.MainWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.stream.XMLStreamException;

import utils.Logger;
import xml.Settings;
import xml.XMLExchanger;
import core.ProjectsManager;

public class StartApp {

   public static final String APP_FOLDER = appDirectory();
   static final String DEFAULT_LOG_PATH = APP_FOLDER + "/logs";
   static final String DEFAULT_SETTINGS_PATH = APP_FOLDER + "/settings.xml";
   static final String DEFAULT_DATABASE_PATH = APP_FOLDER + "/database_1";

   static InputStream defaultSettings = StartApp.class.getClassLoader()
         .getResourceAsStream("xml/settings.xml");
   static Settings currentSettings;

   public static void main(String[] args) {
      Properties p = System.getProperties();
      p.setProperty("derby.system.home", APP_FOLDER);

      try {
         createDirectory(APP_FOLDER);
         Logger.setPathToLog(DEFAULT_LOG_PATH, "errors", "log");
         Logger.init(5);
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         initializeSettings();
         final ProjectsManager prm = new ProjectsManager(
               currentSettings.getPathToDatabase());
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               new MainWindow(prm, currentSettings.getMajorVersion(),
                     currentSettings.getMinorVersion(), currentSettings
                           .getPatchLevel());
            }
         });
      } catch (SQLException e) {
         if (e.getSQLState().equals("XJ040")) {
            JOptionPane
                  .showMessageDialog(
                        null,
                        "Eine Instanz dieser Anwendung ist bereits aktiv. Bitte schließen Sie diese und starten Sie das Programm neu oder wechseln Sie zur offenen Anwendung.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
         } else {
            JOptionPane.showMessageDialog(null,
                  "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
                  JOptionPane.ERROR_MESSAGE);
            Logger.log(e);
         }
      } catch (XMLStreamException | IOException e) {
         JOptionPane.showMessageDialog(null,
               "Es gibt ein Problem beim Einlesen der Einstellungen.",
               "Fehler", JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      } catch (EntryAlreadyThereException | EntryNotFoundException e) {
         JOptionPane.showMessageDialog(null,
               "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      } catch (Throwable t) {
         JOptionPane.showMessageDialog(null,
               "Ein unerwarteter Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(t);
      }
   }

   // check if version number has changed and update version
   private static boolean updatedVersion() throws NumberFormatException,
         FileNotFoundException, XMLStreamException {
      Settings newSettings = XMLExchanger.readConfig(defaultSettings);
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

   private static void initializeSettings() throws FileNotFoundException,
         XMLStreamException {
      if (new File(DEFAULT_SETTINGS_PATH).isFile()) {
         System.out.println("Settings already in user folder");
         // settings already in user folder -> read from settings
         currentSettings = XMLExchanger.readConfig(DEFAULT_SETTINGS_PATH);
         if (currentSettings.getPathToDatabase().equals("null")
               || !(new File(currentSettings.getPathToDatabase()).isDirectory())) {
            System.out
                  .println("Database not where it was expected or not there");
            // database deleted -> create new DB on default path
            currentSettings.setPathToDatabase(DEFAULT_DATABASE_PATH);
            XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
         }
         if (currentSettings.getDatabaseVersion() == 0) { /*
                                                           * correct XML ->
                                                           * right now everybody
                                                           * has DBv1 & no
                                                           * individual stuff is
                                                           * in XML
                                                           */
            currentSettings.setDatabaseVersion(1);
            XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
            System.out
                  .println("Correcting XML-Settings (database version was null)...");
         }
         if (StartApp.updatedVersion()) {
            System.out.println("was updated");
            XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
         }

      } else {
         // first install -> copy default settings.xml into user folder
         System.out
               .println("XML Config not in user folder -> copy into user folder");
         currentSettings = XMLExchanger.readConfig(defaultSettings);
         System.out.println("read config: " + defaultSettings);

         currentSettings.setPathToDatabase(DEFAULT_DATABASE_PATH);
         System.out.println(DEFAULT_SETTINGS_PATH);
         XMLExchanger.writeConfig(DEFAULT_SETTINGS_PATH, currentSettings);
      }

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
      File f = new File(name);
      if (!f.exists()) {
         System.out.println("creating directory: " + name + "...");
         boolean result = false;
         f.mkdir();
         result = true;
         if (result) {
            System.out.println(name + " successfully created!");
         }
      }
   }
}
