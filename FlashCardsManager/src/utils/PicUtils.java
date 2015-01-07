package utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;

public class PicUtils {
	public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
		BufferedImage dbi = null;
		if (sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}
	
	public static void copyPicFile (String src, String target) throws IOException {
		Path srcPath = Paths.get(src);
		Path targetPath = Paths.get(target);
		Files.copy(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static void movePicFile(String src, String target) throws IOException {
	   Path srcPath = Paths.get(src);
      Path targetPath = Paths.get(target);
      Files.move(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
	}

}
