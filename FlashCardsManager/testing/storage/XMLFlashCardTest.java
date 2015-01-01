package storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import export.XMLFlashCard;
import export.XMLFlashcardExchanger;

public class XMLFlashCardTest {
	public static void main(String[] args) throws XMLStreamException, NumberFormatException, IOException {
		String fileName = "/home/moni/Desktop/cards.xml";
//		ArrayList<XMLFlashCard> cards = new ArrayList<XMLFlashCard>();
//		for (int i = 0; i < 10; i++) {
//			XMLFlashCard card = new XMLFlashCard();
//			card.setId(i+1);
//			card.setProjId(1);
//			card.setStack(1);
//			card.setQuestion("Frage " + (i+1));
//			card.setAnswer("Antwort " + (i+1));
//			card.setCustomWidthQuestion(120);
//			card.setCustomWidthAnswer(150);
//			cards.add(card);
//		}
//		XMLFlashcardExchanger.writeXML(fileName, cards);
		
		ArrayList<XMLFlashCard> cards = XMLFlashcardExchanger.readFlashcards(fileName);
		Iterator<XMLFlashCard> it = cards.iterator();
		while(it.hasNext()) {
			XMLFlashCard card = it.next();
			System.out.println(card);
		}
	}
}
