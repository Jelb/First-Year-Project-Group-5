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

    eventWriter.add(eventFactory.createEndElement("", "", "config"));
    eventWriter.add(endLine);
    eventWriter.add(eventFactory.createEndDocument());
    eventWriter.flush();
    eventWriter.close();
  }
  
/**<<<<<<< HEAD
*    public void writeNode(XMLEventWriter eventWriter, String[] rsArr) throws XMLStreamException {
*        String[] name  = new TXTScan("src/kdv_node_unload.txt").scanning(5);}
=======*/
    private void writeNode(XMLEventWriter eventWriter, String[] rsArr) throws XMLStreamException {
        String[] name  = new String[] {"ARC#", "KDV#", "KDV-ID", "X-COORD", "Y-COORD" };
//>>>>>>> 0f6e72e5eefc98e125aa78cba71a4d1f30efe836
        String[] value = rsArr;
                
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent endLine = eventFactory.createIgnorableSpace("\n");
        XMLEvent tab = eventFactory.createIgnorableSpace("\t");
            
        StartElement sNodeElement = eventFactory.createStartElement("", "", "Node");
        EndElement eNodeElement = eventFactory.createEndElement("", "", "Node");
        
        eventWriter.add(sNodeElement);
        eventWriter.add(endLine);
        
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
        
        eventWriter.add(eNodeElement);
        eventWriter.add(endLine);
    }
    
    private void writeAddress(XMLEventWriter eventWriter, String[] rsArr) throws XMLStreamException {
    	String[] name  = new String[] { "FNODE#", "TNODE#", "LENGTH", "DAV_DK#", "DAV_DK-ID", "ROADTYPE", "ROADNAME",
    									"FROMLEFT", "TOLEFT", "FROMRIGHT", "TORIGHT", "FROMLEFT_LETTER",
    									"TOLEFT_LETTER", "FROMRIGHT_LETTER", "TORIGHT_LETTER", "F_PARISH_NO",
    									"T_PARISH_NO", "F_ZIPCODE", "T_ZIPCODE", "MUNICIPAL_NO", "ROADCODE", "SUBNET",
    									"ROUTE_NO", "EXIT", "ZONETYPE", "SPEEDLIMIT", "DRIVETIME", "ONE_WAY", "F_TURN",
    									"T_TURN", "ROAD_NO", "CHANGE_DATE", "CHECK_ID" };
    	String[] value = rsArr;
    	
    	XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent endLine = eventFactory.createIgnorableSpace("\n");
        XMLEvent tab = eventFactory.createIgnorableSpace("\t");
        
        StartElement sEdgeElement = eventFactory.createStartElement("", "", "Edge");
        EndElement eEdgeElement = eventFactory.createEndElement("", "", "Edge");
        
        eventWriter.add(sEdgeElement);
        eventWriter.add(endLine);
        
        for(int i = 0; i < 33; i++) {
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
        
        eventWriter.add(eEdgeElement);
        eventWriter.add(endLine);
    }
}