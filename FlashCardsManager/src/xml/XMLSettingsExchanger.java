package xml;

import java.io.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;

import app.StartApp;

public class XMLSettingsExchanger {
   
   public static void writeConfig(String configFile, Settings settings) throws XMLStreamException, FileNotFoundException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        File config = new File (configFile);
        if (StartApp.DEBUG) System.out.println("Pfad zur Config: " + configFile);
        XMLEventWriter eventWriter = outputFactory
            .createXMLEventWriter(new FileOutputStream(config));
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        // create and write Start Tag
        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);
        eventWriter.add(end);
        // create settings open tag
        StartElement configStartElement = eventFactory.createStartElement("",
            "", "settings");
        eventWriter.add(configStartElement);
        eventWriter.add(end);
        
        // Write the different nodes
        createNode(eventWriter, "majorVersion", Integer.toString(settings.getMajorVersion()));
        createNode(eventWriter, "minorVersion", Integer.toString(settings.getMinorVersion()));
        createNode(eventWriter, "patchLevel", Integer.toString(settings.getPatchLevel()));
        createNode(eventWriter, "pathToDatabase", settings.getPathToDatabase());
        createNode(eventWriter, "databaseVersion", Integer.toString(settings.getDatabaseVersion()));
        createNode(eventWriter, "showIntro", settings.getShowIntro().toString());

        eventWriter.add(eventFactory.createEndElement("", "", "settings"));
        eventWriter.add(end);
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
      }

      private static void createNode(XMLEventWriter eventWriter, String name,
          String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create start element
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create end element
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
      }
      
      public static Settings readConfig(String file) throws NumberFormatException,
		XMLStreamException, IOException {
    	  InputStream inputStream = new FileInputStream(file);
    	  return readConfig (inputStream);
      }

	public static Settings readConfig(InputStream inputStream) throws NumberFormatException,
			XMLStreamException, IOException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
		String content = null;
		Settings settings = null;
		while (reader.hasNext()) {
			int event = reader.next();
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if ("settings".equals(reader.getLocalName())) {
					settings = new Settings();
				}
				break;

			case XMLStreamConstants.CHARACTERS:
				content = reader.getText().trim();
				break;

			case XMLStreamConstants.END_ELEMENT:
				switch (reader.getLocalName()) {
				case "majorVersion":
					settings.setMajorVersion(Integer.parseInt(content));
					break;
				case "minorVersion":
					settings.setMinorVersion(Integer.parseInt(content));
					break;
				case "patchLevel":
					settings.setPatchLevel(Integer.parseInt(content));
					break;
				case "pathToDatabase":
					settings.setPathToDatabase(content);
					break;
				case "databaseVersion":
					settings.setDatabaseVersion(Integer.parseInt(content));
					break;
				case "showIntro":
					settings.setShowIntro(content.equals("true"));
					break;
				}
			}
		}
		return settings;
	}
}
