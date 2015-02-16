package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

import utils.InvalidCharsFilter;
import utils.SizeFilterExtended;
import core.LearningProject;
import exc.CustomErrorHandling;
import exc.CustomInfoHandling;
import gui.helpers.CustomColor;
import gui.helpers.TransparencyTextField;

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
        super(pnl.getMainWindow(), true);
        this.owner = pnl.getMainWindow();
        this.proj = proj;
        this.pnl = pnl;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Titel \u00e4ndern..");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exc) {
            CustomErrorHandling.showInternalError(null, exc);
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
                        CustomInfoHandling.showNoInputInfo(ChangeTitleDialog.this);
                    } else {
                        proj.setTitle(txtTitle.getText());
                        proj.update();
                        owner.updateProjectList();
                        ChangeTitleDialog.this.dispose();
                    }
                } catch (SQLException sqle) {
                    CustomErrorHandling.showDatabaseError(ChangeTitleDialog.this, sqle);
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
        txtTitle = setWidthInCols() ? new TransparencyTextField(proj.getTitle(), 10) : new TransparencyTextField(proj.getTitle());
        txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
        ((AbstractDocument) txtTitle.getDocument()).setDocumentFilter(new SizeFilterExtended(new InvalidCharsFilter(this), 50));
        btnOk = new JButton("  OK  ");
        btnDiscard = new JButton(" Abbrechen ");
    }

    private boolean setWidthInCols() {
        JTextField testField = new JTextField(proj.getTitle());
        return (testField.getPreferredSize().getWidth() < 122); // TODO global
                                                                // variable
    }

    private void addWidgets() {
        getContentPane().add(pnlCenter, BorderLayout.NORTH);
        getContentPane().add(pnlBtns, BorderLayout.SOUTH);
        pnlCenter.add(lblTitle);
        pnlCenter.add(txtTitle);
        pnlBtns.add(btnDiscard);
        pnlBtns.add(Box.createHorizontalStrut(10));
        pnlBtns.add(btnOk);
        txtTitle.setMinimumSize(new Dimension((int) lblTitle.getSize().getWidth(), (int) txtTitle.getPreferredSize().getHeight()));
    }

}
