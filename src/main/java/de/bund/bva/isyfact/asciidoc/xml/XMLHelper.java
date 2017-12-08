package de.bund.bva.isyfact.asciidoc.xml;

import static de.bund.bva.isyfact.asciidoc.konstanten.AsciidocKonstanten.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.bund.bva.isyfact.asciidoc.Asciidoc;
import de.bund.bva.isyfact.asciidoc.MalFormedXMLFileNameSuffix;
import de.bund.bva.isyfact.asciidoc.model.Book;
import de.bund.bva.isyfact.asciidoc.model.NonBook;
import de.bund.bva.isyfact.asciidoc.model.Source;

public class XMLHelper {
	private Logger logger = Logger.getLogger(XMLHelper.class.getName());

	
	/**
	 * Erzeugt eine Asciidoc-Datei aus einer XML-Datei.<br>
	 * <br>
	 * Überprüft zunächst, ob es sich um eine XML-Datei handelt.<br>
	 * Dann wird<br>
	 * <lu> 
	 * <li> die komplette XML-Datei in ein Objekt des Typs Document eingelesen.
	 * <li> das Dokument zu einem HashMap gewandelt.
	 * <li> der HashMap in eine Asciidoc-Datei geschrieben.
	 * 
	 * @param xmlFileName
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws MalFormedXMLFileNameSuffix
	 */
	public Document buildDocument(String xmlFileName) 
			throws ParserConfigurationException, SAXException, 
			IOException, MalFormedXMLFileNameSuffix {
		
		if(!xmlFileName.endsWith(".xml")) {
			throw new MalFormedXMLFileNameSuffix();
		}

		logger.info("Parse das XML-Dokument");
		File xmlFile = new File(xmlFileName);
		DocumentBuilderFactory factory = 
				DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(xmlFile);
		document.getDocumentElement().normalize();
		return document;
	}

	/**
	 * Erzeuge ein HashMap mithilfe eines XML-Dokuments.
	 * 
	 * Die HashMap soll alle erforderlichen Objekte des Domänenmodells
	 * für das Asciidoc-Dokument enthalten.
	 * Hierfür wird jeder Eintrag des HashMap aus einem Tag-Bezeichner als Key 
	 * und einer Source als Value bestehen.
	 *
	 * @param document
	 * @return HashMap<String, Source>
	 */
	public HashMap<String, Source> prepareSourcesMap(Document document) {
		logger.info("Erzeuge die HashMap");
		HashMap<String, Source> hashMap = new HashMap<>();

		logger.info("Itteriere durch die SourceNodes");
		NodeList sourceNodeList = document.getElementsByTagName(SOURCE);
		for (int sourceIndex = 0; sourceIndex < sourceNodeList.getLength(); sourceIndex++) {
			Node sourceNode = sourceNodeList.item(sourceIndex);
			
			logger.info("Beschaffe die AttributNodes aus den SourceNodes");
			NodeList attributeNodes = sourceNode.getChildNodes();
						
			String sourceType = fetchContent(attributeNodes, SOURCETYPE);
			if(BOOK.equals(sourceType)) {				
				logger.info("Es handelt sich um ein Buch");
				Book book = new Book();

				String authors = "";
				List<Node> personNodes = getPersonNodes(sourceNode);
				if(personNodes != null && !personNodes.isEmpty()) {
					String komma = "";			
					for(int i = 0; i < personNodes.size(); i++) {
						Node personNode = personNodes.get(i);
						String first = fetchContent(personNode.getChildNodes(), FIRST);
						String middle = fetchContent(personNode.getChildNodes(), MIDDLE);
						if(first != null && middle != null) {
							first = first + " " + middle;
						}
						String last = fetchContent(personNode.getChildNodes(), LAST);
						authors = authors + komma + first + " " + last;
						komma = ", ";
					}
				}
				book.setAuthors(authors);
				logger.info("Autoren: " + authors);

				String title = fetchContent(attributeNodes, TITLE);
				book.setTitle(title);
				logger.info("Titel: " + title);
				
				String year = fetchContent(attributeNodes, YEAR);
				book.setYear(year);
				logger.info("Jahr: " + year);
				
				String verlag = fetchContent(attributeNodes, PUBLISHER);
				book.setPublisher(verlag);
				logger.info("Verlag: " + verlag);				
			
				hashMap.put(fetchContent(attributeNodes, TAG), book);
			} else {
				NonBook nonBook = new NonBook();
				
				String title = fetchContent(attributeNodes, TITLE);
				nonBook.setTitle(title);
				logger.info("Titel: " + title);

				String url = fetchContent(attributeNodes, URL);
				nonBook.setUrl(url);
				logger.info("URL: " + url);
				
				String jahr = fetchContent(attributeNodes, YEARACCESSED);
				nonBook.setYearAccessed(jahr);
				logger.info("Jahr des letzten Zugriffs: " + jahr);
				
				String monat = fetchContent(attributeNodes, MONTHACCESSED);
				nonBook.setMonthAccessed(monat);
				logger.info("Monat des letzten Zugriffs: " + monat);
				
				String tag = fetchContent(attributeNodes, DAYACCESSED);
				nonBook.setDayAccessed(tag);
				logger.info("Tag des letzten Zugriffs: " + tag);		
				
				hashMap.put(fetchContent(attributeNodes, TAG), nonBook);
			}
		}
		return hashMap;
	}
	
	/**
	 * 
	 * @param sourceNode
	 * @return
	 */
	private List<Node> getPersonNodes(Node sourceNode) {
		List<Node> personNodes = new ArrayList<>();
		
		// AUTHOR 1.Ebene
		List<Node> authorNodes1 = getChildNodesByNodeName(sourceNode, AUTHOR);
		if(authorNodes1 != null && !authorNodes1.isEmpty()) {
			Node authorNode1 = authorNodes1.get(0);	
			
			// AUTHOR 2.Ebene
			List<Node> authorNodes2 = getChildNodesByNodeName(authorNode1, AUTHOR);
			if(authorNodes2 != null && !authorNodes2.isEmpty()) {
				Node authorNode2 = authorNodes2.get(0);	

				// NAMELIST
				List<Node> nameListNodes = getChildNodesByNodeName(authorNode2, NAMELIST);
				if(nameListNodes != null && !nameListNodes.isEmpty()) {
					Node nameListNode = nameListNodes.get(0);	

					// PERSON
					personNodes = getChildNodesByNodeName(nameListNode, PERSON);
				}
			}
		}
		return personNodes;
	}


	/**
	 * 
	 * @param nodeList
	 * @param key
	 * @return
	 */
	private List<Node> getChildNodesByNodeName(Node parentNode, String filterByNodeName) {		
		NodeList allChildNodes = parentNode.getChildNodes();
		List<Node> filteredChildNodes = new ArrayList<>();
		for (int i = 0; i < allChildNodes.getLength(); i++) {
			Node childNode = allChildNodes.item(i);
			
			String foundNodeName = childNode.getNodeName();
			if(foundNodeName.equals(filterByNodeName)) {
				filteredChildNodes.add(childNode);
			}
		}
		return filteredChildNodes;
	}

	/**
	 * 
	 * @param nodeList
	 * @param key
	 * @return
	 */
	private String fetchContent(NodeList nodeList, String key) {
		String content = null;
		for (int index = 0; index < nodeList.getLength(); index++) {			
			Node node = nodeList.item(index);
			String nodeName = node.getNodeName();
			if(key.equals(nodeName)) {
				
				content = node.getTextContent();
				break;
			}
		}
		return content;
	}

}
