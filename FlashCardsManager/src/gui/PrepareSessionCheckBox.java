package gui;

import gui.helpers.IHasOkButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class PrepareSessionCheckBox extends Box {

    private JCheckBox chk;
    private JLabel lblStack, lblNoOfCards;

    PrepareSessionCheckBox(String text, final IHasOkButton d, boolean selected) {
        super(BoxLayout.X_AXIS);
        final IHasOkButton[] dArr = { d };
        chk = new JCheckBox();
        chk.setSelected(selected);
        lblStack = new JLabel(text);
        this.add(chk);
        this.add(lblStack);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));
        setListeners(dArr);
    }

    PrepareSessionCheckBox(String text, int noOfCards, final IHasOkButton d) {
        super(BoxLayout.X_AXIS);
        final IHasOkButton[] dArr = { d };
        chk = new JCheckBox();
        lblStack = new JLabel(text);
        lblNoOfCards = new JLabel(noOfCards + " Karten");
        this.add(chk);
        this.add(lblStack);
        this.add(Box.createHorizontalGlue());
        this.add(lblNoOfCards);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 30));
        setListeners(dArr);
    }

    private void setListeners(final IHasOkButton[] dArr) {
        chk.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                dArr[0].controlOkButton();
            }
        });
    }

    public boolean isSelected() {
        return chk.isSelected();
    }

    public void setEnabled(boolean enabled) {
        chk.setEnabled(enabled);
        if (enabled == false) {
            lblStack.setFont(lblStack.getFont().deriveFont(Font.ITALIC));
            lblStack.setForeground(Color.GRAY);
            if (lblNoOfCards != null) {
                lblNoOfCards.setFont(lblNoOfCards.getFont().deriveFont(Font.ITALIC));
                lblNoOfCards.setForeground(Color.GRAY);
            }
        }
    }

}
