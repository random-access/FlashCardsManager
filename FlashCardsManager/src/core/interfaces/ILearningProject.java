package core.interfaces;

import exc.InvalidLengthException;
import exc.InvalidValueException;
import gui.helpers.IProgressPresenter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import storage.IDBExchanger;
import storage.IMediaExchanger;
import core.*;

public interface ILearningProject {

    // load objects
    public void loadLabels() throws SQLException;

    public void loadLabelsAndFlashcards(IProgressPresenter t) throws SQLException;

    public void unloadLabelsAndFlashcards();

    // DB ops
    public void store() throws SQLException, InvalidLengthException;

    public void update() throws SQLException;

    public void delete() throws SQLException, IOException;

    // specific methods
    public boolean validNoOfStacks(int noOfStacks);

    public int getNumberOfCards() throws SQLException;

    public int getNumberOfCards(int stack) throws SQLException;

    // getter & setter
    public IDBExchanger getDBEX();

    public IMediaExchanger getMex();

    public String getTitle();

    public void setTitle(String title);

    public int getId();

    public int getNumberOfStacks();

    public void setNumberOfStacks(int newNumberOfStacks) throws InvalidValueException, SQLException, IOException;

    public Status getStatus() throws SQLException;

    public ArrayList<Label> getLabels();

    public void addLabel(Label newLabel);

    public void removeLabel(Label labelToRemove);

    public void addCard(FlashCard card);

    void removeCard(FlashCard card);

    public ArrayList<FlashCard> getAllCards();

    public String toString();
}
