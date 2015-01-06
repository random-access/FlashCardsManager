package core;

import importExport.XMLFlashCard;
import importExport.XMLMedia;

import java.io.IOException;
import java.sql.SQLException;

import storage.*;

public class FlashCard {

	private final DBExchanger dbex;
	private final MediaExchanger mex;
	private int id;
	private LearningProject proj;
	private int stack;
	private String question;
	private String answer;
	private int questionWidth;
	private int answerWidth;

	private String pathToQuestionPic;
	private String pathToAnswerPic;

	// CONSTRUCTORS
	// constructs a new flashcard
	public FlashCard(LearningProject proj, String question, String answer, String pathToQuestionPic, String pathToAnswerPic,
			int questionWidth, int answerWidth) throws SQLException {
		dbex = proj.getDBEX();
		mex = proj.getMex();
		this.id = dbex.nextFlashcardId();
		this.proj = proj;
		this.stack = 1;
		this.question = question;
		this.answer = answer;
		this.questionWidth = questionWidth;
		this.answerWidth = answerWidth;

		this.pathToQuestionPic = pathToQuestionPic;
		this.pathToAnswerPic = pathToAnswerPic;
	}

	// restores a flashcard from database
	public FlashCard(int id, LearningProject proj, int stack, String question, String answer, String pathToQuestionPic,
			String pathToAnswerPic, int questionWidth, int answerWidth) {
		dbex = proj.getDBEX();
		mex = proj.getMex();
		this.id = id;
		this.proj = proj;
		this.stack = stack;
		this.question = question;
		this.answer = answer;
		this.questionWidth = questionWidth;
		this.answerWidth = answerWidth;

		this.pathToQuestionPic = pathToQuestionPic;
		this.pathToAnswerPic = pathToAnswerPic;
	}

	public void store() throws SQLException, IOException {
		proj.addCard(this);
		mex.storePic(this, PicType.QUESTION);
		mex.storePic(this, PicType.ANSWER);
		dbex.addFlashcard(this);
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
			System.out.println("Next Level: Karte " + this.id + " ist jetzt in Stapel " + stack);
		}
	}

	public void levelDown() throws SQLException {
		if (stack > 1) {
			stack--;
			System.out.println("Level down: Karte " + this.id + " ist jetzt in Stapel " + stack);
		}
	}

	public XMLFlashCard toXMLFlashcard() {
		XMLFlashCard card = new XMLFlashCard();
		card.setId(this.id);
		card.setProjId(this.proj.getId());
		card.setStack(this.stack);
		if (question.equals("")) {
			card.setQuestion(" ");
		} else {
			card.setQuestion(this.question);
		}
		if (answer.equals("")) {
			card.setAnswer(" ");
		} else {
			card.setAnswer(this.answer);
		}
		card.setCustomWidthQuestion(this.questionWidth);
		card.setCustomWidthAnswer(this.answerWidth);
		return card;
	}

	public XMLMedia getXMLQuestionMedia() throws SQLException {
		return dbex.getPic(this, PicType.QUESTION);
	}

	public XMLMedia getXMLAnswerMedia() throws SQLException {
		return dbex.getPic(this, PicType.ANSWER);
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

	public String toString() {
		return "ID: " + this.getId() + ", PROJECT: " + this.getProj().getTitle() + ", STACK " + this.getStack() + " QUESTION: "
				+ this.getQuestion() + ", ANSWER: " + this.getAnswer() + ", PATH TO QUESTION PIC: " + this.getPathToQuestionPic()
				+ ", PATH TO ANSWER PIC: " + this.getPathToAnswerPic() + ", CUSTOM_WIDTH_Q: " + this.getQuestionWidth()
				+ ", CUSTOM_WIDTH_A: " + this.getAnswerWidth();
	}

}
