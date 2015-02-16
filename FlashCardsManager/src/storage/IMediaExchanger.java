package storage;

import java.io.IOException;

import core.FlashCard;
import core.LearningProject;

public interface IMediaExchanger {

	public void storePic(FlashCard card, PicType type) throws IOException;

	public void transferPic(FlashCard card, PicType type) throws IOException;

	public void deleteAllPics(LearningProject proj) throws IOException;

	public void deleteAllPics(FlashCard card) throws IOException;
}
