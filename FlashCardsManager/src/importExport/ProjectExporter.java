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

import core.*;

public class ProjectExporter {

	private ArrayList<LearningProject> exportProjects;
	private String pathToExportFolder;
	private String pathToMediaFolder;
	private IProgressPresenter p;

	private ArrayList<XMLLearningProject> xmlProjects = new ArrayList<XMLLearningProject>();
	private ArrayList<XMLFlashCard> xmlFlashCards = new ArrayList<XMLFlashCard>();
	private ArrayList<XMLMedia> xmlMedia = new ArrayList<XMLMedia>();
	private ArrayList<XMLLabel> xmlLabels = new ArrayList<XMLLabel>();
	private ArrayList<XMLLabelFlashcardRelation> xmlLfRel = new ArrayList<XMLLabelFlashcardRelation>();

	public ProjectExporter(ArrayList<LearningProject> exportProjects, String pathToMediaFolder, String pathToExportFolder,
			IProgressPresenter p) {
		this.exportProjects = exportProjects;
		this.pathToExportFolder = pathToExportFolder;
		this.pathToMediaFolder = pathToMediaFolder;
		this.p = p;
	}

	public void doExport() throws SQLException, XMLStreamException, IOException {
		loadCardsAndLabels();
		Files.createDirectory(Paths.get(pathToExportFolder));
		loadXMLLists();
		writeXMLFiles();
		copyPics();
	}

	private void loadCardsAndLabels() throws SQLException {
		for (LearningProject proj : exportProjects) {
			p.changeInfo("Lade Karten: " + proj.getTitle());
			proj.loadLabelsAndFlashcards(p);
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
		p.changeProgress(20);
		ex.writeLabels(pathToExportFolder + "/" + XMLFiles.LABELS.getName(), xmlLabels);
		p.changeProgress(40);
		ex.writeFlashcards(pathToExportFolder + "/" + XMLFiles.FLASHCARDS.getName(), xmlFlashCards);
		p.changeProgress(60);
		ex.writeLabelFlashcardRelation(pathToExportFolder + "/" + XMLFiles.LABELS_FLASHCARDS.getName(), xmlLfRel);
		p.changeProgress(80);
		ex.writeMedia(pathToExportFolder + "/" + XMLFiles.MEDIA.getName(), xmlMedia);
		p.changeProgress(100);
	}

	private void loadXMLLists() throws SQLException {
		p.changeProgress(0);
		p.changeInfo("Erstelle Export-Objekte...");
		Iterator<LearningProject> it = exportProjects.iterator();
		while (it.hasNext()) {
			LearningProject proj = it.next();
			xmlProjects.add(proj.toXMLLearningProject());
			for (Label l : proj.getLabels()) {
				xmlLabels.add(l.toXMLLabel());
			}
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
				for (XMLLabelFlashcardRelation lfrel : c.getXMLLfRelations()) {
					xmlLfRel.add(lfrel);
				}
			}
			p.changeProgress(Math.min(100, p.getProgress() + 100 / exportProjects.size()));
		}
	}
}
