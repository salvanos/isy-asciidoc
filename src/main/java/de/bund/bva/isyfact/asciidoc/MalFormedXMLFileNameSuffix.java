package de.bund.bva.isyfact.asciidoc;

/**
 * 
 *
 */
public class MalFormedXMLFileNameSuffix extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MalFormedXMLFileNameSuffix() {
		super("The suffix of the XML-File has to be '.xml'");
	}
}
