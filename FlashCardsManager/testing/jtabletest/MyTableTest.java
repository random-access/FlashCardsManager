package jtabletest;

import importExport.XMLFlashCard;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import tests.JComponentTestFrame;

public class MyTableTest {

   public static void main(String[] args) {
      ArrayList<XMLFlashCard> list = new ArrayList<XMLFlashCard>();
      for (int i = 0; i < 8; i++) {
         XMLFlashCard f = new XMLFlashCard();
         if (i % 2 == 0) {
            f.setId(i+1);
         } else {
            f.setId(i-1);
         }
         if (i % 2 == 0) {
            f.setQuestion("Frage " + (i+1));
         } else {
            f.setQuestion("Das ist Frage No. " + (i+1));
         }
         f.setStack(i % 3);
         list.add(f);
      }
      String[] columnNames =  {
            "ID", "Frage", "Stapel"
          };

      TableModel model = new MyTableModel(list, columnNames);
      JTable table = new JTable(model);
      final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>( model );
      table.setRowSorter( rowSorter );
      table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      table.getColumnModel().getColumn(0).setPreferredWidth(50);  
      table.getColumnModel().getColumn(1).setPreferredWidth(350);
      table.getColumnModel().getColumn(2).setPreferredWidth(50);

      // rowSorter.setSortable( 1, false );
      new JComponentTestFrame(table);

   }

}
