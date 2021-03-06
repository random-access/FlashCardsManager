package org.random_access.flashcardsmanager_desktop.core;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;

import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.importExport.*;
import org.random_access.flashcardsmanager_desktop.storage.*;
import org.random_access.flashcardsmanager_desktop.utils.HTMLToText;

public class FlashCard {

    private final IDBExchanger dbex;
    private final XMLDBExchanger xmlDbex;
    private final IMediaExchanger mex;
    private int id;
    private LearningProject proj;
    private int stack;
    private String question;
    private String answer;
    private int questionWidth;
    private int answerWidth;

    private ArrayList<Label> labels;

    private String pathToQuestionPic;
    private String pathToAnswerPic;

    // CONSTRUCTORS
    // constructs a new flashcard
    public FlashCard(LearningProject proj, String question, String answer, String pathToQuestionPic, String pathToAnswerPic,
            int questionWidth, int answerWidth) throws SQLException {
        dbex = proj.getDBEX();
        xmlDbex = (XMLDBExchanger) dbex;
        mex = proj.getMex();
        this.id = dbex.nextId(TableType.FLASHCARDS);
        this.proj = proj;
        this.stack = 1;
        this.question = question;
        this.answer = answer;
        this.questionWidth = questionWidth;
        this.answerWidth = answerWidth;

        this.labels = new ArrayList<Label>();

        this.pathToQuestionPic = pathToQuestionPic;
        this.pathToAnswerPic = pathToAnswerPic;
    }

    // restores a flashcard from database
    public FlashCard(int id, LearningProject proj, int stack, String question, String answer, String pathToQuestionPic,
            String pathToAnswerPic, int questionWidth, int answerWidth) throws SQLException {
        dbex = proj.getDBEX();
        xmlDbex = (XMLDBExchanger) dbex;
        mex = proj.getMex();
        this.id = id;
        this.proj = proj;
        this.stack = stack;
        this.question = question;
        this.answer = answer;
        this.questionWidth = questionWidth;
        this.answerWidth = answerWidth;
        this.labels = dbex.getAllLabels(this);
        this.pathToQuestionPic = pathToQuestionPic;
        this.pathToAnswerPic = pathToAnswerPic;
    }

    public void store() throws SQLException, IOException {
        proj.addCard(this);
        mex.storePic(this, PicType.QUESTION);
        mex.storePic(this, PicType.ANSWER);
        dbex.addFlashcard(this);
    }

    public void transferTo(LearningProject newProj, boolean keepSuccess, boolean keepLabels) throws SQLException, IOException {
        if (keepLabels) {
            newProj.loadLabels();
            for (Label l : labels) {
                if (!newProj.getLabels().contains(l)) {
                    newProj.addLabel(l);
                }
            }
            // TODO add label to card
        }
        proj.removeCard(this);
        setProj(newProj);
        for (Label l : labels) {
            this.addLabel(l);
        }
        setStack(keepSuccess ? Math.min(getStack(), newProj.getNumberOfStacks()) : 1);
        mex.transferPic(this, PicType.QUESTION);
        mex.transferPic(this, PicType.ANSWER);
        dbex.updateFlashcard(this);
    }

    public void update() throws SQLException, IOException {
        mex.storePic(this, PicType.QUESTION);
        mex.storePic(this, PicType.ANSWER);
        dbex.updateFlashcard(this);
    }

    public void delete() throws SQLException, IOException {
        mex.deleteAllPics(this);
        proj.removeCard(this);
        dbex.deleteFlashcard(this);
    }

    // ID - Getter
    public int getId() {
        return this.id;
    }

    public int getNumberInProj() throws SQLException {
        return dbex.getCardNumberInProject(this);
    }

    public void nextLevel() throws SQLException {
        int maxStack = proj.getNumberOfStacks();
        if (stack < maxStack) {
            stack++;
        }
    }

    public void levelDown() throws SQLException {
        if (stack > 1) {
            stack--;
        }
    }

    public XMLFlashCard toXMLFlashcard() {
        XMLFlashCard card = new XMLFlashCard();
        card.setId(this.id);
        card.setProjId(this.proj.getId());
        card.setStack(this.stack);
        card.setQuestion(question.equals("") ? " " : this.question);
        card.setAnswer(answer.equals("") ? " " : this.answer);
        card.setCustomWidthQuestion(this.questionWidth);
        card.setCustomWidthAnswer(this.answerWidth);
        return card;
    }

    public XMLMedia getXMLQuestionMedia() throws SQLException {
        return xmlDbex.getPic(this, PicType.QUESTION);
    }

    public XMLMedia getXMLAnswerMedia() throws SQLException {
        return xmlDbex.getPic(this, PicType.ANSWER);
    }

    public ArrayList<XMLLabelFlashcardRelation> getXMLLfRelations() throws SQLException {
        return xmlDbex.getXMLLfRelations(this);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPathToQuestionPic() {
        return pathToQuestionPic;
    }

    public void setPathToQuestionPic(String pathToQuestionPic) {
        this.pathToQuestionPic = pathToQuestionPic;
    }

    public String getPathToAnswerPic() {
        return pathToAnswerPic;
    }

    public void setPathToAnswerPic(String pathToAnswerPic) {
        this.pathToAnswerPic = pathToAnswerPic;
    }

    public LearningProject getProj() {
        return proj;
    }

    public void setProj(LearningProject proj) {
        this.proj = proj;
    }

    public int getStack() {
        return stack;
    }

    public void setStack(int stack) {
        this.stack = stack;
    }

    public int getQuestionWidth() {
        return questionWidth;
    }

    public void setQuestionWidth(int questionWidth) {
        this.questionWidth = questionWidth;
    }

    public int getAnswerWidth() {
        return answerWidth;
    }

    public void setAnswerWidth(int answerWidth) {
        this.answerWidth = answerWidth;
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }

    public void synchronizeLabels(ArrayList<Label> newLabels) throws SQLException {
        for (int i = 0; i < newLabels.size(); i++) {
            if (!this.labels.contains(newLabels.get(i))) {
                dbex.addLabelToFlashcard(newLabels.get(i), this);
                labels.add(newLabels.get(i));
            }
        }
        for (int i = labels.size() - 1; i >= 0; i--) {
            if (!newLabels.contains(labels.get(i))) {
                dbex.deleteLabelFromFlashCard(labels.get(i), this);
                labels.remove(labels.get(i));
            }
        }
    }

    public void addLabel(Label label) throws SQLException {
        if (!this.labels.contains(label)) {
            dbex.addLabelToFlashcard(label, this);
            labels.add(label);
        }
    }

    public void removeLabel(Label label) throws SQLException {
        if (this.labels.contains(label)) {
            dbex.deleteLabelFromFlashCard(label, this);
            labels.remove(label);
        }
    }

    public String toString() {
        return "ID: " + this.getId() + ", PROJECT: " + this.getProj().getTitle() + ", STACK " + this.getStack() + " QUESTION: "
                + this.getQuestion() + ", ANSWER: " + this.getAnswer() + ", PATH TO QUESTION PIC: " + this.getPathToQuestionPic()
                + ", PATH TO ANSWER PIC: " + this.getPathToAnswerPic() + ", CUSTOM_WIDTH_Q: " + this.getQuestionWidth()
                + ", CUSTOM_WIDTH_A: " + this.getAnswerWidth();
    }

    public Object getQuestionInPlainText() {
        StringReader in = new StringReader(this.getQuestion());
        HTMLToText parser = new HTMLToText();
        String question = null;
        try {
            parser.parse(in);
            in.close();
            question = parser.getText();
        } catch (IOException ioe) {
            CustomErrorHandling.showParseError(null, ioe);
            question = this.getQuestion();
        }
        return question;
    }

}
