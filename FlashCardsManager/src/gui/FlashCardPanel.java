package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;

import utils.HTMLToText;
import core.*;
import exc.CustomErrorHandling;

@SuppressWarnings("serial")
public class FlashCardPanel extends JPanel {

	private static BufferedImage imgEdit, imgDelete, imgRed, imgYellow, imgGreen;

	static {
		try {
			imgEdit = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgEdit_16x16.png"));
			imgDelete = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgDelete_16x16.png"));
			imgRed = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgRed_8x8.png"));
			imgYellow = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgYellow_8x8.png"));
			imgGreen = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgGreen_8x8.png"));

		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(null, ioe);
		}
	}

	private Box b;
	private JLabel lblStatus, lblText;
	private JButton btnEdit, btnDelete;
	private JCheckBox chkSelected;
	private Status status;
	private FlashCard card;
	private LearningProject project;
	private ProjectPanel projectPnl;
	private EditFlashcardsDialog editDialog;

	public FlashCardPanel(FlashCard card, LearningProject project, Status status, ProjectPanel projectPnl,
			EditFlashcardsDialog editDialog) throws SQLException {
		this.status = status;
		this.card = card;
		this.projectPnl = projectPnl;
		this.editDialog = editDialog;
		this.project = project;
		this.setLayout(new BorderLayout());
		createWidgets();
		addWidgets();
		setListeners();
	}

	private void createWidgets() throws SQLException {
		b = Box.createHorizontalBox();
		b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setStatus(this.status);
		chkSelected = new JCheckBox();
		lblText = new JLabel("Frage " + card.getNumberInProj() + ": " + getQuestionTitle(card));
		lblText.setFont(lblText.getFont().deriveFont(Font.BOLD, 12));
		btnEdit = new JButton(new ImageIcon(imgEdit));
		btnEdit.setToolTipText("FlashCard bearbeiten");
		btnDelete = new JButton(new ImageIcon(imgDelete));
		btnDelete.setToolTipText("FlashCard l\u00f6schen");
	}

	private void addWidgets() {
		this.add(b, BorderLayout.CENTER);
		b.add(chkSelected);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(lblStatus);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(lblText);
		b.add(Box.createHorizontalGlue());

		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(btnEdit);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
		b.add(btnDelete);
		b.add(Box.createRigidArea(new Dimension(15, 0)));
	}

	private String getQuestionTitle(FlashCard f) {
		StringReader in = new StringReader(f.getQuestion());
		HTMLToText parser = new HTMLToText();
		String question = null;
		try {
			parser.parse(in);
			in.close();
			question = parser.getText();
		} catch (IOException ioe) {
			CustomErrorHandling.showParseError(editDialog, ioe);
			question = f.getQuestion();
		}

		String result;
		String[] parts = question.split(" ");
		if (parts[0].length() > 50) {
			result = parts[0].substring(0, 50) + "...";
		} else if (parts.length < 2 || parts[0].length() + parts[1].length() > 50) {
			result = parts[0] + "...";
		} else if (parts.length < 3 || parts[0].length() + parts[1].length() + parts[2].length() > 50) {
			result = parts[0] + " " + parts[1] + "...";
		} else {
			result = parts[0] + " " + parts[1] + " " + parts[2] + "...";
		}
		return result;
	}

	private void setListeners() {

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final OkOrDisposeDialog d = new OkOrDisposeDialog(editDialog.getOwner(), 300, 150);
				d.setTitle("Wirklich l\u00f6schen?");
				d.setText("<html>Wirklich die Lernkarte l\u00f6schen? <br>"
						+ "der Eintrag wird damit vollst\u00e4ndig entfernt.</html>");
				d.addOkAction(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							card.delete();
							editDialog.getOwner().updateProjectStatus(project);
							editDialog.updateCardPanels();
						} catch (SQLException sqle) {
							CustomErrorHandling.showDatabaseError(editDialog, sqle);
						} catch (IOException ioe) {
							CustomErrorHandling.showInternalError(editDialog, ioe);
						} finally {
							d.dispose();
						}
					}
				});
				d.setVisible(true);
			}
		});

		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					AddFlashcardDialog d = new AddFlashcardDialog(editDialog, project, projectPnl, card);
					d.setVisible(true);
				} catch (IOException ioe) {
					CustomErrorHandling.showInternalError(editDialog, ioe);
				} catch (SQLException sqle) {
					CustomErrorHandling.showDatabaseError(editDialog, sqle);
				}
			}
		});
	}

	public boolean isSelected() {
		return chkSelected.isSelected();
	}

	public FlashCard getCard() {
		return this.card;
	}

	void changeStatus(Status s) {
		remove(b);
		b = Box.createHorizontalBox();
		b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setStatus(s);
		addWidgets();
		revalidate();
	}

	private void setStatus(Status status) {
		switch (status) {
		case RED:
			lblStatus = new JLabel(new ImageIcon(imgRed));
			lblStatus.setToolTipText("Los geht's! Diese Kart liegt noch im ersten Stapel");
			break;
		case YELLOW:
			lblStatus = new JLabel(new ImageIcon(imgYellow));
			lblStatus.setToolTipText("Weiter so! - Diese Karte wurde mindestens einmal richtig beantwortet");
			break;
		case GREEN:
			lblStatus = new JLabel(new ImageIcon(imgGreen));
			lblStatus.setToolTipText("Bravo! Diese Karte liegt schon im letzten Stapel");
		}
	}

}
