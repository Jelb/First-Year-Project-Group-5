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
	
  public KrakEdge(EdgeData data, Graph<KrakEdge,KrakNode> graph){
    this.v1 = graph.getNode(data.FNODE);
    this.v2 = graph.getNode(data.TNODE);
     
    length = data.LENGTH;
    DAV_DK = data.DAV_DK;
    DAV_DK_ID = data.DAV_DK_ID;
    type = data.TYP;
    // sestoft: Share roadname strings to save space
    String interned = interner.get(data.VEJNAVN);
    if (interned != null) {
      roadname = interned;
    } else {
      roadname = data.VEJNAVN;
      interner.put(roadname, roadname);
    } 
    String dir = data.ONE_WAY;
    /*
     * 	tf = ensrettet modsat digitalise�rings�retning (To-From)
     *	ft = ensrettet i digitaliseringsretning (From-To)
     *  n = ingen k�rsel tilladt (henholdsvis typ 8, 21-28)
     *  <blank> = ingen ensretning
     */
    if(dir.equalsIgnoreCase("tf")){
      this.direction = Edge.BACKWARD;
    } else if(dir.equalsIgnoreCase("ft")){
      this.direction = Edge.FORWARD;
    } else if(dir.equalsIgnoreCase("n")){
      // FIXME:this road can not be travelled on, shouldn't really be included
      this.direction = Edge.BOTH;
    } else{
      this.direction = Edge.BOTH;
    }
    FROMLEFT = data.FROMLEFT;
    TOLEFT = data.TOLEFT;
    FROMRIGHT = data.FROMRIGHT;
    TORIGHT = data.TORIGHT;  
 }
  
  public static void clear() {
    interner = null;
  }
}
