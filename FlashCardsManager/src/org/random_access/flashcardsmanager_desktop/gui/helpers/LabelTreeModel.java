package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.sql.SQLException;

import javax.swing.tree.*;

import org.random_access.flashcardsmanager_desktop.core.Label;
import org.random_access.flashcardsmanager_desktop.core.LearningProject;
import org.random_access.flashcardsmanager_desktop.exc.CustomErrorHandling;

@SuppressWarnings("serial")
public class LabelTreeModel extends DefaultTreeModel {

    public LabelTreeModel(TreeNode root) {
        super(root);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.getUserObject() instanceof Label) {
            Label l = (Label) node.getUserObject();
            l.setName((String) newValue);
            try {
                l.update();
            } catch (SQLException sqle) {
                CustomErrorHandling.showDatabaseError(null, sqle);
            }
            nodeChanged(node);
        } else if (node.getUserObject() instanceof LearningProject) {
            LearningProject p = (LearningProject) node.getUserObject();
            p.setTitle((String) newValue);
            try {
                p.update();
            } catch (SQLException sqle) {
                CustomErrorHandling.showDatabaseError(null, sqle);
            }
            nodeChanged(node);
        }
    }

}
