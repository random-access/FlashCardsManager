package tools;

public class ForeignKey {
	
	private String text;
	
	public ForeignKey(String constraintTitle, String referredTable, String referredColumn) {
		text = "CONSTRAINT " + constraintTitle + " REFERENCES " + referredTable + "(" + referredColumn + ")";
	}
	
	@Override
	public String toString () {
		return text;
	}
}
