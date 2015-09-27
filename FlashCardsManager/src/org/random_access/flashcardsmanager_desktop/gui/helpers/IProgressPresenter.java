package org.random_access.flashcardsmanager_desktop.gui.helpers;

public interface IProgressPresenter {
	
	public void changeProgress(int progress);
	
	public void changeInfo (String text);

	public int getProgress();

}
