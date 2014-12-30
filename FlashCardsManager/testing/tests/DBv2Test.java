package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import core.*;

public class DBv2Test {
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		ProjectsController ctl = new ProjectsController("/home/moni/Desktop/TestDB");
		ctl.loadProjects();
		LearningProject p1 = new LearningProject (ctl,"Projekt 1", 3);
		p1.store();
		LearningProject p2 = new LearningProject (ctl,"Projekt 2", 4);
		p2.store();
		LearningProject p3 = new LearningProject (ctl,"Projekt 3", 3);
		p3.store();
		LearningProject p4 = new LearningProject (ctl,"Projekt 4", 2);
		p4.store();
		LearningProject p5 = new LearningProject (ctl,"Projekt 5", 3);
		p5.store();
		
		Iterator<LearningProject> it = ctl.getProjects().iterator();
		while (it.hasNext()) {
			LearningProject p = it.next();
			System.out.println(p);
		}
		System.out.println("Erster Durchlauf");
//		LearningProject p = ctl.getProjects().get(1);
//		p.delete();
//		Iterator<FlashCard> fit1 = p.getAllCards().iterator();
//		while(fit1.hasNext()) {
//			System.out.println(fit1.next());
//		}
//		FlashCard f = p.getAllCards().get(0);
//		f.delete();
//		System.out.println();
//		Iterator<FlashCard> fit2 = p.getAllCards().iterator();
//		while(fit2.hasNext()) {
//			System.out.println(fit2.next());
//		}
//		for (int i = 0; i < ctl.getProjects().size(); i++) {
//			ctl.getProjects().get(i).delete();
//		}
		ctl.loadProjects();
		ctl.getProjects().get(0).loadFlashcards();
		System.out.println(ctl.getProjects());
		System.out.println(ctl.getProjects().get(0));
		System.out.println(ctl.getProjects().get(0));
		System.out.println(ctl.getProjects().get(0));
		FlashCard f1 = new FlashCard(ctl.getProjects().get(0), "Bla", "Blubb", "/home/moni/Desktop/pic1", "/home/moni/Desktop/pic2", 100,100 );
		
		
		System.out.println(ctl.getProjects().get(0));
		System.out.println(f1);
		f1.store();
//		FlashCard f2 = new FlashCard(ctl.getProjects().get(0), "Bla", "Blubb", "/home/moni/Desktop/pic3", "/home/moni/Desktop/pic4", 100,100 );
//		f2.store();
//		FlashCard f3 = new FlashCard(ctl.getProjects().get(0), "Bla", "Blubb", "/home/moni/Desktop/pic5", "/home/moni/Desktop/pic6", 100,100 );
//		f3.store();
//	 	ctl.getProjects().get(1).loadFlashcards();
	}
}
