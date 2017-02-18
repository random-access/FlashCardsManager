package org.random_access.flashcardsmanager_desktop.gui.helpers;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class DefaultMutableTreeNodeSorter {

	private static Comparator<DefaultMutableTreeNode> tnc = new Comparator<DefaultMutableTreeNode>() {
		@Override
		public int compare(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
			// Sort the parent and child nodes separately:
			if (a.isLeaf() && !b.isLeaf()) {
				return 1;
			} else if (!a.isLeaf() && b.isLeaf()) {
				return -1;
			} else {
				String sa = a.getUserObject().toString();
				String sb = b.getUserObject().toString();
				return sa.compareToIgnoreCase(sb);
			}
		}
	};

	public static void sortTreeNodes(MutableTreeNode parent) {
		int n = parent.getChildCount();
		for (int i = 0; i < n - 1; i++) {
			int min = i;
			for (int j = i + 1; j < n; j++) {
				if (tnc.compare((DefaultMutableTreeNode) parent.getChildAt(min),
						(DefaultMutableTreeNode) parent.getChildAt(j)) > 0) {
					min = j;
				}
			}
			if (i != min) {
				MutableTreeNode a = (MutableTreeNode) parent.getChildAt(i);
				MutableTreeNode b = (MutableTreeNode) parent.getChildAt(min);
				parent.insert(b, i);
				parent.insert(a, min);
			}
		}
	}
}
