package XML_handin3;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XMLCreater {
 private TXTScan txtscan;
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
     configStartElement = eventFactory.createStartElement("", "http://config.dk", "config");
     namespace = eventFactory.createNamespace("", "http://config.dk");
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
  eventWriter.add(eventFactory.createEndElement("", "", "config"));
     eventWriter.add(endLine);
     eventWriter.add(eventFactory.createEndDocument());
     eventWriter.flush();
     eventWriter.close();
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
  int k = 0;
  startDoc();
  
  //-----------------------------
  title = txtscan.scanning(size);
  for(int j = 0; j < title.length; j++) title[j] = title[j].replaceAll("#", "");
  while(k <200/**txtscan.hasNext()*/){
      try{
          k++;
          arr = txtscan.scanning(size);
          eventWriter.add(eventFactory.createStartElement("", "", main));
          eventWriter.add(endLine);
          for(int i = 0; i < size; i++){
              createSubNode(eventWriter, title[i], arr[i]);
          }
      } catch (NoSuchElementException e) {
          endDoc();
          continue;
      }
   eventWriter.add(eventFactory.createEndElement("", "", main));
   eventWriter.add(endLine);
  }
  //-----------------------------
  
  endDoc();
 }
 
 public void createSubNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(eventFactory.createCharacters(value));
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(endLine);
 }
}