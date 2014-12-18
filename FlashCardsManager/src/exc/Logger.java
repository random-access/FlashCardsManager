package exc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	public static String pathToLog;
	public static String suffix;

	public static void setPathToLog(String directory, String name, String individualSuffix) throws IOException {
		pathToLog = directory + "/" + name;
		suffix = "." + individualSuffix;
	}

	public static void init(int numberOfLogfiles) throws IOException {
		File currentLogFile = new File(pathToLog + suffix);

		if (currentLogFile.exists()) {
			rotateLog(numberOfLogfiles);
		}
		currentLogFile.createNewFile();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(currentLogFile)));
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		out.write("********** Started program at: " + dateFormat.format(date) + " **********");
		out.newLine();
		out.newLine();
		out.flush();
		out.close();
	}

	private static void rotateLog(int numberOfLogfiles) throws IOException {
		if (numberOfLogfiles > 0) {
			Path currentLog = Paths.get(pathToLog + "_" + numberOfLogfiles + suffix);
			if (Files.exists(currentLog)) {
				Files.delete(currentLog);
			}
			Path lastLog = null;
			if (numberOfLogfiles > 1) {
				lastLog = Paths.get(pathToLog + "_" + (numberOfLogfiles - 1) + suffix);
			} else {
				lastLog = Paths.get(pathToLog + suffix);
			}
			if (Files.exists(lastLog)) {
				Files.move(lastLog, currentLog, StandardCopyOption.REPLACE_EXISTING);
			}
			rotateLog(numberOfLogfiles - 1);
		} 
	}
	
	public static void log(String s) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(pathToLog + suffix, true)));
		out.write("*************************** Error ***************************");
		out.newLine();
		out.write(s);
		out.newLine();
		out.newLine();
		out.flush();
		out.close();
	}
	
	public static void log(Exception e) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(pathToLog + suffix, true)));
		out.write("*************************** Error ***************************");
		out.newLine();
		e.printStackTrace(new PrintWriter(out));
		out.newLine();
		out.newLine();
		out.flush();
		out.close();
	}
	
	public static void addSystemProperties() throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(pathToLog + suffix, true)));
		out.write("********************** System properties ********************");
		out.newLine();
		System.getProperties().list(new PrintWriter(out));
		out.newLine();
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws IOException {
		Logger.setPathToLog("/home/moni/Desktop/LogTest", "ErrorLog", "log");
		Logger.init(3);
		Logger.log("Dies ist ein seeehr böser Fehler!");
		Logger.log("Dies ist noch ein schlimmerer Fehler!\n"
				+ "Du willst nicht wissen, was passiert ist...");
		try {
			String s = null;
			s.toCharArray();
		} catch (NullPointerException e){
			Logger.log(e);
		}
		Logger.addSystemProperties();
	}
}
