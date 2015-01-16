package jTreeTest;

import javax.swing.tree.*;

@SuppressWarnings("serial")
public class MyTreeModel extends DefaultTreeModel {

    // private ArrayList<TestLabel> labels;

    public MyTreeModel(TreeNode root) {
        super(root);
    }

    // @Override
    // public boolean isLeaf(Object node) {
    // return (node instanceof TestLabel);
    // }
    //
    // @Override
    // public int getChildCount(Object parent) {
    // return labels.size();
    // }
    //
    // @Override
    // public Object getChild(Object parent, int index) {
    // return labels.get(index);
    // }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.getUserObject() instanceof TestLabel) {
            TestLabel l = (TestLabel) node.getUserObject();
            l.setTitle((String) newValue);
            nodeChanged(node);
        } else if (node.getUserObject() instanceof TestLearningProject) {
            TestLearningProject p = (TestLearningProject) node.getUserObject();
            p.setTitle((String) newValue);
            nodeChanged(node);
        }
    }

}
