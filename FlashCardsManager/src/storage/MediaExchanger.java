package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import app.StartApp;
import utils.PicUtils;
import core.FlashCard;

public class MediaExchanger {

	private String pathToMediaFolder;

	public MediaExchanger(String pathToMediaFolder) {

		this.pathToMediaFolder = pathToMediaFolder;
	}

	public void storePic(FlashCard card, PicType type) throws IOException {
		String targetPath = computeTargetPath(card, type);
		switch (type) {
		case QUESTION:
			if (card.getPathToQuestionPic() == null) {
				Files.deleteIfExists(Paths.get(targetPath));
				if (StartApp.DEBUG) System.out.println("delete if exists: " + targetPath);
			} else if (!card.getPathToQuestionPic().equals(targetPath)) {
				PicUtils.copyPicFile(card.getPathToQuestionPic(), targetPath);
				card.setPathToQuestionPic(targetPath);
				if (StartApp.DEBUG) System.out.println("copy pic to " + targetPath);
			}
			break;
		case ANSWER:
			if (card.getPathToAnswerPic() == null) {
				Files.deleteIfExists(Paths.get(targetPath));
				if (StartApp.DEBUG) System.out.println("delete if exists: " + targetPath);
			} else if (!card.getPathToAnswerPic().equals(targetPath)) {
				PicUtils.copyPicFile(card.getPathToAnswerPic(), targetPath);
				card.setPathToAnswerPic(targetPath);
				if (StartApp.DEBUG) System.out.println("copy pic to " + targetPath);
			}
			break;
		}
	}

	public void deleteAllPics(FlashCard card) throws IOException {
		String targetQPath = computeTargetPath(card, PicType.QUESTION);
		String targetAPath = computeTargetPath(card, PicType.ANSWER);
		Files.deleteIfExists(Paths.get(targetQPath));
		Files.deleteIfExists(Paths.get(targetAPath));
		if (StartApp.DEBUG) System.out.println("delete if exists: " + targetQPath + ", " + targetAPath);
	}

	private String computeTargetPath(FlashCard card, PicType type) {
		return pathToMediaFolder + "/pic-" + card.getProj().getId() + "-" + card.getId() + "-" + type.getShortForm() + ".png";
	}
}
