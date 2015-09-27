package org.random_access.flashcardsmanager_desktop.events;


public interface ProjectChangedSource {
	 
	  public void addEventListener(ProjectDataChangedListener listener);
	  public void removeEventListener(ProjectDataChangedListener listener);
	  public void fireProjectDataChangedEvent();
}
