package org.random_access.flashcardsmanager_desktop.importExport;

import java.sql.SQLException;
import java.util.ArrayList;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;
import org.random_access.flashcardsmanager_desktop.storage.PicType;

public interface XMLDBExchanger {

	public XMLMedia getPic(FlashCard card, PicType type) throws SQLException;

	public ArrayList<XMLLabelFlashcardRelation> getXMLLfRelations(FlashCard f) throws SQLException;

}
