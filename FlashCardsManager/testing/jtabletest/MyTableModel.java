package jtabletest;

import importExport.XMLFlashCard;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class MyTableModel extends AbstractTableModel {
   private ArrayList<XMLFlashCard> data;
   private String [] columnNames;

   MyTableModel(ArrayList<XMLFlashCard> data, String [] columnNames) {
      this.data = data;
      this.columnNames = columnNames;
   }

   @Override
   public int getRowCount() {
      return data.size();
   }

   @Override
   public int getColumnCount() {
      return columnNames.length;
   }
  
   @Override
   public String getColumnName(int col) {
      return columnNames[col];
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
         return data.get(rowIndex).getId();
      case 1:
         return data.get(rowIndex).getQuestion();
      case 2:
         return data.get(rowIndex).getStack();
      default:
         return 0;
      }
   }

}
