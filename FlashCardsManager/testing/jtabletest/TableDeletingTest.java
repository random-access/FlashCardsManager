package jtabletest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

@SuppressWarnings("serial")
public class TableDeletingTest extends JFrame {

    private String[] columnNames = { "Auswahl", "ID" };
    private ArrayList<TableTestData> data;
    private JTable table;

    private JScrollPane scp;
    private JPanel pnlControls;
    private JButton btnDelete;

    public TableDeletingTest() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createTestData(5);
        constructTable();
        createGUI();
        setListeners();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createGUI() {
        scp = new JScrollPane(table);
        pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnDelete = new JButton("delete");
        add(scp, BorderLayout.CENTER);
        add(pnlControls, BorderLayout.SOUTH);
        pnlControls.add(btnDelete);
    }

    private void createTestData(int length) {
        data = new ArrayList<TableTestData>();
        for (int i = 0; i < length; i++) {
            data.add(new TableTestData(i));
        }
    }

    private void constructTable() {
        MyTableTestModel model = new MyTableTestModel(data, columnNames);
        table = new JTable(model);
    }

    private void setListeners() {
        btnDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = data.size()-1; i >= 0; --i) {
                    if (data.get(i).isSelected()) {
                        ((MyTableTestModel) table.getModel()).removeRow(i);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TableDeletingTest();
            }
        });
    }

}
