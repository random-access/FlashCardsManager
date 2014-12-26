package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.*;

import utils.Logger;
import core.FlashCard;
import core.LearningProject;
import db.PicType;
import exc.EntryNotFoundException;
import gui.helpers.MyButton;

@SuppressWarnings("serial")
public class LearningSession extends JDialog {

   private static BufferedImage imgSwitch, imgPrev, imgNext, imgRight, imgWrong,
         imgExit, imgFlashcardInfo;

   static {
      try {
         imgFlashcardInfo = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/AddFlashcardInfo_450x338.png"));
         imgSwitch = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgSwitch_28x28.png"));
         imgPrev = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgPrev_28x28.png"));
         imgNext = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgNext_28x28.png"));
         imgRight = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgRight_28x28.png"));
         imgWrong = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgWrong_28x28.png"));
         imgExit = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgExit_28x28.png"));
      } catch (IOException e) {
         JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      }
   }

   private boolean movedFwd = true, beginning;
   private MainWindow owner;
   private LearningProject project;
   private ArrayList<FlashCard> allCards;
   private FlashCard currentCard;
   private ListIterator<FlashCard> lit;
   
   protected JPanel pnlButtons, pnlControls, centerPanel, pnlTitle,
         pnlProgress;
   protected PicAndTextPanel pnlQ, pnlA;
   protected JScrollPane scpCenter;
   protected JLabel lblTitle;
   protected JButton btnSwitch, btnBack, btnTrue, btnFalse, btnFwd, btnClose;
   protected JProgressBar progress;

   protected LearningSession(MainWindow owner) {
      super(owner, true);
   }

   public LearningSession(MainWindow owner, LearningProject project,
         ArrayList<FlashCard> allCards) {
      super(owner, true);
      this.owner = owner;
      this.project = project;
      this.allCards = allCards;
      lit = allCards.listIterator();
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setLayout(new BorderLayout());
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e) {
         JOptionPane.showMessageDialog(null,
               "Ein interner Fehler ist aufgetreten", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(e);
      }

      createWidgets();
      createProgressBar();
      addWidgets();
      setListeners();
      setSize(800, 600);
      setLocationRelativeTo(owner);
      beginning = true;
   }

   protected void createWidgets() {
      pnlTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlTitle.setBackground(Color.DARK_GRAY);
      pnlTitle.setBorder(BorderFactory.createLineBorder(getContentPane()
            .getBackground(), 8));
      lblTitle = new JLabel("Lernen...");
      lblTitle.setOpaque(true);
      lblTitle.setBackground(Color.DARK_GRAY);
      lblTitle.setForeground(Color.WHITE);
      lblTitle.setFont(getFont().deriveFont(18.0F));

      pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlControls = new JPanel(new BorderLayout());
      btnSwitch = new MyButton("umdrehen", new ImageIcon(imgSwitch) );
      btnBack = new MyButton("zur\u00fcck", new ImageIcon(imgPrev));
      btnFalse = new MyButton("falsch", new ImageIcon(imgWrong));
      btnTrue = new MyButton("richtig", new ImageIcon(imgRight));
      btnFwd = new MyButton("vor", new ImageIcon(imgNext));
      btnClose = new MyButton("schlie\u00dfen", new ImageIcon(imgExit));

      centerPanel = new JPanel();
      centerPanel.setLayout(new GridBagLayout());
      scpCenter = new JScrollPane();
      scpCenter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 30, 10, 30),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)));
      scpCenter.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10,
            10));
   }

   private void createProgressBar() {
      pnlProgress = new JPanel(new FlowLayout(FlowLayout.CENTER));
      progress = new JProgressBar(0, allCards.size());
      progress.setString(progress.getValue() + " von " + progress.getMaximum());
      progress.setStringPainted(true);
   }

   protected void addWidgets() {
      createNextFlashcardFields();
      this.add(pnlTitle, BorderLayout.NORTH);
      this.add(pnlControls, BorderLayout.SOUTH);
      this.add(scpCenter, BorderLayout.CENTER);
      scpCenter.setViewportView(centerPanel);
      centerPanel.add(pnlQ);
      pnlControls.add(pnlButtons, BorderLayout.SOUTH);
      pnlControls.add(pnlProgress, BorderLayout.NORTH);
      pnlProgress.add(progress);
      pnlButtons.add(btnSwitch);
      pnlButtons.add(btnBack);
      pnlButtons.add(btnFalse);
      pnlButtons.add(btnTrue);
      pnlButtons.add(btnFwd);
      pnlButtons.add(btnClose);
      pnlTitle.add(lblTitle);
   }

   protected void setListeners() {
      btnSwitch.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            boolean isQuestion = centerPanel.isAncestorOf(pnlQ);
            if (isQuestion) {
               centerPanel.remove(pnlQ);
               centerPanel.add(pnlA);
               btnSwitch.setToolTipText("Frage zeigen");
               System.out.println("Show answer");
            } else {
               centerPanel.remove(pnlA);
               centerPanel.add(pnlQ);
               btnSwitch.setToolTipText("Antwort zeigen");
               System.out.println("show question");
            }
            centerPanel.revalidate();
            centerPanel.repaint();
         }
      });

      btnClose.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            LearningSession.this.dispose();
         }
      });

      btnFwd.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            boolean isQuestion = centerPanel.isAncestorOf(pnlQ);
            if (isQuestion) {
               centerPanel.remove(pnlQ);
            } else {
               centerPanel.remove(pnlA);
               btnSwitch.setToolTipText("Antwort zeigen");
            }
            createNextFlashcardFields();
            centerPanel.add(pnlQ);
            centerPanel.revalidate();
            centerPanel.repaint();
         }
      });

      btnBack.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            boolean isQuestion = centerPanel.isAncestorOf(pnlQ);
            if (isQuestion) {
               centerPanel.remove(pnlQ);
            } else {
               centerPanel.remove(pnlA);
               btnSwitch.setToolTipText("Antwort zeigen");
            }
            createPreviousFlashcardFields();
            centerPanel.add(pnlQ);
            centerPanel.revalidate();
            centerPanel.repaint();
         }
      });

      btnFalse.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               currentCard.levelDown();
               LearningSession.this.owner.updateProjectStatus(project);
               boolean isQuestion = centerPanel.isAncestorOf(pnlQ);
               if (isQuestion) {
                  centerPanel.remove(pnlQ);
               } else {
                  centerPanel.remove(pnlA);
                  btnSwitch.setToolTipText("Antwort zeigen");
               }
               createNextFlashcardFields();
               centerPanel.add(pnlQ);
               centerPanel.revalidate();
               centerPanel.repaint();
            } catch (EntryNotFoundException | SQLException exc) {
               JOptionPane.showMessageDialog(LearningSession.this,
                     "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(exc);
            }
         }

      });

      btnTrue.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               currentCard.nextLevel();
               LearningSession.this.owner.updateProjectStatus(project);
               boolean isQuestion = centerPanel.isAncestorOf(pnlQ);
               if (isQuestion) {
                  centerPanel.remove(pnlQ);
               } else {
                  centerPanel.remove(pnlA);
                  btnSwitch.setToolTipText("Antwort zeigen");
               }
               createNextFlashcardFields();
               centerPanel.add(pnlQ);
               centerPanel.revalidate();
               centerPanel.repaint();
            } catch (EntryNotFoundException | SQLException exc) {
               JOptionPane.showMessageDialog(LearningSession.this,
                     "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(exc);
            }
         }

      });

   }

   private FlashCard getNextCard() {
      if (lit.hasNext()) {
         if (!movedFwd) {
            lit.next();
            movedFwd = true;
         }
         System.out.println("next index: " + lit.nextIndex());
         return lit.next();
      }
      return null;
   }

   private FlashCard getPreviousCard() {
      if (lit.hasPrevious()) {
         if (movedFwd) {
            lit.previous();
            movedFwd = false;
         }
         System.out.println("previous index: " + lit.previousIndex());
         return lit.previous();
      }
      return null;
   }

   private void createPreviousFlashcardFields() {
      currentCard = getPreviousCard();
      enableNavigationAsNeeded();
      if (currentCard.getQuestion() != null) {
         System.out.println(currentCard.getQuestion());
      }
      try {
         pnlQ = new PicAndTextPanel(currentCard.getQuestionPic(),
               currentCard.getQuestion(), PicType.QUESTION, false, currentCard.getQuestionWidth());
         pnlA = new PicAndTextPanel(currentCard.getAnswerPic(),
               currentCard.getAnswer(), PicType.ANSWER, false, currentCard.getAnswerWidth());
         lblTitle.setText(project.getTitle() + " - Karte "
               + currentCard.getId());
         progress.setValue(progress.getValue() - 1);
         progress.setString(progress.getValue() + " von "
               + progress.getMaximum());
      } catch (IOException | SQLException exc) {
         JOptionPane.showMessageDialog(LearningSession.this,
               "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(exc);
      }
   }

   private void createNextFlashcardFields() {
      currentCard = getNextCard();
      enableNavigationAsNeeded();
      try {
         if (currentCard == null) {
            if (allCards.size() == 0) {
               pnlQ = new PicAndTextPanel(imgFlashcardInfo, "", null, false, 0);
            } else {
               pnlQ = new PicAndTextPanel(null, "Super! Geschafft!",
                     null, false, 0);
            }
            btnFwd.setEnabled(false);
            btnSwitch.setEnabled(false);
            btnTrue.setEnabled(false);
            btnFalse.setEnabled(false);
            lblTitle.setText(project.getTitle());
         } else { // valid card
            if (currentCard.getQuestion() != null) {
               System.out.println(currentCard.getQuestion());
            }
            pnlQ = new PicAndTextPanel(  currentCard.getQuestionPic(),
                  currentCard.getQuestion(), PicType.QUESTION, false,currentCard.getQuestionWidth());
            pnlA = new PicAndTextPanel(currentCard.getAnswerPic(),
                  currentCard.getAnswer(), PicType.ANSWER, false, currentCard.getAnswerWidth());
            lblTitle.setText(project.getTitle() + " - Karte "
                  + currentCard.getId());
         }
         progress.setValue(progress.getValue() + 1);
         progress.setString(progress.getValue() + " von "
               + progress.getMaximum());
      } catch (IOException | SQLException exc) {
         JOptionPane.showMessageDialog(LearningSession.this,
               "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
               JOptionPane.ERROR_MESSAGE);
         Logger.log(exc);
      }
   }

   private void enableNavigationAsNeeded() {
      btnFwd.setEnabled(lit.nextIndex() != allCards.size());
      btnBack.setEnabled(lit.previousIndex() != -1);
      if (!beginning) {
         btnBack.setEnabled(false);
      }
   }

}
