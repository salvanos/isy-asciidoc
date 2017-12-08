package de.bund.bva.isyfact.asciidoc.model;

/**
 * Eine Person (ist bei IsyFact-Quellverweisen �blicherverweise der Autor eines Buches).<br>
 * <br> 
 * Die Person beinhaltet drei Attribute firstName, middleName und lastName.<br> 
 *
 */
public class Person {	
	private String firstName;
	private String middleName;
	private String lastName;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
