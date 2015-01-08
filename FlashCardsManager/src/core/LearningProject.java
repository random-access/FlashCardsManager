package core;

import gui.helpers.IProgressPresenter;
import importExport.XMLLearningProject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import storage.DBExchanger;
import storage.MediaExchanger;
import exc.InvalidLengthException;
import exc.InvalidValueException;

public class LearningProject {

	private final DBExchanger dbex;
	private final MediaExchanger mex;
	private final ProjectsController ctl;

	private final int id;
	private String title;
	private int numberOfStacks;
	private ArrayList<FlashCard> allCards;

	// CONSTRUCTORS
	// new project
	public LearningProject(ProjectsController ctl, String title, int numberOfStacks) throws SQLException, InvalidValueException {
		this.ctl = ctl;
		dbex = ctl.getDbex();
		mex = ctl.getMex();
		id = dbex.nextProjectId();
		this.title = title;
		if (!validNoOfStacks(numberOfStacks)) {
			throw new InvalidValueException();
		} else {
			this.numberOfStacks = numberOfStacks;
		}
		allCards = new ArrayList<FlashCard>();
	}

	// restore from database
	public LearningProject(ProjectsController ctl, int id, String title, int numberOfStacks) {
		this.ctl = ctl;
		dbex = ctl.getDbex();
		mex = ctl.getMex();
		this.id = id;
		this.title = title;
		this.numberOfStacks = numberOfStacks;
	}

	public void loadFlashcards(IProgressPresenter t) throws SQLException {
		allCards = dbex.getAllCards(this, t);
	}

	public void store() throws SQLException, InvalidLengthException {
		dbex.addProject(this);
		ctl.addProject(this);
	}

	public void update() throws SQLException {
		dbex.updateProject(this);
	}

	public void delete() throws SQLException, IOException {
		ctl.removeProject(this);
		mex.deleteAllPics(this);
		dbex.deleteProject(this);
	}

	public XMLLearningProject toXMLLearningProject() {
		XMLLearningProject proj = new XMLLearningProject();
		proj.setProjId(id);
		proj.setProjTitle(title);
		proj.setNoOfStacks(numberOfStacks);
		return proj;
	}

	// Adds a flashcard to the project
	public void addCard(FlashCard card) {
		System.out.println(allCards);
		allCards.add(card);
	}

	// Removes a flashcard from the project
	public void removeCard(FlashCard card) {
		allCards.remove(card);
	}

	public ArrayList<FlashCard> getAllCards() {
		return allCards;
	}

	// Get database exchanger
	public DBExchanger getDBEX() {
		return this.dbex;
	}

	// Get media exchanger
	public MediaExchanger getMex() {
		return mex;
	}

	// TITLE - Getter & setter
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// ID - Getter
	public int getId() {
		return id;
	}

	// NUMBER OF STACKS - Getter & Setter
	public int getNumberOfStacks() {
		return numberOfStacks;
	}

	public void setNumberOfStacks(int newNumberOfStacks) throws InvalidValueException, SQLException, IOException {
		if (!validNoOfStacks(newNumberOfStacks)) {
			throw new InvalidValueException();
		}
		// if there are less stacks than before - shift all cards with too high
		// no of stack into highest stack
		if (this.numberOfStacks > newNumberOfStacks) {
			for (int i = 0; i < allCards.size(); i++) {
				FlashCard f = allCards.get(i);
				if (f.getStack() > newNumberOfStacks) {
					f.setStack(newNumberOfStacks);
					f.update();
				}
			}
		}
		this.numberOfStacks = newNumberOfStacks;
		this.update();
	}

	public boolean validNoOfStacks(int noOfStacks) {
		return (noOfStacks > 0 && noOfStacks < 100);
	}

	// COUNT CARDS in whole project / stacks
	public int getNumberOfCards() throws SQLException {
		return dbex.countRows(this);
	}

	public int getNumberOfCards(int stack) throws SQLException {
		return dbex.countRows(this, stack);
	}

	public Status getStatus() throws SQLException {
		int maxStack = dbex.getMaxStack(this);
		int minStack = dbex.getMinStack(this);
		Status s;
		if (maxStack == 1 || maxStack == 0) {
			s = Status.RED;
		} else if (maxStack == numberOfStacks && maxStack == minStack) {
			s = Status.GREEN;
		} else {
			s = Status.YELLOW;
		}
		return s;
	}

	public String toString() {
		return this.getTitle(); // --> for combobox; TODO use other method
		// return "ID: " + this.getId() + ", TITLE: " + this.getTitle() +
		// ", NO_OF_STACKS: " + this.getNumberOfStacks();
	}
}
