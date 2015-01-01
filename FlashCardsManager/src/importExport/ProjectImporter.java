package importExport;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import storage.PicType;
import core.FlashCard;
import core.LearningProject;
import core.ProjectsController;

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
         System.out.println(p.getId());
         p.store();
         for (XMLFlashCard xmlC : xmlFlashCards) {
            if (xmlC.getProjId() == xmlP.getProjId()) {
               String qPath = null, aPath = null;
               for (XMLMedia xmlM : xmlMedia) {
                  if (xmlM.getCardId() == xmlC.getId()) {
                     if (xmlM.getPicType() == PicType.QUESTION.getShortForm()) {
                        if (xmlM.getPathToMedia() != null) {
                           qPath = pathToImportMediaFolder + "/" + xmlM.getPathToMedia();
                        }
                        xmlMedia.remove(xmlM);
                     } else if (xmlM.getPicType() == PicType.ANSWER.getShortForm()) {
                        if (xmlM.getPathToMedia() != null) {
                           aPath = pathToImportMediaFolder + "/" + xmlM.getPathToMedia();
                        }
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


   }
   
   
   
}
