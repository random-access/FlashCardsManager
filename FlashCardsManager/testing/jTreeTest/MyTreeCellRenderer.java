package jTreeTest;

import exc.CustomErrorHandling;
import gui.ProjectPanel;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import utils.Logger;
import core.IHasStatus;

@SuppressWarnings("serial")
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

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

    public MyTreeCellRenderer() {
        icnRed = new ImageIcon(imgRed);
        icnYellow = new ImageIcon(imgYellow);
        icnGreen = new ImageIcon(imgGreen);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        try {
            IHasStatus treeMember = getTreeMember(value);
            switch (treeMember.getStatus()) {
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
        return this;
    }

    private IHasStatus getTreeMember(Object value) throws SQLException {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof IHasStatus) {
            IHasStatus nodeInfo = (IHasStatus) (node.getUserObject());
            return nodeInfo;
        }
        return null;
    }

}
