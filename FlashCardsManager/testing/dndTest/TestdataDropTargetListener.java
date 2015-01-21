package dndTest;

import jTreeTest.TestLabel;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import jtabletest.TableTestData;

public class TestdataDropTargetListener extends DropTargetAdapter {

    private JTree tree;
    private DropTarget dropTarget;

    public TestdataDropTargetListener(JTree tree) {
        this.tree = tree;
        dropTarget = new DropTarget(tree, DnDConstants.ACTION_COPY, this, true, null);
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            Transferable tr = dtde.getTransferable();
            TableTestData data = (TableTestData) tr.getTransferData(TransferableTestdata.testFlavor);

            if (dtde.isDataFlavorSupported(TransferableTestdata.testFlavor)) {

                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                System.out.println(tree.getDropLocation());
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                System.out.println(node);
                if (node.getUserObject() instanceof TestLabel) {
                    ((TestLabel) node.getUserObject()).setId(data.getId());
                    ((TestLabel) node.getUserObject()).setTitle(data.getTitle());
                    tree.repaint();

                    System.out.println("done");
                }
                dtde.dropComplete(true);
                return;
            }
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }

    }

}
