package org.random_access.flashcardsmanager_desktop.importExport;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.xml.stream.XMLStreamException;

import org.random_access.flashcardsmanager_desktop.core.*;
import org.random_access.flashcardsmanager_desktop.exc.InvalidLengthException;
import org.random_access.flashcardsmanager_desktop.exc.InvalidValueException;
import org.random_access.flashcardsmanager_desktop.gui.helpers.IProgressPresenter;
import org.random_access.flashcardsmanager_desktop.storage.PicType;

public class ProjectImporter {
	OfflineProjectsController ctl;
	String pathToImport;
	String pathToImportMediaFolder;
	String pathToMediaFolder;
	IProgressPresenter p;

	ArrayList<LearningProject> importProjects;
	ArrayList<XMLLearningProject> xmlProjects;
	ArrayList<XMLFlashCard> xmlFlashCards;
	ArrayList<XMLMedia> xmlMedia;
	ArrayList<XMLLabel> xmlLabels;
	ArrayList<XMLLabelFlashcardRelation> xmlLfRels;
	Map<Integer, Integer> labelIdConversionMap = new HashMap<Integer, Integer>();

	public ProjectImporter(String pathToImport, String pathToMediaFolder, OfflineProjectsController ctl, IProgressPresenter p) {
		this.ctl = ctl;
		this.pathToImport = pathToImport;
		this.pathToMediaFolder = pathToMediaFolder;
		this.p = p;
		pathToImportMediaFolder = pathToImport + "/" + XMLFiles.MEDIA_FOLDER.getName();
	}

	public void doImport() throws NumberFormatException, XMLStreamException, IOException, SQLException, InvalidValueException,
			InvalidLengthException {
		readXMLLists();
		importXMLObjects();
	}

	private void importXMLObjects() throws SQLException, IOException, InvalidLengthException, InvalidValueException {
		p.changeProgress(0);
		convertProjects();
	}

	public void convertProjects() throws SQLException, InvalidValueException, InvalidLengthException, IOException {
		importProjects = new ArrayList<LearningProject>();
		Iterator<XMLLearningProject> it = xmlProjects.iterator();
		while (it.hasNext()) {
			XMLLearningProject xmlP = it.next();
			p.changeInfo("Importiere " + xmlP.getProjTitle() + "...");
			LearningProject proj = xmlP.toLearningProject(ctl);
			proj.store();
			convertLabels(xmlP, proj);
			convertFlashCards(xmlP, proj);
			p.changeProgress(Math.min(p.getProgress() + 100 / xmlProjects.size(), 100));
		}
	}

	public void convertLabels(XMLLearningProject xmlP, LearningProject proj) throws SQLException, InvalidLengthException {
		for (XMLLabel xmlL : xmlLabels) {
			if (xmlL.getProjId() == xmlP.getProjId()) {
				Label l = xmlL.toLabel(proj);
				l.store();
				labelIdConversionMap.put(xmlL.getId(), l.getId());
			}
		}
	}

	public void convertFlashCards(XMLLearningProject xmlP, LearningProject proj) throws SQLException, IOException {
		for (XMLFlashCard xmlC : xmlFlashCards) {
			if (xmlC.getProjId() == xmlP.getProjId()) {
				String[] picPaths = convertMedia(xmlC);
				FlashCard c = xmlC.toFlashCard(proj, picPaths[0], picPaths[1]);
				c.store();
				convertLfRels(proj, xmlC, c);
			}
		}
	}

	public void convertLfRels(LearningProject proj, XMLFlashCard xmlC, FlashCard c) throws SQLException {
		for (XMLLabelFlashcardRelation xmlR : xmlLfRels) {
			if (xmlR.getCardId() == xmlC.getId()) {
				int currentLabelId = labelIdConversionMap.get(xmlR.getLabelId());
				for (Label l : proj.getLabels()) {
					if (l.getId() == currentLabelId) {
						c.addLabel(l);
					}
				}
			}
		}
	}

	public String[] convertMedia(XMLFlashCard xmlC) {
		String[] picPaths = new String[2];
		// String qPath = null, aPath = null;
		for (XMLMedia xmlM : xmlMedia) {
			if (xmlM.getCardId() == xmlC.getId()) {
				if (xmlM.getPicType() == PicType.QUESTION.getShortForm() && xmlM.getPathToMedia() != null) {
					picPaths[0] = pathToImportMediaFolder + "/" + xmlM.getPathToMedia();
				} else if (xmlM.getPicType() == PicType.ANSWER.getShortForm() && xmlM.getPathToMedia() != null) {
					picPaths[1] = pathToImportMediaFolder + "/" + xmlM.getPathToMedia();
				}
			}
		}
		return picPaths;
	}

	private void readXMLLists() throws NumberFormatException, XMLStreamException, IOException {
		p.changeProgress(0);
		p.changeInfo("Lese Daten...");
		XMLExchanger ex = new XMLExchanger();
		xmlProjects = ex.readProjects(pathToImport + "/" + XMLFiles.LEARNING_PROJECTS.getName());
		p.changeProgress(20);
		if (new File(pathToImport + "/" + XMLFiles.LABELS.getName()).exists()) {
			xmlLabels = ex.readLabels(pathToImport + "/" + XMLFiles.LABELS.getName());
		} else {
			xmlLabels = new ArrayList<XMLLabel>();
		}
		p.changeProgress(40);
		xmlMedia = ex.readMedia(pathToImport + "/" + XMLFiles.MEDIA.getName());
		p.changeProgress(60);
		xmlFlashCards = ex.readFlashcards(pathToImport + "/" + XMLFiles.FLASHCARDS.getName());
		p.changeProgress(80);
		if (new File(pathToImport + "/" + XMLFiles.LABELS_FLASHCARDS.getName()).exists()) {
			xmlLfRels = ex.readLabelFlashcardRelations(pathToImport + "/" + XMLFiles.LABELS_FLASHCARDS.getName());
		} else {
			xmlLfRels = new ArrayList<XMLLabelFlashcardRelation>();
		}
		p.changeProgress(100);
	}
}
