package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import utils.Logger;
import core.FlashCard;
import core.LearningProject;

@SuppressWarnings("serial")
public class EditFlashcardsDialog extends JDialog {

   private static BufferedImage imgSettings, imgPlus, imgFlashcardInfo;
   {
      try {
         imgSettings = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgSettings_28x28.png"));
         imgPlus = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/ImgPlus_16x16.png"));
         imgFlashcardInfo = ImageIO.read(ProjectPanel.class.getClassLoader()
               .getResourceAsStream("img/AddFlashcardInfo_450x338.png"));
      } catch (IOException e) {
         System.out.println("Picture not found");
         // TODO: JDialog mit ErrorMsg
      }
   }
   
   private MainWindow owner;
   private LearningProject project;
   private ProjectPanel projPnl;
   private JLabel lblFlashcardInfo;
   // make them private!! -> method for flashcard panel
   JPanel pnlControls, pnlCenter, pnlSouth;
   Box centerBox;
   JScrollPane scpCenter;
   ArrayList<FlashCardPanel> cardPnls;
   ArrayList<FlashCard> cards;
   private JButton btnAddCard, btnClose;

   private JMenuBar mnuBar;
   private JMenu mnuSettings;
   private JMenuItem mnuSettingsNewCard, mnuSettingsViewSingle,
         mnuSettingsViewList;

   public EditFlashcardsDialog(ProjectPanel projPnl,
         ArrayList<FlashCard> cards, LearningProject project) {
      super(projPnl.getOwner(), true);
      this.owner = projPnl.getOwner();
      this.project = project;
      this.projPnl = projPnl;
      this.cards = cards;
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setTitle(owner.getTitle() + " - Lernkarten bearbeiten");
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
      addWidgets();
      setListeners();

      setSize(500, 500);
      setLocationRelativeTo(owner);
   }
   
   @Override
public MainWindow getOwner() {
	   return owner;
   }

   private void createWidgets() {
      pnlControls = new JPanel();
      pnlControls.setLayout(new FlowLayout(FlowLayout.RIGHT));
      pnlControls.setBorder(BorderFactory.createLineBorder(getContentPane()
            .getBackground(), 10));
      pnlControls.setOpaque(true);
      pnlControls.setBackground(Color.DARK_GRAY);
      pnlCenter = new JPanel(new BorderLayout());
      centerBox = Box.createVerticalBox();

      scpCenter = new JScrollPane(pnlCenter);
      btnAddCard = new JButton(new ImageIcon(imgPlus));
      btnAddCard.setToolTipText("Neue Lernkarte hinzuf\u00fcgen");

      btnClose = new JButton("Schlie\u00dfen");
      pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));

      lblFlashcardInfo = new JLabel(new ImageIcon(imgFlashcardInfo));

      mnuBar = new JMenuBar();
      mnuSettings = new JMenu("");
      mnuSettings.setToolTipText("Einstellungen..");
      mnuSettings.setIcon(new ImageIcon(imgSettings));
      mnuSettings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
      mnuSettingsNewCard = new MyMenuItem("Neue Lernkarte hinzuf\u00fcgen");
      mnuSettingsViewSingle = new MyMenuItem("zeige einzeln");
      mnuSettingsViewSingle.setEnabled(false);
      mnuSettingsViewList = new MyMenuItem("zeige \u00dcbersicht");
      mnuSettingsViewList.setEnabled(false);
   }

   private void addWidgets() {
      this.add(pnlControls, BorderLayout.NORTH);
      this.add(scpCenter, BorderLayout.CENTER);
      this.add(pnlSouth, BorderLayout.SOUTH);
      
      pnlSouth.add(btnClose);

      pnlControls.add(btnAddCard);
      pnlControls.add(Box.createHorizontalStrut(4));
      pnlControls.add(mnuBar);
      pnlControls.add(Box.createHorizontalStrut(2));

      mnuBar.add(mnuSettings);
      mnuSettings.add(mnuSettingsNewCard);
      mnuSettings.add(mnuSettingsViewList);
      mnuSettings.add(mnuSettingsViewSingle);
      cardPnls = new ArrayList<FlashCardPanel>();
      createCardPanels();
      addCardsToEditPanel();
      pnlCenter.add(centerBox, BorderLayout.NORTH);
   }

   public void createCardPanels() {
      for (int i = 0; i < cards.size(); i++) {
         FlashCardPanel newCardPanel;
         if (cards.get(i).getStack() == 1) {
            newCardPanel = new FlashCardPanel(cards.get(i), project,
                  Status.RED, projPnl, EditFlashcardsDialog.this);
         } else if (cards.get(i).getStack() == project.getNumberOfStacks()) {
            newCardPanel = new FlashCardPanel(cards.get(i), project,
                  Status.GREEN, projPnl, EditFlashcardsDialog.this);
         } else {
            newCardPanel = new FlashCardPanel(cards.get(i), project,
                  Status.YELLOW, projPnl, EditFlashcardsDialog.this);
         }
         cardPnls.add(newCardPanel);
      }
   }

   public void addCardsToEditPanel() {
      if (cardPnls.size() == 0) {
         centerBox.add(lblFlashcardInfo);
      } else {
         for (int i = 0; i < cardPnls.size(); i++) {
            centerBox.add(cardPnls.get(i));
         }
         centerBox.add(Box.createVerticalGlue());
      }
   }

   public void updateCardPanels() {
      pnlCenter.remove(centerBox);
      centerBox = Box.createVerticalBox();
      cardPnls = new ArrayList<FlashCardPanel>();
      createCardPanels();
      addCardsToEditPanel();
      pnlCenter.add(centerBox, BorderLayout.NORTH);
      
      revalidate();
      repaint();
   }

   private void setListeners() {
      btnAddCard.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            AddFlashcardDialog2 d = new AddFlashcardDialog2(
                  EditFlashcardsDialog.this, project, projPnl);
            d.setVisible(true);
         }
      });

      mnuSettingsNewCard.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            AddFlashcardDialog2 d = new AddFlashcardDialog2(
                  EditFlashcardsDialog.this, project, projPnl);
            d.setVisible(true);
         }
      });
      
      btnClose.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			EditFlashcardsDialog.this.dispose();		
		}
	});

   }

}
