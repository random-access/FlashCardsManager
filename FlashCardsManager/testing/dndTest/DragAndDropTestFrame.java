package dndTest;

import jTreeTest.JTreeTest;

import java.awt.BorderLayout;

import javax.swing.*;

import jtabletest.TableDeletingTest;

@SuppressWarnings("serial")
public class DragAndDropTestFrame extends JFrame {

    private JSplitPane sp;

    public DragAndDropTestFrame(JComponent cLeft, JComponent cRight) {
        setTitle("JComponent Testklasse");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        sp = new JSplitPane();
        add(sp, BorderLayout.CENTER);
        sp.add(cLeft, JSplitPane.LEFT, 0);
        sp.add(cRight, JSplitPane.RIGHT, 1);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new DragAndDropTestFrame(new JTreeTest(), new TableDeletingTest());
    }
}