package Part1;

public class Edge {
	private Node FNODE, TNODE;
	private double length, driveTime;
	private String VEJNAVN, V_POSTNR, H_POSTNR;
	private int TYP;
	private boolean drawable;


	public Edge(Node FNODE, Node TNODE, double LENGTH, String VEJNAVN, int TYP, String V_POSTNR, String H_POSTNR,
			double driveTime, boolean drawable)
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
}