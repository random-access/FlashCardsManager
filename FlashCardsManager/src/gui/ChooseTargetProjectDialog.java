package gui;

import gui.helpers.MyComboBoxModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.FlashCard;
import core.ProjectsController;

public class ChooseTargetProjectDialog extends JDialog {
   private Component owner;
   private ArrayList<FlashCard> cards;

   private JPanel pnlBottom;
   private Box centerBox;
   private JButton btnDiscard, btnOk;
   private JLabel lblChooseProjects;
   private JComboBox<String> cmbChooseProject;
   private JCheckBox chkKeepProgress;

   public ChooseTargetProjectDialog(ProjectsController ctl, MainWindow owner, ArrayList<FlashCard> cards) {
      super(owner, true);
      this.owner = owner;
      this.cards = cards;
      
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);

      pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
      btnDiscard = new JButton("abbrechen");
      btnOk = new JButton("ok");
      
      centerBox = Box.createVerticalBox();
      ComboBoxModel<String> model = new MyComboBoxModel(ctl.getProjects());
      cmbChooseProject = new JComboBox<String>(model);
      lblChooseProjects = new JLabel("Projekt ausw\u00e4hlen");
      chkKeepProgress = new JCheckBox("Lernfortschritt beibehalten");
      
      add(pnlBottom, BorderLayout.SOUTH);
      pnlBottom.add(btnDiscard);
      pnlBottom.add(btnOk);
      
      add(centerBox, BorderLayout.CENTER);
      centerBox.add(lblChooseProjects);
      centerBox.add(cmbChooseProject);
      centerBox.add(chkKeepProgress);
      
      pack();
      setLocationRelativeTo(owner);
   }
   


}
