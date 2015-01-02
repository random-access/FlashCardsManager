package importExport;

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
   ArrayList<LearningProject> exportProjects;
   String pathToExportFolder;
   String pathToMediaFolder;
   
   ArrayList<XMLLearningProject> xmlProjects;
   ArrayList<XMLFlashCard> xmlFlashCards;
   ArrayList<XMLMedia> xmlMedia;
   
   public ProjectExporter (ArrayList<LearningProject> exportProjects, String pathToMediaFolder, String pathToExportFolder) {
      this.exportProjects = exportProjects;
      this.pathToExportFolder = pathToExportFolder;
      this.pathToMediaFolder = pathToMediaFolder;
   }
   
   public void doExport() throws SQLException, XMLStreamException, IOException {
      Files.createDirectory(Paths.get(pathToExportFolder));
      loadXMLLists();
      writeXMLFiles();
      copyPics();
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
      XMLExchanger ex = new XMLExchanger();
      ex.writeProjects(pathToExportFolder + "/" + XMLFiles.LEARNING_PROJECTS.getName(), xmlProjects);
      ex.writeFlashcards(pathToExportFolder + "/" + XMLFiles.FLASHCARDS.getName(), xmlFlashCards);
      ex.writeMedia(pathToExportFolder + "/" + XMLFiles.MEDIA.getName(), xmlMedia);
   }

   private void loadXMLLists() throws SQLException {
      xmlProjects = new ArrayList<XMLLearningProject>();
      xmlFlashCards = new ArrayList<XMLFlashCard>();
      xmlMedia = new ArrayList<XMLMedia>();
      Iterator<LearningProject> it = exportProjects.iterator();
      while(it.hasNext()) {
         LearningProject p = it.next();
         xmlProjects.add(p.toXMLLearningProject());
         for (FlashCard c : p.getAllCards()) {
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
      }
   }
}
