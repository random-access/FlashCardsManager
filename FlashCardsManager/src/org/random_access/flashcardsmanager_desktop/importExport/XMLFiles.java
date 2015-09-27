package org.random_access.flashcardsmanager_desktop.importExport;

import java.util.ArrayList;

public enum XMLFiles {
	FLASHCARDS("flashcards.xml"), LEARNING_PROJECTS("projects.xml"), MEDIA("media.xml"), MEDIA_FOLDER("media"), LABELS(
			"labels.xml"), LABELS_FLASHCARDS("labels-flashcards-rel.xml");

	private String name;

	XMLFiles(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ArrayList<String> getAllNames() {
		ArrayList<String> filenames = new ArrayList<String>();
		for (XMLFiles f : XMLFiles.values()) {
			filenames.add(f.name);
		}
		return filenames;
	}
}
