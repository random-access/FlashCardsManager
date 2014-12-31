package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.*;

import utils.Logger;
import core.LearningProject;
import core.ProjectsController;
import exc.*;

@SuppressWarnings("serial")
public class ChangeStacksDialog extends JDialog {

   private JPanel pnlCenter, pnlBtns;
   private JLabel lblNoOfStacks;
   private JTextField txtNoOfStacks;
   private JButton btnOk, btnDiscard;
   private MainWindow owner;
   private ProjectPanel pnl;
   private LearningProject project;
   private ProjectsController ctl;

   ChangeStacksDialog(ProjectPanel pnl, LearningProject project,
         ProjectsController ctl) {
      super(pnl.getOwner(), true);
      this.owner = pnl.getOwner();
      this.ctl = ctl;
      this.project = project;
      this.pnl = pnl;
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setTitle("Anzahl Durchl\u00e4ufe \u00e4ndern..");

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
               if (txtNoOfStacks.getText().equals("")) {
                  throw new NoValueException();
               }
               // verarbeite Eingabe in DB
               int nr = Integer.parseInt(txtNoOfStacks.getText());
               pnl.noOfStacks = nr;
               project.loadFlashcards();
               project.setNumberOfStacks(nr);
               project.update();
               owner.updateProjectStatus(project);
               ChangeStacksDialog.this.dispose();
            } catch (NoValueException exc) {
               JOptionPane.showMessageDialog(ChangeStacksDialog.this,
                     "Es wurde kein Wert f�r die Stapelanzahl eingegeben.",
                     "Fehler", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException | InvalidValueException exc) {
               JOptionPane.showMessageDialog(ChangeStacksDialog.this,
                     "Ung\u00fcltige Zeichenfolge.", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
            } catch (SQLException exc) {
               JOptionPane.showMessageDialog(ChangeStacksDialog.this,
                     "Ein interner Datenbankfehler ist aufgetreten.", "Fehler",
                     JOptionPane.ERROR_MESSAGE);
               Logger.log(exc);
            } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
         }
      });

      btnDiscard.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            ChangeStacksDialog.this.dispose();
         }
      });
   }

   private void createWidgets() {
      pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlCenter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
      pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
      lblNoOfStacks = new JLabel("Anzahl Durchl\u00e4ufe:  ");
      txtNoOfStacks = new JTextField(String.valueOf(pnl.noOfStacks), 5);
      txtNoOfStacks.setHorizontalAlignment(SwingConstants.CENTER);
      btnOk = new JButton("  OK  ");
      btnDiscard = new JButton(" Abbrechen ");
   }

   private void addWidgets() {
      getContentPane().add(pnlCenter, BorderLayout.NORTH);
      getContentPane().add(pnlBtns, BorderLayout.SOUTH);
      pnlCenter.add(lblNoOfStacks);
      pnlCenter.add(txtNoOfStacks);
      pnlBtns.add(btnDiscard);
      pnlBtns.add(Box.createHorizontalStrut(10));
      pnlBtns.add(btnOk);
   }

}
