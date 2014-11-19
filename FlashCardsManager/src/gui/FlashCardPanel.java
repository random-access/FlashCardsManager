package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.FlashCard;
import core.LearningProject;
import exc.EntryNotFoundException;

public class FlashCardPanel extends JPanel {

   static BufferedImage imgShowMore, imgEdit, imgDelete, imgRed, imgYellow,
         imgGreen;

   static {
      try {
         // TODO: design imgShowMore
         imgEdit = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgEdit_16x16.png"));
         imgDelete = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgDelete_16x16.png"));
         imgRed = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgRed_8x8.png"));
         imgYellow = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgYellow_8x8.png"));
         imgGreen = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgGreen_8x8.png"));

      } catch (IOException e) {
         System.out.println("Picture not found");
         // TODO: JDialog mit ErrorMsg
      }
   }

   private Box b;
   JLabel lblStatus, lblText;
   private JButton btnShowMore, btnEdit, btnDelete;
   private Status status;
   // private JCheckBox chk;
   private FlashCard card;
   private LearningProject project;
   private ProjectPanel projectPnl;
   EditFlashcardsDialog editDialog;

   public FlashCardPanel(FlashCard card, LearningProject project,
         Status status, ProjectPanel projectPnl,
         EditFlashcardsDialog editDialog) {
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

   private void createWidgets() {
      b = Box.createHorizontalBox();
      b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      setStatus(this.status);
      // chk = new JCheckBox();
       lblText = new JLabel("Frage " + card.getId() + ": " + getQuestionTitle(card));
      lblText.setFont(lblText.getFont().deriveFont(Font.BOLD, 12));
      btnEdit = new JButton(new ImageIcon(imgEdit));
      btnEdit.setToolTipText("FlashCard bearbeiten");
      btnDelete = new JButton(new ImageIcon(imgDelete));
      btnDelete.setToolTipText("FlashCard l\u00f6schen");
   }

   private void addWidgets() {
      this.add(b, BorderLayout.CENTER);
      b.add(lblStatus);
      b.add(Box.createRigidArea(new Dimension(15, 0)));
      b.add(lblText);
      b.add(Box.createHorizontalGlue());


      
      b.add(Box.createRigidArea(new Dimension(15, 0)));
      b.add(btnEdit);
      b.add(Box.createRigidArea(new Dimension(15, 0)));
      b.add(btnDelete);
      b.add(Box.createRigidArea(new Dimension(15, 0)));
      // b.add(chk);
   }
   
   private String getQuestionTitle(FlashCard f) {
      String question = f.getQuestion();
      String result;
      String[] parts = question.split(" ");
      if (parts[0].length() > 50) {
         result = parts[0].substring(0, 40) + "...";
      } else if (parts.length < 2 || parts[0].length() + parts[1].length() > 50) {
         result = parts[0] + "...";
      } else if (parts.length < 3
            || parts[0].length() + parts[1].length() + parts[2].length() > 50) {
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
            // TODO: remove item in database
            final OkOrDisposeDialog d = new OkOrDisposeDialog(editDialog.getOwner(), 300, 150);
            d.setTitle("Wirklich l\u00f6schen?");
            d.setText("<html>Wirklich die Lernkarte l\u00f6schen? <br>"
                  + "der Eintrag wird damit vollst\u00e4ndig entfernt.</html>");
            d.addOkAction(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  try {
                     project.removeCard(FlashCardPanel.this.card);
                     projectPnl.removeCard(card);
                     editDialog.cardPnls.remove(FlashCardPanel.this);
                     editDialog.pnlCenter.remove(editDialog.centerBox);
                     editDialog.centerBox = Box.createVerticalBox();
                     editDialog.addCardsToEditPanel();
                     editDialog.pnlCenter.add(editDialog.centerBox, BorderLayout.NORTH);
                     projectPnl.getOwner().updateProjectStatus(project);
                     editDialog.repaint();
                     editDialog.revalidate();
                  } catch (EntryNotFoundException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                  } catch (SQLException e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                  }
                  d.dispose();
               }
            });
            d.setVisible(true);

         }
      });

      btnEdit.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            ChangeFlashcardDialog cfd = new ChangeFlashcardDialog(editDialog, project, projectPnl, card);
            cfd.setVisible(true);
         }

      });

   }

   public void changeStatus(Status s) {
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
         lblStatus
               .setToolTipText("Los geht's! Diese Kart liegt noch im ersten Stapel");
         break;
      case YELLOW:
         lblStatus = new JLabel(new ImageIcon(imgYellow));
         lblStatus
               .setToolTipText("Weiter so! - Diese Karte wurde mindestens 1mal richtig beantwortet");
         break;
      case GREEN:
         lblStatus = new JLabel(new ImageIcon(imgGreen));
         lblStatus
               .setToolTipText("Bravo! Diese Karte liegt schon im letzten Stapel");
      }
   }

}
