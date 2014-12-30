package core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import db.*;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;
import exc.InvalidValueException;

public class LearningProject implements OrderedItem {

	private final DBExchanger<OrderedItem> dbex;
	private final ProjectsController ctl;

	private final int id;
	private String title;
	private int numberOfStacks;
	private ArrayList<FlashCard> allCards;

	// CONSTRUCTORS
	// new project
	public LearningProject(ProjectsController ctl, String title, int numberOfStacks) throws SQLException {
		this.ctl = ctl;
		dbex = ctl.getDbex();
		id = dbex.nextProjectId();
		this.title = title;
		this.numberOfStacks = numberOfStacks;
		allCards = new ArrayList<FlashCard>();
	}

	// restore from database
	public LearningProject(ProjectsController ctl, int id, String title, int numberOfStacks) {
		this.ctl = ctl;
		dbex = ctl.getDbex();
		this.id = id;
		this.title = title;
		this.numberOfStacks = numberOfStacks;
	}

	public void loadFlashcards() throws SQLException {
		allCards = dbex.getAllCards(this);
	}

	public void store() throws SQLException {
		dbex.addProject(this);
		ctl.addProject(this);
		// TODO save pics
	}
	
	public void update() throws SQLException {
		dbex.updateProject(this);
	}

	public void delete() throws SQLException {
		ctl.removeProject(this);
		dbex.deleteProject(this);
	}

	//
	public boolean validNoOfStacks(int noOfStacks) {
		return (noOfStacks > 0);
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

	// Get database exchanger
	public DBExchanger<OrderedItem> getDBEX() {
		return this.dbex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public void setNumberOfStacks(int newNumberOfStacks) throws InvalidValueException, SQLException{
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
			System.out.println("Fitted cards into remaining stacks");
		}
		this.numberOfStacks = newNumberOfStacks;
		// TODO update database
	}

	// NEXT CARD ID - Getter

	public int getNumberOfCards() throws SQLException {
		return 0;
		// TODO
	}

	public int getNumberOfCards(int stack) throws SQLException {
		return dbex.countRows(this, stack);
	}

	public ArrayList<FlashCard> getAllCards() {
		return allCards;
	}


	public Status getStatus() throws SQLException {
		int maxStack = dbex.getMaxStack(this);
		Status s;
		if (maxStack== 1 || maxStack == 0) {
			s = Status.RED;
		} else if (maxStack == numberOfStacks){
			s = Status.GREEN;
		} else {
			s = Status.YELLOW;
		}
		return s;
	}

	public String toString() {
		return "ID: " + this.getId() + ", TITLE: " + this.getTitle() + ", NO_OF_STACKS: " + this.getNumberOfStacks();
	}
}
