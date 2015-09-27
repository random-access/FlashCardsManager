package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.core.*;
import org.random_access.flashcardsmanager_desktop.core.Label;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.gui.helpers.IHasOkButton;

@SuppressWarnings("serial")
public class PrepareLearningSessionDialog extends JDialog implements IHasOkButton {

	private MainWindow owner;
	private ArrayList<FlashCard> cards, sessionCards;
	private LearningProject project;

	private JPanel pnlControls, pnlSessionChoice, pnlCriteriaChoice;
	private Box centerBox;
	private JScrollPane scpCenter;
	private PrepareSessionCheckBox[] stackBoxes, labelBoxes;
	private JLabel lblStacks, lblLabels, lblSessionChoice, lblCriteria1, lblCriteria2;
	private Box stackTitleBox, labelTitleBox, controlBox;
	private JButton btnOk, btnDiscard;
	private JRadioButton rbRandomSession, rbOrderedSession, rbAndCriteria, rbOrCriteria;
	private ButtonGroup groupSessionChoice, groupCriteriaChoice;

	PrepareLearningSessionDialog(MainWindow owner, ArrayList<FlashCard> allCards, LearningProject project) throws SQLException {
		super(owner, false);
		this.owner = owner;
		this.project = project;
		cards = copyCards(allCards);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Lernauswahl..");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
			CustomErrorHandling.showInternalError(null, exc);
		}

		createWidgets();
		addWidgets();
		setListeners();
		setSize(getPreferredSize().width + 10,
				Math.min(getPreferredSize().height + 50, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.5)));
		setLocationRelativeTo(owner);
	}

	// CREATE WIDGETS: create GUI components
	private void createWidgets() throws SQLException {
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		controlBox = Box.createVerticalBox();
		centerBox = Box.createVerticalBox();
		centerBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scpCenter = new JScrollPane(centerBox);
		scpCenter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		stackBoxes = new PrepareSessionCheckBox[project.getNumberOfStacks()];
		for (int i = 1; i <= project.getNumberOfStacks(); i++) {
			String text = "Stapel " + i + ": ";
			stackBoxes[i - 1] = new PrepareSessionCheckBox(text, project.getNumberOfCards(i), PrepareLearningSessionDialog.this);
			if (project.getNumberOfCards(i) == 0) {
				stackBoxes[i - 1].setEnabled(false);
			}
		}
		labelBoxes = new PrepareSessionCheckBox[project.getLabels().size()];
		for (int i = 0; i < project.getLabels().size(); i++) {
			String text = project.getLabels().get(i).getName() + ": ";
			labelBoxes[i] = new PrepareSessionCheckBox(text, project.getLabels().get(i).getNumberOfCards(),
					PrepareLearningSessionDialog.this);
			if (project.getLabels().get(i).getNumberOfCards() == 0) {
				labelBoxes[i].setEnabled(false);
			}
		}
		lblStacks = new JLabel("Stapel w\u00e4hlen: ");
		lblStacks.setFont(lblStacks.getFont().deriveFont(Font.BOLD));
		lblLabels = new JLabel("Label w\u00e4hlen: ");
		lblLabels.setFont(lblLabels.getFont().deriveFont(Font.BOLD));
		stackTitleBox = new Box(BoxLayout.X_AXIS);
		labelTitleBox = new Box(BoxLayout.X_AXIS);
		btnOk = new JButton("Ok");
		btnOk.setEnabled(false);
		btnDiscard = new JButton("Abbrechen");

		pnlSessionChoice = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		rbRandomSession = new JRadioButton("zuf\u00e4llig");
		rbRandomSession.setSelected(true);
		rbOrderedSession = new JRadioButton("geordnet");
		groupSessionChoice = new ButtonGroup();
		groupSessionChoice.add(rbRandomSession);
		groupSessionChoice.add(rbOrderedSession);
		lblSessionChoice = new JLabel("Reihenfolge: ");

		pnlCriteriaChoice = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		rbAndCriteria = new JRadioButton("UND   /");
		rbOrCriteria = new JRadioButton("ODER ");
		rbAndCriteria.setSelected(true);
		groupCriteriaChoice = new ButtonGroup();
		groupCriteriaChoice.add(rbAndCriteria);
		groupCriteriaChoice.add(rbOrCriteria);
		lblCriteria1 = new JLabel("Stapel ");
		lblCriteria2 = new JLabel("Label");
	}

	// ADD WIDGETS: put the GUI together
	private void addWidgets() {
		this.add(scpCenter, BorderLayout.CENTER);
		this.add(controlBox, BorderLayout.SOUTH);
		controlBox.add(pnlCriteriaChoice);
		controlBox.add(pnlSessionChoice);
		controlBox.add(pnlControls);
		pnlCriteriaChoice.add(lblCriteria1);
		pnlCriteriaChoice.add(rbAndCriteria);
		pnlCriteriaChoice.add(rbOrCriteria);
		pnlCriteriaChoice.add(lblCriteria2);
		pnlSessionChoice.add(lblSessionChoice);
		pnlSessionChoice.add(rbRandomSession);
		pnlSessionChoice.add(rbOrderedSession);
		if (project.getLabels().size() > 0) {
			centerBox.add(labelTitleBox);
			labelTitleBox.add(lblLabels);
		}
		for (int i = 0; i < project.getLabels().size(); i++) {
			centerBox.add(labelBoxes[i]);
		}
		centerBox.add(Box.createVerticalStrut(10));
		centerBox.add(stackTitleBox);
		stackTitleBox.add(lblStacks);
		for (int i = 1; i <= project.getNumberOfStacks(); i++) {
			centerBox.add(stackBoxes[i - 1]);
		}
		pnlControls.add(btnDiscard);
		pnlControls.add(btnOk);
	}

	// SET LISTENERS: add listeners to GUI elements
	private void setListeners() {
		btnDiscard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrepareLearningSessionDialog.this.dispose();
			}
		});

		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbOrderedSession.isSelected()) {
					sessionCards = constructOrderedSession();
				} else {
					sessionCards = constructRandomSession();
				}
				LearningSession newSession = new LearningSession(PrepareLearningSessionDialog.this.owner,
						PrepareLearningSessionDialog.this.project, PrepareLearningSessionDialog.this.sessionCards);
				newSession.setVisible(true);
				PrepareLearningSessionDialog.this.dispose();
			}
		});
	}

	// COPY CARDS: copy flashcard array from Project Panel, because the cards
	// will be deleted
	// while constructing a learning session
	private ArrayList<FlashCard> copyCards(ArrayList<FlashCard> srcCards) {
		ArrayList<FlashCard> targetCards = new ArrayList<FlashCard>();
		targetCards.addAll(srcCards);
		return targetCards;
	}

	// CONSTRUCT ORDERED SESSION: constructs session with cards from selected
	// stacks / labels in card id order
	private ArrayList<FlashCard> constructOrderedSession() {
		sessionCards = new ArrayList<FlashCard>();
		for (int i = 0; i < cards.size(); i++) {
			FlashCard currentCard = cards.get(i);
			addIfCriteriasAreMatching(currentCard);
		}
		return sessionCards;
	}

	// CONSTRUCT RANDOM SESSION: constructs session with cards from selected
	// stacks / labels in random order
	private ArrayList<FlashCard> constructRandomSession() {
		sessionCards = new ArrayList<FlashCard>();
		Random random = new Random();
		while (!cards.isEmpty()) {
			int randomIndex = random.nextInt(cards.size());
			FlashCard currentCard = cards.get(randomIndex);
			cards.remove(currentCard); // anyway remove card from project
										// arraylist
			addIfCriteriasAreMatching(currentCard);
		}
		return sessionCards;
	}

	public void addIfCriteriasAreMatching(FlashCard currentCard) {
		int index = currentCard.getStack() - 1; // map stack numbers to indices
		ArrayList<Label> cardLabels = currentCard.getLabels();
		ArrayList<Label> selectionLabels = getSelectedLabels();
		// if there is no selection, initial boolean must be true for match
		// calculation
		boolean isStack = noSelection(stackBoxes);
		boolean isLabel = noSelection(labelBoxes);
		// is card in selected stack?
		if (stackBoxes[index].isSelected())
			isStack = true;
		// has card one of selected labels?
		for (int j = 0; j < selectionLabels.size(); j++) {
			if (cardLabels.contains(selectionLabels.get(j))) {
				isLabel = true;
			}
		}
		if (matchCriteria(isStack, isLabel)) {
			sessionCards.add(currentCard);
		}
	}

	// returns true if no selection or empty array
	private boolean noSelection(PrepareSessionCheckBox[] boxes) {
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isSelected()) {
				return false;
			}
		}
		return true;
	}

	// apply the chosen match criteria, AND or OR, depending on which radio
	// button is active
	private boolean matchCriteria(boolean isStack, boolean isLabel) {
		return rbAndCriteria.isSelected() ? isStack && isLabel : isStack || isLabel;
	}

	// returns a list of all labels that are selected
	private ArrayList<Label> getSelectedLabels() {
		ArrayList<Label> selectionLabels = new ArrayList<Label>();
		for (int i = 0; i < labelBoxes.length; i++) {
			if (labelBoxes[i].isSelected()) {
				selectionLabels.add(project.getLabels().get(i));
			}
		}
		return selectionLabels;
	}

	public void controlOkButton() {
		boolean anythingSelected = false;
		for (PrepareSessionCheckBox box : stackBoxes) {
			if (box.isSelected()) {
				anythingSelected = true;
			}
		}
		for (PrepareSessionCheckBox box : labelBoxes) {
			if (box.isSelected()) {
				anythingSelected = true;
			}
		}
		btnOk.setEnabled(anythingSelected);
	}

}