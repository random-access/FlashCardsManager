package events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ProjectDataChangedEvent extends EventObject {
	
	
	public ProjectDataChangedEvent(Object source) {
		super(source);
	}

}
