package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.FlashCard;
import core.LearningProject;
import db.PicType;
import exc.EntryNotFoundException;

public class LearningSession extends JDialog {

   static BufferedImage imgSwitch, imgPrev, imgNext, imgRight, imgWrong,
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
         System.out.println("Picture not found");
         // TODO: JDialog mit ErrorMsg
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
      createProgressBar();
      addWidgets();
      setListeners();
      // pack();
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
      btnSwitch = new JButton(new ImageIcon(imgSwitch));
      btnBack = new JButton(new ImageIcon(imgPrev));
      btnFalse = new JButton(new ImageIcon(imgWrong));
      btnTrue = new JButton(new ImageIcon(imgRight));
      btnFwd = new JButton(new ImageIcon(imgNext));
      btnClose = new JButton(new ImageIcon(imgExit));
      btnSwitch.setToolTipText("Antwort zeigen");

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
            } catch (EntryNotFoundException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
            } catch (SQLException e1) {
               // TODO Auto-generated catch block
               e1.printStackTrace();
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
            } catch (EntryNotFoundException e1) {
               // TODO error handling
               System.out.println("FlashCard not found - in LearningSession");
            } catch (SQLException e1) {
               // TODO error handling
               System.out.println("SQL Error - in LearningSession");
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
               currentCard.getQuestion(), PicType.QUESTION);
         pnlA = new PicAndTextPanel(currentCard.getAnswerPic(),
               currentCard.getAnswer(), PicType.ANSWER);
         lblTitle.setText(project.getTitle() + " - Karte "
               + currentCard.getId());
         progress.setValue(progress.getValue() - 1);
         progress.setString(progress.getValue() + " von "
               + progress.getMaximum());
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void createNextFlashcardFields() {
      currentCard = getNextCard();
      enableNavigationAsNeeded();
      try {
         if (currentCard == null) {
            if (allCards.size() == 0) {
               pnlQ = new PicAndTextPanel(imgFlashcardInfo, "", PicType.NO_PIC);
            } else {
               pnlQ = new PicAndTextPanel(null, "Super! Geschafft!",
                     PicType.THE_END);
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

            pnlQ = new PicAndTextPanel(currentCard.getQuestionPic(),
                  currentCard.getQuestion(), PicType.QUESTION);

            pnlA = new PicAndTextPanel(currentCard.getAnswerPic(),
                  currentCard.getAnswer(), PicType.ANSWER);
            lblTitle.setText(project.getTitle() + " - Karte "
                  + currentCard.getId());
         }
         progress.setValue(progress.getValue() + 1);
         progress.setString(progress.getValue() + " von "
               + progress.getMaximum());
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
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
