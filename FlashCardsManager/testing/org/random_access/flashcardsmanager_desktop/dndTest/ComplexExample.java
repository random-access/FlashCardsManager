package org.random_access.flashcardsmanager_desktop.dndTest;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ComplexExample extends JFrame implements DragGestureListener {

	private static final long serialVersionUID = 1L;
	
	JPanel panel;
    JPanel left;

    public ComplexExample() {

        setTitle("Complex Example");

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 15));

        JButton openb = new JButton("Choose Color");
        openb.setFocusable(false);

        left = new JPanel();
        left.setBackground(Color.red);
        left.setPreferredSize(new Dimension(100, 100));

        openb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Color color = JColorChooser.showDialog(panel, "Choose Color", Color.white);
                left.setBackground(color);
            }
        });

        JPanel right = new JPanel();
        right.setBackground(Color.white);
        right.setPreferredSize(new Dimension(100, 100));

        new MyDropTargetListener(right);

        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(left, DnDConstants.ACTION_COPY, this);

        panel.add(openb);
        panel.add(left);
        panel.add(right);
        add(panel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void dragGestureRecognized(DragGestureEvent event) {
        Cursor cursor = null;
        JPanel panel = (JPanel) event.getComponent();

        Color color = panel.getBackground();

        if (event.getDragAction() == DnDConstants.ACTION_COPY) {
            cursor = DragSource.DefaultCopyDrop;
        }

        event.startDrag(cursor, new TransferableColor(color));
    }

    class MyDropTargetListener extends DropTargetAdapter {

        private JPanel panel;

        public MyDropTargetListener(JPanel panel) {
            this.panel = panel;

            new DropTarget(panel, DnDConstants.ACTION_COPY, this, true, null);
        }

        public void drop(DropTargetDropEvent event) {
            try {

                Transferable tr = event.getTransferable();
                Color color = (Color) tr.getTransferData(TransferableColor.colorFlavor);

                if (event.isDataFlavorSupported(TransferableColor.colorFlavor)) {

                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    this.panel.setBackground(color);
                    event.dropComplete(true);
                    return;
                }
                event.rejectDrop();
            } catch (Exception e) {
                e.printStackTrace();
                event.rejectDrop();
            }
        }
    }

    public static void main(String[] args) {
        new ComplexExample();
    }
}
