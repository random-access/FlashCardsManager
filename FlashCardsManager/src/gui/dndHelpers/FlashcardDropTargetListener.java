package gui.dndHelpers;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import app.StartApp;
import core.*;
import exc.CustomErrorHandling;

public class FlashcardDropTargetListener extends DropTargetAdapter {

	private JTree tree;
	private ProjectsController ctl;

	public FlashcardDropTargetListener(JTree tree, ProjectsController ctl) {
		this.tree = tree;
		this.ctl = ctl;
		new DropTarget(tree, DnDConstants.ACTION_COPY, this, true, null);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		int row = tree.getRowForLocation(dtde.getLocation().x, dtde.getLocation().y);
		tree.setSelectionInterval(row, row);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		super.dropActionChanged(dtde);

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable tr = dtde.getTransferable();
			FlashCard card = (FlashCard) tr.getTransferData(TransferableFlashcard.flashcardFlavor);
			if (dtde.isDataFlavorSupported(TransferableFlashcard.flashcardFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node.getUserObject() instanceof Label) {
					card.addLabel((Label) node.getUserObject());
				}
				dtde.dropComplete(true);
				ctl.fireProjectDataChangedEvent();
				return;
			}
			dtde.rejectDrop();

		} catch (UnsupportedFlavorException | IOException exc) {
			if (StartApp.DEBUG)
				exc.printStackTrace();
			dtde.rejectDrop();
		} catch (SQLException sqle) {
			if (StartApp.DEBUG)
				sqle.printStackTrace();
			CustomErrorHandling.showDatabaseError(null, sqle);
		}
	}

}
