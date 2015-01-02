package importExport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLExchanger {
	// flashcard property strings
	private static final String ELEM_FLASHCARDS = "flashcards";
	private static final String ELEM_FLASHCARD = "flashcard";
	private static final String ELEM_CARD_ID = "card_id";
	private static final String ELEM_STACK = "stack";
	private static final String ELEM_QUESTION = "question";
	private static final String ELEM_ANSWER = "answer";
	private static final String ELEM_CUSTOM_WIDTH_QUESTION = "custom_width_question";
	private static final String ELEM_CUSTOM_WIDTH_ANSWER = "custom_width_answer";

	// project property strings
	private static final String ELEM_PROJECTS = "projects";
	private static final String ELEM_PROJECT = "project";
	private static final String ELEM_PROJ_ID = "proj_id";
	private static final String ELEM_PROJ_TITLE = "proj_title";
	private static final String ELEM_NO_OF_STACKS = "no_of_stacks";

	// media property strings
	private static final String ELEM_MEDIAS = "medias";
	private static final String ELEM_MEDIA = "media";
	private static final String ELEM_MEDIA_ID = "media_id";
	private static final String ELEM_PATH_TO_MEDIA = "path_to_media";
	private static final String ELEM_PICTYPE = "pictype";

	// ******************** flashcard import/export ***************************
	public ArrayList<XMLFlashCard> readFlashcards(String inputFile) throws NumberFormatException, XMLStreamException, IOException {
		ArrayList<XMLFlashCard> cards = new ArrayList<XMLFlashCard>();
		XMLFlashCard card = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_COALESCING, true); // for reading
																	// everything
																	// from xml
		FileInputStream inputStream = new FileInputStream(inputFile);
		XMLEventReader reader = factory.createXMLEventReader(inputStream);
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
		if (inputStream != null) {
		   inputStream.close();
		}
		return cards;
	}

	public void writeFlashcards(String outputFile, ArrayList<XMLFlashCard> cards) throws FileNotFoundException,
			XMLStreamException {
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		FileOutputStream outputStream = new FileOutputStream (outputFile);
		 XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(outputStream, "UTF-8");
		// For Debugging - below code to print XML to Console
//		 XMLEventWriter xmlEventWriter =
//		 xmlOutputFactory.createXMLEventWriter(System.out);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		startDocAndCreateRootElement(ELEM_FLASHCARDS, xmlEventWriter, eventFactory, end);

		Iterator<XMLFlashCard> it = cards.iterator();
		while (it.hasNext()) {
			XMLFlashCard card = it.next();
			createStartElem(ELEM_FLASHCARD, xmlEventWriter, eventFactory, end);
			// Write the element nodes
			createNode(xmlEventWriter, ELEM_CARD_ID, Integer.toString(card.getId()));
			createNode(xmlEventWriter, ELEM_PROJ_ID, Integer.toString(card.getProjId()));
			createNode(xmlEventWriter, ELEM_STACK, Integer.toString(card.getStack()));
			createNode(xmlEventWriter, ELEM_QUESTION, card.getQuestion());
			createNode(xmlEventWriter, ELEM_ANSWER, card.getAnswer());
			createNode(xmlEventWriter, ELEM_CUSTOM_WIDTH_QUESTION, Integer.toString(card.getCustomWidthQuestion()));
			createNode(xmlEventWriter, ELEM_CUSTOM_WIDTH_ANSWER, Integer.toString(card.getCustomWidthAnswer()));
			createEndElem(ELEM_FLASHCARD, xmlEventWriter, eventFactory, end);
		}
		createRootEndAndCloseStream(ELEM_FLASHCARDS, xmlEventWriter, eventFactory, end);
		if (outputStream != null) {
		   try {
            outputStream.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
		}
	}

	// ****************** project import/export ***********************
	public ArrayList<XMLLearningProject> readProjects(String inputFile) throws NumberFormatException, XMLStreamException,
			IOException {
		ArrayList<XMLLearningProject> projects = new ArrayList<XMLLearningProject>();
		XMLLearningProject project = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_COALESCING, true); // for reading
																	// everything
																	// from xml
		FileInputStream inputStream = new FileInputStream(inputFile);
		XMLEventReader reader = factory.createXMLEventReader(inputStream);
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				switch (startElement.getName().getLocalPart()) {
				case ELEM_PROJECT:
					project = new XMLLearningProject();
					break;
				case ELEM_PROJ_ID:
					event = reader.nextEvent();
					project.setProjId(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_PROJ_TITLE:
					event = reader.nextEvent();
					project.setProjTitle(event.asCharacters().getData());
					break;
				case ELEM_NO_OF_STACKS:
					event = reader.nextEvent();
					project.setNoOfStacks(Integer.parseInt(event.asCharacters().getData()));
					break;
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equals(ELEM_PROJECT)) {
					projects.add(project);
				}
			}
		}
		if (inputStream != null) {
		   inputStream.close();
		}
		return projects;
	}

	public void writeProjects(String outputFile, ArrayList<XMLLearningProject> projects) throws FileNotFoundException,
			XMLStreamException {
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		FileOutputStream outputStream = new FileOutputStream (outputFile);
		 XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(outputStream, "UTF-8");
		// For Debugging - below code to print XML to Console
//		 XMLEventWriter xmlEventWriter =
//		 xmlOutputFactory.createXMLEventWriter(System.out);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		startDocAndCreateRootElement(ELEM_PROJECTS, xmlEventWriter, eventFactory, end);
		Iterator<XMLLearningProject> it = projects.iterator();
		while (it.hasNext()) {
			XMLLearningProject project = it.next();
			createStartElem(ELEM_PROJECT, xmlEventWriter, eventFactory, end);
			// Write the element nodes
			createNode(xmlEventWriter, ELEM_PROJ_ID, Integer.toString(project.getProjId()));
			createNode(xmlEventWriter, ELEM_PROJ_TITLE, project.getProjTitle());
			createNode(xmlEventWriter, ELEM_NO_OF_STACKS, Integer.toString(project.getNoOfStacks()));
			createEndElem(ELEM_PROJECT, xmlEventWriter, eventFactory, end);
		}
		createRootEndAndCloseStream(ELEM_PROJECTS, xmlEventWriter, eventFactory, end);
		if (outputStream != null) {
		   try {
            outputStream.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
		}
	}


	// ********************* media import/export ***********************
	public ArrayList<XMLMedia> readMedia(String inputFile) throws NumberFormatException, XMLStreamException, IOException {
		ArrayList<XMLMedia> medias = new ArrayList<XMLMedia>();
		XMLMedia media = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_COALESCING, true); // for reading
																	// everything
																	// from xml
		FileInputStream inputStream = new FileInputStream(inputFile);
		XMLEventReader reader = factory.createXMLEventReader(inputStream);
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				switch (startElement.getName().getLocalPart()) {
				case ELEM_MEDIA:
					media = new XMLMedia();
					break;
				case ELEM_MEDIA_ID:
					event = reader.nextEvent();
					media.setMediaId(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_CARD_ID:
					event = reader.nextEvent();
					media.setCardId(Integer.parseInt(event.asCharacters().getData()));
					break;
				case ELEM_PATH_TO_MEDIA:
					event = reader.nextEvent();
					media.setPathToMedia(event.asCharacters().getData());
					break;
				case ELEM_PICTYPE: 
					event = reader.nextEvent();
					media.setPicType(event.asCharacters().getData().trim().charAt(0));
					break;
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equals(ELEM_MEDIA)) {
					medias.add(media);
				}
			}
		}
		if (inputStream != null) {
		   inputStream.close();
		}
		return medias;
	}

	public void writeMedia(String outputFile, ArrayList<XMLMedia> medias) throws FileNotFoundException,
			XMLStreamException {
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		 XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(outputStream, "UTF-8");
		// For Debugging - below code to print XML to Console
//		 XMLEventWriter xmlEventWriter =
//		 xmlOutputFactory.createXMLEventWriter(System.out);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		startDocAndCreateRootElement(ELEM_MEDIAS, xmlEventWriter, eventFactory, end);
		Iterator<XMLMedia> it = medias.iterator();
		while (it.hasNext()) {
			XMLMedia media = it.next();
			createStartElem(ELEM_MEDIA, xmlEventWriter, eventFactory, end);
			// Write the element nodes
			createNode(xmlEventWriter, ELEM_MEDIA_ID, Integer.toString(media.getMediaId()));
			createNode(xmlEventWriter, ELEM_CARD_ID, Integer.toString(media.getCardId()));
			createNode(xmlEventWriter, ELEM_PATH_TO_MEDIA, media.getPathToMedia());
			createNode(xmlEventWriter, ELEM_PICTYPE, Character.toString(media.getPicType()));
			createEndElem(ELEM_MEDIA, xmlEventWriter, eventFactory, end);
		}
		createRootEndAndCloseStream(ELEM_MEDIAS, xmlEventWriter, eventFactory, end);
		if (outputStream != null) {
		   try {
            outputStream.close();
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
		}
	}

	// ********************* helper methods *************************
	

   private void createStartElem(String elemName, XMLEventWriter xmlEventWriter, XMLEventFactory eventFactory, XMLEvent end)
         throws XMLStreamException {
      StartElement configStartCardElement = eventFactory.createStartElement("", "", elemName);
      xmlEventWriter.add(eventFactory.createDTD("\t"));
      xmlEventWriter.add(configStartCardElement);
      xmlEventWriter.add(end);
   }

	private void createEndElem(String elemName, XMLEventWriter xmlEventWriter, XMLEventFactory eventFactory, XMLEvent end)
			throws XMLStreamException {
		xmlEventWriter.add(eventFactory.createDTD("\t"));
		xmlEventWriter.add(eventFactory.createEndElement("", "", elemName));
		xmlEventWriter.add(end);
	}

	private void startDocAndCreateRootElement(String elemName, XMLEventWriter xmlEventWriter, XMLEventFactory eventFactory,
			XMLEvent end) throws XMLStreamException {
		StartDocument startDocument = eventFactory.createStartDocument();
		xmlEventWriter.add(startDocument);
		xmlEventWriter.add(end);
		StartElement configStartElement = eventFactory.createStartElement("", "", elemName);
		xmlEventWriter.add(configStartElement);
		xmlEventWriter.add(end);
	}

	private void createRootEndAndCloseStream(String elemName, XMLEventWriter xmlEventWriter, XMLEventFactory eventFactory,
			XMLEvent end) throws XMLStreamException {
		xmlEventWriter.add(eventFactory.createEndElement("", "", elemName));
		xmlEventWriter.add(end);
		xmlEventWriter.add(eventFactory.createEndDocument());
		xmlEventWriter.close();
	}

	private void createNode(XMLEventWriter eventWriter, String element, String value) throws XMLStreamException {
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
