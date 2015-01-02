package importExport;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.apache.derby.tools.sysinfo;

import storage.PicType;
import core.*;

public class ProjectImporter {
	ProjectsController ctl;
	String pathToImport;
	String pathToImportMediaFolder;
	String pathToMediaFolder;

	ArrayList<LearningProject> importProjects;
	ArrayList<XMLLearningProject> xmlProjects;
	ArrayList<XMLFlashCard> xmlFlashCards;
	ArrayList<XMLMedia> xmlMedia;

	public ProjectImporter(String pathToImport, String pathToMediaFolder, ProjectsController ctl) {
		this.ctl = ctl;
		this.pathToImport = pathToImport;
		this.pathToMediaFolder = pathToMediaFolder;
		pathToImportMediaFolder = pathToImport + "/" + XMLFiles.MEDIA_FOLDER.getName();
	}

	public void doImport() throws NumberFormatException, XMLStreamException, IOException, SQLException {
		System.out.println("in importer");
		readXMLLists();
		convertXMLObjects();
		System.out.println("success");
	}

	private void convertXMLObjects() throws SQLException, IOException {
		importProjects = new ArrayList<LearningProject>();
		Iterator<XMLLearningProject> it = xmlProjects.iterator();
		while (it.hasNext()) {
			XMLLearningProject xmlP = it.next();
			LearningProject p = xmlP.toLearningProject(ctl);
			p.store();
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
					FlashCard c = xmlC.toFlashCard(p, qPath, aPath);
					c.store();
					System.out.println("Stored " + xmlC.getId());
				}
			}
		}
	}

	private void readXMLLists() throws NumberFormatException, XMLStreamException, IOException {
		XMLExchanger ex = new XMLExchanger();
		xmlProjects = ex.readProjects(pathToImport + "/" + XMLFiles.LEARNING_PROJECTS.getName());
		xmlMedia = ex.readMedia(pathToImport + "/" + XMLFiles.MEDIA.getName());
		xmlFlashCards = ex.readFlashcards(pathToImport + "/" + XMLFiles.FLASHCARDS.getName());
		System.out.println("read xml");
	}

}
