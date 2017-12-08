package de.bund.bva.isyfact.asciidoc.model;

/**
 * Jedes Instanz einer Source entspricht einem Quellen-Eintrag im Bibliotheksquellenverzeichnis.
 * Im Bibliotheksquellenverzeichnis ist jede Quelle als XML-Element '<b:Source>' definiert.
 *
 */
public class NonBook extends Source {
	private String url;
	private String yearAccessed;
	private String monthAccessed;
	private String dayAccessed;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getYearAccessed() {
		return yearAccessed;
	}

	public void setYearAccessed(String yearAccessed) {
		this.yearAccessed = yearAccessed;
	}

	public String getMonthAccessed() {
		return monthAccessed;
	}

	public void setMonthAccessed(String monthAccessed) {
		this.monthAccessed = monthAccessed;
	}

	public String getDayAccessed() {
		return dayAccessed;
	}

	public void setDayAccessed(String dayAccessed) {
		this.dayAccessed = dayAccessed;
	}
	
	public String getLastAccessed() {
		if(dayAccessed == null || monthAccessed == null || yearAccessed == null) {
			return null;
		}
		return " (Zugriff am " + dayAccessed + "." + monthAccessed + "." + yearAccessed + ")";
	}
}
