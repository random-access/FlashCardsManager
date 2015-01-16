package gui;

import gui.helpers.FlashcardTableModel;

import javax.swing.RowFilter;

import core.FlashCard;
import core.Label;

public class CustomRowFilter extends RowFilter<FlashcardTableModel, Integer> {

    private Label label;

    CustomRowFilter(Label label) {
        this.label = label;
    }

    @Override
    public boolean include(javax.swing.RowFilter.Entry<? extends FlashcardTableModel, ? extends Integer> entry) {
        FlashcardTableModel model = entry.getModel();
        FlashCard card = model.getCard(entry.getIdentifier());
        if (card.getLabels().contains(label)) {
            return true;
        }
        return false;
    }

}
