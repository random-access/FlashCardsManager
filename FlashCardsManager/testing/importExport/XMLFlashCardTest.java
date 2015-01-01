package importExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

public class XMLFlashCardTest {
	public static void main(String[] args) throws XMLStreamException, NumberFormatException, IOException {
		String fileName = "/home/moni/Desktop/cards.xml";
		ArrayList<XMLFlashCard> cards = new ArrayList<XMLFlashCard>();
		for (int i = 0; i < 10; i++) {
			XMLFlashCard card = new XMLFlashCard();
			card.setId(i+1);
			card.setProjId(1);
			card.setStack(1);
			card.setQuestion("Frage " + (i+1));
			card.setAnswer("<!DOCTYPE html >\n<html lang=\"de\">\n\n<head>\n"
			+ "</head>\n<body>\n\n</body>\n</html>)");
			card.setCustomWidthQuestion(120);
			card.setCustomWidthAnswer(150);
			cards.add(card);
		}
		
		XMLExchanger ex = new XMLExchanger();
		ex.writeFlashcards(fileName, cards);

		ArrayList<XMLFlashCard> cards2 = ex.readFlashcards(fileName);
		Iterator<XMLFlashCard> it = cards2.iterator();
		while(it.hasNext()) {
			XMLFlashCard card = it.next();
			System.out.println(card);
		}
	}
}
