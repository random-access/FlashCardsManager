package core.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Label;
import core.LearningProject;

public interface IFlashCard {

    // DB operations
    public void store() throws SQLException, IOException;

    public void transferTo(LearningProject newProj, boolean keepSuccess, boolean keepLabels) throws SQLException, IOException;

    public void update() throws SQLException, IOException;

    public void delete() throws SQLException, IOException;

    // ops
    public int getNumberInProj() throws SQLException;

    public void nextLevel() throws SQLException;

    public void levelDown() throws SQLException;

    public Object getQuestionInPlainText();

    // getter & setter
    public int getId();

    public String getQuestion();

    public void setQuestion(String question);

    public String getAnswer();

    public void setAnswer(String answer);

    public String getPathToQuestionPic();

    public void setPathToQuestionPic(String pathToQuestionPic);

    public String getPathToAnswerPic();

    public void setPathToAnswerPic(String pathToAnswerPic);

    public LearningProject getProj();

    public void setProj(LearningProject proj);

    public int getStack();

    public void setStack(int stack);

    public int getQuestionWidth();

    public void setQuestionWidth(int questionWidth);

    public int getAnswerWidth();

    public void setAnswerWidth(int answerWidth);

    public ArrayList<Label> getLabels();

    public void synchronizeLabels(ArrayList<Label> newLabels) throws SQLException;

    public void addLabel(Label label) throws SQLException;

    public void removeLabel(Label label) throws SQLException;

}
