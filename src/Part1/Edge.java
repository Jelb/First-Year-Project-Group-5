package Part1;

import java.util.ArrayList;


/**
 * An instance of the edge class contains all the information about a
 * given road in the data-file.
 * 
 *
 */
public class Edge {
	//The nodes which the edge goes between.
	private final Node FNODE, TNODE;
	//The length in meter and the estimated drive time in minutes. 
	private final double LENGTH, DRIVETIME;
	//The name of the road, the ZIP code at the left end of the edge and the ZIP code at the right end of the edge.
	private String STREETNAME, V_POSTNR, H_POSTNR;
	private final int TYPE;
	private final boolean drawable;
	private int houseNumberMinEven, houseNumberMaxEven, houseNumberMinOdd, houseNumberMaxOdd;

	/**
	 * Constructor for the edge class. 
	 * Initializes all the fields of the instance based on the parameters. 
	 * @param FNODE The departure node.
	 * @param TNODE The destination node.
	 * @param LENGTH The length of the edge in meters.
	 * @param streetname The street name.
	 * @param type An integer describing the edge-type e.g. Freeway, highway, main Road... 
	 * @param V_POSTNR The ZIP code at the left end of the edge.
	 * @param H_POSTNR The ZIP code at the right end of the edge.
	 * @param driveTime The estimated drive time for the edge. [(length / speed limit) + 15%]
	 * @param houseNumberFromLeft The floor of house numbering on the left side of the edge.
	 * @param houseNumberToLeft The sealing of house numbering on the left side of the edge.
	 * @param houseNumberFromRight The floor of house numbering on the right side of the edge.
	 * @param houseNumberToRight The sealing of house numbering on the right side of the edge.
	 * @param drawable Boolean telling whether or not the edge should be drawn.
	 */
	public Edge(Node FNODE, Node TNODE, double LENGTH, String streetname, int type, String V_POSTNR, String H_POSTNR,
			double driveTime, int houseNumberFromLeft, int houseNumberToLeft, int houseNumberFromRight,
			int houseNumberToRight, boolean drawable)
	{
		this.FNODE = FNODE;
		this.TNODE = TNODE;
		this.LENGTH = LENGTH;
		this.STREETNAME = streetname;
		this.TYPE = type;
		this.V_POSTNR = V_POSTNR;
		this.H_POSTNR = H_POSTNR;
		this.drawable = drawable;
		this.DRIVETIME = driveTime;
		setHouseNumbers(houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight);
	}
	
	/**
	 * Determines which road numbers should be bound too each
	 * side of the edge.
	 * 
	 * @param houseNumberFrom1 The floor of the first list of house numbers.
	 * @param houseNumberTo1 The sealing of the first list of house numbers.
	 * @param houseNumberFrom2 The floor of the second list of house numbers.
	 * @param houseNumberTo2 The sealing of the second list of house numbers.
	 */
	private void setHouseNumbers(int houseNumberFrom1, int houseNumberTo1, int houseNumberFrom2, int houseNumberTo2) {
		if (houseNumberFrom1 % 2 == 0 && houseNumberTo1 % 2 == 0) {
			if (houseNumberFrom1 < houseNumberTo1) {
				houseNumberMinEven = houseNumberFrom1;
				houseNumberMaxEven = houseNumberTo1;
			}
			else {
				houseNumberMinEven = houseNumberTo1;
				houseNumberMaxEven = houseNumberFrom1;
			}
		}
		else if (houseNumberFrom1 % 2 != 0 && houseNumberTo1 % 2 != 0) {
			if (houseNumberFrom1 < houseNumberTo1) {
				houseNumberMinOdd = houseNumberFrom1;
				houseNumberMaxOdd = houseNumberTo1;
			}
			else {
				houseNumberMinOdd = houseNumberTo1;
				houseNumberMaxOdd = houseNumberFrom1;
			}
		}
		if (houseNumberFrom2 % 2 == 0 && houseNumberTo2 % 2 == 0) {
			if (houseNumberFrom2 < houseNumberTo2) {
				houseNumberMinEven = houseNumberFrom2;
				houseNumberMaxEven = houseNumberTo2;
			}
			else {
				houseNumberMinEven = houseNumberTo2;
				houseNumberMaxEven = houseNumberFrom2;
			}
		}
		else if (houseNumberFrom2 % 2 != 0 && houseNumberTo2 % 2 != 0) {
			if (houseNumberFrom2 < houseNumberTo2) {
				houseNumberMinOdd = houseNumberFrom2;
				houseNumberMaxOdd = houseNumberTo2;
			}
			else {
				houseNumberMinOdd = houseNumberTo2;
				houseNumberMaxOdd = houseNumberFrom2;
			}
		}
	}
	
	/**
	 * Initiates a search for a street name on this or adjacent edges.
	 * @param e		The initial edge
	 * @return		The street name
	 */
	public String lookForStreetname(Edge e) {
		String streetname;
		streetname = lookForStreetname(e, e.from());
		if(streetname != null)
			return streetname;
		streetname = lookForStreetname(e, e.to());
		if(streetname != null)
			return streetname;
		return "No streetname found";
	}
	
	/**
	 * Looks through adjacent edges until it finds an edge with a name, or until the road branches off.
	 * @param e		The edge being examined.
	 * @param n		The 'start' node of the edge, on the side already checked
	 * @return		String containing the street name, or an error message
	 */
	public String lookForStreetname(Edge e, Node n) {
		ArrayList<Edge> connectedEdges = (ArrayList<Edge>) WindowHandler.getGraph().adjArr(n.getKdvID());	// gets a list of edges connected to the given node
		if(connectedEdges.size() <= 2) {																// makes sure the node has no more than 2 edges adjacent
			for(Edge edge : connectedEdges) {															// iterates through these edges
				if(edge != e) {																			// makes sure not to perform operation on the edge it just came from
					String vejnavn = edge.getStreetname();													// gets the streetname from this edge
					if(vejnavn.length() > 0) {															// determines whether edge has a streetname
						return vejnavn;																	// if it does, return this name
					} else {																			
						lookForStreetname(edge, otherNode(n));											// if not, recursively call this method on the next adjacent edge	
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Given one of the two node in this edge, return the other.
	 * @param n		The one node
	 * @return		The other node
	 */
	public Node otherNode(Node n) {
		if(n == FNODE)
			return TNODE;
		else
			return FNODE;
	}
	
	/**
	 * Getter for the left hand zip-code.
	 * @return The zip-code.
	 */
	public String getV_POSTNR(){
		return V_POSTNR;
	}
	
	/**
	 * Getter for the right hand zip-code.
	 * @return The zip-code.
	 */
	public String getH_POSTNR(){
		return H_POSTNR;
	}
	
	/**
	 * @return The length of the edge.
	 */
	public double length(){  
		return LENGTH;  
	}
     
	/**
	 * Getter for the index of the from node.
	 * @return The index of the from node.
	 */
    public int fromID(){  
    	return FNODE.getKdvID();  
    }
    
    /**
	 * Getter for the index of the to node.
	 * @return The index of the to node.
	 */
    public int toID(){
     	return TNODE.getKdvID();
    }
    
    /**
     * Checks if the edges are identical in terms of their nodes position.
     * @param other		The other edge of the comparison
     * @return			Returns true if both edges use the same nodes
     */
    public boolean equals(Object other) {
		if (other instanceof Edge) {
			Edge otherEdge = (Edge) other;
			if (FNODE.equals(otherEdge.from()) && TNODE.equals(otherEdge.to()))
				return true;
			else
				return false;
		} else
			return false;
	}
    
    /**
     * Compares two edges to see which is the longest.
     * @param that		The other edge of the comparison
     * @return			Returns -1 if the other edge is longest,
     * 					+1 if this one is the longest and 0 if they are equal length
     */
    public int compareTo(Edge that){
    	if      (this.length() < that.length()) return -1;
        else if (this.length() > that.length()) return +1;
        else                                    return  0;
    }

    /**
     * Creates a String containing the from/to node ID numbers.
     * @return		Returns a String with node ID numbers
     */
    public String printEdge() {
    	String string = FNODE.getKdvID() + " -> " + TNODE.getKdvID();
    	return string;
    }
    
    @Override
	public String toString() {
		String data = FNODE.getKdvID() + " " + TNODE.getKdvID() + " "
				+ length() + " " + FNODE.getXCord() + " " + FNODE.getYCord()
				+ " " + TNODE.getXCord() + " " + TNODE.getYCord() + " ";
		return data.trim();
	}

    /**
     * @return The from node.
     */
	public Node from() {
		return FNODE;
	}

	/**
	 * @return The to node.
	 */
	public Node to() {
		return TNODE;
	}

	/**
	 * @return The type of the road.
	 */
	public int type() {
		return TYPE;
	}

	/**
	 * @return The street name
	 */
	public String getStreetname() {
		return STREETNAME;
	}
	
	/**
	 * Setter for the street name.
	 * @param streetname The name of the street.
	 */
	public void setStreetname(String streetname) {
		STREETNAME = streetname;
	}

	/**
	 * @return true if the edge is draw able.
	 */
	public boolean isDrawable() {
		return drawable;
	}

	/**
	 * @return The estimated drive time.
	 */
	public double getDriveTime() {
		return DRIVETIME;
	}

	/**
	 * @return The floor of the even house numbers.
	 */
	public int getHouseNumberMinEven() {
		return houseNumberMinEven;
	}

	/**
	 * @return The sealing of the even house numbers.
	 */
	public int getHouseNumberMaxEven() {
		return houseNumberMaxEven;
	}

	/**
	 * @return The floor of the odd house numbers.
	 */
	public int getHouseNumberMinOdd() {
		return houseNumberMinOdd;
	}

	/**
	 * @return The sealing of the odd house numbers.
	 */
	public int getHouseNumberMaxOdd() {
		return houseNumberMaxOdd;
	}
}