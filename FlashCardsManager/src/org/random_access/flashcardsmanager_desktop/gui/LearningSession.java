package org.random_access.flashcardsmanager_desktop.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.random_access.flashcardsmanager_desktop.core.FlashCard;
import org.random_access.flashcardsmanager_desktop.core.LearningProject;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;
import org.random_access.flashcardsmanager_desktop.gui.helpers.MyButton;
import org.random_access.flashcardsmanager_desktop.storage.PicType;
import org.random_access.flashcardsmanager_desktop.utils.Logger;

@SuppressWarnings("serial")
public class LearningSession extends JDialog {

    private static BufferedImage imgSwitch, imgPrev, imgNext, imgRight, imgWrong, imgExit; // imgFlashcardInfo;

    static {
        try {
            imgSwitch = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgSwitch_28x28.png"));
            imgPrev = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgPrev_28x28.png"));
            imgNext = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgNext_28x28.png"));
            imgRight = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgRight_28x28.png"));
            imgWrong = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgWrong_28x28.png"));
            imgExit = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("org.random_access.flashcardsmanager_desktop.img/ImgExit_28x28.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
            Logger.log(e);
        }
    }

    private boolean movedFwd = true, beginning;
    private MainWindow owner;
    private LearningProject project;
    private ArrayList<FlashCard> allCards;
    private FlashCard currentCard;
    private ListIterator<FlashCard> lit;

    protected JPanel pnlButtons, pnlControls, centerPanel, pnlTitle, pnlProgress;
    protected PicAndTextPanel pnlQ, pnlA;
    protected JScrollPane scpCenter;
    protected JLabel lblTitle;
    protected JButton btnSwitch, btnBack, btnTrue, btnFalse, btnFwd, btnClose;
    protected JProgressBar progress;

    protected LearningSession(MainWindow owner) {
        super(owner, true);
    }

    public LearningSession(MainWindow owner, LearningProject project, ArrayList<FlashCard> allCards) {
        super(owner, true);
        this.owner = owner;
        this.project = project;
        this.allCards = allCards;
        lit = allCards.listIterator();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
            CustomErrorHandling.showInternalError(null, exc);
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
        pnlTitle.setBorder(BorderFactory.createLineBorder(getContentPane().getBackground(), 8));
        lblTitle = new JLabel("Lernen...");
        lblTitle.setOpaque(true);
        lblTitle.setBackground(Color.DARK_GRAY);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(getFont().deriveFont(18.0F));

        pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlControls = new JPanel(new BorderLayout());
        btnSwitch = new MyButton("umdrehen", new ImageIcon(imgSwitch));
        btnBack = new MyButton("zur\u00fcck", new ImageIcon(imgPrev));
        btnFalse = new MyButton("falsch", new ImageIcon(imgWrong));
        btnTrue = new MyButton("richtig", new ImageIcon(imgRight));
        btnFwd = new MyButton("vor", new ImageIcon(imgNext));
        btnClose = new MyButton("schlie\u00dfen", new ImageIcon(imgExit));

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        scpCenter = new JScrollPane();
        scpCenter.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)));
        scpCenter.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
                centerPanel.remove(isQuestion ? pnlQ : pnlA);
                centerPanel.add(isQuestion ? pnlA : pnlQ);
                btnSwitch.setToolTipText(isQuestion ? "Frage zeigen" : "Antwort zeigen");
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LearningSession.this.dispose();
                project.unloadLabelsAndFlashcards();
            }
        });

        btnFwd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.remove(centerPanel.isAncestorOf(pnlQ) ? pnlQ : pnlA);
                btnSwitch.setToolTipText("Antwort zeigen");
                createNextFlashcardFields();
                centerPanel.add(pnlQ);
                centerPanel.revalidate();
                centerPanel.repaint();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.remove(centerPanel.isAncestorOf(pnlQ) ? pnlQ : pnlA);
                btnSwitch.setToolTipText("Antwort zeigen");
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
                    currentCard.update();
                    owner.updateProjectStatus(project);
                    LearningSession.this.owner.updateProjectStatus(project);
                    centerPanel.remove(centerPanel.isAncestorOf(pnlQ) ? pnlQ : pnlA);
                    btnSwitch.setToolTipText("Antwort zeigen");
                    createNextFlashcardFields();
                    centerPanel.add(pnlQ);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(LearningSession.this, sqle);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(LearningSession.this, ioe);
                }
            }
        });

        btnTrue.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    currentCard.nextLevel();
                    currentCard.update();
                    owner.updateProjectStatus(project);
                    LearningSession.this.owner.updateProjectStatus(project);
                    centerPanel.remove(centerPanel.isAncestorOf(pnlQ) ? pnlQ : pnlA);
                    btnSwitch.setToolTipText("Antwort zeigen");
                    createNextFlashcardFields();
                    centerPanel.add(pnlQ);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(LearningSession.this, sqle);
                } catch (IOException ioe) {
                    CustomErrorHandling.showInternalError(LearningSession.this, ioe);
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                project.unloadLabelsAndFlashcards();
            }
        });
    }

    private FlashCard getNextCard() {
        if (lit.hasNext()) {
            if (!movedFwd) {
                lit.next();
                movedFwd = true;
            }
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
            return lit.previous();
        }
        return null;
    }

    private void createPreviousFlashcardFields() {
        currentCard = getPreviousCard();
        enableNavigationAsNeeded();
        try {
            pnlQ = new PicAndTextPanel(currentCard.getPathToQuestionPic(), currentCard.getQuestion(), PicType.QUESTION, false,
                    currentCard.getQuestionWidth());
            pnlA = new PicAndTextPanel(currentCard.getPathToAnswerPic(), currentCard.getAnswer(), PicType.ANSWER, false,
                    currentCard.getAnswerWidth());
            lblTitle.setText(project.getTitle() + " - Karte " + currentCard.getNumberInProj());
            progress.setValue(progress.getValue() - 1);
            progress.setString(progress.getValue() + " von " + progress.getMaximum());
        } catch (SQLException sqle) {
            CustomErrorHandling.showDatabaseError(LearningSession.this, sqle);
        } catch (IOException ioe) {
            CustomErrorHandling.showInternalError(LearningSession.this, ioe);
        }
    }

    private void createNextFlashcardFields() {
        currentCard = getNextCard();
        enableNavigationAsNeeded();
        try {
            if (currentCard == null) {
                pnlQ = allCards.size() == 0 ? new PicAndTextPanel("org.random_access.flashcardsmanager_desktop.img/AddFlashcardInfo_450x338.png", "", null, false, 0)
                        : new PicAndTextPanel(null, "Super! Geschafft!", null, false, 0);
                btnFwd.setEnabled(false);
                btnSwitch.setEnabled(false);
                btnTrue.setEnabled(false);
                btnFalse.setEnabled(false);
                lblTitle.setText(project.getTitle());
            } else { // valid card
                pnlQ = new PicAndTextPanel(currentCard.getPathToQuestionPic(), currentCard.getQuestion(), PicType.QUESTION,
                        false, currentCard.getQuestionWidth());
                pnlA = new PicAndTextPanel(currentCard.getPathToAnswerPic(), currentCard.getAnswer(), PicType.ANSWER, false,
                        currentCard.getAnswerWidth());
                lblTitle.setText(project.getTitle() + " - Karte " + currentCard.getNumberInProj());
            }
            progress.setValue(progress.getValue() + 1);
            progress.setString(progress.getValue() + " von " + progress.getMaximum());
        } catch (SQLException sqle) {
            CustomErrorHandling.showDatabaseError(LearningSession.this, sqle);
        } catch (IOException ioe) {
            CustomErrorHandling.showInternalError(LearningSession.this, ioe);
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
