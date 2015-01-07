package importExport;

import java.io.IOException;
import java.nio.file.*;

public class MIMETest {
	public static void main(String[] args) throws IOException {
		String pathName = "/home/moni/Desktop/pic-1-3-q";
		Path source = Paths.get(pathName);
	    String mimeType = Files.probeContentType(source);
	    System.out.println(mimeType);
	    String extension = null;
	    if (!(mimeType == null)) {
	    	extension = mimeType.substring(mimeType.indexOf('/')+1, mimeType.length());
	    }
	    System.out.println(extension);
	    Files.move(Paths.get(pathName), Paths.get(pathName + "." + extension));
	}
}
