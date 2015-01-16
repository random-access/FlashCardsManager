package jTreeTest;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class MyTreeModelListener implements TreeModelListener {

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        // DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        // (e.getTreePath().getLastPathComponent());
        // if (e.getChildIndices() != null) {
        // node = (DefaultMutableTreeNode)
        // (node.getChildAt(e.getChildIndices()[0]));
        // }
        // if (node.getUserObject() instanceof TestLabel) {
        // TestLabel l = (TestLabel) node.getUserObject();
        // System.out.println(l);
        // } else if (node.getUserObject() instanceof TestLearningProject) {
        // TestLearningProject p = (TestLearningProject) node.getUserObject();
        // System.out.println(p);
        // }
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        // TODO Auto-generated method stub

    }

}
