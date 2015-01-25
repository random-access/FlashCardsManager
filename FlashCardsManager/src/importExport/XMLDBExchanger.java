package importExport;

import java.sql.SQLException;
import java.util.ArrayList;

import storage.PicType;
import core.FlashCard;

public interface XMLDBExchanger {

	public XMLMedia getPic(FlashCard card, PicType type) throws SQLException;

	public ArrayList<XMLLabelFlashcardRelation> getXMLLfRelations(FlashCard f) throws SQLException;

}
