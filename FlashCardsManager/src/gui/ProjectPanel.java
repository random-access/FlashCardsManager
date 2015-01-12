package gui;

import exc.CustomErrorHandling;
import gui.helpers.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import utils.Logger;
import core.*;

@SuppressWarnings("serial")
public class ProjectPanel extends JPanel {

	private static BufferedImage imgPlay, imgEdit, imgDelete, imgRed, imgYellow, imgGreen;

	static {
		try {
			imgPlay = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgPlay_16x16.png"));
			imgEdit = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgEdit_16x16.png"));
			imgDelete = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgDelete_16x16.png"));
			imgRed = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgRed_8x8.png"));
			imgYellow = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgYellow_8x8.png"));
			imgGreen = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgGreen_8x8.png"));

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
			Logger.log(e);
		}
	}

	static JFileChooser fileChooser; 
	// this is static to hand it over to flashcard editing for open it at last chosen path

	int noOfStacks;
	private Box b;
	private JLabel lblStatus, lblText;
	private JButton btnPlay, btnEdit, btnDelete;
	private Status status;
	private String projectTitle;
	private JPopupMenu popupEdit;
	private JMenuItem popupEditChangeName, popupEditChangeNoOfStacks, popupEditAddCards, popupEditOrganizeCards,
			popupEditResetProgress;
	private MainWindow parentWindow;
	private ProjectsController ctl;
	private LearningProject project;
	private ArrayList<FlashCard> cards;

	public enum DialogType {
		EDIT, ADD, RESET, PLAY, CHANGE_STACKS;
	}

	// Constructor
	public ProjectPanel(LearningProject project, MainWindow parentWindow, ProjectsController ctl) {
		this.ctl = ctl;
		this.project = project;
		this.status = Status.RED;
		this.projectTitle = project.getTitle();
		this.parentWindow = parentWindow;
		this.noOfStacks = project.getNumberOfStacks();
		this.setLayout(new BorderLayout());
		createWidgets();
		addWidgets();
		setListeners();
	}

	public MainWindow getOwner() {
		return this.parentWindow;
	}
	
	public String getProjectTitle() {
		return projectTitle;
	}

	public LearningProject getProject() {
		return this.project;
	}

	private void addWidgets() {
		this.add(b, BorderLayout.CENTER);
		b.add(lblStatus);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(lblText);
		b.add(Box.createHorizontalGlue());
		b.add(Box.createRigidArea(new Dimension(30, 0)));
		b.add(btnPlay);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(btnEdit);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(btnDelete);

		popupEdit.add(popupEditChangeName);
		popupEdit.add(popupEditChangeNoOfStacks);
		popupEdit.add(popupEditAddCards);
		popupEdit.add(popupEditOrganizeCards);
		popupEdit.add(popupEditResetProgress);
	}

	private void createWidgets() {
		b = Box.createHorizontalBox();
		b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setStatus(this.status);
		lblText = new JLabel(projectTitle);
		lblText.setFont(lblText.getFont().deriveFont(Font.BOLD, 12));
		btnPlay = new JButton(new ImageIcon(imgPlay));
		btnPlay.setToolTipText("Lernen");
		btnEdit = new JButton(new ImageIcon(imgEdit));
		btnEdit.setToolTipText("Projekt bearbeiten");
		btnDelete = new JButton(new ImageIcon(imgDelete));
		btnDelete.setToolTipText("Projekt l\u00f6schen");

		popupEdit = new JPopupMenu();
		popupEditChangeName = new JMenuItem("Titel bearbeiten..");
		popupEditChangeNoOfStacks = new JMenuItem("Anzahl Durchl\u00e4ufe \u00e4ndern..");
		popupEditAddCards = new JMenuItem("Lernkarten hinzuf\u00fcgen..");
		popupEditOrganizeCards = new JMenuItem("Lernkarten bearbeiten..");
		popupEditResetProgress = new JMenuItem("Projekt zur\u00fccksetzen..");

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Bilddateien", "png", "jpg", "jpeg")); 
		// TODO test if other extensions work as well; add them; global list of known extensions
	}

	JFileChooser getFileChooser() {
		return fileChooser;
	}

//	public Status getStatus() {
//		return this.status;
//	}

	private void setStatus(Status s) {
		switch (s) {
		case RED:
			lblStatus = new JLabel(new ImageIcon(imgRed));
			lblStatus.setToolTipText("Los geht's - es wurde noch keine Frage korrekt beantwortet");
			break;
		case YELLOW:
			lblStatus = new JLabel(new ImageIcon(imgYellow));
			lblStatus.setToolTipText("Weiter so - einige Fragen wurden schon korrekt beantwortet");
			break;
		case GREEN:
			lblStatus = new JLabel(new ImageIcon(imgGreen));
			lblStatus.setToolTipText("Bravo! Alle Fragen sind im letzten Stapel!");
		}
	}

	public void changeStatus(Status s) {
		remove(b);
		b = Box.createHorizontalBox();
		b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setStatus(s);
		addWidgets();
		revalidate();
	}

	private void setListeners() {
		btnDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final OkOrDisposeDialog d = new OkOrDisposeDialog(parentWindow, 300, 150);
				d.setTitle("Wirklich l\u00f6schen?");
				d.setText("<html>Wirklich das Projekt und alle <br>" + "zugeh\u00f6rigen Karten l\u00f6schen?</html>");
				d.addOkAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							project.loadFlashcards(null);
							project.delete();
							parentWindow.updateProjectList();
						} catch (SQLException sqle) {
							CustomErrorHandling.showDatabaseError(parentWindow, sqle);
						} catch (IOException ioe) {
							CustomErrorHandling.showInternalError(parentWindow, ioe);
						} finally {
							d.dispose();
						}
					}
				});
				d.setVisible(true);
			}
		});
		
		// open edit menu with several options
		btnEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component source = (Component) e.getSource();
				Dimension size = source.getSize();
				int xPos = size.width - btnEdit.getPreferredSize().width;
				int yPos = size.width; 
				popupEdit.show(source, xPos, yPos);
			}
		});

		popupEditChangeName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeTitleDialog p = new ChangeTitleDialog(ProjectPanel.this, project);
				p.setVisible(true);
			}
		});

		popupEditChangeNoOfStacks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ProgressDialog dialog = new ProgressDialog(parentWindow, "Lade Karten...");
				dialog.setVisible(true);
				LoadCardsTask task = new LoadCardsTask(dialog, parentWindow, ProjectPanel.this, project, DialogType.CHANGE_STACKS);
				task.addPropertyChangeListener(dialog);
				task.execute();
			}
		});

		popupEditAddCards.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ProgressDialog dialog = new ProgressDialog(parentWindow, "Lade Karten...");
				dialog.setVisible(true);
				LoadCardsTask task = new LoadCardsTask(dialog, parentWindow, ProjectPanel.this, project, DialogType.ADD);
				task.addPropertyChangeListener(dialog);
				task.execute();
			}
		});

		popupEditOrganizeCards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProgressDialog dialog = new ProgressDialog(parentWindow, "Lade Karten...");
				dialog.setVisible(true);
				LoadCardsTask task = new LoadCardsTask(dialog, parentWindow, ProjectPanel.this, project, DialogType.EDIT);
				task.addPropertyChangeListener(dialog);
				task.execute();
			}
		});

		popupEditResetProgress.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final OkOrDisposeDialog d = new OkOrDisposeDialog(ProjectPanel.this.getOwner(), 300, 150);
				d.setText("<html>M\u00f6chtest Du wirklich alle Karten <br> in den ersten Stapel zur\u00fccklegen?</html>");
				d.setTitle("Lernerfolg zur\u00fccksetzen?");
				d.addOkAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ProgressDialog dialog = new ProgressDialog(parentWindow, "Lade Karten...");
						dialog.setVisible(true);
						LoadCardsTask task = new LoadCardsTask(dialog, parentWindow, ProjectPanel.this, project, DialogType.RESET);
						task.addPropertyChangeListener(dialog);
						task.execute();
						d.dispose();
					}
				});
				d.setVisible(true);
			}
		});

		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProgressDialog dialog = new ProgressDialog(parentWindow, "Lade Karten...");
				dialog.setVisible(true);
				LoadCardsTask task = new LoadCardsTask(dialog, parentWindow, ProjectPanel.this, project, DialogType.PLAY);
				task.addPropertyChangeListener(dialog);
				task.execute();
			}

		});

	}

	private void showAddFlashcardsDialog() {
		cards = project.getAllCards();
		FlashcardEditorDialog d;
		try {
			d = new FlashcardEditorDialog(null, project, ProjectPanel.this);
			d.setVisible(true);
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(parentWindow, ioe);
		}

	}

	private void showEditFlashcardsDialog() {
		cards = project.getAllCards();
		FlashcardOverviewDialog d;
		try {
			d = new FlashcardOverviewDialog(ProjectPanel.this, cards, project);
			d.setVisible(true);
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(parentWindow, sqle);
		}
		

	}

	private void resetProgress() {
		cards = project.getAllCards();
		for (int i = 0; i < cards.size(); i++) {
			cards.get(i).setStack(1);
			try {
				cards.get(i).update();
				changeStatus(Status.RED);
			} catch (SQLException sqle) {
				CustomErrorHandling.showDatabaseError(parentWindow, sqle);
			} catch (IOException ioe) {
				CustomErrorHandling.showInternalError(parentWindow, ioe);
			}

		}
	}

	private void prepareLearningSession() {
		cards = project.getAllCards();
		PrepareLearningSessionDialog chooseStacks;
		try {
			chooseStacks = new PrepareLearningSessionDialog(ProjectPanel.this.getOwner(), ProjectPanel.this.cards,
					ProjectPanel.this.project);
			chooseStacks.setVisible(true);
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(parentWindow, sqle);
		}
	}

	private void showChangeStacksDialog() {
		ChangeStacksDialog p = new ChangeStacksDialog(ProjectPanel.this, project, ctl);
		p.setVisible(true);
	}
	
	// TODO work on better solution (own event handling? wait/notify?)
	public void resume(DialogType type) {
		switch (type) {
		case ADD:
			showAddFlashcardsDialog();
			break;
		case EDIT:
			showEditFlashcardsDialog();
			break;
		case RESET:
			resetProgress();
			break;
		case PLAY:
			prepareLearningSession();
			break;
		case CHANGE_STACKS:
			showChangeStacksDialog();
			break;
		}

	}

}
