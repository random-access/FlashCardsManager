package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.FlashCard;
import core.LearningProject;
import core.ProjectsManager;
import exc.EntryNotFoundException;

@SuppressWarnings("serial")
public class ProjectPanel extends JPanel {

	static BufferedImage imgPlay, imgEdit, imgDelete, imgRed, imgYellow, imgGreen;

	static {
		try {
			imgPlay = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgPlay_16x16.png"));
			imgEdit = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgEdit_16x16.png"));
			imgDelete = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgDelete_16x16.png"));
			imgRed = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgRed_8x8.png"));
			imgYellow = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgYellow_8x8.png"));
			imgGreen = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgGreen_8x8.png"));

		} catch (IOException e) {
			System.out.println("Picture not found");
			// TODO: JDialog mit ErrorMsg
		}
	}

	static JFileChooser fileChooser;

	int noOfStacks;
	private Box b;
	JLabel lblStatus, lblText;
	private JButton btnPlay, btnEdit, btnDelete;
	private Status status;
	private String name;
	private JPopupMenu popupEdit;
	private JMenuItem popupEditChangeName, popupEditChangeNoOfStacks, popupEditAddCards, popupEditOrganizeCards,
			popupEditResetProgress;
	private MainWindow parentWindow;
	private ProjectsManager prm;
	private LearningProject project;
	private ArrayList<FlashCard> cards;

	// Constructor
	public ProjectPanel(LearningProject project, MainWindow parentWindow, ProjectsManager prm) {
		this.prm = prm;
		this.project = project;
		this.status = Status.RED;
		this.name = project.getTitle();
		this.parentWindow = parentWindow;
		this.noOfStacks = project.getNumberOfStacks();
		cards = project.getAllCards();
		this.setLayout(new BorderLayout());
		createWidgets();
		addWidgets();
		setListeners();
	}

	MainWindow getOwner() {
		return this.parentWindow;
	}

	LearningProject getProject() {
		return this.project;
	}

	public void addWidgets() {
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

	public void createWidgets() {
		b = Box.createHorizontalBox();
		b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setStatus(this.status);
		lblText = new JLabel(name);
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
		fileChooser.setFileFilter(new FileNameExtensionFilter("Bilddateien", "png", "jpg"));
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	public void addCard(FlashCard card) {
		cards.add(card);
	}

	public void removeCard(FlashCard card) {
		cards.remove(card);
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status s) {
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

	public void setListeners() {
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
							prm.deleteProject(project);
						} catch (EntryNotFoundException e1) {
							// TODO Error handling
							System.out.println("Eintrag in DB nicht vorhanden - in ProjectPanel");
						} catch (SQLException e1) {
							System.out.println("SQL Fehler - in ProjectPanel");
							e1.printStackTrace();
						}
						parentWindow.projectPnls.remove(ProjectPanel.this);
						parentWindow.pnlCenter.remove(parentWindow.centerBox);
						parentWindow.centerBox = Box.createVerticalBox();
						parentWindow.addProjectsToPanel();
						parentWindow.pnlCenter.add(parentWindow.centerBox, BorderLayout.NORTH);
						parentWindow.repaint();
						parentWindow.revalidate();
						d.dispose();
					}
				});
				d.setVisible(true);
			}
		});

		btnEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component source = (Component) e.getSource();
				Dimension size = source.getSize();

				int xPos = size.width - btnEdit.getPreferredSize().width;
				int yPos = size.width; // - btnEdit.getPreferredSize().height;

				popupEdit.show(source, xPos, yPos);
			}

		});

		popupEditChangeName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeTitleDialog p = new ChangeTitleDialog(ProjectPanel.this, project, prm);
				p.setVisible(true);
			}

		});

		popupEditChangeNoOfStacks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeStacksDialog p = new ChangeStacksDialog(ProjectPanel.this, project, prm);
				p.setVisible(true);
			}

		});

		popupEditAddCards.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddFlashcardDialog d = new AddFlashcardDialog(project, ProjectPanel.this);
				d.setVisible(true);
			}

		});

		popupEditOrganizeCards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditFlashcardsDialog d = new EditFlashcardsDialog(ProjectPanel.this, cards, project);
				d.setVisible(true);
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
						for (int i = 0; i < cards.size(); i++) {
							try {
								cards.get(i).setStack(1);
								changeStatus(Status.RED);
							} catch (SQLException exc) {
								JOptionPane.showMessageDialog(ProjectPanel.this,
										"Keine Verbindung zur Datenbank. Bitte probiere es noch einmal.", "Datenbankfehler",
										JOptionPane.ERROR_MESSAGE);
							} catch (EntryNotFoundException e1) {
								JOptionPane.showMessageDialog(ProjectPanel.this,
										"Keine Verbindung zur Datenbank. Bitte probiere es noch einmal.", "Datenbankfehler",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						d.dispose();
					}
				});
				d.setVisible(true);
			}
		});

		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					ChooseStacksDialog chooseStacks = new ChooseStacksDialog(ProjectPanel.this.getOwner(),
							ProjectPanel.this.cards, ProjectPanel.this.project);
					chooseStacks.setVisible(true);
				} catch (SQLException e) {
					System.out.println("SQL-Fehler bei Stapelauswahl!");
					// TODO: error handling
				}
			}

		});

	}

}
