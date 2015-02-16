package storage;

import java.sql.SQLException;
import java.util.ArrayList;

import core.*;
import exc.InvalidLengthException;
import gui.helpers.IProgressPresenter;

public interface IDBExchanger {

	public void createConnection() throws SQLException;

	public void closeConnection();

	public void createTablesIfNotExisting() throws SQLException;

	/***************************************** PROJECT QUERIES ****************************************************/

	public void addProject(LearningProject project) throws SQLException, InvalidLengthException;

	public void updateProject(LearningProject project) throws SQLException;

	public void deleteProject(LearningProject project) throws SQLException;

	public ArrayList<LearningProject> getAllProjects() throws SQLException;

	public int getMaxStack(LearningProject p) throws SQLException;

	public int getMinStack(LearningProject p) throws SQLException;

	public int countRows(LearningProject p) throws SQLException;

	public int countRows(LearningProject proj, int stack) throws SQLException;

	/***************************************** LABEL QUERIES ****************************************************/

	public void addLabelToProject(Label l) throws SQLException, InvalidLengthException;

	public void addLabelToFlashcard(Label l, FlashCard f) throws SQLException;

	public void updateLabelFromProject(Label l) throws SQLException;

	public void deleteLabelFromFlashCard(Label l, FlashCard f) throws SQLException;

	public void deleteLabelFromProject(Label l) throws SQLException;

	public ArrayList<Label> getAllLabels(LearningProject p) throws SQLException;

	public ArrayList<Label> getAllLabels(FlashCard f) throws SQLException;

	public int getMaxStack(Label l) throws SQLException;

	public int getMinStack(Label l) throws SQLException;

	public int countRows(Label l) throws SQLException;

	/***************************************** FLASHCARD QUERIES ****************************************************/

	public void addFlashcard(FlashCard card) throws SQLException;

	public void updateFlashcard(FlashCard card) throws SQLException;

	public void deleteFlashcard(FlashCard card) throws SQLException;

	public ArrayList<FlashCard> getAllCards(LearningProject proj, IProgressPresenter p) throws SQLException;

	public String getPathToPic(FlashCard f, PicType type) throws SQLException;

	public void setPathToPic(FlashCard f, PicType type) throws SQLException;

	public void deletePathToPic(FlashCard f, PicType type) throws SQLException;

	public int getCardNumberInProject(FlashCard f) throws SQLException;

	/***************************************** OTHER QUERIES ****************************************************/

	public int nextId(TableType type) throws SQLException;

}
