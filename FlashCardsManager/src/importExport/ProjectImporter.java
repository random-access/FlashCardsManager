package importExport;

import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import storage.PicType;
import core.*;

public class ProjectImporter {
	ProjectsController ctl;
	String pathToImport;
	String pathToImportMediaFolder;
	String pathToMediaFolder;
	IProgressPresenter p;

	ArrayList<LearningProject> importProjects;
	ArrayList<XMLLearningProject> xmlProjects;
	ArrayList<XMLFlashCard> xmlFlashCards;
	ArrayList<XMLMedia> xmlMedia;

	public ProjectImporter(String pathToImport, String pathToMediaFolder, ProjectsController ctl, IProgressPresenter p) {
		this.ctl = ctl;
		this.pathToImport = pathToImport;
		this.pathToMediaFolder = pathToMediaFolder;
		this.p = p;
		pathToImportMediaFolder = pathToImport + "/" + XMLFiles.MEDIA_FOLDER.getName();
	}

	public void doImport() throws NumberFormatException, XMLStreamException, IOException, SQLException, InvalidValueException, InvalidLengthException {
		readXMLLists();
		convertXMLObjects();
	}

	private void convertXMLObjects() throws SQLException, IOException, InvalidLengthException, InvalidValueException {
		p.changeProgress(0);
		importProjects = new ArrayList<LearningProject>();
		Iterator<XMLLearningProject> it = xmlProjects.iterator();
		while (it.hasNext()) {	
			XMLLearningProject xmlP = it.next();
			p.changeInfo("Importiere " + xmlP.getProjTitle() + "...");
			LearningProject proj = xmlP.toLearningProject(ctl);
			proj.store();
			for (XMLFlashCard xmlC : xmlFlashCards) {
				System.out.println("for " + xmlC.getId());
				if (xmlC.getProjId() == xmlP.getProjId()) {
					System.out.println("if " + xmlP.getProjId());
					String qPath = null, aPath = null;
					for (XMLMedia xmlM : xmlMedia) {
						System.out.println("for: " + xmlM.getMediaId());
						if (xmlM.getCardId() == xmlC.getId()) {
							if (xmlM.getPicType() == PicType.QUESTION.getShortForm() && xmlM.getPathToMedia() != null) {
								qPath = pathToImportMediaFolder + "/" + xmlM.getPathToMedia();
							} else if (xmlM.getPicType() == PicType.ANSWER.getShortForm() && xmlM.getPathToMedia() != null) {
								aPath = pathToImportMediaFolder + "/" + xmlM.getPathToMedia();
							}
						}
					}
					FlashCard c = xmlC.toFlashCard(proj, qPath, aPath);
					c.store();
					System.out.println("Stored " + xmlC.getId());
				}
			}
			p.changeProgress(Math.min(p.getProgress() + 100/xmlProjects.size(), 100));
		}
	}

	private void readXMLLists() throws NumberFormatException, XMLStreamException, IOException {
		p.changeProgress(0);
		p.changeInfo("Lese Daten...");
		XMLExchanger ex = new XMLExchanger();
		xmlProjects = ex.readProjects(pathToImport + "/" + XMLFiles.LEARNING_PROJECTS.getName());
		p.changeProgress(33);
		xmlMedia = ex.readMedia(pathToImport + "/" + XMLFiles.MEDIA.getName());
		p.changeProgress(66);
		xmlFlashCards = ex.readFlashcards(pathToImport + "/" + XMLFiles.FLASHCARDS.getName());
		p.changeProgress(99);
	}

}
