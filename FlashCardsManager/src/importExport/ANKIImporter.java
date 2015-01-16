package importExport;

import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

import utils.FileUtils;
import xml.Settings;
import xml.XMLSettingsExchanger;
import core.*;

public class ANKIImporter {

    ProjectsController ctl;
    String pathToDatabase;
    String pathToMediaFolder;
    IProgressPresenter p;

    LearningProject project;
    ArrayList<FlashCard> cards;

    Connection conn;

    public ANKIImporter(String projectName, String pathToDatabase, ProjectsController ctl, IProgressPresenter p) {
        this.ctl = ctl;
        this.pathToDatabase = pathToDatabase;

        if (!(pathToDatabase.endsWith("/") || pathToDatabase.endsWith("\\"))) {
            this.pathToDatabase = this.pathToDatabase + "\\";
        }
        this.p = p;
        try {
            project = new LearningProject(ctl, projectName, 3);
        } catch (SQLException | InvalidValueException e) {
            e.printStackTrace();
        }
        conn = openConnection(pathToDatabase + "/collection.anki2");

    }

    public void doImport() throws NumberFormatException, XMLStreamException, IOException, SQLException, InvalidValueException,
            InvalidLengthException {

        readAndStoreCards();

    }

    public Map<String, Integer> parseImageString(String istr) {
        // filtern von {}"
        istr = istr.replace("{", "");
        istr = istr.replace("}", "");
        istr = istr.replace("\"", "");
        // aufsplitten in Wertepaare
        Map<String, Integer> images = new HashMap<String, Integer>();
        String[] pairs = istr.split(",");
        for (String pair : pairs) {
            String[] splittedPair = pair.split(":");
            splittedPair[0] = splittedPair[0].trim();
            splittedPair[1] = splittedPair[1].trim();
            // speichern in der Map nur, wenn nicht leer
            try {
                if (splittedPair[0] != "" && splittedPair[1] != "") {
                    images.put(splittedPair[1], Integer.parseInt(splittedPair[0]));
                }

            } catch (NumberFormatException e) {

                e.printStackTrace();

            }
        }
        System.out.println("done parsing ...");
        return images;
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
        System.out.println("Parsing Images...");
        return parseImageString(mediaText);
    }

    private void readAndStoreCards() {
        Statement query;
        ResultSet result = null;
        try {
            project.store();
            query = conn.createStatement();
            query.execute("SELECT sfld,flds FROM cards INNER JOIN notes ON CARDS.nid = NOTES.id");
            result = query.getResultSet();
            Map<String, Integer> images = readImages();

            while (result.next()) {
                System.out.println(result.getString("sfld"));
                System.out.println(result.getString("flds"));

                // adding html framework
                FlashCard card = new FlashCard(project, "<html> <head>  </head>  <body>" + result.getString("sfld")
                        + "</body></html>", "<html> <head>  </head>  <body>" + result.getString("flds") + "</body></html>", null,
                        null, 0, 0);
                // filtering characters smaller than 32 (happened on import and
                // gives errors)
                for (int i = 0; i < card.getQuestionWidth(); i++) {
                    if (card.getQuestion().charAt(i) < 32 && card.getQuestion().charAt(i) != 10
                            && card.getQuestion().charAt(i) != 13) {
                        // remove control sequence if not CR or LF
                        card.setQuestion(card.getQuestion().substring(0, i) + card.getQuestion().substring(i));
                    }
                    if (card.getAnswer().charAt(i) < 32 && card.getAnswer().charAt(i) != 10 && card.getAnswer().charAt(i) != 13) {
                        // remove control sequence if not CR or LF
                        card.setAnswer(card.getAnswer().substring(0, i) + card.getAnswer().substring(i));
                    }
                }

                // adding pictures to cards, if any
                // lookup image url in question or answer
                try {
                    String question = card.getQuestion();
                    String answer = card.getAnswer();
                    // lookup image url in question or answer
                    List<String> aImages = new ArrayList<String>();
                    List<String> qImages = new ArrayList<String>();
                    for (String imgName : images.keySet()) {
                        boolean found = false;
                        if (question.contains(imgName)) {
                            qImages.add(images.get(imgName).toString());
                            int startpos = question.indexOf("<img");
                            int endpos = question.indexOf("/>", startpos);
                            while (startpos != -1 && endpos != -1) {
                                question = question.substring(0, startpos) + question.substring(endpos + 2);
                                startpos = question.indexOf("<img");
                                endpos = question.indexOf("/>", startpos);
                            }
                            card.setQuestion(question);
                            found = true;
                        }
                        if (answer.contains(imgName)) {
                            aImages.add(images.get(imgName).toString());
                            int startpos = answer.indexOf("<img");
                            int endpos = answer.indexOf("/>", startpos);
                            while (startpos != -1 && endpos != -1) {
                                answer = answer.substring(0, startpos) + answer.substring(endpos + 2, answer.length());
                                startpos = answer.indexOf("<img");
                                endpos = answer.indexOf("/>", startpos);
                            }
                            card.setAnswer(answer);
                            found = true;
                        }
                        if (!qImages.isEmpty()) {
                            card.setPathToQuestionPic(combineImage(qImages));
                        }
                        if (!aImages.isEmpty()) {
                            card.setPathToAnswerPic(combineImage(aImages));
                        }

                    }

                    project.addCard(card);
                    card.store();
                } catch (Exception e) {
                    // something happened. We rename back the files and end
                    e.printStackTrace();
                } finally {
                    // // renaming the images back
                    // for (Integer imgName : images.values()) {
                    // FileUtils.movePicFile(pathToDatabase + imgName.toString()
                    // + ".jpg", pathToDatabase + imgName.toString());
                    // }
                }

            }

            result.close();
            query.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidLengthException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Result: " + result.toString());

    }

    public String combineImage(List<String> images) throws IOException {
        // reading the images to BufferedImage[] and painting them to a new one
        List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
        // estimating width and height
        int width = 0, height = 0;
        for (String image : images) {
            BufferedImage bufIm = ImageIO.read(new File(pathToDatabase + "/" + image));
            bufferedImages.add(bufIm);
            height += bufIm.getHeight();
            width = (width < bufIm.getWidth()) ? bufIm.getWidth() : width;
        }

        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = combinedImage.getGraphics();
        int actualHeight = 0;
        for (BufferedImage buf : bufferedImages) {
            g.drawImage(buf, 0, actualHeight, buf.getWidth(), actualHeight + buf.getHeight(), 0, 0, buf.getWidth(),
                    buf.getHeight(), null);
        }
        g.dispose();
        String combinedFilename = pathToDatabase + "/temp"
                + combinedImage.toString().substring(0, combinedImage.toString().indexOf(":")) + ".jpg";
        ImageIO.write(combinedImage, "jpg", new File(combinedFilename));
        return combinedFilename;
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

    public static void main(String args[]) {
        String APP_FOLDER = FileUtils.appDirectory("Lernkarten");
        String DEFAULT_SETTINGS_PATH = APP_FOLDER + "/settings.xml";
        String PATH_TO_MEDIA = APP_FOLDER + "/media";
        Settings currentSettings;
        ANKIImporter anki = null;
        try {
            currentSettings = XMLSettingsExchanger.readConfig(DEFAULT_SETTINGS_PATH);

            anki = new ANKIImporter("TestImportANKI", "resources/ANKITestFolder/1895/", new ProjectsController(
                    currentSettings.getPathToDatabase(), PATH_TO_MEDIA), null);
        } catch (NumberFormatException | XMLStreamException | IOException | SQLException | ClassNotFoundException e1) {
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
