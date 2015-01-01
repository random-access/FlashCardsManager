package importExport;

import java.io.*;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;

import storage.DBExchanger;
import storage.MediaExchanger;
import core.OrderedItem;

public class XMLFlashcardExchanger {

	private static final String ELEM_FLASHCARDS = "flashcards";
	private static final String ELEM_FLASHCARD = "flashcard";
	private static final String ELEM_CARD_ID = "card_id";
	private static final String ELEM_PROJ_ID = "proj_id";
	private static final String ELEM_STACK = "stack";
	private static final String ELEM_QUESTION = "question";
	private static final String ELEM_ANSWER = "answer";
	private static final String ELEM_CUSTOM_WIDTH_QUESTION = "custom_width_question";
	private static final String ELEM_CUSTOM_WIDTH_ANSWER = "custom_width_answer";

	public static ArrayList<XMLFlashCard> readFlashcards(String inputFile) throws NumberFormatException,
			XMLStreamException, IOException {
		ArrayList<XMLFlashCard> cards = new ArrayList<XMLFlashCard>();
		XMLFlashCard card = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(inputFile));
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				switch (startElement.getName().getLocalPart()) {
				case ELEM_FLASHCARD:
					card = new XMLFlashCard();
					break;
				case ELEM_CARD_ID:
					event = reader.nextEvent();
					card.setId(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_PROJ_ID:
					event = reader.nextEvent();
					card.setProjId(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_STACK:
					event = reader.nextEvent();
					card.setStack(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_QUESTION:
					event = reader.nextEvent();
					card.setQuestion(event.asCharacters().getData());
					break;
				case ELEM_ANSWER:
					event = reader.nextEvent();
					card.setAnswer(event.asCharacters().getData());
					break;
				case ELEM_CUSTOM_WIDTH_QUESTION:
					event = reader.nextEvent();
					card.setCustomWidthQuestion(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_CUSTOM_WIDTH_ANSWER:
					event = reader.nextEvent();
					card.setCustomWidthAnswer(Integer.parseInt(event.asCharacters().getData()));
					break;
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equals(ELEM_FLASHCARD)) {
					cards.add(card);
				}
			}
		}
		return cards;
	}

	public static void writeXML(String outputFile, ArrayList<XMLFlashCard> cards) throws FileNotFoundException,
			XMLStreamException {
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

		XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(new FileOutputStream(outputFile), "UTF-8");
		// For Debugging - below code to print XML to Console
		// XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(System.out);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		StartDocument startDocument = eventFactory.createStartDocument();
		xmlEventWriter.add(startDocument);
		xmlEventWriter.add(end);
		StartElement configStartElement = eventFactory.createStartElement("", "", ELEM_FLASHCARDS);
		xmlEventWriter.add(configStartElement);
		xmlEventWriter.add(end);

		Iterator<XMLFlashCard> it = cards.iterator();
		while (it.hasNext()) {
			XMLFlashCard card = it.next();
			StartElement configStartCardElement = eventFactory.createStartElement("", "", ELEM_FLASHCARD);
			xmlEventWriter.add(eventFactory.createDTD("\t"));
			xmlEventWriter.add(configStartCardElement);
			xmlEventWriter.add(end);

			// Write the element nodes
			createNode(xmlEventWriter, ELEM_CARD_ID, Integer.toString(card.getId()));
			createNode(xmlEventWriter, ELEM_PROJ_ID, Integer.toString(card.getProjId()));
			createNode(xmlEventWriter, ELEM_STACK, Integer.toString(card.getStack()));
			createNode(xmlEventWriter, ELEM_QUESTION, card.getQuestion());
			createNode(xmlEventWriter, ELEM_ANSWER, card.getAnswer());
			createNode(xmlEventWriter, ELEM_CUSTOM_WIDTH_QUESTION, Integer.toString(card.getCustomWidthQuestion()));
			createNode(xmlEventWriter, ELEM_CUSTOM_WIDTH_ANSWER, Integer.toString(card.getCustomWidthAnswer()));

			xmlEventWriter.add(eventFactory.createDTD("\t"));
			xmlEventWriter.add(eventFactory.createEndElement("", "", ELEM_FLASHCARD));
			xmlEventWriter.add(end);
		}
		xmlEventWriter.add(eventFactory.createEndElement("", "", ELEM_FLASHCARDS));
		xmlEventWriter.add(end);
		xmlEventWriter.add(eventFactory.createEndDocument());
		xmlEventWriter.close();
	}

	private static void createNode(XMLEventWriter eventWriter, String element, String value) throws XMLStreamException {
		XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
		XMLEvent end = xmlEventFactory.createDTD("\n");
		XMLEvent tab = xmlEventFactory.createDTD("\t\t");
		// Create Start node
		StartElement sElement = xmlEventFactory.createStartElement("", "", element);
		eventWriter.add(tab);
		eventWriter.add(sElement);
		// Create Content
		Characters characters = xmlEventFactory.createCharacters(value);
		eventWriter.add(characters);
		// Create End node
		EndElement eElement = xmlEventFactory.createEndElement("", "", element);
		eventWriter.add(eElement);
		eventWriter.add(end);

	}
}
