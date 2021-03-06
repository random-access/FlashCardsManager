package org.random_access.flashcardsmanager_desktop.utils;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;

import org.random_access.flashcardsmanager_desktop.app.StartApp;

public class FileUtils {

	public static void createDirectory(String name) {
		File f = new File(name);
		if (!f.exists()) {
			if (StartApp.DEBUG)
				System.out.println("creating directory: " + name + "...");
			f.mkdir();
		}
	}

	public static void deleteDirectory(String pathToDirectory) throws IOException {
		Path dir = Paths.get(pathToDirectory);
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (StartApp.DEBUG)
					System.out.println("Deleting file: " + file);
				File f = new File(file.toString());
				boolean success = f.canWrite();
				if (StartApp.DEBUG) {
					if (success) {
						System.out.println("File " + file + " is writable!");
					} else {
						System.out.println("!!! File " + file + " is not writable!");
					}
				}
				Files.delete(file);
				return CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

				if (StartApp.DEBUG)
					System.out.println("Deleting dir: " + dir);
				if (exc == null) {
					Files.delete(dir);
					return CONTINUE;
				} else {
					throw exc;
				}
			}

		});
	}

	public static String appDirectory(String name) {
		String OS = System.getProperty("os.name").toUpperCase();
		// Windows:
		if (OS.contains("WIN")) {
			if (StartApp.DEBUG)
				System.out.println("On Windows - org.random_access.flashcardsmanager_desktop.app directory: " + System.getenv("APPDATA") + "/" + name);
			return System.getenv("APPDATA") + "/" + name;
		}
		// Mac:
		else if (OS.contains("MAC")) {
			if (StartApp.DEBUG)
				System.out.println("On Mac - org.random_access.flashcardsmanager_desktop.app directory:" + System.getProperty("user.home") + "/Library" + "/" + name);
			return System.getProperty("user.home") + "/Library" + "/" + name;
		}
		// Linux:
		else if (OS.contains("NUX")) {
			if (StartApp.DEBUG)
				System.out.println("On Linux - org.random_access.flashcardsmanager_desktop.app directory: " + System.getProperty("user.home") + "/." + name);
			return System.getProperty("user.home") + "/." + name;
		} else {
			if (StartApp.DEBUG)
				System.out.println("Elsewhere - org.random_access.flashcardsmanager_desktop.app directory: " + System.getProperty("user.dir") + "/." + name);
			return System.getProperty("user.dir") + "/." + name;
		}
	}

	public static boolean directoryContainsCertainFines(String pathToDirectory, ArrayList<String> requiredFiles) {
		File dir = new File(pathToDirectory);
		if (!dir.isDirectory()) {
			return false;
		} else {
			boolean result = true;

			File[] dirFiles = dir.listFiles();
			Iterator<String> it = requiredFiles.iterator();
			while (it.hasNext()) {
				boolean b = false;
				String requiredFile = it.next();
				for (File f : dirFiles) {
					if (f.getName().equals(requiredFile)) {
						b = true;
					}
				}
				result &= b;
			}
			return result;
		}
	}

	public static boolean directoryContainsOnlyCertainFiles(String pathToDirectory, ArrayList<String> allowedFiles) {
		File dir = new File(pathToDirectory);
		if (!dir.isDirectory()) {
			return false;
		} else {
			boolean result = true;
			File[] dirFiles = dir.listFiles();
			for (File file : dirFiles) {
				boolean b = false;
				for (String name : allowedFiles) {
					if (file.getName().equals(name)) {
						b = true;
					}
				}
				result &= b;
			}
			return result;
		}
	}

	public static void deleteIfExistsWithAnyExtension(String fileName) throws IOException {
		String directoryName = Paths.get(fileName).getParent().toString();
		String relFileName = Paths.get(fileName).getFileName().toString();
		for (String s : fileList(directoryName)) {
			if (s.contains(relFileName)) {
				Files.delete(Paths.get(directoryName + "/" + s));
				if (StartApp.DEBUG)
					System.out.println("Deleted " + directoryName + "/" + s);
			}
		}
	}

	public static ArrayList<String> fileList(String directoryName) {
		ArrayList<String> fileNames = new ArrayList<String>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryName))) {
			for (Path path : directoryStream) {
				fileNames.add(path.getFileName().toString());
			}
		} catch (IOException ex) {
		}
		return fileNames;
	}

	public static void copyPicFile(String src, String target) throws IOException {
		if (StartApp.DEBUG)
			System.out.println("in copy method: src=" + src + ", target=" + target);
		Path srcPath = Paths.get(src);
		Path targetPath = Paths.get(target);
		Files.copy(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void movePicFile(String src, String target) throws IOException {
		Path srcPath = Paths.get(src);
		Path targetPath = Paths.get(target);
		Files.move(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
	}

	public static String getFileExtension(String pathName) {
		if (pathName != null) {
			return pathName.substring(pathName.lastIndexOf('.') + 1, pathName.length());
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		// deleteIfExistsWithAnyExtension("/home/moni/Desktop/media/pic-1-2-q");
		// fileList("/home/moni/Desktop/media");
	}
}
