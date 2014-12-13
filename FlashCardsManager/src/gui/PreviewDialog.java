package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.FlashCard;
import db.PicType;

public class PreviewDialog extends LearningSession {

   private String question, answer;
   private String pathToQuestionPic, pathToAnswerPic;
   private FlashCard card;

   public PreviewDialog(MainWindow owner, FlashCard card, String question,
         String answer, String pathToQuestionPic, String pathToAnswerPic) {
      super(owner);
      this.card = card;
      this.question = question;
      this.answer = answer;
      this.pathToQuestionPic = pathToQuestionPic;
      this.pathToAnswerPic = pathToAnswerPic;
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
      createCardPanels();
      addWidgets();
      setListeners();

      setSize(800, 600);
      setLocationRelativeTo(owner);
   }

   private void createCardPanels() {
      BufferedImage imgQ, imgA;
      if (pathToQuestionPic != null) {
         imgQ = getPicFromFile(pathToQuestionPic, PicType.QUESTION);
      } else {
         imgQ = getPicFromDatabase(card, PicType.QUESTION);
      }
      if (pathToAnswerPic != null) {
         imgA = getPicFromFile(pathToAnswerPic, PicType.ANSWER);
      } else {
         imgA = getPicFromDatabase(card, PicType.ANSWER);
      }
      pnlQ = new PicAndTextPanel(imgQ, question, PicType.QUESTION);
      pnlA = new PicAndTextPanel(imgA, answer, PicType.ANSWER);
   }

   private BufferedImage getPicFromDatabase(FlashCard card, PicType type) {
      BufferedImage pic = null;
      try {
         if (card != null) {
            switch (type) {
            case QUESTION:

               pic = card.getQuestionPic();
               break;
            case ANSWER:
               pic = card.getAnswerPic();
               break;
            }
         }        
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return pic;
     
   }

   private BufferedImage getPicFromFile(String pathToPic, PicType type) {
      BufferedImage img = null;
      try {
         img = ImageIO.read(new File(pathToPic));
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return img;
   }

   @Override
   protected void addWidgets() {
      this.add(pnlTitle, BorderLayout.NORTH);
      this.add(pnlButtons, BorderLayout.SOUTH);
      this.add(scpCenter, BorderLayout.CENTER);
      scpCenter.setViewportView(centerPanel);
      centerPanel.add(pnlQ);
      pnlButtons.add(btnSwitch);
      pnlButtons.add(btnClose);
      pnlTitle.add(lblTitle);
   }

   @Override
   protected void setListeners() {
      btnClose.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            PreviewDialog.this.dispose();
         }
      });

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
   }
}
