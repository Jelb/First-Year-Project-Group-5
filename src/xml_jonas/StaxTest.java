package xml_jonas;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;


public class StaxTest {

 /**
  * @param args
  */
   public static void main(String[] args) {
      XMLCreater kdv_node_unload = new XMLCreater();
      XMLCreater kvd_unload = new XMLCreater();
      try {
       kdv_node_unload.init("src/xml_jonas/kdv_node_unload.xml","src/xml_jonas/kdv_unload2.txt");
       kdv_node_unload.createDocument(33, "edge");
       //kvd_unload.init("kdv_unload.xml","kdv_unload.txt");
       //kvd_unload.createDocument(33, "edge");
   } catch (FileNotFoundException e1) {
    // TODO Auto-generated catch block
    e1.printStackTrace();
   } catch (XMLStreamException e1) {
    // TODO Auto-generated catch block
    e1.printStackTrace();
   } catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   }
}
