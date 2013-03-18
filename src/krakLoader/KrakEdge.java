package krakLoader;

import graphlib.Edge;
import graphlib.Graph;
import java.util.HashMap;

/**
 * A graph edge created from an EdgeData object. 
 * For the sake of memory efficiency a KrakEdge doesnt
 * store all the information available in EdgeData, for example 
 * the zip code and similar are not currently stored.
 * This can easily be changed, by simply adding the necessary fields,and
 * setting their value corresponding to the EdgeData object
 *
 * @author Peter Tiedemann petert@itu.dk
 */
public class KrakEdge extends Edge<KrakNode> {
  // sestoft: For sharing roadname strings
  private static HashMap<String,String> interner = new HashMap<String,String>();
  public final double length;
  public final int DAV_DK, DAV_DK_ID;
  public final int type;
  public final String roadname;
  public final int FROMLEFT;
  public final int TOLEFT;
  public final int FROMRIGHT;
  public final int TORIGHT;
	
  // Tilføj eventuelt check for om der er 33 elementer.
  public KrakEdge(String[] data, Graph<KrakEdge,KrakNode> graph){
	  if(data.length != 33) throw new IndexOutOfBoundsException("The data array is to short");
    this.v1 = graph.getNode(Integer.parseInt(data[0]));
    this.v2 = graph.getNode(Integer.parseInt(data[1]));
     
    length = Double.parseDouble(data[2]);
    DAV_DK = Integer.parseInt(data[3]);
    DAV_DK_ID = Integer.parseInt(data[4]);
    type = Integer.parseInt(data[5]);
    // sestoft: Share roadname strings to save space
    String interned = interner.get(data[6]);
    if (interned != null) {
      roadname = interned;
    } else {
      roadname = data[6];
      interner.put(roadname, roadname);
    } 
    String dir = data[27].toLowerCase();
    /*
     * 	tf = ensrettet modsat digitalise�rings�retning (To-From)
     *	ft = ensrettet i digitaliseringsretning (From-To)
     *  n = ingen k�rsel tilladt (henholdsvis typ 8, 21-28)
     *  <blank> = ingen ensretning
     */
    if(dir.equals("tf")){
      this.direction = Edge.BACKWARD;
    } else if(dir.equals("ft")){
      this.direction = Edge.FORWARD;
    } else if(dir.equals("n")){
      // FIXME:this road can not be travelled on, shouldn't really be included
      this.direction = Edge.BOTH;
    } else{
      this.direction = Edge.BOTH;
    }
    FROMLEFT = Integer.parseInt(data[7]);
    TOLEFT = Integer.parseInt(data[8]);
    FROMRIGHT = Integer.parseInt(data[9]);
    TORIGHT = Integer.parseInt(data[10]);  
 }
  
  public static void clear() {
    interner = null;
  }
}
