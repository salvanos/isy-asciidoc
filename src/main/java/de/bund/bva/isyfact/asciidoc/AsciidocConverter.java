package de.bund.bva.isyfact.asciidoc;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;

/**
 * 
 *
 */
public class AsciidocConverter {
	private Logger logger = Logger.getLogger(AsciidocConverter.class.getName());


	/**
	 * 
	 * @param fileName
	 */
	public void convert(String fileName) {
		logger.info("Start der Konvertierung!");		
		Asciidoctor asciidoctor = Factory.create();
		String[] result = asciidoctor.convertFiles(
			    Arrays.asList(new File(fileName)),
			    new HashMap<String, Object>());
	
		for (String html : result) {
		    System.out.println(html);
		}
		logger.info("Ende der Konvertierung!");		
	}

}
