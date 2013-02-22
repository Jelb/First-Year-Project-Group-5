//Modified code from Lars Vogel
//http://www.vogella.com/articles/JavaXML/article.html
//Yannis Panagis, 2013

package XML_handin3;

import java.io.FileOutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



public class StaxWriter {
  private String configFile;

  public void setFile(String configFile) {
    this.configFile = configFile;
  }

  public void saveConfig() throws Exception {
    // Create a XMLOutputFactory
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    // Create XMLEventWriter
    XMLEventWriter eventWriter = outputFactory
        .createXMLEventWriter(new FileOutputStream(configFile));
    // Create a EventFactory
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent endLine = eventFactory.createIgnorableSpace("\n");
    // Create and write Start Tag
    StartDocument startDocument = eventFactory.createStartDocument("utf-8", "1.1");
    eventWriter.add(startDocument);
    
    // Create config open tag
    // also creates default namespace xsi namespace and an attribute 
    // used to bind with external XML Schema
    StartElement configStartElement = eventFactory.createStartElement("", "http://config.dk", "config");
    XMLEvent namespace = eventFactory.createNamespace("", "http://config.dk");
    XMLEvent xsiNamespace = eventFactory.createNamespace("xsi", "http://www.w3.org/2000/10/XMLSchema-instance");
    XMLEvent attribute = eventFactory.createAttribute("xsi", "http://www.w3.org/2000/10/XMLSchema-instance", "schemaLocation", "http://config.dk path_to_xsd.xsd");
    
    eventWriter.add(endLine);
    eventWriter.add(configStartElement);
    eventWriter.add(namespace);
    eventWriter.add(xsiNamespace);
    eventWriter.add(attribute);
    eventWriter.add(endLine);
    // Write the different nodes
    /*createNode(eventWriter, "mode", "1");
    createNode(eventWriter, "unit", "901");
    createNode(eventWriter, "current", "0");
    createNode(eventWriter, "interactive", "0");
    */
   String[] test = new String[] {"123", "432", "44576.3845", "23453465", "2432345.345345" };
   writeNode(eventWriter, test);

    eventWriter.add(eventFactory.createEndElement("", "", "config"));
    eventWriter.add(endLine);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.flush();
    eventWriter.close();
  }

  /**
   * Will write the different XML elements of the form (element name, value) 
   * with the help of the eventWriter. If you need to write attributes to an element
   * the code needs to be modified to include a HashMap<String,String> of possible attribute
   * values and then use attr = eventFactory.createAttribute(...) and eventWriter.add(attr)
   * before the element is ended. A similar case is encountered in creating xsi namespace
   *  
   * @param eventWriter The external XMLEventWriter
   * @param name The element name to be created
   * @param value The element value
   */
  private void createNode(XMLEventWriter eventWriter, String name,
      String value) throws XMLStreamException {
    
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent endLine = eventFactory.createIgnorableSpace("\n");
    XMLEvent tab = eventFactory.createIgnorableSpace("\t");
    // Create Start node
    StartElement sElement = eventFactory.createStartElement("", "", name);
    eventWriter.add(tab);
    eventWriter.add(sElement);
    // Create Content
    Characters characters = eventFactory.createCharacters(value);
    eventWriter.add(characters);
    // Create End node
    EndElement eElement = eventFactory.createEndElement("", "", name);
    eventWriter.add(eElement);
    eventWriter.add(endLine);

  }
  
    public void writeNode(XMLEventWriter eventWriter, String[] array) throws XMLStreamException {
        String[] name  = new String[] {"ARC#", "KDV#", "KDV-ID", "X-COORD", "Y-COORD" };
        String[] value = array;
        
        
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent endLine = eventFactory.createIgnorableSpace("\n");
        XMLEvent tab = eventFactory.createIgnorableSpace("\t");
            
        StartElement startKnudeElement = eventFactory.createStartElement("", "", "Knudepunkt");
        EndElement slutKnudeElement = eventFactory.createEndElement("", "", "Knudepunkt");
        
        eventWriter.add(startKnudeElement);
        
        for(int i = 0; i < 5; i++) {
            // Create Start node
            StartElement sElement = eventFactory.createStartElement("", "", name[i]);
            eventWriter.add(tab);
            eventWriter.add(sElement);
            // Create Content
            Characters characters = eventFactory.createCharacters(value[i]);
            eventWriter.add(characters);
            // Create End node
            EndElement eElement = eventFactory.createEndElement("", "", name[i]);
            eventWriter.add(eElement);
            eventWriter.add(endLine);
        }
        
        eventWriter.add(slutKnudeElement);
    }
}