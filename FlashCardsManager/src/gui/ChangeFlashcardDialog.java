package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import core.FlashCard;
import core.LearningProject;
import db.PicType;
import exc.EntryNotFoundException;

public class ChangeFlashcardDialog extends AddFlashcardDialog {

	FlashCard card;

	ChangeFlashcardDialog(EditFlashcardsDialog efcDialog,
			LearningProject project, ProjectPanel pnl, FlashCard card) {
		super(efcDialog, project, pnl);
		this.card = card;
		super.setQuestion(card.getQuestion());
		super.setAnswer(card.getAnswer());
		setPictureFunction();
		pnlControls.remove(btnSaveAndNext);
		super.setTitle("Karte bearbeiten..");

	}

	private void setPictureFunction() {
		if (card.hasQuestionPic()) {
			try {
				txtQuestion.addPicture(card, PicType.QUESTION);
			} catch (IOException e) {
				// TODO error handling
				System.out.println("Error while adding question pic");
			}
			btnAddPicQuestion.setText("Bild \u00e4ndern...");
			System.out.println("added delete button question");
			boxControlsQuestion.add(btnDelPicQ);
		}
		if (card.hasAnswerPic()) {
			try {
				txtAnswer.addPicture(card, PicType.ANSWER);
			} catch (IOException e) {
				// TODO error handling
				System.out.println("Error while adding answer pic");
			}
			btnAddPicAnswer.setText("Bild \u00e4ndern...");
			System.out.println("added delete button answer");
			boxControlsAnswer.add(btnDelPicA);
		}
	}

	@Override
	protected void setListeners() {
		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeFlashcardDialog.this.dispose();
			}

		});

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					card.setQuestion(txtQuestion.getText());
					card.setAnswer(txtAnswer.getText());
					try {
						project.updateCard(card, pathToQuestionPic,
								pathToAnswerPic);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (efcDialog != null) {
						efcDialog.updateCardPanels();
					}
				} catch (EntryNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				ChangeFlashcardDialog.this.dispose();
			}

		});

		btnPreview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PreviewDialog d = new PreviewDialog(projPnl.getOwner(), card,
						txtQuestion.getText(), txtAnswer.getText(),
						pathToQuestionPic, pathToAnswerPic);
				d.setVisible(true);
			}
		});

		btnAddPicQuestion
				.addActionListener(new AddFlashcardDialog.AddPictureListener(
						PicType.QUESTION));
		btnAddPicAnswer
				.addActionListener(new AddFlashcardDialog.AddPictureListener(
						PicType.ANSWER));
		btnDelPicQ
				.addActionListener(new DeletePictureListener(PicType.QUESTION));
		btnDelPicA.addActionListener(new DeletePictureListener(PicType.ANSWER));

	}

	// Listener for deleting the selected pic
	private class DeletePictureListener implements ActionListener {
		PicType type;

		DeletePictureListener(PicType type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				card.deletePicture(type);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
			ChangeFlashcardDialog.this.revalidate();
		}
	}

}
