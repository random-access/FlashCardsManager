package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import app.StartApp;
import utils.FileUtils;
import utils.PicUtils;
import core.FlashCard;

public class MediaExchanger {

	private String pathToMediaFolder;

	public MediaExchanger(String pathToMediaFolder) {

		this.pathToMediaFolder = pathToMediaFolder;
	}

	public void storePic(FlashCard card, PicType type) throws IOException {
		String targetPathWithoutExtension = computeTargetPathWithoutEnding(card, type);
		String extension = null;
		String targetPath = null;
		switch (type) {
		case QUESTION:
			extension = FileUtils.getFileExtension(card.getPathToQuestionPic());
			if (extension != null) {
				targetPath = targetPathWithoutExtension + "." + extension;
			}
			if (card.getPathToQuestionPic() == null || !card.getPathToQuestionPic().equals(targetPath)) {
				if (StartApp.DEBUG)
					System.out.println("delete if exists with any extension: " + targetPathWithoutExtension);
				FileUtils.deleteIfExistsWithAnyExtension(targetPathWithoutExtension);
				if (card.getPathToQuestionPic() != null) {
					FileUtils.copyPicFile(card.getPathToQuestionPic(), targetPath);
					card.setPathToQuestionPic(targetPath);
					if (StartApp.DEBUG)
						System.out.println("copy pic to " + targetPath);
				}
			}
			break;
		case ANSWER:
			extension = FileUtils.getFileExtension(card.getPathToAnswerPic());
			if (extension != null) {
				targetPath = targetPathWithoutExtension + "." + extension;
			}
			if (card.getPathToAnswerPic() == null || !card.getPathToAnswerPic().equals(targetPath)) {
				if (StartApp.DEBUG)
					System.out.println("delete if exists with any extension: " + targetPathWithoutExtension);
				FileUtils.deleteIfExistsWithAnyExtension(targetPathWithoutExtension);
				if (card.getPathToAnswerPic() != null) {
					FileUtils.copyPicFile(card.getPathToAnswerPic(), targetPath);
					card.setPathToAnswerPic(targetPath);
					if (StartApp.DEBUG)
						System.out.println("copy pic to " + targetPath);
				}
			}
			break;
		}
	}

	public void transferPic(FlashCard card, PicType type) throws IOException {
		String targetPathWithoutExtension = computeTargetPathWithoutEnding(card, type);
		String extension;
		String targetPath = null;
		switch (type) {
		case QUESTION:
			extension = FileUtils.getFileExtension(card.getPathToQuestionPic());
			if (extension != null) {
				targetPath = targetPathWithoutExtension + "." + extension;
			}
			if (card.getPathToQuestionPic() == null ||!card.getPathToQuestionPic().equals(targetPath)) {
				FileUtils.deleteIfExistsWithAnyExtension(targetPathWithoutExtension);
				if (StartApp.DEBUG)
					System.out.println("delete if exists: " + targetPathWithoutExtension);
				if (card.getPathToQuestionPic() != null) {
					FileUtils.movePicFile(card.getPathToQuestionPic(), targetPath);
					card.setPathToQuestionPic(targetPath);
					if (StartApp.DEBUG)
						System.out.println("copy pic to " + targetPath);
				}
			}
			break;
		case ANSWER:
			extension = FileUtils.getFileExtension(card.getPathToAnswerPic());
			targetPath = null;
			if (extension != null) {
				targetPath = targetPathWithoutExtension + "." + extension;
			}
			if (card.getPathToAnswerPic() == null || !card.getPathToAnswerPic().equals(targetPath)) {
				FileUtils.deleteIfExistsWithAnyExtension(targetPathWithoutExtension);
				if (StartApp.DEBUG)
					System.out.println("delete if exists: " + targetPathWithoutExtension);
				if (card.getPathToAnswerPic() != null) {
					FileUtils.movePicFile(card.getPathToAnswerPic(), targetPath);
					card.setPathToAnswerPic(targetPath);
					if (StartApp.DEBUG)
						System.out.println("copy pic to " + targetPath);
				}
			}
			break;
		}

	}

	public void deleteAllPics(FlashCard card) throws IOException {
		String targetQPath = computeTargetPathWithoutEnding(card, PicType.QUESTION);
		String targetAPath = computeTargetPathWithoutEnding(card, PicType.ANSWER);
		FileUtils.deleteIfExistsWithAnyExtension(targetQPath);
		FileUtils.deleteIfExistsWithAnyExtension(targetAPath);
		if (StartApp.DEBUG)
			System.out.println("delete if exists: " + targetQPath + ", " + targetAPath);
	}

	private String computeTargetPathWithoutEnding(FlashCard card, PicType type) {
		System.out.println("path to media folder=" + pathToMediaFolder + ", card.getProj().getId()=" + card.getProj().getId()
				+ ", card.getId()=" + card.getId() + ", type.getShortForm=" + type.getShortForm());
		return pathToMediaFolder + "/pic-" + card.getProj().getId() + "-" + card.getId() + "-" + type.getShortForm();
	}

}
