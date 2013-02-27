package HandIn4;

public class Edge {
	private String[] info;
	private Node FNODE, TNODE;
	private double length;

	/**
	 * [0] FNODE
	 * [1] TNODE
	 * [2] LENGTH
	 * [3] VEJNAVN
	 * [4] FROMLEFT
	 * [5] TOLEFT
	 * [6] FROMRIGHT
	 * [7] TORIGHT
	 * [8] FROMLEFT_BOGSTAV
	 * [9] TOLEFT_BOGSTAV
	 * [10] FROMRIGHT_BOGSTA
	 * [11] TORIGHT_BOGSTAV
	 * [12] V_POSTNR
	 * [13] H_POSTNR
	 * [14] DRIVETIME
	 */
	public Edge(Node FNODE, Node TNODE, double LENGTH, String VEJNAVN, int FROMLEFT, int TOLEFT, 
				int FROMRIGHT, int TORIGHT,String FROMLEFT_BOGSTAV, String TOLEFT_BOGSTAV, 
				String FROMRIGHT_BOGSTA, String TORIGHT_BOGSTAV, int V_POSTNR, int H_POSTNR, double DRIVETIME) {
		this.FNODE = FNODE;
		this.TNODE = TNODE;
		this.length = LENGTH;
		info = new String[]{VEJNAVN, Integer.toString(FROMLEFT), Integer.toString(TOLEFT), Integer.toString(FROMRIGHT), 
				Integer.toString(TORIGHT), FROMLEFT_BOGSTAV, TOLEFT_BOGSTAV, FROMRIGHT_BOGSTA, TORIGHT_BOGSTAV, 
				Integer.toString(V_POSTNR), Integer.toString(H_POSTNR), Double.toString(DRIVETIME)};
	}
	
	public double length(){  
		return length;  
	}
     
    public int either(){  
    	return FNODE.getID();  
    }
    public int other(int vertex){
     	if      (vertex == FNODE.getID()) return TNODE.getID();
        else if (vertex == TNODE.getID()) return FNODE.getID();
        else throw new RuntimeException("Inconsistent edge");
    }
    public int compareTo(Edge that){
    	if      (this.length() < that.length()) return -1;
        else if (this.length() > that.length()) return +1;
        else                                    return  0;
    }

     public String toString(){  
    	 String data = FNODE.getID() + " " + TNODE.getID() + " " + length() + " " + FNODE.getXcord() + " " + FNODE.getYcord +
    			 " " + TNODE.getXcord() + " " + TNODE.getYcord() +" ";
    	 for(String s: info){
    		 data += s + " ";
    	 }
     	 return data.trim();  
     }
}