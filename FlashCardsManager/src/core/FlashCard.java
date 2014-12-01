package core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import db.PicType;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;

public class FlashCard implements OrderedItem {

	private final int id;
	private String question;
	private String answer;
	private boolean hasQuestionPic;
	private boolean hasAnswerPic;
	private LearningProject proj;
	private int stack;

	// CONSTRUCTORS

	// constructs a new flashcard
	public FlashCard(LearningProject proj, String question, String answer,
			String pathToQuestionPic, String pathToAnswerPic)
			throws EntryAlreadyThereException, EntryNotFoundException,
			SQLException, IOException{
		this.id = proj.getNextCardId();
		this.question = question;
		this.answer = answer;
		if (pathToQuestionPic != null) {
			hasQuestionPic = true;
		}
		if (pathToAnswerPic != null) {
			hasAnswerPic = true;
		}
		this.proj = proj;
		this.stack = 1;
		proj.addCard(this, pathToQuestionPic, pathToAnswerPic);
	}

	// restores a flashcard from database
	public FlashCard(LearningProject proj, int id, int stack, String question,
			String answer, boolean hasQuestionPic, boolean hasAnswerPic) {
		this.id = id;
		this.stack = stack;
		this.question = question;
		this.answer = answer;
		this.hasQuestionPic = hasQuestionPic;
		this.hasAnswerPic = hasAnswerPic;
		this.proj = proj;
	}

	// TODO Auto-generated constructor stub
	public FlashCard(LearningProject proj, int id, int stack, String question,
			String answer) {
		this.id = id;
		this.stack = stack;
		this.question = question;
		this.answer = answer;
	}
	
	// Picture - Getter & Setter
	public BufferedImage getQuestionPic () throws SQLException {
		if (!hasQuestionPic) {
			return null;
		}
		return proj.getDBEX().getPic(PicType.QUESTION, this, proj);
	}
	
	public BufferedImage getAnswerPic () throws SQLException {
		if (!hasAnswerPic) {
			return null;
		}
		return proj.getDBEX().getPic(PicType.ANSWER, this, proj);
	}
	
	public void deletePicture (PicType type) throws SQLException {
	   switch(type) {
	   case QUESTION:
	      hasQuestionPic = false;
	      proj.getDBEX().deletePic(type, this, proj);
	      break;
	   case ANSWER:
	      hasAnswerPic = false;
	      proj.getDBEX().deletePic(type, this, proj);
	      break;
	   }
	}

	// ID - Getter
	@Override
	public int getId() {
		return this.id;
	}

	// STACK - Getter & Setter
	public int getStack() {
		return this.stack;
	}

	public void setStack(int stack) throws EntryNotFoundException, SQLException{
		this.stack = stack;
		proj.updateCard(this);
	}

	public void nextLevel() throws EntryNotFoundException, SQLException {
		int maxStack = proj.getNumberOfStacks();
		if (stack < maxStack) {
			stack++;
			System.out.println("Next Level: Karte " + this.id
					+ " ist jetzt in Stapel " + stack);
		}
		proj.updateCard(this);
	}

	public void levelDown() throws EntryNotFoundException, SQLException {
		if (stack > 1) {
			stack--;
			System.out.println("Level down: Karte " + this.id
					+ " ist jetzt in Stapel " + stack);
		}
		proj.updateCard(this);
	}

	// QUESTION - Getter & Setter
	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getQuestionLength() {
		return this.question.length();
	}

	// ANSWER - Getter & Setter
	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getAnswerLength() {
		return this.answer.length();
	}

	// QUESTIONPIC - Getter & setter
	public boolean hasQuestionPic() {
		return this.hasQuestionPic;
	}
	
	public void setQuestionPic(boolean hasQuestionPic) {
	   this.hasQuestionPic = hasQuestionPic;
	}

	// ANSWERPIC - Getter & setter
	public boolean hasAnswerPic() {
		return this.hasAnswerPic;
	}
	
	public void setAnswerPic(boolean hasAnswerPic) {
      this.hasAnswerPic = hasAnswerPic;
   }

	// PROJECT - Getter
	public LearningProject getLearningProject() {
		return proj;
	}

}
