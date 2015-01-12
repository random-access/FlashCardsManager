package gui.helpers;

import exc.CustomErrorHandling;
import gui.ProjectPanel;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import utils.Logger;
import core.*;

@SuppressWarnings("serial")
public class LabelTreeCellRenderer extends DefaultTreeCellRenderer {

	private static BufferedImage imgRed, imgYellow, imgGreen;

	static {
		try {
			imgRed = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgRed_8x8.png"));
			imgYellow = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgYellow_8x8.png"));
			imgGreen = ImageIO.read(ProjectPanel.class.getClassLoader().getResourceAsStream("img/ImgGreen_8x8.png"));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ein interner Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
			Logger.log(e);
		}
	}

	ImageIcon icnRed, icnYellow, icnGreen;
	private LearningProject pnl;

	public LabelTreeCellRenderer(LearningProject proj) {
		this.pnl = proj;
		icnRed = new ImageIcon(imgRed);
		icnYellow = new ImageIcon(imgYellow);
		icnGreen = new ImageIcon(imgGreen);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (!leaf) {
			try {
				switch (pnl.getStatus()) {
				case RED:
					setIcon(icnRed);
					break;
				case YELLOW:
					setIcon(icnYellow);
					break;
				case GREEN:
					setIcon(icnGreen);
					break;
				}
			} catch (SQLException sqle) {
				CustomErrorHandling.showDatabaseError(null, sqle);
				setIcon(icnRed);
			}
			
		} else {
			switch (getStatus(value)) {
			case RED:
				setIcon(icnRed);
				break;
			case YELLOW:
				setIcon(icnYellow);
				break;
			case GREEN:
				setIcon(icnGreen);
				break;
			}
		}
		return this;
	}

	private Status getStatus(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof Label) {
			Label nodeInfo = (Label) (node.getUserObject());
			switch (nodeInfo.getId() % 3) {
			case 0:
				return Status.RED;
			case 1:
				return Status.YELLOW;
			case 2:
				return Status.GREEN;
			default:
				return Status.RED;
			}
		} else {
			return Status.RED;
		}
	}

}
