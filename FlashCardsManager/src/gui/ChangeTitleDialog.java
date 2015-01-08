package gui;

import gui.helpers.CustomColor;
import gui.helpers.TransparencyTextField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import utils.*;
import core.LearningProject;

@SuppressWarnings("serial")
public class ChangeTitleDialog extends JDialog {

   private JPanel pnlCenter, pnlBtns;
   private JLabel lblTitle;
   private JTextField txtTitle;
   private JButton btnOk, btnDiscard;
   private MainWindow owner;
   private ProjectPanel pnl;
   private LearningProject proj;

   ChangeTitleDialog(ProjectPanel pnl, LearningProject proj) {
      super(pnl.getOwner(), true);
      this.owner = pnl.getOwner();
      this.proj = proj;
      this.pnl = pnl;
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setTitle("Titel \u00e4ndern..");

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
      addWidgets();
      setListeners();

      pack();
      setLocationRelativeTo(owner);
   }

   private void setListeners() {
      btnOk.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            // pruefe ob vollstaendige Eingabe
            try {
               if (txtTitle.getText().equals("")) {
            	   txtTitle.setBackground(CustomColor.BACKGROUND_ERROR_RED);
                   JOptionPane.showMessageDialog(ChangeTitleDialog.this,
                         "Es wurde kein Titel eingegeben.",
                         "Fehler", JOptionPane.ERROR_MESSAGE);
               } else {
               // verarbeite Eingabe in DB
               proj.setTitle(txtTitle.getText());
               proj.update();
               owner.updateProjectList();
               ChangeTitleDialog.this.dispose();
               }
            } catch (SQLException exc) {
               JOptionPane.showMessageDialog(ChangeTitleDialog.this,
                     "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(exc);
            }
         }
      });

      btnDiscard.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            ChangeTitleDialog.this.dispose();
         }
      });

   }

   private void createWidgets() {
      pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
      pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
      lblTitle = new JLabel("Titel:  ");
      if (setWidthInCols()) {
         txtTitle = new TransparencyTextField(pnl.lblText.getText(), 10);
      } else {
         txtTitle = new TransparencyTextField(pnl.lblText.getText());
      }
      txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
      ((AbstractDocument) txtTitle.getDocument())
            .setDocumentFilter(new SizeFilterExtended(new InvalidCharsFilter(
                  this), 50));
      btnOk = new JButton("  OK  ");
      btnDiscard = new JButton(" Abbrechen ");
   }

   private boolean setWidthInCols() {
      JTextField testField = new JTextField(pnl.lblText.getText());
      double prefWidth = testField.getPreferredSize().getWidth();
      double defWidth = 122;
      return (prefWidth < defWidth);
   }

   private void addWidgets() {
      getContentPane().add(pnlCenter, BorderLayout.NORTH);
      getContentPane().add(pnlBtns, BorderLayout.SOUTH);
      pnlCenter.add(lblTitle);
      pnlCenter.add(txtTitle);
      pnlBtns.add(btnDiscard);
      pnlBtns.add(Box.createHorizontalStrut(10));
      pnlBtns.add(btnOk);
      txtTitle.setMinimumSize(new Dimension(
            (int) lblTitle.getSize().getWidth(), (int) txtTitle
                  .getPreferredSize().getHeight()));
   }

}
