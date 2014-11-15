package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.FlashCard;
import core.LearningProject;

public class ChooseStacksDialog extends JDialog {

	MainWindow owner;
	ArrayList<FlashCard> allCards, sessionCards;
	private LearningProject project;

	private JPanel pnlControls;
	private Box centerBox;
	private StackBox[] boxes;
	private JButton btnOk, btnDiscard;
	private boolean activeSelection;

	public ChooseStacksDialog(MainWindow owner, ArrayList<FlashCard> allCards,
			LearningProject project) throws SQLException {
		super(owner, false);
		this.owner = owner;
		this.allCards = allCards;
		this.project = project;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Stapelauswahl..");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		createWidgets();
		addWidgets();
		setListeners();

		pack();
		setLocationRelativeTo(owner);
	}

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

	private void addWidgets() {
		this.add(centerBox, BorderLayout.CENTER);
		this.add(pnlControls, BorderLayout.SOUTH);
		for (int i = 1; i <= project.getNumberOfStacks(); i++) {
			centerBox.add(boxes[i - 1]);
		}
		pnlControls.add(btnDiscard);
		pnlControls.add(btnOk);
	}

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
				sessionCards = constructSession();
				LearningSession newSession = new LearningSession(
						ChooseStacksDialog.this.owner,
						ChooseStacksDialog.this.project,
						ChooseStacksDialog.this.sessionCards);
				if (activeSelection) {
					newSession.setVisible(true);
					ChooseStacksDialog.this.dispose();
				} else {
					JOptionPane.showMessageDialog(ChooseStacksDialog.this,
							"Bitte Stapel ausw\u00e4hlen!", "Achtung",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}

	private ArrayList<FlashCard> constructSession() {
		sessionCards = new ArrayList<FlashCard>();
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].isSelected()) {
				activeSelection = true;
				ListIterator<FlashCard> lit = allCards.listIterator();
				while (lit.hasNext()) {
					FlashCard f = lit.next();
					if (f.getStack() == i + 1) {
						sessionCards.add(f);
						System.out.println("Copied card " + f.getId()
								+ " (Stack " + f.getStack() + ")...");
					}
				}
			}
		}
		return sessionCards;
	}

}
