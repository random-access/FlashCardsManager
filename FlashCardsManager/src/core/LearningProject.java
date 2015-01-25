package core;

import importExport.XMLLearningProject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import storage.*;
import app.StartApp;
import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

public class LearningProject implements IHasStatus {

	private IDBExchanger dbex; // TODO final
	private IMediaExchanger mex; // TODO final
	private IProjectsController ctl;

	private final int id;
	private String title;
	private int numberOfStacks;
	private ArrayList<FlashCard> allCards;

	private ArrayList<Label> labels;

	// CONSTRUCTORS
	// new project
	public LearningProject(IProjectsController ctl, String title, int numberOfStacks) throws SQLException, InvalidValueException {
		this.ctl = ctl;
		dbex = ctl.getDbex();
		mex = ctl.getMex();
		id = dbex.nextId(TableType.PROJECTS);
		this.title = title;
		if (!validNoOfStacks(numberOfStacks)) {
			throw new InvalidValueException();
		} else {
			this.numberOfStacks = numberOfStacks;
		}
		allCards = new ArrayList<FlashCard>();
		labels = new ArrayList<Label>();
	}

	// restore from database
	public LearningProject(IProjectsController ctl, int id, String title, int numberOfStacks) {
		this.ctl = ctl;
		dbex = ctl.getDbex();
		mex = ctl.getMex();
		this.id = id;
		this.title = title;
		this.numberOfStacks = numberOfStacks;
	}

	public void loadLabelsAndFlashcards(IProgressPresenter t) throws SQLException {
		labels = dbex.getAllLabels(this);
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
		allCards.add(card);
	}

	// Removes a flashcard from the project
	void removeCard(FlashCard card) {
		allCards.remove(card);
	}

	public ArrayList<FlashCard> getAllCards() {
		return allCards;
	}

	// Get database exchanger
	public IDBExchanger getDBEX() {
		return this.dbex;
	}

	// Get media exchanger
	public IMediaExchanger getMex() {
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
		// if there are less stacks than before, shift all cards with too high
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

	public ArrayList<Label> getLabels() {
		return labels;
	}

	public void addLabel(Label newLabel) {
		labels.add(newLabel);
	}

	public void removeLabel(Label labelToRemove) {
		labels.remove(labelToRemove);
	}

	public String toString() {
		try {
			return this.getTitle() + " (" + dbex.countRows(this) + ")";
		} catch (SQLException e) {
			if (StartApp.DEBUG)
				e.printStackTrace();
			return this.getTitle();
		}
	}

}
