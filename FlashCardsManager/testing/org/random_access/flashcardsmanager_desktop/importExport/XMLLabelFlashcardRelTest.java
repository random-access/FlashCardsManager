package org.random_access.flashcardsmanager_desktop.importExport;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.random_access.flashcardsmanager_desktop.importExport.XMLExchanger;
import org.random_access.flashcardsmanager_desktop.importExport.XMLLabelFlashcardRelation;

public class XMLLabelFlashcardRelTest {
	public static void main(String[] args) throws XMLStreamException, NumberFormatException, IOException {

		String fileName = "/home/moni/Desktop/labels-flashcards-rel.xml";

		ArrayList<XMLLabelFlashcardRelation> lfrelations = new ArrayList<XMLLabelFlashcardRelation>();
		// for (int i = 0; i < 20; i++) {
		// XMLLabelFlashcardRelation l = new XMLLabelFlashcardRelation();
		// l.setId(i + 1);
		// l.setCardId((i + 1) % 5);
		// l.setLabelId((i + 1) % 2);
		// lfrelations.add(l);
		// }

		XMLExchanger ex = new XMLExchanger();
		// ex.writeLabelFlashcardRelation(fileName, lfrelations);
		lfrelations = ex.readLabelFlashcardRelations(fileName);
		for (int i = 0; i < lfrelations.size(); i++) {
			System.out.println(lfrelations.get(i));
		}
	}
}
