package org.random_access.flashcardsmanager_desktop.importExport;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.random_access.flashcardsmanager_desktop.importExport.XMLExchanger;
import org.random_access.flashcardsmanager_desktop.importExport.XMLLabel;

public class XMLLabelTest {

	public static void main(String[] args) throws XMLStreamException, NumberFormatException, IOException {

		String fileName = "/home/moni/Desktop/labels.xml";

		ArrayList<XMLLabel> labels = new ArrayList<XMLLabel>();
		// for (int i = 0; i < 20; i++) {
		// XMLLabel l = new XMLLabel();
		// l.setId(i + 1);
		// l.setProjId((i + 1) % 5);
		// l.setName("Label " + (i + 1));
		// labels.add(l);
		// }

		XMLExchanger ex = new XMLExchanger();
		// ex.writeLabels(fileName, labels);
		labels = ex.readLabels(fileName);
		for (int i = 0; i < labels.size(); i++) {
			System.out.println(labels.get(i));
		}
	}

}
