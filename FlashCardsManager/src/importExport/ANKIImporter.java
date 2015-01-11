package importExport;

import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import utils.FileUtils;
import xml.Settings;
import xml.XMLSettingsExchanger;
import core.FlashCard;
import core.LearningProject;
import core.ProjectsController;

public class ANKIImporter {

	ProjectsController ctl;
	String pathToDatabase;
	String pathToMediaFolder;
	IProgressPresenter p;

	LearningProject project;
	ArrayList<FlashCard> cards;
	ArrayList<XMLLearningProject> xmlProjects;
	ArrayList<XMLFlashCard> xmlFlashCards;
	ArrayList<XMLMedia> xmlMedia;
	Connection conn;

	public ANKIImporter(String projectName, String pathToDatabase,
			ProjectsController ctl, IProgressPresenter p) {
		this.ctl = ctl;
		this.pathToDatabase = pathToDatabase;
		this.p = p;
		try {
			project = new LearningProject(ctl, projectName, 3);
		} catch (SQLException | InvalidValueException e) {
			e.printStackTrace();
		}
		conn = openConnection(pathToDatabase + "collection.anki2");

	}

	public void doImport() throws NumberFormatException, XMLStreamException,
			IOException, SQLException, InvalidValueException,
			InvalidLengthException {
		readCards();
		storeObjects();
	}

	private void storeObjects() {
		// TODO Auto-generated method stub

	}

	public Map<String, Integer> readImages() {
		// reading the file "media" from importPath
		String mediaText = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pathToDatabase + "media"));
			mediaText = br.readLine();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(mediaText);
		return null;
	}

	private void readCards() {
		Statement query;
		ResultSet result = null;
		try {
			project.store();
			query = conn.createStatement();

			query.execute("SELECT sfld,flds FROM cards INNER JOIN notes ON CARDS.nid = NOTES.id");

			result = query.getResultSet();

			while (result.next()) {
				System.out.println(result.getString("sfld"));
				System.out.println(result.getString("flds"));
				// adding html framework
				FlashCard card = new FlashCard(project,
						"<html> <head>  </head>  <body>"
								+ result.getString("sfld") + "</body></html>",
						"<html> <head>  </head>  <body>"
								+ result.getString("flds") + "</body></html>",
						null, null, 0, 0);
				project.addCard(card);
				card.store();
				// adding pictures to cards

			}

			result.close();
			query.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Result: " + result.toString());

	}

	private Connection openConnection(String pathToDatabase) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("trying to open Database " + pathToDatabase);
			c = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Opened database successfully");
		return c;
	}

	// private void convertXMLObjects() throws SQLException, IOException,
	// InvalidLengthException, InvalidValueException {
	// p.changeProgress(0);
	// importProjects = new ArrayList<LearningProject>();
	// Iterator<XMLLearningProject> it = xmlProjects.iterator();
	// while (it.hasNext()) {
	// XMLLearningProject xmlP = it.next();
	// p.changeInfo("Importiere " + xmlP.getProjTitle() + "...");
	// LearningProject proj = xmlP.toLearningProject(ctl);
	// proj.store();
	// for (XMLFlashCard xmlC : xmlFlashCards) {
	// System.out.println("for " + xmlC.getId());
	// if (xmlC.getProjId() == xmlP.getProjId()) {
	// System.out.println("if " + xmlP.getProjId());
	// String qPath = null, aPath = null;
	// for (XMLMedia xmlM : xmlMedia) {
	// System.out.println("for: " + xmlM.getMediaId());
	// if (xmlM.getCardId() == xmlC.getId()) {
	// if (xmlM.getPicType() == PicType.QUESTION
	// .getShortForm()
	// && xmlM.getPathToMedia() != null) {
	// qPath = pathToImportMediaFolder + "/"
	// + xmlM.getPathToMedia();
	// } else if (xmlM.getPicType() == PicType.ANSWER
	// .getShortForm()
	// && xmlM.getPathToMedia() != null) {
	// aPath = pathToImportMediaFolder + "/"
	// + xmlM.getPathToMedia();
	// }
	// }
	// }
	// FlashCard c = xmlC.toFlashCard(proj, qPath, aPath);
	// c.store();
	// System.out.println("Stored " + xmlC.getId());
	// }
	// }
	// p.changeProgress(Math.min(
	// p.getProgress() + 100 / xmlProjects.size(), 100));
	// }
	// }

	// private void readXMLLists() throws NumberFormatException,
	// XMLStreamException, IOException {
	// p.changeProgress(0);
	// p.changeInfo("Lese Daten...");
	// XMLExchanger ex = new XMLExchanger();
	// xmlProjects = ex.readProjects(pathToImport + "/"
	// + XMLFiles.LEARNING_PROJECTS.getName());
	// p.changeProgress(33);
	// xmlMedia = ex.readMedia(pathToImport + "/" + XMLFiles.MEDIA.getName());
	// p.changeProgress(66);
	// xmlFlashCards = ex.readFlashcards(pathToImport + "/"
	// + XMLFiles.FLASHCARDS.getName());
	// p.changeProgress(99);
	// }

	public static void main(String args[]) {
		String APP_FOLDER = FileUtils.appDirectory("Lernkarten");
		String DEFAULT_SETTINGS_PATH = APP_FOLDER + "/settings.xml";
		String PATH_TO_MEDIA = APP_FOLDER + "/media";
		Settings currentSettings;
		ANKIImporter anki = null;
		try {
			currentSettings = XMLSettingsExchanger
					.readConfig(DEFAULT_SETTINGS_PATH);

			anki = new ANKIImporter("TestImportANKI",
					"resources/ANKITestFolder/1895/",
					new ProjectsController(currentSettings.getPathToDatabase(),
							PATH_TO_MEDIA), null);
		} catch (NumberFormatException | XMLStreamException | IOException
				| SQLException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			anki.doImport();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
