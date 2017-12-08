package de.bund.bva.isyfact.asciidoc.model;

/**
 * Eine Instanz des Typs Book stellt im Quellenverzeichnis ein Buch als Quelle dar.<br>
 * 
 * Üblicherweise werden die folgenden Attribute gesetzt:<br>
 * Der Autor, das Jahr der Veröffentlichung und der Verlag
 * 
 *
 */
public class Book extends Source {
	private String authors;
	private String year;
	private String publisher;
	
	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
}