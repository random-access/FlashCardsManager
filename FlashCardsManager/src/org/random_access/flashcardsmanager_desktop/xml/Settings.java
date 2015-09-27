package org.random_access.flashcardsmanager_desktop.xml;

public class Settings {
	private int minorVersion, majorVersion, patchLevel, databaseVersion;
	private String pathToDatabase;
	private Boolean showIntro;
	
	public int getMinorVersion() {
		return minorVersion;
	}
	
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	public int getMajorVersion() {
		return majorVersion;
	}
	
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}	
	
	public int getPatchLevel() {
		return patchLevel;
	}
	
	public void setPatchLevel(int patchLevel) {
		this.patchLevel = patchLevel;
	}
	
	public String getPathToDatabase() {
		return pathToDatabase;
	}
	
	public void setPathToDatabase (String pathToDatabase) {
		this.pathToDatabase = pathToDatabase;
	}
	
	public int getDatabaseVersion() {
		return databaseVersion;
	}
	
	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}
	
	public Boolean getShowIntro () {
		return showIntro;
	}
	
	public void setShowIntro(Boolean showIntro) {
		this.showIntro = showIntro;
	}
	
	@Override
	public String toString() {
		return "settings [majorVersion: " + majorVersion + ", minorVersion: " + minorVersion + ", patchLevel: " + patchLevel + ", pathToDatabase: " + pathToDatabase + ", showIntro: " + showIntro + "]";
	}
}
