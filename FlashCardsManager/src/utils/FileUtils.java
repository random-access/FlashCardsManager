package utils;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtils {
    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean deleted = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                deleted = deleted && FileUtils.deleteRecursive(f);
            }
        }
        return deleted && path.delete();
    }
}
