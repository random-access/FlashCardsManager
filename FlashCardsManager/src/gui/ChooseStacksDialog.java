package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import utils.Logger;
import core.FlashCard;
import core.LearningProject;

@SuppressWarnings("serial")
public class ChooseStacksDialog extends JDialog {

	private MainWindow owner;
	private ArrayList<FlashCard> cards, sessionCards;
	private LearningProject project;

	private JPanel pnlControls;
	private Box centerBox;
	private StackBox[] boxes;
	private JButton btnOk, btnDiscard;
	private boolean activeSelection;

	ChooseStacksDialog(MainWindow owner, ArrayList<FlashCard> allCards,
			LearningProject project) throws SQLException {
		super(owner, false);
		this.owner = owner;
		this.project = project;
		cards = copyCards(allCards);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Stapelauswahl..");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      }

		createWidgets();
		addWidgets();
		setListeners();

		pack();
		setLocationRelativeTo(owner);
	}

	// CREATE WIDGETS: create GUI components
	private void createWidgets() throws SQLException {
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		centerBox = Box.createVerticalBox();
		boxes = new StackBox[project.getNumberOfStacks()];
		for (int i = 1; i <= project.getNumberOfStacks(); i++) {
			boxes[i - 1] = new StackBox(i, project.getNumberOfCards(i));
		}
		btnOk = new JButton("Ok");
		btnDiscard = new JButton("Abbrechen");

	}

	// ADD WIDGETS: put the GUI together
	private void addWidgets() {
		this.add(centerBox, BorderLayout.CENTER);
		this.add(pnlControls, BorderLayout.SOUTH);
		for (int i = 1; i <= project.getNumberOfStacks(); i++) {
			centerBox.add(boxes[i - 1]);
		}
		pnlControls.add(btnDiscard);
		pnlControls.add(btnOk);
	}

	// SET LISTENERS: add listeners to GUI elements
	private void setListeners() {
		btnDiscard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChooseStacksDialog.this.dispose();
			}
		});

		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sessionCards = constructRandomSession();
				LearningSession newSession = new LearningSession(
						ChooseStacksDialog.this.owner,
						ChooseStacksDialog.this.project,
						ChooseStacksDialog.this.sessionCards);
				if (activeSelection) {
					newSession.setVisible(true);
					ChooseStacksDialog.this.dispose();
				} else {
					JOptionPane.showMessageDialog(ChooseStacksDialog.this,
							"Bitte Stapel mit Karten ausw\u00e4hlen!", "Achtung",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}

	// COPY CARDS: copy flashcard array from Project Panel, because the cards
	// will be deleted
	// while constructing an empty session, and are needed e.g. in
	// LearningSession
	private ArrayList<FlashCard> copyCards(ArrayList<FlashCard> srcCards) {
		ArrayList<FlashCard> targetCards = new ArrayList<FlashCard>();
		targetCards.addAll(srcCards);
		return targetCards;
	}

	// CONSTRUCT SESSION: constructs session with cards from selected stacks in
	// order from database entries
	// private ArrayList<FlashCard> constructSession() {
	// sessionCards = new ArrayList<FlashCard>();
	// for (int i = 0; i < boxes.length; i++) {
	// if (boxes[i].isSelected()) {
	// activeSelection = true; // for error msg when no selection
	// ListIterator<FlashCard> lit = cards.listIterator();
	// while (lit.hasNext()) {
	// FlashCard f = lit.next();
	// if (f.getStack() == i + 1) { // stacks go from 1 upwards
	// sessionCards.add(f);
	// + " (Stack " + f.getStack() + ")...");
	// }
	// }
	// }
	// }
	// return sessionCards;
	// }

	// CONSTRUCT RANDOM SESSION: constructs session with cards from selected
	// stacks in random order
	private ArrayList<FlashCard> constructRandomSession() {
		sessionCards = new ArrayList<FlashCard>();
		Random random = new Random();
		while (!cards.isEmpty()) {
			int randomIndex = random.nextInt(cards.size());
			FlashCard currentCard = cards.get(randomIndex);
			cards.remove(currentCard); // anyway remove card from project
										// arraylist
			int stack = currentCard.getStack() - 1; // stack numbers are from 1
													// upwards;
			if (boxes[stack].isSelected()) { // get only cards from selected
												// stacks
				activeSelection = true; // for error msg when no selection
				sessionCards.add(currentCard);
			}
		}
		return sessionCards;
	}

}