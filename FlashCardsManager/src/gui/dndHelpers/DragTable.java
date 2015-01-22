package gui.dndHelpers;

import gui.helpers.FlashcardTableModel;

import java.awt.Cursor;
import java.awt.dnd.*;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import core.FlashCard;

@SuppressWarnings("serial")
public class DragTable extends JTable implements DragGestureListener {

	public DragTable(TableModel model) {
		super(model);
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Cursor cursor = null;
		JTable table = (JTable) dge.getComponent();
		FlashCard card = ((FlashcardTableModel) table.getModel()).getCard(table.getSelectedRow());
		if (dge.getDragAction() == DnDConstants.ACTION_COPY) {
			cursor = DragSource.DefaultCopyDrop;
		}
		dge.startDrag(cursor, new TransferableFlashcard(card));
	}

}
