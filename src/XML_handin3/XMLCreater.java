package XML_handin3;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLCreater {
	private TXTScan txtscan;
	@SuppressWarnings("unused")
	private String configFile;
	private XMLOutputFactory outputFactory;
	private XMLEventWriter eventWriter;
	private XMLEventFactory eventFactory;
	private StartDocument startDocument;
	private StartElement configStartElement;
	private XMLEvent namespace, xsiNamespace, attribute, endLine, tab;
 
	/**
	 * Initializes the fields of the object.
	 * 
	 * @param configFile, The name of the XML-file.
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public void init(String configFile, String txtFilePath) throws FileNotFoundException, XMLStreamException {
		this.configFile = configFile;
		txtscan = new TXTScan(txtFilePath);
		// Create a XMLOutputFactory
		outputFactory = XMLOutputFactory.newInstance();
		// Create XMLEventWriter
		eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(configFile));
		// Create a EventFactory
		eventFactory = XMLEventFactory.newInstance();
		endLine = eventFactory.createIgnorableSpace("\n");
		tab = eventFactory.createIgnorableSpace("\t");
		// Create config open tag
		// also creates default namespace xsi namespace and an attribute 
		// used to bind with external XML Schema
		configStartElement = eventFactory.createStartElement("krak", "http://config.dk", "config");
		namespace = eventFactory.createNamespace("krak", "http://config.dk");
		xsiNamespace = eventFactory.createNamespace("xsi", "http://www.w3.org/2000/10/XMLSchema-instance");
		attribute = eventFactory.createAttribute("xsi", "http://www.w3.org/2000/10/XMLSchema-instance", "schemaLocation", "http://config.dk path_to_xsd.xsd");
	}
 
	/**
	 * Creates the beginning of the XML-document.
	 * 
	 * @throws XMLStreamException
	 */
	private void startDoc() throws XMLStreamException {
		// Create and write Start Tag
		startDocument = eventFactory.createStartDocument("utf-8", "1.1");
		eventWriter.add(startDocument);
		eventWriter.add(endLine);
		eventWriter.add(configStartElement);
		eventWriter.add(namespace);
		eventWriter.add(xsiNamespace);
		eventWriter.add(attribute);
		eventWriter.add(endLine);
	}
 
	/**
	 * Creates the ending of the XML-document.
	 * 
	 * @throws XMLStreamException
	 */
	private void endDoc() throws XMLStreamException {
		eventWriter.add(eventFactory.createEndElement("krak", "", "config"));
		eventWriter.add(endLine);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.flush();
		eventWriter.close();
		txtscan.closeFile();
	}
 
	/**
	 * Creates a .XML-file based on a .TXT-file
	 * 
	 * @param size, The number of elements in the TXT-file.
	 * @param file, The name of the .TXT-file.
	 * @throws XMLStreamException
	 */
	public void createDocument(int size, String name) throws XMLStreamException {
		String[] title, arr;
		String main = name;
		startDoc();
		
		//-----------------------------
		title = txtscan.titleScanning(size);
		for(int j = 0; j < title.length; j++) title[j] = title[j].replaceAll("#", "");
		while(txtscan.hasNext()){
				arr = txtscan.scanning(size);
				eventWriter.add(eventFactory.createStartElement("krak", "", main));
				eventWriter.add(endLine);
				for(int i = 0; i < title.length; i++){
					createSubNode(eventWriter, title[i], arr[i]);
				}
			eventWriter.add(eventFactory.createEndElement("krak", "", main));
			eventWriter.add(endLine);
		}
		//-----------------------------
		
		endDoc();
	}
 
	public void createSubNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
		eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("krak", "", name));
        eventWriter.add(eventFactory.createCharacters(value));
        eventWriter.add(eventFactory.createEndElement("krak", "", name));
        eventWriter.add(endLine);
	}
}