package storage;

public enum PicType {
	QUESTION('q'), ANSWER('a');
	
	char shortForm;
	
	PicType(char shortForm) {
		this.shortForm = shortForm;
	}
	
	public char getShortForm(){
		return this.shortForm;
	}
}
