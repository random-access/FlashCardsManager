package core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import db.DBExchanger;
import db.PicType;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import exc.InvalidValueException;

public class LearningProject implements OrderedItem {

	private final ProjectsManager projMgr;
	private final DBExchanger<OrderedItem> dbex;

	private final int id;
	private String title;
	private int numberOfStacks;
	private String tableName;
	private ArrayList<FlashCard> allCards;
	private int maxCharsQuestion;
	private int maxCharsAnswer;

	// CONSTRUCTORS
	// new project
	public LearningProject(ProjectsManager projMgr, String title,
			int numberOfStacks) throws EntryAlreadyThereException,
			ClassNotFoundException, SQLException, InvalidValueException {
		if (!validNoOfStacks(numberOfStacks)) {
			throw new InvalidValueException();
		}
		dbex = new DBExchanger<OrderedItem>(projMgr.getPathToDatabase());
		dbex.createConnection();
		if (dbex.titleAlreadyExisting(title)) {
			throw new EntryAlreadyThereException();
		}
		this.id = projMgr.getNextProjectId();
		this.numberOfStacks = numberOfStacks;
		this.title = title;
		this.tableName = "PROJEKT_" + id;
		this.projMgr = projMgr;
		projMgr.addProject(this);
		dbex.createTable(this.tableName);
		allCards = new ArrayList<FlashCard>();
	}
	
	// import project from external DB
	public LearningProject(ProjectsManager projMgr, LearningProject srcProject, ArrayList<FlashCard> cardArray) throws EntryAlreadyThereException,
			ClassNotFoundException, SQLException, InvalidValueException {
		if (!validNoOfStacks(srcProject.getNumberOfStacks())) {
			throw new InvalidValueException();
		}
		dbex = new DBExchanger<OrderedItem>(projMgr.getPathToDatabase());
		dbex.createConnection();
		if (dbex.titleAlreadyExisting(title)) {
			throw new EntryAlreadyThereException();
		}
		this.id = projMgr.getNextProjectId();
		this.numberOfStacks = srcProject.getNumberOfStacks();
		this.title = srcProject.getTitle();
		this.tableName = "PROJEKT_" + id;
		this.projMgr = projMgr;
		projMgr.addProject(this);
		dbex.createTable(this.tableName);
		allCards = cardArray;
	}

	// import project from local DB
	public LearningProject(ProjectsManager projMgr, int id, String title,
			int numberOfStacks, int maxCharsQuestion, int maxCharsAnswer)
			throws ClassNotFoundException, SQLException, EntryNotFoundException, IOException {
		this.id = id;
		this.numberOfStacks = numberOfStacks;
		this.title = title;
		this.tableName = "PROJEKT_" + id;
		this.projMgr = projMgr;
		this.maxCharsQuestion = maxCharsQuestion;
		this.maxCharsAnswer = maxCharsAnswer;
		dbex = new DBExchanger<OrderedItem>(projMgr.getPathToDatabase());
		dbex.createConnection();
		allCards = dbex.readAllData(this.tableName, this);
	}

	//
	public boolean validNoOfStacks(int noOfStacks) {
		return (noOfStacks > 0);
	}

	// Adds a flashcard to the project
	public void addCard(FlashCard card, String pathToQuestionPic, String pathToAnswerPic) throws SQLException,
			EntryNotFoundException, IOException{
		dbex.addRow(card, this, pathToQuestionPic, pathToAnswerPic);
	}

	// Removes a flashcard from the project
	public void removeCard(FlashCard card) throws EntryNotFoundException,
			SQLException {
		dbex.deleteRow(card, this);
	}

	// Updates a flashcard from the project
	public void updateCard(FlashCard card) throws EntryNotFoundException, SQLException {
	   dbex.updateRow(card, this);
	}
	
	public void updateCard(FlashCard card, String pathToQuestionPic, String pathToAnswerPic) throws EntryNotFoundException,
			SQLException, FileNotFoundException, IOException{
		dbex.updateRow(card, this);
		if (pathToQuestionPic != null) {
		   card.setQuestionPic(true);
		   dbex.updatePic(PicType.QUESTION, card, this, pathToQuestionPic);
		}
		if (pathToAnswerPic != null) {
		   card.setAnswerPic(true);
         dbex.updatePic(PicType.ANSWER, card, this, pathToAnswerPic);
		}
	}
	
	
	
	// Get database exchanger
	public DBExchanger<?> getDBEX () {
		return this.dbex;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// TABLE NAME - Getter
	public String getTableName() {
		return this.tableName;
	}

	// ID - Getter
	@Override
   public int getId() {
		return id;
	}

	// NUMBER OF STACKS - Getter & Setter
	public int getNumberOfStacks() {
		return numberOfStacks;
	}

	public void setNumberOfStacks(int newNumberOfStacks)
			throws InvalidValueException, EntryNotFoundException, SQLException {
		if (!validNoOfStacks(newNumberOfStacks)) {
			throw new InvalidValueException();
		}
		// if there are less stacks than before - shift all cards with too high no of stack
		// into highest stack
		if (this.numberOfStacks > newNumberOfStacks) {
			for (int i = 0; i < allCards.size(); i++) {
				if (allCards.get(i).getStack() > newNumberOfStacks) {
					allCards.get(i).setStack(newNumberOfStacks);
				}
			}
			System.out.println("Fitted cards into remaining stacks");
		}
		this.numberOfStacks = newNumberOfStacks;	
	}

	// NEXT CARD ID - Getter
	public int getNextCardId() throws SQLException {
		int id = 1;
		while (true) {
			if (!dbex.idAlreadyExisting(id, tableName)) {
				return id;
			}
			id++;
		}
	}

	public int getNumberOfCards() throws SQLException {
		return dbex.countRows(tableName);
	}

	public int getNumberOfCards(int stack) throws SQLException {
		return dbex.countRows(tableName, stack);
	}

	public ArrayList<FlashCard> getAllCards() {
		return allCards;
	}

	// MAX CHARS - getter & setter
	public int getMaxCharsQuestion() {
		return maxCharsQuestion;
	}

	public void setMaxCharsQuestion(int newValue) {
		maxCharsQuestion = newValue;
	}

	public int getMaxCharsAnswer() {
		return maxCharsAnswer;
	}

	public void setMaxCharsAnswer(int newValue) {
		maxCharsAnswer = newValue;
	}
}
