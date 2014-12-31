package utils;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

	public static void createDirectory(String name, boolean debug) {
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

	public static void deleteDirectory(String pathToDirectory, boolean debug) {
		Path dir = Paths.get(pathToDirectory);

		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					System.out.println("Deleting file: " + file);
					File f = new File(file.toString());
					boolean success = f.canWrite();
					if (success) {
						System.out.println("File " + file + " is writable!");
					} else {
						System.out.println("!!! File " + file + " is not writable!");
					}
					Files.delete(file);
					return CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

					System.out.println("Deleting dir: " + dir);
					if (exc == null) {
						Files.delete(dir);
						return CONTINUE;
					} else {
						throw exc;
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String appDirectory(String name, boolean debug) {
		String OS = System.getProperty("os.name").toUpperCase();
		// Windows:
		if (OS.contains("WIN")) {
			if (debug) System.out.println("On Windows - app directory: " + System.getenv("APPDATA") + "/" + name);
			return System.getenv("APPDATA") + "/" + name;
		}
		// Mac:
		else if (OS.contains("MAC")) {
			if (debug) System.out.println("On Mac - app directory:" + System.getProperty("user.home") + "/Library" + "/" + name);
			return System.getProperty("user.home") + "/Library" + "/" + name;
		}
		// Linux:
		else if (OS.contains("NUX")) {
			if (debug) System.out.println("On Linux - app directory: " + System.getProperty("user.home") + "/." + name);
			return System.getProperty("user.home") + "/." + name;
		} else {
			if (debug) System.out.println("Elsewhere - app directory: " + System.getProperty("user.dir") + "/." + name);
			return System.getProperty("user.dir") + "/." + name;
		}
	}

	public static boolean directoryContainsOnlyCertainFiles(String pathToDirectory, String[] allowedNames) {
		File dir = new File(pathToDirectory);
		if (!dir.isDirectory()) {
			return false;
		} else {
			boolean result = true;
			File[] dirFiles = dir.listFiles();
			for (File file : dirFiles) {
				boolean b = false;
				for (String name : allowedNames) {
					if (file.getName().equals(name)) {
						b = true;
					}
				}
				result &= b;
			}
			return result;
		}
	}
}
