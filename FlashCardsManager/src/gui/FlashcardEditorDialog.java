package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import storage.PicType;
import utils.IndividualAction;
import core.*;
import core.Label;
import exc.CustomErrorHandling;
import gui.helpers.MyButton;

@SuppressWarnings("serial")
public class FlashcardEditorDialog extends JDialog {

	private static BufferedImage imgBold, imgItalic, imgUnderlined, imgLeftAlign, imgCenterAlign, imgRightAlign, imgList, imgNum,
			imgSwitch, imgDiscard, imgSave, imgSaveAndNext, imgAddPic, imgEditPic, imgRemovePic, imgLargerCard, imgSmallerCard;

	static {
		try {
			imgBold = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgBold_28x28.png"));
			imgItalic = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgItalic_28x28.png"));
			imgUnderlined = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgUnderlined_28x28.png"));
			imgLeftAlign = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgLeftAlign_28x28.png"));
			imgCenterAlign = ImageIO
					.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgCenterAlign_28x28.png"));
			imgRightAlign = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgRightAlign_28x28.png"));
			imgList = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgList_28x28.png"));
			imgNum = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgNum_28x28.png"));
			imgSwitch = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgSwitch_28x28.png"));
			imgDiscard = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgDiscard_28x28.png"));
			imgSave = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgSave_28x28.png"));
			imgSaveAndNext = ImageIO
					.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgSaveAndNext_28x28.png"));
			imgAddPic = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgAddPic_28x28.png"));
			imgEditPic = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgEditPic_28x28.png"));
			imgRemovePic = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgRemovePic_28x28.png"));
			imgLargerCard = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgLargerCard_28x28.png"));
			imgSmallerCard = ImageIO
					.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgSmallerCard_28x28.png"));
		} catch (IOException e) {
			CustomErrorHandling.showInternalError(null, e);
		}
	}

	// private MainWindow owner;
	private LearningProject project;
	private ProjectPanel projPnl;
	private FlashcardOverviewFrame editFrame;

	// only for existing flashcards
	private FlashCard existingCard;
	private ArrayList<Label> labels;

	private PicAndTextPanel pnlQ, pnlA;
	private JPanel pnlTitle, pnlTop, pnlEditor, pnlEdit, pnlBottom, centerPanel;
	private JScrollPane scpEditor, scpCenter;
	private JLabel lblTitle;
	private MyButton btnLargerCard, btnSmallerCard, btnFlip, btnDiscard, btnSave, btnSaveAndNext, btnAddLabel;
	private MyButton btnBold, btnItalic, btnUnderlined, btnLeftAlign, btnRightAlign, btnCenterAlign, btnList, btnNum, btnAddPic,
			btnEditPic, btnRemovePic;
	private JComboBox<String> cmbFontFamilies;
	private JSpinner spFontSizes;
	private BasicArrowButton west, east;

	private Action boldAction = new StyledEditorKit.BoldAction();
	private Action italicAction = new StyledEditorKit.ItalicAction();
	private Action underlinedAction = new StyledEditorKit.UnderlineAction();
	private Action leftAction = new StyledEditorKit.AlignmentAction("Left Align", StyleConstants.ALIGN_LEFT);
	private Action rightAction = new StyledEditorKit.AlignmentAction("Right Align", StyleConstants.ALIGN_RIGHT);
	private Action centerAction = new StyledEditorKit.AlignmentAction("Center Align", StyleConstants.ALIGN_CENTER);

	// constructor for editing a flashcard
	public FlashcardEditorDialog(JFrame owner, LearningProject project, ProjectPanel projPnl, FlashCard card) throws IOException,
			SQLException {
		this(owner, project, projPnl);
		this.existingCard = card;
		labels = card.getLabels();
		centerPanel.remove(pnlQ);
		pnlQ = new PicAndTextPanel(card.getPathToQuestionPic(), card.getQuestion(), PicType.QUESTION, true,
				card.getQuestionWidth());
		pnlA = new PicAndTextPanel(card.getPathToAnswerPic(), card.getAnswer(), PicType.ANSWER, true, card.getAnswerWidth());
		centerPanel.add(pnlQ);
		lblTitle.setText("Karte " + card.getNumberInProj() + " bearbeiten...");
		setPicButtons();
		pnlBottom.remove(btnSaveAndNext);
		btnDiscard.setText("abbrechen");
	}

	// constructor for adding a new flashcard
	public FlashcardEditorDialog(JFrame owner, LearningProject project, ProjectPanel projPnl) throws IOException {
		super(owner, true);
		if (owner instanceof FlashcardOverviewFrame) {
			editFrame = (FlashcardOverviewFrame) owner;
		}
		this.project = project;
		this.projPnl = projPnl;

		labels = new ArrayList<Label>();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
			CustomErrorHandling.showInternalError(null, exc);
		}

		createWidgets();
		addWidgets();
		setListeners();
		pack();
		setLocationRelativeTo(owner);
	}

	private void createWidgets() throws IOException {
		// top area: title frame & editor
		pnlTop = new JPanel(new BorderLayout());

		pnlTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlTitle.setBackground(Color.DARK_GRAY);
		pnlTitle.setBorder(BorderFactory.createLineBorder(getContentPane().getBackground(), 8));
		lblTitle = new JLabel("Neue Lernkarte...");
		lblTitle.setOpaque(true);
		lblTitle.setBackground(Color.DARK_GRAY);
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(getFont().deriveFont(18.0F));
		pnlEdit = new JPanel(new BorderLayout());
		pnlEditor = new JPanel(new FlowLayout(FlowLayout.CENTER));
		scpEditor = new JScrollPane(pnlEditor);
		scpEditor.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scpEditor.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		final JScrollBar horizontal = scpEditor.getHorizontalScrollBar();
		west = new BasicArrowButton(BasicArrowButton.WEST);
		west.setAction(new IndividualAction("", horizontal, "negativeBlockIncrement"));
		east = new BasicArrowButton(BasicArrowButton.EAST);
		east.setAction(new IndividualAction("", horizontal, "positiveBlockIncrement"));
		// Choose the font family; TODO replace sample values
		String cmbFontSamples[] = { "SampleFont1", "SampleFont2", "SampleFont3", "SampleFont4", "SampleFont5" };
		cmbFontFamilies = new JComboBox<String>(cmbFontSamples);
		cmbFontFamilies.setEnabled(false);
		cmbFontFamilies.setToolTipText("Schriftart (noch nicht implementiert)");

		// Choose font size
		SpinnerModel sizeModel = new SpinnerNumberModel(12, 6, 48, 2);
		spFontSizes = new JSpinner(sizeModel);
		spFontSizes.setEnabled(false);
		spFontSizes.setToolTipText("Schriftgr\u00f6\u00dfe (noch nicht implementiert)");
		// for (int i = 6; i <= 48; i=i+2){
		// new StyledEditorKit.FontSizeAction(String.valueOf(fontSizes[i]),
		// fontSizes[i]);
		// }

		btnBold = new MyButton(boldAction, new ImageIcon(imgBold));
		btnBold.setToolTipText("Fett");
		btnItalic = new MyButton(italicAction, new ImageIcon(imgItalic));
		btnItalic.setToolTipText("kursiv");
		btnUnderlined = new MyButton(underlinedAction, new ImageIcon(imgUnderlined));
		btnUnderlined.setToolTipText("unterstrichen");
		btnLeftAlign = new MyButton(leftAction, new ImageIcon(imgLeftAlign));
		btnLeftAlign.setToolTipText("linksb\u00fcndig");
		btnCenterAlign = new MyButton(centerAction, new ImageIcon(imgCenterAlign));
		btnCenterAlign.setToolTipText("zentriert");
		btnRightAlign = new MyButton(rightAction, new ImageIcon(imgRightAlign));
		btnRightAlign.setToolTipText("rechtsb\u00fcndig");

		btnList = new MyButton(new ImageIcon(imgList)); // TODO List action
		btnList.setEnabled(false);
		btnList.setToolTipText("Liste (noch nicht implementiert)");
		btnNum = new MyButton(new ImageIcon(imgNum)); // TODO Num action
		btnNum.setEnabled(false);
		btnNum.setToolTipText("Aufz\u00e4hlung (noch nicht implementiert");
		btnAddPic = new MyButton(new ImageIcon(imgAddPic));
		btnAddPic.setToolTipText("Bild hinzuf\u00fcgen");
		btnEditPic = new MyButton(new ImageIcon(imgEditPic));
		btnEditPic.setToolTipText("Bild \u00e4ndern");
		btnRemovePic = new MyButton(new ImageIcon(imgRemovePic));
		btnRemovePic.setToolTipText("Bild l\u00f6schen");
		btnAddLabel = new MyButton("Labels...");

		// bottom area: control buttons
		pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnLargerCard = new MyButton("Karte vergr\u00f6\u00dfern", new ImageIcon(imgLargerCard));
		btnSmallerCard = new MyButton("Karte verkleinern", new ImageIcon(imgSmallerCard));
		btnFlip = new MyButton("umdrehen", new ImageIcon(imgSwitch));
		btnDiscard = new MyButton("verwerfen", new ImageIcon(imgDiscard));
		btnSave = new MyButton("speichern", new ImageIcon(imgSave));
		btnSaveAndNext = new MyButton("speichern & neu", new ImageIcon(imgSaveAndNext));
		pnlQ = new PicAndTextPanel(null, null, PicType.QUESTION, true, 0);
		pnlA = new PicAndTextPanel(null, null, PicType.ANSWER, true, 0);

		// center area: learning card
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		scpCenter = new JScrollPane();
		scpCenter.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30),
				BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)));
		scpCenter.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	}

	private void addWidgets() {
		this.add(pnlTop, BorderLayout.NORTH);
		this.add(pnlBottom, BorderLayout.SOUTH);

		pnlTop.add(pnlTitle, BorderLayout.NORTH);
		pnlTitle.add(lblTitle);

		pnlTop.add(pnlEdit, BorderLayout.CENTER);
		pnlEdit.add(scpEditor, BorderLayout.CENTER);
		pnlEdit.add(east, BorderLayout.EAST);
		pnlEdit.add(west, BorderLayout.WEST);

		pnlEditor.add(cmbFontFamilies);
		pnlEditor.add(spFontSizes);
		pnlEditor.add(btnBold);
		pnlEditor.add(btnItalic);
		pnlEditor.add(btnUnderlined);
		pnlEditor.add(btnLeftAlign);
		pnlEditor.add(btnCenterAlign);
		pnlEditor.add(btnRightAlign);
		pnlEditor.add(btnList);
		pnlEditor.add(btnNum);
		pnlEditor.add(btnAddPic);
		pnlEditor.add(btnAddLabel);

		pnlBottom.add(btnLargerCard);
		pnlBottom.add(btnSmallerCard);
		pnlBottom.add(btnFlip);
		pnlBottom.add(btnDiscard);
		pnlBottom.add(btnSave);
		pnlBottom.add(btnSaveAndNext);

		this.add(scpCenter, BorderLayout.CENTER);
		scpCenter.setViewportView(centerPanel);
		centerPanel.add(pnlQ);

	}

	private void setListeners() {

		// add scroll arrows to editor line only when necessary
		scpEditor.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				int scpWidth = scpEditor.getWidth();
				int prefEditorWidth = pnlEditor.getPreferredSize().width;
				west.setVisible(scpWidth < prefEditorWidth);
				east.setVisible(scpWidth < prefEditorWidth);
			}
		});

		btnLargerCard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point lastLocation = storeComponentCenter();
				if (centerPanel.isAncestorOf(pnlQ)) {
					pnlQ.makeLarger();
				} else {
					pnlA.makeLarger();
				}
				pack();
				loadComponentCenter(lastLocation);
			}
		});

		btnSmallerCard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point lastLocation = storeComponentCenter();
				if (centerPanel.isAncestorOf(pnlQ)) {
					pnlQ.makeSmaller();
				} else {
					pnlA.makeSmaller();
				}
				pack();
				loadComponentCenter(lastLocation);
			}
		});

		btnFlip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (centerPanel.isAncestorOf(pnlQ)) {
					centerPanel.remove(pnlQ);
					centerPanel.add(pnlA);
					btnFlip.setToolTipText("Frage zeigen");
				} else {
					centerPanel.remove(pnlA);
					centerPanel.add(pnlQ);
					btnFlip.setToolTipText("Antwort zeigen");
				}

				setPicButtons();
				revalidate();
				repaint();
			}
		});

		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FlashcardEditorDialog.this.dispose();
			}
		});

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (existingCard != null) {
					saveExistingCardToDatabase();
				} else {
					saveNewCardToDatabase();
				}
				FlashcardEditorDialog.this.dispose();
			}

		});

		btnSaveAndNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveNewCardToDatabase();
				try {
					resetEditor();
				} catch (IOException ioe) {
					CustomErrorHandling.showInternalError(FlashcardEditorDialog.this, ioe);
				}

			}
		});

		// Listener for adding a question pic / answer pic
		btnAddPic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addPicFromFile();
			}
		});

		btnEditPic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addPicFromFile();
			}
		});

		btnRemovePic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (centerPanel.isAncestorOf(pnlQ)) {
					pnlQ.removePicture();
				} else {
					pnlA.removePicture();
				}
				setPicButtons();
				FlashcardEditorDialog.this.revalidate();
			}
		});

		btnAddLabel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddLabelToCardDialog d;
				try {
					d = new AddLabelToCardDialog(FlashcardEditorDialog.this, project, existingCard);
					d.setVisible(true);
				} catch (SQLException sqle) {
					CustomErrorHandling.showDatabaseError(FlashcardEditorDialog.this, sqle);
				}

			}
		});
	}

	private void resetEditor() throws IOException {
		labels = new ArrayList<Label>();
		centerPanel.removeAll();
		pnlQ = new PicAndTextPanel(null, null, PicType.QUESTION, true, 0);
		pnlA = new PicAndTextPanel(null, null, PicType.ANSWER, true, 0);
		centerPanel.add(pnlQ);
		btnFlip.setToolTipText("Antwort zeigen");
		setPicButtons();
		this.revalidate();
	}

	private void loadComponentCenter(Point lastLocation) {
		Point currentLocation = lastLocation;
		currentLocation.x = currentLocation.x - FlashcardEditorDialog.this.getWidth() / 2;
		currentLocation.y = currentLocation.y - FlashcardEditorDialog.this.getHeight() / 2;
		FlashcardEditorDialog.this.setLocation(currentLocation);
	}

	private Point storeComponentCenter() {
		Point lastLocation = FlashcardEditorDialog.this.getLocationOnScreen();
		lastLocation.x = lastLocation.x + FlashcardEditorDialog.this.getWidth() / 2;
		lastLocation.y = lastLocation.y + FlashcardEditorDialog.this.getHeight() / 2;
		return lastLocation;
	}

	private void saveExistingCardToDatabase() {
		try {
			existingCard.setQuestion(pnlQ.getText());
			existingCard.setAnswer(pnlA.getText());
			existingCard.setPathToQuestionPic(pnlQ.getPathToPic());
			existingCard.setPathToAnswerPic(pnlA.getPathToPic());
			existingCard.setQuestionWidth(pnlQ.getCustomWidth());
			existingCard.setAnswerWidth(pnlA.getCustomWidth());
			existingCard.update();
			existingCard.synchronizeLabels(labels);
			if (editFrame != null) {
				editFrame.updateTableView();
			}
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(FlashcardEditorDialog.this, sqle);
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(FlashcardEditorDialog.this, ioe);
		}
	}

	private void saveNewCardToDatabase() {
		try {
			FlashCard newCard = new FlashCard(project, pnlQ.getText(), pnlA.getText(), pnlQ.getPathToPic(), pnlA.getPathToPic(),
					pnlQ.getCustomWidth(), pnlA.getCustomWidth());
			newCard.store();
			newCard.synchronizeLabels(labels);
			projPnl.getMainWindow().updateProjectStatus(project);
			if (editFrame != null) {
				editFrame.updateTableView();
			}
		} catch (SQLException sqle) {
			CustomErrorHandling.showDatabaseError(FlashcardEditorDialog.this, sqle);
		} catch (IOException ioe) {
			CustomErrorHandling.showInternalError(FlashcardEditorDialog.this, ioe);
		}
	}

	private void addPicFromFile() {
		int returnVal = projPnl.getFileChooser().showOpenDialog(FlashcardEditorDialog.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				if (centerPanel.isAncestorOf(pnlQ)) {
					pnlQ.addPicture(projPnl.getFileChooser().getSelectedFile().getAbsolutePath());
				} else {
					pnlA.addPicture(projPnl.getFileChooser().getSelectedFile().getAbsolutePath());
				}
				setPicButtons();
				FlashcardEditorDialog.this.revalidate();
			} catch (IOException ioe) {
				CustomErrorHandling.showDatabaseError(FlashcardEditorDialog.this, ioe);
			}
		}
	}

	private void setPicButtons() {
		if (centerPanel.isAncestorOf(pnlQ)) {
			if (pnlQ.getPathToPic() != null || (existingCard != null && existingCard.getPathToQuestionPic() != null)) {
				if (pnlEditor.isAncestorOf(btnAddPic)) {
					setRemovePicMode();
				}
			} else { // has question pic
				if (!pnlEditor.isAncestorOf(btnAddPic)) {
					setAddPicMode();
				}
			}
		} else { // answer pic displayed
			if (pnlA.getPathToPic() != null || (existingCard != null && existingCard.getPathToAnswerPic() != null)) {
				if (pnlEditor.isAncestorOf(btnAddPic)) {
					setRemovePicMode();
				}
			} else { // has answer pic
				if (!pnlEditor.isAncestorOf(btnAddPic)) {
					setAddPicMode();
				}
			}
		}
	}

	private void setAddPicMode() {
		pnlEditor.add(btnAddPic);
		pnlEditor.remove(btnEditPic);
		pnlEditor.remove(btnRemovePic);
	}

	private void setRemovePicMode() {
		pnlEditor.remove(btnAddPic);
		pnlEditor.add(btnEditPic);
		pnlEditor.add(btnRemovePic);
	}

	public ArrayList<Label> getLabels() {
		return this.labels;
	}

	public void setLabels(ArrayList<Label> labels) {
		this.labels = labels;
	}

}
