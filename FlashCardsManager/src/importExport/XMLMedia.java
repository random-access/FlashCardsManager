package importExport;

public class XMLMedia {
	private int mediaId;
	private int cardId;
	private String pathToMedia;
	private char picType;
	
	public int getMediaId() {
		return mediaId;
	}
	
	public void setMediaId(int mediaId) {
		this.mediaId = mediaId;
	}
	
	public int getCardId() {
		return cardId;
	}
	
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	
	public String getPathToMedia() {
		return pathToMedia;
	}
	
	public void setPathToMedia(String pathToMedia) {
		this.pathToMedia = pathToMedia;
	}
	
	public char getPicType() {
		return picType;
	}
	
	public void setPicType(char picType) {
		this.picType = picType;
	}
	
	@Override
	public String toString() {
		return "XMLMedia [mediaId=" + mediaId + ", cardId=" + cardId + ", pathToMedia=" + pathToMedia + ", picType=" + picType
				+ "]";
	}
}
