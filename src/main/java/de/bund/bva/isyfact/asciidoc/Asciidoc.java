package de.bund.bva.isyfact.asciidoc;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.bund.bva.isyfact.asciidoc.model.Book;
import de.bund.bva.isyfact.asciidoc.model.NonBook;
import de.bund.bva.isyfact.asciidoc.model.Source;
import de.bund.bva.isyfact.asciidoc.xml.XMLHelper;

/**
 * Die Isy-Asciidoc Bibliothek basiert auf das Asciidoctor-Framework und 
 * setzt hiermit unterschiedliche Funktionalitäten für die Erstellung 
 * von Asciidoc Dateien um.
 * 
 * Die Bibliothek lässt sich mit unterschiedlichen Optionen aufrufen:
 * 
 * -c --createBibliography 
 * 	
 * 	Über -c wird die Biblio  
 *
 * Jede Instanz einer Source entspricht einem Quellen-Eintrag im Bibliotheksquellenverzeichnis.
 * Im Bibliotheksquellenverzeichnis ist jede Quelle als XML-Element '<b:Source>' definiert.
 *
 * Das CREATE-Skript soll die XML-Datei IsyFactBibSources.xml einlesen<br>
 * und hieraus eine Asciidoc-Datei erstellen, indem die Literaturverzeichnisfelder<br> 
 * in einer festgelegten Struktur ausgegeben werden.<br>
 * <br> 
 * Bei dieser Struktur wird zwischen Büchern und anderen Quelltypen unterschieden.<br> 
 * <br>
 * Die folgende Auflistung beschreibt die Auswahl der bei allen Quelltypen<br> 
 * zu berücksichtigenden XML-Felder:
 *
 * <li>
 * <b>b:Sources</b><br>
 * Das Wurzelelement der XML-Struktur.<br>
 * Jede Quelle ist darunter jeweils ein Kind-Element.<br>
 * <br>
 * <li>
 * <b>b:Source</b><br> 		
 * Die Quelle enthält die Literaturverzeichnisfelder als Kind-Elemente.<br>
 * <br>
 * <li>
 * <b>b:SourceType</b><br>
 * Der Quelltyp. Wichtig ist hierbei der Sourcetype "Book",<br> 
 * da er im Gegensatz zu den anderen SourceTypes eine 
 * abweichende Ausgabe erfordert.<br>
 * <br>
 * <li>
 * <b>b:Tag</b><br>
 * Eindeutiger Tag-Bezeichner<br>
 * <br>
 * <li>
 * <b>b:Title</b><br>			
 * Der Titel der Quelle<br>
 * <br>
 * In der folgenden Auflistung werden die XML-Elemente beschrieben,<br> 
 * die für alle Quelltypen außer Bücher relevant sind:<br> 
 * <br>
 * <li>
 * <b>b:URL</b><br>
 * Die URL<br>
 * <br>
 * <li>
 * <b>b:YearAccessed</b><br>
 * Jahr des Zugriffs<br>
 * <br>
 * <li>
 * <b>b:MonthAccessed</b><br>
 * Monat des Zugriffs<br>
 * <br>
 * <li>
 * <b>b:DayAccessed</b><br>
 * Tag des Zugriffs<br>
 * <br>
 * In der folgenden Auflistung werden die XML-Elemente<br> 
 * beschrieben, die lediglich für Bücher (SourceType="Book")<br>
 * relevant sind.<br>
 * <br>
 * <li>
 * <b>b:Year</b><br>
 * Das Jahr der Veröffentlichung<br>
 * <li>
 * <b>b:Publisher</b><br>
 * Der Verlag<br>
 * <br>
 * <li>
 * <b>b:Author</b><br>
 * Unter dem XML-Element b:Author sind nicht nur die Autoren,<br>
 * sondern auch die Übersetzer und die Herausgeber aufgelistet.<br> 
 * Hierfür kann das XML-Element b:Author Kind-Elemente<br> 
 * des Typs b:Author,b:Translator und b:Editor enthalten.<br> 
 * Für IsyFact ist lediglich das XML-Element b:Author relevant.<br> 
 * Unglücklich ist hierbei, dass der Bezeichner b:Author im<br> 
 * XML-Element doppelt vergeben wurde,<br>
 * denn das Kindelement wurde ebenso benannt.<br>
 * <br>
 * <li>
 * <b>b:Author</b><br>
 * Jedes Autor Element steht für einen Autor.<br>
 * <lu>
 * <li>
 * &nbsp; &nbsp;<b>b:Namelist</b><br>
 * &nbsp; &nbsp;Die Namensliste mit allen Autoren.<br>
 * <lu>
 * <li>
 * &nbsp; &nbsp; &nbsp; &nbsp;<b>b:Person</b><br>
 * &nbsp; &nbsp; &nbsp; &nbsp;Ein Autor<br>
 * <li>
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<b>b:Last</b><br>
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Der Nachname des Autors.<br>
 * <li>
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<b>b:First</b><br>
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Der erste Vorname des Autors.<br>
 * <li>
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<b>b:Middle</b><br>
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Der zweite Vorname des Autors.<br>
 *
 */
public class Asciidoc {
	private Logger logger = Logger.getLogger(Asciidoc.class.getName());

	public static void main(String args[]) 
			throws ParserConfigurationException, SAXException, 
					IOException, MalFormedXMLFileNameSuffix {
		if(args.length < 2) {
			System.err.println("Usage: Asciidoc [Options] XML-File");
			System.err.println("-c --createBibliography");
			System.err.println("Beispiel: Asciidoc -c IsyFactBibSources.xml");
		} else {
			if(args[0].equals("-c") || args[0].equals("--createBibliography")) {
				Asciidoc asciidoc = new Asciidoc();
				asciidoc.create(args[1]);
			}
		}
	}
	
	/**
	 * 
	 * @param xmlFileName
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws MalFormedXMLFileNameSuffix
	 */
	public void create(String xmlFileName) 
			throws ParserConfigurationException, SAXException, 
			IOException, MalFormedXMLFileNameSuffix {
		logger.info("Asciidoc startet!");
		logger.info("Erzeuge Asciidoc Datei aus XML-Datei!");
		logger.info("XML-Datei: " + xmlFileName);

		int n = xmlFileName.length() - "xml".length();
		String adocFileName = xmlFileName.substring(0, n) + "adoc";
		logger.info("Asciidoc-Datei: " + adocFileName);

		XMLHelper xmlHelper = new XMLHelper();

		logger.info("Parse das XML-Dokument");
		Document document = xmlHelper.buildDocument(xmlFileName);
		logger.info("Erzeuge HashMap aus den Daten");
		HashMap<String, Source> hashMap = xmlHelper.prepareSourcesMap(document);
		logger.info("Schreibe die Asciidoc Datei");
		writeAsciidoc(hashMap, adocFileName);		
		logger.info("Erzeuge die HTML-Datei");
		AsciidocConverter converter = new AsciidocConverter();
		converter.convert(adocFileName);
		logger.info("Asciidoc erfolgreich beendet!");
	}
		
	/**
	 * Schreibt in die Asciidoc Datei.
	 * 
	 * @param xmlFileName
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws MalFormedXMLFileNameSuffix
	 */
	private void writeAsciidoc(HashMap<String, Source> hashMap, String adocFileName)
			throws ParserConfigurationException, SAXException, IOException, 
			MalFormedXMLFileNameSuffix {
		
		logger.info("Versuche die Asciidoc Datei anzulegen");
		Path path = Paths.get(adocFileName);
		try (Writer out = Files.newBufferedWriter(path, StandardCharsets.UTF_16)) {
			String ls = System.lineSeparator();

			logger.info("Erzeuge Header");
			out.write(ls + "[bibliography]" + ls);
			out.write("== Literaturverweise" + ls + ls);
			
			Map<String, Source> treeMap = new TreeMap<String, Source>(hashMap);
			for (String tag : treeMap.keySet()) {
				logger.info("Erzeuge Quell-Eintrag " + "[" + tag + "]");
				out.write("- [[[" + tag + "]]] + " + ls);
				Source source = treeMap.get(tag);
				if(source instanceof Book) {
					logger.info("Es handelt sich um ein Buch.");
					Book book = (Book) source;
					logger.info("Schreibe Autoren, Titel, Jahr und Verlag");
					out.write(
						"  " + book.getAuthors()
						+ ". " + clean(book.getTitle())
						+ ". " + clean(book.getYear()) 
						+ ". " + clean(book.getPublisher()) + " + " + ls)
						;
				} else {
					logger.info("Es handelt sich nicht um ein Buch.");
					NonBook nonBook = (NonBook) source;
					
					logger.info("Schreibe Titel und URL");
					out.write("  " + clean(nonBook.getTitle()) + " + " + ls);
					String url = nonBook.getUrl();
					if(url != null) {
						out.write(
							"  " + clean(nonBook.getUrl()) 
							+ clean(nonBook.getLastAccessed()) + " + " + ls);
					}
				}
				out.write(ls);
			}
			out.flush();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	private String clean(String str) {
		if(str == null) {
			str = "";
		} else {
			str = str
			.replaceAll(System.lineSeparator(), "")
			.replaceAll("\r", "")
			.replaceAll("\n", "")
			.replaceAll("\t", "");
		}
		return str;
	}
}
