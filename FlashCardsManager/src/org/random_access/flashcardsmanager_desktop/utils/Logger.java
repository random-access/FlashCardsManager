package org.random_access.flashcardsmanager_desktop.utils;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

   public static String pathToLog;
   public static String suffix;

   public static void setPathToLog(String directory, String name,
         String individualSuffix) {
      Path dir = Paths.get(directory);
      if (Files.notExists(dir)) {
         try {
            Files.createDirectory(dir);
         } catch (IOException e) {
            try {
               System.setErr(new PrintStream(pathToLog + suffix));
            } catch (FileNotFoundException e1) {
               e1.printStackTrace();
            }
            e.printStackTrace();
         }
      }
      pathToLog = directory + "/" + name;
      suffix = "." + individualSuffix;
   }

   public static void init(int numberOfLogfiles) {
      File currentLogFile = new File(pathToLog + suffix);
      if (currentLogFile.exists()) {
         rotateLog(numberOfLogfiles);
      }
      try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(currentLogFile)))) {
         currentLogFile.createNewFile();
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         Date date = new Date();
         out.write("********** Started program at: " + dateFormat.format(date)
               + " **********");
         out.newLine();
         out.newLine();
         out.flush();
      } catch (IOException exc) {
         try {
            System.setErr(new PrintStream(pathToLog + suffix));
            System.err.println(exc);
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
         exc.printStackTrace();
      }
   }

   private static void rotateLog(int numberOfLogfiles) {
      try {
         if (numberOfLogfiles > 0) {
            Path currentLog = Paths.get(pathToLog + "_" + numberOfLogfiles
                  + suffix);
            if (Files.exists(currentLog)) {
               Files.delete(currentLog);
            }
            Path lastLog = null;
            if (numberOfLogfiles > 1) {
               lastLog = Paths.get(pathToLog + "_" + (numberOfLogfiles - 1)
                     + suffix);
            } else {
               lastLog = Paths.get(pathToLog + suffix);
            }
            if (Files.exists(lastLog)) {
               Files.move(lastLog, currentLog,
                     StandardCopyOption.REPLACE_EXISTING);
            }
            rotateLog(numberOfLogfiles - 1);
         }
      } catch (IOException exc) {
         try {
            System.setErr(new PrintStream(pathToLog + suffix));
            System.err.println(exc);
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
         exc.printStackTrace();
      }
   }

   public static void log(String s) {
      try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(pathToLog + suffix, true)))) {
         out.write("*************************** Error ***************************");
         out.newLine();
         out.write(s);
         out.newLine();
         out.newLine();
         out.flush();
      } catch (IOException exc) {
         try {
            System.setErr(new PrintStream(pathToLog + suffix));
            System.err.println(exc);
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
         exc.printStackTrace();
      }
   }
   
   public static void log(Throwable t) {
      try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(pathToLog + suffix, true)))) {
         out.write("*************************** Error ***************************");
         out.newLine();
         t.printStackTrace(new PrintWriter (out));
         out.newLine();
         out.newLine();
         out.flush();
      } catch (IOException exc) {
         try {
            System.setErr(new PrintStream(pathToLog + suffix));
            System.err.println(exc);
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
         exc.printStackTrace();
      }
   }

   public static void log(Exception e) {
      try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(pathToLog + suffix, true)))) {
         out.write("*************************** Error ***************************");
         out.newLine();
         e.printStackTrace(new PrintWriter(out));
         out.newLine();
         out.newLine();
         out.flush();
         out.close();
      } catch (IOException exc) {
         try {
            System.setErr(new PrintStream(pathToLog + suffix));
            System.err.println(exc);
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
         exc.printStackTrace();
      }
   }

   public static void addSystemProperties() {
      try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(pathToLog + suffix, true)))) {
         out.write("********************** System properties ********************");
         out.newLine();
         System.getProperties().list(new PrintWriter(out));
         out.newLine();
         out.flush();
         out.close();
      } catch (IOException exc) {
         try {
            System.setErr(new PrintStream(pathToLog + suffix));
            System.err.println(exc);
         } catch (FileNotFoundException ex) {
            ex.printStackTrace();
         }
         exc.printStackTrace();
      }
   }
}
