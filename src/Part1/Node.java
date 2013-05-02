package Part1;

public class Node {
	
	private double xCord;
	private double yCord;
	private int kdvID;
	private static double xOffset;
	private static double yOffset;
//	private LinkedList<Edge> edges;
	
	public Node(double xCord, double yCord, int kdvID) {
		this.xCord = xCord;
		this.yCord = yCord;
		this.kdvID = kdvID;
//		edges = new LinkedList<Edge>();
	}
	
	/**
	 * Returns the absolute UTM X offset of the 'outer' map constraints
	 * @return	UTM X coordinate
	 */
	public static double getAbsoluteXoffset() {
		return xOffset;
	}
	
	/**
	 * Returns the absolute UTM Y offset of the 'outer' map constraints
	 * @return	UTM Y coordinate
	 */
	public static double getAbsoluteYoffset() {
		return yOffset;
	}
	
	/**
	 * Returns the absolute UTM X coordinate of node
	 * @return	UTM X coordinate
	 */
	public double getAbsoluteXCoordinate() {
		return xCord;
	}
	
	/**
	 * Returns the absolute UTM Y coordinate of node
	 * @return	UTM Y coordinate
	 */
	public double getAbsoluteYCoordinate() {
		return yCord;
	}
	
	/**
	 * Returns the UTM X coordinate relative to the 'outer' map constraints
	 * @return 'Relative' UTM X coordinate
	 */
	public double getXCord(){
		return xCord - xOffset;
	}
	
	/**
	 * Returns the UTM Y coordinate relative to the 'outer' map constraints
	 * @return 'Relative' UTM Y coordinate
	 */
	public double getYCord(){
		return yCord - yOffset;
	}
	
	/**
	 * Return the ID of the node.
	 * @return	Node ID number
	 */
	public int getKdvID(){
		return kdvID;
	}
	
	public static void setXOffset(double xOffset) {
		Node.xOffset = xOffset;
	}
	
	public static void setYOffset(double yOffset) {
		Node.yOffset = yOffset;
	}
	
	public boolean equals(Node n) {
		return (xCord == n.getXCord() && yCord == n.getYCord());
	}
	
//	public void addEdge(Edge edge) {
//		edges.add(edge);
//	}
//	
//	public LinkedList<Edge> getEdges() {
//		return edges;
//	}

}
