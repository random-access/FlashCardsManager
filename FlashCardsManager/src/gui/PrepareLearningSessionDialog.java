package gui;

import exc.CustomErrorHandling;
import gui.helpers.IHasOkButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import core.*;
import core.Label;

@SuppressWarnings("serial")
public class PrepareLearningSessionDialog extends JDialog implements IHasOkButton {

    private MainWindow owner;
    private ArrayList<FlashCard> cards, sessionCards;
    private LearningProject project;

    private JPanel pnlControls;
    private Box centerBox;
    private JScrollPane scpCenter;
    private PrepareSessionCheckBox[] stackBoxes, labelBoxes;
    private JLabel lblStacks, lblLabels;
    private Box stackTitleBox, labelTitleBox;
    private JButton btnOk, btnDiscard;

    PrepareLearningSessionDialog(MainWindow owner, ArrayList<FlashCard> allCards, LearningProject project) throws SQLException {
        super(owner, false);
        this.owner = owner;
        this.project = project;
        cards = copyCards(allCards);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Stapelauswahl..");
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

    }

    // ADD WIDGETS: put the GUI together
    private void addWidgets() {
        this.add(scpCenter, BorderLayout.CENTER);
        this.add(pnlControls, BorderLayout.SOUTH);
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
                sessionCards = constructRandomSession();
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
            ArrayList<Label> cardLabels = currentCard.getLabels();
            ArrayList<Label> selectionLabels = getSelectedLabels();
            if (stackBoxes[stack].isSelected()) { // get only cards from
                                                  // selected stacks
                sessionCards.add(currentCard);
            } else { // get cards from selected labels
                for (int i = 0; i < selectionLabels.size(); i++) {
                    if (cardLabels.contains(selectionLabels.get(i))) {
                        sessionCards.add(currentCard);
                    }
                }
            }
        }
        return sessionCards;
    }

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