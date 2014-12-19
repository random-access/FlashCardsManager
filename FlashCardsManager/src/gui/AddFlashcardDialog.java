package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import utils.Logger;
import core.FlashCard;
import core.LearningProject;
import db.PicType;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;

@SuppressWarnings("serial")
public class AddFlashcardDialog extends JDialog {
	protected static BufferedImage imgDelete;
	static {
		try {
			imgDelete = ImageIO.read(ProjectPanel.class.getClassLoader()
					.getResourceAsStream("img/ImgDelete_16x16.png"));
		} catch (IOException e) {
			// TODO error handling
			System.out.println("Pic not found");
		}
	}

	private MainWindow owner;
	protected LearningProject project;
	protected ProjectPanel projPnl;
	protected Box centerBox, boxControlsQuestion, boxControlsAnswer;
	protected JScrollPane scpQuestion, scpAnswer;
	protected JLabel lblQuestion, lblAnswer;
	protected String pathToQuestionPic, pathToAnswerPic;
	protected JPanel pnlControls, pnlQuestion, pnlAnswer;
	protected JButton btnDiscard, btnSave, btnSaveAndNext, btnPreview,
			btnAddPicQuestion, btnAddPicAnswer, btnDelPicQ, btnDelPicA;
	protected DesignFlashcardPanel txtQuestion, txtAnswer;
	protected FlashCardPanel fcPnl;
	protected EditFlashcardsDialog efcDialog;

	public AddFlashcardDialog(LearningProject project, ProjectPanel projPnl) {
		super(projPnl.getOwner(), true);
		this.owner = projPnl.getOwner();
		this.project = project;
		this.projPnl = projPnl;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Neue Lernkarte..");
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException |UnsupportedLookAndFeelException e) {
		   JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
		}

		createWidgets();
		addWidgets();
		setListeners();

		setSize(600, 600);
		// pack();
		setLocationRelativeTo(owner);
	}

	public AddFlashcardDialog(EditFlashcardsDialog efcDialog,
			LearningProject project, ProjectPanel projPnl) {
		this(project, projPnl);
		this.efcDialog = efcDialog;
	}

	protected void setListeners() {
		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddFlashcardDialog.this.dispose();
			}

		});

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FlashCard newCard = new FlashCard(project, txtQuestion
							.getText(), txtAnswer.getText(), pathToQuestionPic,
							pathToAnswerPic);
					projPnl.addCard(newCard);
					owner.updateProjectStatus(project);
					if (efcDialog != null) {
						System.out.println("EFCDiag != null");
						efcDialog.updateCardPanels();
					}
					System.out.println("Successfully added card!");
					AddFlashcardDialog.this.dispose();
				} catch (EntryAlreadyThereException | EntryNotFoundException | SQLException e1) {
				   JOptionPane.showMessageDialog(null,
		               "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
		               JOptionPane.ERROR_MESSAGE);
		         Logger.log(e1);
				} catch (IOException e1) {
				   JOptionPane.showMessageDialog(null,
                     "Ein interner Fehler ist aufgetreten", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(e1);
				}
			}

		});

		btnSaveAndNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FlashCard newCard = new FlashCard(project, txtQuestion
							.getText(), txtAnswer.getText(), pathToQuestionPic,
							pathToAnswerPic);
					projPnl.addCard(newCard);
					owner.updateProjectStatus(project);
					if (efcDialog != null) {
						efcDialog.updateCardPanels();
					}
					System.out.println("Successfully added card!");
					AddFlashcardDialog.this.dispose();
					AddFlashcardDialog d = new AddFlashcardDialog(efcDialog,
							project, projPnl);
					d.setVisible(true);
				} catch (EntryAlreadyThereException | EntryNotFoundException | SQLException e1) {
				   JOptionPane.showMessageDialog(null,
                     "Ein interner Datenbankfehler ist aufgetreten", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(e1);
				} catch (IOException e1) {
				   JOptionPane.showMessageDialog(null,
                     "Ein interner Fehler ist aufgetreten", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(e1);
				}
			}
		});

		btnAddPicQuestion.addActionListener(new AddPictureListener(
				PicType.QUESTION));
		btnAddPicAnswer
				.addActionListener(new AddPictureListener(PicType.ANSWER));
		btnDelPicQ
				.addActionListener(new DeletePictureListener(PicType.QUESTION));
		btnDelPicA.addActionListener(new DeletePictureListener(PicType.ANSWER));

		btnPreview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PreviewDialog d = new PreviewDialog(projPnl.getOwner(), null,
						txtQuestion.getText(), txtAnswer.getText(),
						pathToQuestionPic, pathToAnswerPic);
				d.setVisible(true);
			}
		});
		
	}

	// Listener for deleting the selected pic
	private class DeletePictureListener implements ActionListener {
		PicType type;

		DeletePictureListener(PicType type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (type) {
			case QUESTION:
				pathToQuestionPic = null;
				txtQuestion.removePicture();
				txtQuestion.repaint();
				btnAddPicQuestion.setText("Bild hinzuf\u00fcgen...");
				boxControlsQuestion.remove(btnDelPicQ);
				boxControlsQuestion.repaint();
				break;
			case ANSWER:
				pathToAnswerPic = null;
				txtAnswer.removePicture();
				txtAnswer.repaint();
				btnAddPicAnswer.setText("Bild hinzuf\u00fcgen...");
				boxControlsAnswer.remove(btnDelPicA);
				boxControlsAnswer.repaint();
				break;
			}
			AddFlashcardDialog.this.revalidate();
		}

	}

	// Listener for adding a question pic / answer pic
	class AddPictureListener implements ActionListener {
		PicType type;

		AddPictureListener(PicType type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			int returnVal = projPnl.getFileChooser().showOpenDialog(
					AddFlashcardDialog.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					switch (type) {
					case QUESTION:
						pathToQuestionPic = projPnl.getFileChooser()
								.getSelectedFile().getAbsolutePath();
						System.out.println("Pfad zum Frage-Pic:"
								+ pathToQuestionPic);
						txtQuestion.addPicture(pathToQuestionPic);
						txtQuestion.repaint();
						btnAddPicQuestion.setText("Bild \u00e4ndern...");
						if (!boxControlsQuestion.isAncestorOf(btnDelPicQ)) {
							System.out.println("added delete button question");
							boxControlsQuestion.add(btnDelPicQ);
							boxControlsQuestion.repaint();
						}
						break;
					case ANSWER:
						pathToAnswerPic = projPnl.getFileChooser()
								.getSelectedFile().getAbsolutePath();
						System.out.println("Pfad zum Antwort-Pic:"
								+ pathToAnswerPic);
						txtAnswer.addPicture(pathToAnswerPic);
						txtAnswer.repaint();
						btnAddPicAnswer.setText("Bild \u00e4ndern...");
						if (!boxControlsAnswer.isAncestorOf(btnDelPicA)) {
							System.out.println("added delete button answer");
							boxControlsAnswer.add(btnDelPicA);
							boxControlsAnswer.repaint();
						}
						break;
					}
					AddFlashcardDialog.this.revalidate();
				} catch (IOException exc) {
				   JOptionPane.showMessageDialog(null,
                     "Ein interner Fehler ist aufgetreten", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(exc);
				}
			} else {
				System.out.println("just close");
			}
		}
	}

	protected void addWidgets() {
		this.add(centerBox, BorderLayout.CENTER);
		this.add(pnlControls, BorderLayout.SOUTH);
		boxControlsQuestion.add(lblQuestion);
		boxControlsQuestion.add(Box.createHorizontalGlue());
		boxControlsQuestion.add(btnAddPicQuestion);
		boxControlsAnswer.add(lblAnswer);
		boxControlsAnswer.add(Box.createHorizontalGlue());
		boxControlsAnswer.add(btnAddPicAnswer);
		centerBox.add(Box.createVerticalStrut(10));
		centerBox.add(boxControlsQuestion);
		centerBox.add(Box.createVerticalStrut(10));
		centerBox.add(txtQuestion);
		centerBox.add(Box.createVerticalStrut(10));
		centerBox.add(boxControlsAnswer);
		centerBox.add(Box.createVerticalStrut(10));
		centerBox.add(txtAnswer);
		centerBox.add(Box.createVerticalStrut(10));
		pnlControls.add(btnDiscard);
		pnlControls.add(btnPreview);
		pnlControls.add(btnSave);
		pnlControls.add(btnSaveAndNext);

	}

	private void createWidgets() {
		centerBox = Box.createVerticalBox();
		centerBox.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlControls.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
		lblQuestion = new JLabel("Frage:");
		lblAnswer = new JLabel("Antwort:");
		boxControlsQuestion = Box.createHorizontalBox();
		boxControlsAnswer = Box.createHorizontalBox();
		btnDiscard = new JButton("Abbrechen");
		btnPreview = new JButton("Vorschau");
		btnSave = new JButton("Speichern");
		btnSaveAndNext = new JButton("Speichern und n\u00e4chste");
		btnAddPicQuestion = new JButton("Bild hinzuf\u00fcgen...");
		btnAddPicAnswer = new JButton("Bild hinzuf\u00fcgen...");
		btnDelPicQ = new JButton(new ImageIcon(imgDelete));
		btnDelPicA = new JButton(new ImageIcon(imgDelete));
		txtQuestion = new DesignFlashcardPanel(this);
		txtAnswer = new DesignFlashcardPanel(this);
		scpQuestion = new JScrollPane();
		scpAnswer = new JScrollPane();
		pnlQuestion = new JPanel();
		pnlAnswer = new JPanel();
	}

	public void setQuestion(String question) {
		txtQuestion.setText(question);
	}

	public void setAnswer(String answer) {
		txtAnswer.setText(answer);
	}

}
