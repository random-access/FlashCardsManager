package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

import utils.InvalidCharsFilter;
import core.FlashCard;
import db.PicType;

public class DesignFlashcardPanel extends JPanel {
   private CountedTextArea txt;
   private JScrollPane scp;
   private JLabel lblCurrentCharCount, lblPic;
   private DefaultStyledDocument doc;
   private static final int MAX_PIC_SIZE = 150;
   private Component owner;

   public DesignFlashcardPanel(Component owner) {
	  this.owner = owner;
      this.setLayout(new BorderLayout());
      this.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      createWidgets();
      addWidgets();
      setListeners();
   }

   void removePicture() {
      this.remove(lblPic);
      lblPic = null;
   }

   void addPicture(FlashCard card, PicType type) throws IOException, SQLException {
      BufferedImage img = null;
      switch (type) {
      case QUESTION:
         img = card.getQuestionPic();
         break;
      case ANSWER:
         img = card.getAnswerPic();
         break;
      }
      double height = img.getHeight();
      System.out.println("original height: " + height);
      double width = img.getWidth();
      System.out.println("original width: " + width);
      double scaleFactor = 1.0;
      if (height > MAX_PIC_SIZE || width > MAX_PIC_SIZE) {
         scaleFactor = 1 / (Math.max(height / MAX_PIC_SIZE, width
               / MAX_PIC_SIZE));
         System.out.println("Wert 1: " + height / MAX_PIC_SIZE + ", Wert 2: " + width / MAX_PIC_SIZE + " Max: " + (Math.max(height / MAX_PIC_SIZE, width
               / MAX_PIC_SIZE)));
      }
      System.out.println("scale factor: " + scaleFactor);
      BufferedImage previewPic;
      if (scaleFactor != 1) {
          previewPic = scale(img, BufferedImage.TYPE_INT_ARGB,
               (int) (width * scaleFactor), (int) (height * scaleFactor),
               scaleFactor, scaleFactor);
      } else {
         previewPic = img;
      }
      if (lblPic != null) {
         this.remove(lblPic);
      }
      lblPic = new JLabel(new ImageIcon(previewPic));
      lblPic.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
      this.add(lblPic, BorderLayout.EAST);
   }

   void addPicture(String picUrl) throws IOException {
      BufferedImage img;
      img = ImageIO.read(new File(picUrl));
      double height = img.getHeight();
      System.out.println("original height: " + height);
      double width = img.getWidth();
      System.out.println("original width: " + width);
      double scaleFactor = 1;
      if (height > MAX_PIC_SIZE || width > MAX_PIC_SIZE) {
         scaleFactor = 1 / (Math.max(height / MAX_PIC_SIZE, width
               / MAX_PIC_SIZE));
      }
      System.out.println("scale factor: " + scaleFactor);
      BufferedImage bmg;
      if (scaleFactor != 1) {
         bmg = scale(img, BufferedImage.TYPE_INT_ARGB,
               (int) (width * scaleFactor), (int) (height * scaleFactor),
               scaleFactor, scaleFactor);
         System.out.println("new height: " + bmg.getHeight());
         System.out.println("new width: " + bmg.getWidth());
      } else {
         bmg = img;
      }
      if (lblPic != null) {
         this.remove(lblPic);
      }
      lblPic = new JLabel(new ImageIcon(bmg));
      lblPic.setMinimumSize(new Dimension(150,150));
      lblPic.setPreferredSize(new Dimension(150,150));
      lblPic.setMaximumSize(new Dimension(150,150));
      lblPic.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
      this.add(lblPic, BorderLayout.EAST);
   }

   public static BufferedImage scale(BufferedImage sbi, int imageType,
         int dWidth, int dHeight, double fWidth, double fHeight) {
      BufferedImage dbi = null;
      if (sbi != null) {
         dbi = new BufferedImage(dWidth, dHeight, imageType);
         Graphics2D g = dbi.createGraphics();
         AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
         g.drawRenderedImage(sbi, at);
      }
      return dbi;
   }

   private void createWidgets() {
      txt = new CountedTextArea();
      txt.setLineWrap(true);
      txt.setWrapStyleWord(true);
      doc = new DefaultStyledDocument();
      doc.setDocumentFilter(new InvalidCharsFilter(owner));
      lblCurrentCharCount = new JLabel(doc.getLength() + " Zeichen");
      scp = new JScrollPane();
   }

   private void addWidgets() {
      txt.setDocument(doc);
      this.add(scp, BorderLayout.CENTER);
      scp.setViewportView(txt);
      this.add(lblCurrentCharCount, BorderLayout.SOUTH);
      this.setPreferredSize(new Dimension(400, 150));
   }

   private void setListeners() {
      doc.addDocumentListener(new DocumentListener() {
         @Override
         public void changedUpdate(DocumentEvent e) {
            txt.updateCount();
         }

         @Override
         public void insertUpdate(DocumentEvent e) {
            txt.updateCount();
         }

         @Override
         public void removeUpdate(DocumentEvent e) {
            txt.updateCount();
         }

      });
   }

   private class CountedTextArea extends JTextArea {
      void updateCount() {
         lblCurrentCharCount.setText(doc.getLength() + " Zeichen");
      }
   }

   public void setText(String text) {
      txt.setText(text);
   }

   public String getText() {
      return txt.getText();
   }

}
