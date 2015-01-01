package importExport;

public class XMLFlashCard {
	private int id;
	private int projId;
	private int stack;
	private String question;
	private String answer;
	private int customWidthQuestion;
	private int customWidthAnswer;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjId() {
		return projId;
	}
	public void setProjId(int projId) {
		this.projId = projId;
	}
	public int getStack() {
		return stack;
	}
	public void setStack(int stack) {
		this.stack = stack;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getCustomWidthQuestion() {
		return customWidthQuestion;
	}
	public void setCustomWidthQuestion(int customWidthQuestion) {
		this.customWidthQuestion = customWidthQuestion;
	}
	public int getCustomWidthAnswer() {
		return customWidthAnswer;
	}
	public void setCustomWidthAnswer(int customWidthAnswer) {
		this.customWidthAnswer = customWidthAnswer;
	}
	
	@Override
	public String toString() {
		return "XMLFlashCard [id=" + id + ", projId=" + projId + ", stack=" + stack + ", question=" + question + ", answer="
				+ answer + ", customWidthQuestion=" + customWidthQuestion + ", customWidthAnswer=" + customWidthAnswer + "]";
	}
	
}
