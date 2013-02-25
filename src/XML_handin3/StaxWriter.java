//Modified code from Lars Vogel
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
  private TXTScan txtscan;
  
  XMLEventFactory eventFactory = XMLEventFactory.newInstance();
  XMLEvent endLine = eventFactory.createIgnorableSpace("\n");
  XMLEvent tab = eventFactory.createIgnorableSpace("\t");

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

    txtscan = new TXTScan("src/XML_handin3/kdv_unload_lille.txt");
    while (txtscan.hasNext()) {
    	writeEdge(eventWriter, txtscan.scanning(33));
    
    eventWriter.add(eventFactory.createEndElement("", "", "config"));
    eventWriter.add(endLine);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.flush();
    eventWriter.close();
    
    
    }
  }
  
  /**
   * Writes a single node of data in the XML document.
   * 
   * @param eventWriter			The XMLEventWriter we use to create elements
   * @param name				The name or type of node element
   * @param value				The value of the node element
   */
  private void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
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
  
/**<<<<<<< HEAD
*    public void writeNode(XMLEventWriter eventWriter, String[] inputArr) throws XMLStreamException {
*        String[] name  = new TXTScan("src/kdv_node_unload.txt").scanning(5);}
=======*/
  
	/**
	 * Takes a String array of 5 values and writes 5 data nodes.
	 * 
	 * @param eventWriter		The XMLEventWriter we use to create elements
	 * @param inputArr			The String array containing the node values
	 */
    private void writeNode(XMLEventWriter eventWriter, String[] inputArr) throws XMLStreamException {
        String[] name  = new String[] {"ARC#", "KDV#", "KDV-ID", "X-COORD", "Y-COORD" };
//>>>>>>> 0f6e72e5eefc98e125aa78cba71a4d1f30efe836
        String[] value = inputArr;
            
        StartElement sNodeElement = eventFactory.createStartElement("", "", "Node");
        EndElement eNodeElement = eventFactory.createEndElement("", "", "Node");
        
        eventWriter.add(sNodeElement);
        eventWriter.add(endLine);
        
        // Calls the createNode method to create the 5 nodes
        for(int i = 0; i < 5; i++) {
            createNode(eventWriter, name[i], value[i]);
        }
        
        eventWriter.add(eNodeElement);
        eventWriter.add(endLine);
    }
    
    /**
     * Takes a String array of 33 values and writes 33 data nodes.
     * 
     * @param eventWriter		The XMLEventWriter we use to create elements
     * @param inputArr			The String array containing the node values
     */
    private void writeEdge(XMLEventWriter eventWriter, String[] inputArr) throws XMLStreamException {
    	String[] name  = new String[] { "FNODE#", "TNODE#", "LENGTH", "DAV_DK#", "DAV_DK-ID", "ROADTYPE", "ROADNAME",
    									"FROMLEFT", "TOLEFT", "FROMRIGHT", "TORIGHT", "FROMLEFT_LETTER",
    									"TOLEFT_LETTER", "FROMRIGHT_LETTER", "TORIGHT_LETTER", "F_PARISH_NO",
    									"T_PARISH_NO", "F_ZIPCODE", "T_ZIPCODE", "MUNICIPAL_NO", "ROADCODE", "SUBNET",
    									"ROUTE_NO", "EXIT", "ZONETYPE", "SPEEDLIMIT", "DRIVETIME", "ONE_WAY", "F_TURN",
    									"T_TURN", "ROAD_NO", "CHANGE_DATE", "CHECK_ID" };
    	String[] value = inputArr;
        
        StartElement sEdgeElement = eventFactory.createStartElement("", "", "Edge");
        EndElement eEdgeElement = eventFactory.createEndElement("", "", "Edge");
        
        eventWriter.add(sEdgeElement);
        eventWriter.add(endLine);
        
    	// Calls the createNode method to create the 33 nodes
        for(int i = 0; i < 33; i++) {
            createNode(eventWriter, name[i], value[i]);
        }
        
        eventWriter.add(eEdgeElement);
        eventWriter.add(endLine);
    }
}