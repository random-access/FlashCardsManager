package importExport;

import gui.helpers.IProgressPresenter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import core.FlashCard;
import core.LearningProject;

public class ProjectExporter {

	private ArrayList<LearningProject> exportProjects;
	private String pathToExportFolder;
	private String pathToMediaFolder;
	private IProgressPresenter p;

	private ArrayList<XMLLearningProject> xmlProjects;
	private ArrayList<XMLFlashCard> xmlFlashCards;
	private ArrayList<XMLMedia> xmlMedia;

	public ProjectExporter(ArrayList<LearningProject> exportProjects, String pathToMediaFolder, String pathToExportFolder,
			IProgressPresenter p) {
		this.exportProjects = exportProjects;
		this.pathToExportFolder = pathToExportFolder;
		this.pathToMediaFolder = pathToMediaFolder;
		this.p = p;
	}

	public void doExport() throws SQLException, XMLStreamException, IOException {
		loadCards();
		Files.createDirectory(Paths.get(pathToExportFolder));
		loadXMLLists();
		writeXMLFiles();
		copyPics();
	}

	private void loadCards() throws SQLException {
		for (LearningProject proj : exportProjects) {
			p.changeInfo("Lade Karten: " + proj.getTitle());
			proj.loadFlashcards(p);
			p.changeProgress(0);
		}

	}

	private void copyPics() throws IOException {
		String mediaTargetFolder = pathToExportFolder + "/" + XMLFiles.MEDIA_FOLDER.getName();
		Files.createDirectory(Paths.get(mediaTargetFolder));
		for (XMLMedia m : xmlMedia) {
			Files.copy(Paths.get(pathToMediaFolder + "/" + m.getPathToMedia()),
					Paths.get(mediaTargetFolder + "/" + m.getPathToMedia()));
		}
	}

	private void writeXMLFiles() throws FileNotFoundException, XMLStreamException {
		p.changeProgress(0);
		p.changeInfo("Exportiere...");
		XMLExchanger ex = new XMLExchanger();
		ex.writeProjects(pathToExportFolder + "/" + XMLFiles.LEARNING_PROJECTS.getName(), xmlProjects);
		p.changeProgress(33);
		ex.writeFlashcards(pathToExportFolder + "/" + XMLFiles.FLASHCARDS.getName(), xmlFlashCards);
		p.changeProgress(66);
		ex.writeMedia(pathToExportFolder + "/" + XMLFiles.MEDIA.getName(), xmlMedia);
		p.changeProgress(99);
	}

	private void loadXMLLists() throws SQLException {
		p.changeProgress(0);
		p.changeInfo("Erstelle Export-Objekte...");
		xmlProjects = new ArrayList<XMLLearningProject>();
		xmlFlashCards = new ArrayList<XMLFlashCard>();
		xmlMedia = new ArrayList<XMLMedia>();
		Iterator<LearningProject> it = exportProjects.iterator();
		while (it.hasNext()) {
			LearningProject proj = it.next();
			xmlProjects.add(proj.toXMLLearningProject());
			for (FlashCard c : proj.getAllCards()) {
				xmlFlashCards.add(c.toXMLFlashcard());
				XMLMedia mQuestion = c.getXMLQuestionMedia();
				XMLMedia mAnswer = c.getXMLAnswerMedia();
				if (mQuestion != null) {
					xmlMedia.add(mQuestion);
				}
				if (mAnswer != null) {
					xmlMedia.add(mAnswer);
				}
			}
			p.changeProgress(Math.min(100, p.getProgress() + 100/exportProjects.size()));
		}
	}
}
