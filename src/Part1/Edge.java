package Part1;

import java.util.ArrayList;

public class Edge {
	private Node FNODE, TNODE;
	private double length, driveTime;
	private String VEJNAVN, V_POSTNR, H_POSTNR;
	private int TYP;
	private boolean drawable;
	private int houseNumberMinEven, houseNumberMaxEven, houseNumberMinOdd, houseNumberMaxOdd;

	public Edge(Node FNODE, Node TNODE, double LENGTH, String VEJNAVN, int TYP, String V_POSTNR, String H_POSTNR,
			double driveTime, int houseNumberFromLeft, int houseNumberToLeft, int houseNumberFromRight,
			int houseNumberToRight, boolean drawable)
	{
		this.FNODE = FNODE;
		this.TNODE = TNODE;
		this.length = LENGTH;
		this.VEJNAVN = VEJNAVN;
		this.TYP = TYP;
		this.V_POSTNR = V_POSTNR;
		this.H_POSTNR = H_POSTNR;
		this.drawable = drawable;
		this.driveTime = driveTime;
		setHouseNumbers(houseNumberFromLeft, houseNumberToLeft, houseNumberFromRight, houseNumberToRight);
	}
	
	private void setHouseNumbers(int houseNumberFrom1, int houseNumberTo1, int houseNumberFrom2, int houseNumberTo2) {
//		System.out.println("House number from 1: " + houseNumberFrom1);
//		System.out.println("House number to 1: " + houseNumberTo1);
//		System.out.println("House number from 2: " + houseNumberFrom2);
//		System.out.println("House number to 2: " + houseNumberTo2);
		if (houseNumberFrom1 % 2 == 0 && houseNumberFrom2 % 2 != 0) {
			if (houseNumberFrom1 < houseNumberTo1) {
				houseNumberMinEven = houseNumberFrom1;
				houseNumberMaxEven = houseNumberTo1;
			}
			else {
				houseNumberMinEven = houseNumberTo1;
				houseNumberMaxEven = houseNumberFrom1;
			}
			if (houseNumberFrom2 < houseNumberTo2) {
				houseNumberMinOdd = houseNumberFrom2;
				houseNumberMaxOdd = houseNumberTo2;
			}
			else {
				houseNumberMinOdd = houseNumberTo2;
				houseNumberMaxOdd = houseNumberFrom2;
			}
		}
		else if (houseNumberFrom1 % 2 != 0 && houseNumberFrom2 % 2 == 0) {
			if (houseNumberFrom2 < houseNumberTo2) {
				houseNumberMinEven = houseNumberFrom2;
				houseNumberMaxEven = houseNumberTo2;
			}
			else {
				houseNumberMinEven = houseNumberTo2;
				houseNumberMaxEven = houseNumberFrom2;
			}
			if (houseNumberFrom1 < houseNumberTo1) {
				houseNumberMinOdd = houseNumberFrom1;
				houseNumberMaxOdd = houseNumberTo1;
			}
			else {
				houseNumberMinOdd = houseNumberTo1;
				houseNumberMaxOdd = houseNumberFrom1;
			}
		}
		else {
//			System.out.println("House numbers are not ordered as even on one side and uneven on the other for " + VEJNAVN);
		}
	}
	
	public String lookForStreetname(Edge e) {
		String streetname;
		streetname = lookForStreetname(e, e.getFromNode());
		if(streetname != null)
			return streetname;
		streetname = lookForStreetname(e, e.getToNode());
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
		ArrayList<Edge> connectedEdges = (ArrayList<Edge>) WindowHandler.graph.adjArr(n.getKdvID());	// gets a list of edges connected to the given node
		if(connectedEdges.size() <= 2) {																// makes sure the node has no more than 2 edges adjacent
			for(Edge edge : connectedEdges) {															// iterates through these edges
				if(edge != e) {																			// makes sure not to perform operation on the edge it just came from
					String vejnavn = edge.getVEJNAVN();													// gets the streetname from this edge
					if(vejnavn.length() > 0) {															// determines whether edge has a streetname
						return vejnavn;																	// if it does, return this name
					} else {																			
						System.out.println("Recursively looking for a streetname...");					// debuggin' yo!
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
	private  Node otherNode(Node n) {
		if(n == FNODE)
			return TNODE;
		else
			return FNODE;
	}
	
	public String getV_POSTNR(){
		return V_POSTNR;
	}
	
	public String getH_POSTNR(){
		return H_POSTNR;
	}
	
	public double length(){  
		return length;  
	}
	
	public int getFromNodeID() {
		return FNODE.getKdvID();
	}
	
	public int getToNodeID() {
		return TNODE.getKdvID();
	}
     
    public int from(){  
    	return FNODE.getKdvID();  
    }
    public int to(){
     	return TNODE.getKdvID();
    }
    public int compareTo(Edge that){
    	if      (this.length() < that.length()) return -1;
        else if (this.length() > that.length()) return +1;
        else                                    return  0;
    }

    public String printEdge() {
    	String string = FNODE.getKdvID() + " -> " + TNODE.getKdvID();
    	return string;
    }
    
     public String toString(){  
    	 String data = FNODE.getKdvID() + " " + TNODE.getKdvID() + " " + length() + " " + FNODE.getXCord() + " " + FNODE.getYCord() +
    			 " " + TNODE.getXCord() + " " + TNODE.getYCord() +" ";
     	 return data.trim();  
     }
     
     public Node getFromNode() {
    	 return FNODE;
     }
     
     public Node getToNode() {
    	 return TNODE;
     }
     
     public int getType() {
    	 return TYP;
     }

	public String getVEJNAVN() {
		return VEJNAVN;
	}

	public void setVEJNAVN(String vEJNAVN) {
		VEJNAVN = vEJNAVN;
	}
	
	public boolean isDrawable() {
		return drawable;
	}

	public double getDriveTime() {
		return driveTime;
	}

	public int getHouseNumberMinEven() {
		return houseNumberMinEven;
	}

	public int getHouseNumberMaxEven() {
		return houseNumberMaxEven;
	}

	public int getHouseNumberMinOdd() {
		return houseNumberMinOdd;
	}

	public int getHouseNumberMaxOdd() {
		return houseNumberMaxOdd;
	}
}