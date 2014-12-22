package utils;

import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class IndividualAction extends AbstractAction
{
	private Action originalAction;
	private JComponent component;
	private String actionCommand = "";


	public IndividualAction(String name, JComponent component, String actionKey){
		super(name);
		originalAction = component.getActionMap().get(actionKey);
		if (originalAction == null) {
			String message = "no Action for action key: " + actionKey;
			throw new IllegalArgumentException(message);
		}
		this.component = component;
	}

	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	// perform the action as if it was performed from the original source on the original component
	public void actionPerformed(ActionEvent e) {
		e = new ActionEvent(
			component,
			ActionEvent.ACTION_PERFORMED,
			actionCommand,
			e.getWhen(),
			e.getModifiers());
		originalAction.actionPerformed(e);
	}
}
