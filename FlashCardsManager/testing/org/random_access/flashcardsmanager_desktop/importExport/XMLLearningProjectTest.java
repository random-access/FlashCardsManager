package org.random_access.flashcardsmanager_desktop.importExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.random_access.flashcardsmanager_desktop.importExport.XMLExchanger;
import org.random_access.flashcardsmanager_desktop.importExport.XMLLearningProject;

public class XMLLearningProjectTest {
	public static void main(String[] args) throws XMLStreamException, NumberFormatException, IOException {
		String fileName = "/home/moni/Desktop/Testoo/projects.xml";
//		ArrayList<XMLLearningProject> projects = new ArrayList<XMLLearningProject>();
//		for (int i = 0; i < 10; i++) {
//			XMLLearningProject project = new XMLLearningProject();
//			project.setProjId(1);
//			project.setProjTitle("Projekt " + (i+1));
//			project.setNoOfStacks(3);
//			projects.add(project);
//		}
		
		XMLExchanger ex = new XMLExchanger();
		//ex.writeProjects(fileName, projects);

		ArrayList<XMLLearningProject> projects2 = ex.readProjects(fileName);
		Iterator<XMLLearningProject> it = projects2.iterator();
		while(it.hasNext()) {
			XMLLearningProject project = it.next();
			System.out.println(project);
		}
	}
}
