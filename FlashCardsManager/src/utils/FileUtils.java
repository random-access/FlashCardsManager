package utils;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
	
	public static void deleteDirectory (String pathToDirectory) {
		Path dir  = Paths.get(pathToDirectory);
		
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) throws IOException {
					 System.out.println("Deleting file: " + file);
			          Files.delete(file);
			          return CONTINUE;
			      }

			      @Override
			      public FileVisitResult postVisitDirectory(Path dir,
			              IOException exc) throws IOException {

			          System.out.println("Deleting dir: " + dir);
			          if (exc == null) {
			              Files.delete(dir);
			              return CONTINUE;
			          } else {
			              throw exc;
			          }
			      }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
//    public static boolean deleteRecursive(File path) throws FileNotFoundException{
//        if( path.exists() ) {
//            File[] files = path.listFiles();
//            for(int i=0; i<files.length; i++) {
//               if(files[i].isDirectory()) {
//                 deleteRecursive(files[i]);
//               } else {
//                 files[i].delete();
//               }
//            }
//          }
//          return( path.delete() );
//    }
//    
    public static void main(String[] args) {
		FileUtils.deleteDirectory("C:/Users/test/Desktop/TestDB");
	}
}
