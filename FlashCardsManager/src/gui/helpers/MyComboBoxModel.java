package gui.helpers;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import core.LearningProject;

@SuppressWarnings("serial")
public class MyComboBoxModel extends DefaultComboBoxModel<LearningProject> {
   
   private ArrayList<LearningProject> data;
   
   public MyComboBoxModel(ArrayList<LearningProject> data) {
      this.data = data;
   } 

   @Override
   public int getSize() {
      return data.size();
   }

   @Override
   public LearningProject getElementAt(int index) {
      return data.get(index);
   }

}