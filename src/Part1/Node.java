package Part1;

/**
 * Class containing information on a map coordinate. Used to identify
 * the location of instances of the Edge class in the world.
 */
public class Node {
	
	private double xCord;			// UTM x-axis value
	private double yCord;			// UTM y-axis value
	private int kdvID;				// ID number
	private static double xOffset;	// the UTM distance from the absolute UTM X=0 to the lowest value xCord in the dataset 
	private static double yOffset;	// the UTM distance from the absolute UTM Y=0 to the lowest value yCord in the dataset 
	
	/**
	 * Constructor, taking the coordinates and ID number of the node.
	 * @param xCord		X-axis UTM value
	 * @param yCord		Y-axis UTM value
	 * @param kdvID		Node ID number
	 */
	public Node(double xCord, double yCord, int kdvID) {
		this.xCord = xCord;
		this.yCord = yCord;
		this.kdvID = kdvID;
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
	
	/**
	 * Sets the absolute UTM X offset of the 'outer' map constraints
	 * @param xOffset	X-axis UTM value of offset
	 */
	public static void setXOffset(double xOffset) {
		Node.xOffset = xOffset;
	}
	
	/**
	 * Sets the absolute UTM Y offset of the 'outer' map constraints
	 * @param yOffset	Y-axis UTM value of offset
	 */
	public static void setYOffset(double yOffset) {
		Node.yOffset = yOffset;
	}
	
	/**
	 * Compares two nodes to see if their position is identical.
	 */
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node n = (Node) o;
			return (xCord == n.getAbsoluteXCoordinate() && yCord == n.getAbsoluteYCoordinate());
		}
		else return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + ((Double)xCord).hashCode();
		hash = 31 * hash + ((Double)yCord).hashCode();
		return hash;
	}
}
