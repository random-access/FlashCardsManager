package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

import utils.Logger;
import core.FlashCard;
import core.LearningProject;
import db.PicType;
import exc.EntryAlreadyThereException;
import exc.EntryNotFoundException;

@SuppressWarnings("serial")
public class AddFlashcardDialog2 extends JDialog {

	private static BufferedImage imgBold, imgItalic, imgUnderlined, imgLeftAlign, imgCenterAlign, imgRightAlign, imgList, imgNum,
			imgSwitch, imgDiscard, imgSave, imgSaveAndNext, imgAddPic, imgEditPic, imgRemovePic;

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
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
			Logger.log(e);
		}
	}

	private MainWindow owner;
	private LearningProject project;
	private ProjectPanel projPnl;
	private String pathToQuestionPic, pathToAnswerPic;
	private EditFlashcardsDialog efcDialog;
	
	// only for existing flashcards
	private FlashCard existingCard;
	private boolean editExistingCard;

	private PicAndTextPanel2 pnlQ, pnlA;
	private JPanel pnlTitle, pnlTop, pnlEditor, pnlEdit, pnlBottom, centerPanel;
	private JScrollPane scpEditor, scpCenter;
	private JLabel lblTitle;
	private MyButton btnFlip, btnDiscard, btnSave, btnSaveAndNext;
	private MyButton btnBold, btnItalic, btnUnderlined, btnLeftAlign, btnRightAlign, btnCenterAlign, btnList, btnNum, btnAddPic,
			btnEditPic, btnRemovePic;
	private JComboBox<String> cmbFontFamily;
	private JSpinner spFontSize;
	private BasicArrowButton west, east;

	private Action boldAction = new StyledEditorKit.BoldAction();
	private Action italicAction = new StyledEditorKit.ItalicAction();
	private Action underlinedAction = new StyledEditorKit.UnderlineAction();
	private Action leftAction = new StyledEditorKit.AlignmentAction("Left Align", StyleConstants.ALIGN_LEFT);
	private Action rightAction = new StyledEditorKit.AlignmentAction("Right Align", StyleConstants.ALIGN_RIGHT);
	private Action centerAction = new StyledEditorKit.AlignmentAction("Center Align", StyleConstants.ALIGN_CENTER);
	
	public AddFlashcardDialog2(EditFlashcardsDialog efcDialog, LearningProject project, ProjectPanel projPnl, FlashCard card) {
		this(project, projPnl);
		editExistingCard = true;
		this.efcDialog = efcDialog;
		this.existingCard = card;
		centerPanel.remove(pnlQ);
		try {
			pnlQ = new PicAndTextPanel2(card.getQuestionPic(), card.getQuestion(), PicType.QUESTION, true);
			pnlA = new PicAndTextPanel2(card.getAnswerPic(), card.getAnswer(), PicType.ANSWER, true);
			centerPanel.add(pnlQ);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pnlBottom.remove(btnSaveAndNext);
	}
	
	// constructor for adding a new flashcard
	public AddFlashcardDialog2(LearningProject project, ProjectPanel projPnl) {
		super(projPnl.getOwner(), true);
		this.owner = projPnl.getOwner();
		this.project = project;
		this.projPnl = projPnl;
		editExistingCard = false;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
			Logger.log(e);
		}

		createWidgets();
		addWidgets();
		setListeners();

		pack();
		setLocationRelativeTo(owner);
	}
	
	
	public AddFlashcardDialog2(EditFlashcardsDialog efcDialog,
			LearningProject project, ProjectPanel projPnl) {
		this(project, projPnl);
		this.efcDialog = efcDialog;
	}

	private void createWidgets() {
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
		west.setAction(new ActionMapAction("", horizontal, "negativeBlockIncrement"));
		east = new BasicArrowButton(BasicArrowButton.EAST);
		east.setAction(new ActionMapAction("", horizontal, "positiveBlockIncrement"));
		// Choose the font family; TODO replace sample values
		String cmbFontSamples[] = { "SampleFont1", "SampleFont2", "SampleFont3", "SampleFont4", "SampleFont5" };
		cmbFontFamily = new JComboBox<String>(cmbFontSamples);
		cmbFontFamily.setEnabled(false);
		cmbFontFamily.setToolTipText("Schriftart (noch nicht implementiert)");
		
		// Choose font size
		SpinnerModel sizeModel = new SpinnerNumberModel(12, 6, 48, 2);
		spFontSize = new JSpinner(sizeModel);
		spFontSize.setEnabled(false);
		spFontSize.setToolTipText("Schriftgr\u00f6\u00dfe (noch nicht implementiert)");

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

		// bottom area: control buttons
		pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnFlip = new MyButton("umdrehen", new ImageIcon(imgSwitch));
		btnDiscard = new MyButton("verwerfen", new ImageIcon(imgDiscard));
		btnSave = new MyButton("speichern", new ImageIcon(imgSave));
		btnSaveAndNext = new MyButton("speichern & neu", new ImageIcon(imgSaveAndNext));
		pnlQ = new PicAndTextPanel2(null, null, PicType.QUESTION, true);
		pnlA = new PicAndTextPanel2(null, null, PicType.ANSWER, true);
		
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

		pnlEditor.add(cmbFontFamily);
		pnlEditor.add(spFontSize);
		pnlEditor.add(btnBold);
		pnlEditor.add(btnItalic);
		pnlEditor.add(btnUnderlined);
		pnlEditor.add(btnLeftAlign);
		pnlEditor.add(btnCenterAlign);
		pnlEditor.add(btnRightAlign);
		pnlEditor.add(btnList);
		pnlEditor.add(btnNum);
		pnlEditor.add(btnAddPic);

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
				if (scpWidth < prefEditorWidth) {
					west.setVisible(true);
					east.setVisible(true);
				} else {
					west.setVisible(false);
					east.setVisible(false);
				}
			}
		});

		btnFlip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (centerPanel.isAncestorOf(pnlQ)) {
					centerPanel.remove(pnlQ);
					centerPanel.add(pnlA);	
					btnFlip.setToolTipText("Frage zeigen");
					System.out.println("Show answer");
				} else {
					centerPanel.remove(pnlA);
					centerPanel.add(pnlQ);
					btnFlip.setToolTipText("Antwort zeigen");
					System.out.println("show question");
				}
				
				setPicButtons();
				revalidate();
				repaint();
			}
		});
		
		btnDiscard.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AddFlashcardDialog2.this.dispose();
			}
		});
		
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (editExistingCard) {
					saveExistingCardToDatabase();
				} else {
					saveNewCardToDatabase();
				}
			}

		});
		
		btnSaveAndNext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveNewCardToDatabase();
				AddFlashcardDialog2 d = new AddFlashcardDialog2( project, projPnl);
				d.setVisible(true);
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
					pathToQuestionPic = null;
					pnlQ.removePicture();;
				} else {
					pathToAnswerPic = null;
					pnlA.removePicture();
				}
				setPicButtons();
				AddFlashcardDialog2.this.revalidate();
			}
		});
	}
	
	private void saveExistingCardToDatabase() {
		try {
			existingCard.setQuestion(pnlQ.getText());
			existingCard.setAnswer(pnlA.getText());
			try {
				project.updateCard(existingCard, pathToQuestionPic,
						pathToAnswerPic);
			} catch (IOException exc) {
			   JOptionPane.showMessageDialog(AddFlashcardDialog2.this,
                  "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
                  JOptionPane.ERROR_MESSAGE);
            Logger.log(exc);
			}
			if (efcDialog != null) {
				efcDialog.updateCardPanels();
			}
		} catch (EntryNotFoundException | SQLException exc) {
		   JOptionPane.showMessageDialog(AddFlashcardDialog2.this,
             "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
             JOptionPane.ERROR_MESSAGE);
       Logger.log(exc);
		}
		AddFlashcardDialog2.this.dispose();
	}
	
	private void saveNewCardToDatabase() {
		try {
			FlashCard newCard = new FlashCard(project, pnlQ
					.getText(), pnlA.getText(), pathToQuestionPic,
					pathToAnswerPic);
			projPnl.addCard(newCard);
			owner.updateProjectStatus(project);
			if (efcDialog != null) {
				efcDialog.updateCardPanels();
			}
			System.out.println("Successfully added card!");
			AddFlashcardDialog2.this.dispose();
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
	
	private void addPicFromFile() {
		int returnVal = projPnl.getFileChooser().showOpenDialog(AddFlashcardDialog2.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				if (centerPanel.isAncestorOf(pnlQ)) {
					pathToQuestionPic = projPnl.getFileChooser().getSelectedFile().getAbsolutePath();
					System.out.println("Pfad zum Frage-Pic:" + pathToQuestionPic);
					pnlQ.addPicture(pathToQuestionPic);					
				} else {
					pathToAnswerPic = projPnl.getFileChooser().getSelectedFile().getAbsolutePath();
					System.out.println("Pfad zum Antwort-Pic:" + pathToAnswerPic);
					pnlA.addPicture(pathToAnswerPic);
				}
				setPicButtons();
				AddFlashcardDialog2.this.revalidate();
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler",
						JOptionPane.ERROR_MESSAGE);
				Logger.log(exc);
			}
		} else {
			System.out.println("just close");
		}
	}

	private void setPicButtons() {
		if (centerPanel.isAncestorOf(pnlQ)) {
			if (pathToQuestionPic == null) {
				if (!pnlEditor.isAncestorOf(btnAddPic)) {
					setAddPicMode();
				}
			} else { // has question pic
				if (pnlEditor.isAncestorOf(btnAddPic)) {
					setRemovePicMode();
				}
			}
		} else { // answer pic displayed
			if (pathToAnswerPic == null) {
				if (!pnlEditor.isAncestorOf(btnAddPic)) {
					setAddPicMode();
				}
			} else { // has answer pic
				if (pnlEditor.isAncestorOf(btnAddPic)) {
					setRemovePicMode();
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

}
