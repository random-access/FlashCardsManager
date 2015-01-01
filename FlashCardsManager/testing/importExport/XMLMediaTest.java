package importExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

public class XMLMediaTest {
	public static void main(String[] args) throws XMLStreamException, NumberFormatException, IOException {
		String fileName = "/home/moni/Desktop/media.xml";
		ArrayList<XMLMedia> medias = new ArrayList<XMLMedia>();
		for (int i = 0; i < 10; i++) {
			XMLMedia media = new XMLMedia();
			media.setMediaId(i+1);
			media.setCardId(i+4);
			media.setPathToMedia("/home/moni/.Lernkarten/media/adsfsdf" + (i+1) + ".png");
			media.setPicType('q');
			medias.add(media);
		}
		
		XMLExchanger ex = new XMLExchanger();
		ex.writeMedia(fileName, medias);

		ArrayList<XMLMedia> medias2 = ex.readMedia(fileName);
		Iterator<XMLMedia> it = medias2.iterator();
		while(it.hasNext()) {
			XMLMedia media = it.next();
			System.out.println(media);
		}
	}
}
