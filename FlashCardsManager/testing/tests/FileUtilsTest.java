package tests;

import importExport.XMLFiles;

import java.io.File;
import java.util.ArrayList;

import utils.FileUtils;

public class FileUtilsTest {
   public static void main(String[] args) {
      File f = new File("C:\\Users\\IT-Helpline16\\Desktop\\TestExport");
      ArrayList<String> names = new ArrayList<String>();
      names.add("media");
      System.out.println(FileUtils.directoryContainsCertainFines(f.getAbsolutePath(), XMLFiles.getAllNames()));
      System.out.println(FileUtils.directoryContainsCertainFines(f.getAbsolutePath(), names));
   }
}
