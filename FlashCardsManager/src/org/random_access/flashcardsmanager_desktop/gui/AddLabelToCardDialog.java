package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.core.*;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.gui.helpers.IHasOkButton;

@SuppressWarnings("serial")
public class AddLabelToCardDialog extends JDialog implements IHasOkButton {

	private LearningProject p;

	private JPanel pnlControls;
	private Box centerBox;
	private JScrollPane scpCenter;
	private PrepareSessionCheckBox[] boxes;
	private JButton btnOk, btnDiscard;
	private JDialog owner;

	public AddLabelToCardDialog(JDialog owner, LearningProject p, FlashCard c) throws SQLException {
		super(owner, false);
		this.owner = owner;
		this.p = p;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Label hinzufuegen...");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
			CustomErrorHandling.showInternalError(null, exc);
		}

		createWidgets();
		addWidgets();
		setListeners();
		setSize(getPreferredSize().width + 10, 300);
		setLocationRelativeTo(owner);
	}

	// CREATE WIDGETS: create GUI components
	private void createWidgets() throws SQLException {
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		centerBox = Box.createVerticalBox();
		centerBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scpCenter = new JScrollPane(centerBox);
		scpCenter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		boxes = new PrepareSessionCheckBox[p.getLabels().size()];
		for (int i = 0; i < p.getLabels().size(); i++) {
			boxes[i] = new PrepareSessionCheckBox(p.getLabels().get(i).getName(), AddLabelToCardDialog.this, hasLabel(p.getLabels().get(i)));
		}

		btnOk = new JButton("Ok");
		// btnOk.setEnabled(false);
		btnDiscard = new JButton("Abbrechen");

	}

	private boolean hasLabel(Label label) {
		return ((FlashcardEditorDialog) owner).getLabels().contains(label);
	}

	// ADD WIDGETS: put the GUI together
	private void addWidgets() {
		this.add(scpCenter, BorderLayout.CENTER);
		this.add(pnlControls, BorderLayout.SOUTH);
		for (int i = 0; i < p.getLabels().size(); i++) {
			centerBox.add(boxes[i]);
		}
		pnlControls.add(btnDiscard);
		pnlControls.add(btnOk);
	}

	// SET LISTENERS: add listeners to GUI elements
	private void setListeners() {
		btnDiscard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddLabelToCardDialog.this.dispose();
			}
		});

		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Label> labels = new ArrayList<Label>();
				for (int i = 0; i < boxes.length; i++) {
					if (boxes[i].isSelected()) {
						labels.add(p.getLabels().get(i));
					}
				}
				((FlashcardEditorDialog) owner).setLabels(labels);
				AddLabelToCardDialog.this.dispose();
			}
		});
	}

	public void controlOkButton() {
		// boolean anythingSelected = false;
		// for (StackBox box : boxes) {
		// if (box.isSelected()) {
		// anythingSelected = true;
		// }
		// }
		// btnOk.setEnabled(anythingSelected);
	}
}
