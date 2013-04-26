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
	
	public double getXCord(){
		return xCord - xOffset;
	}
	
	public double getYCord(){
		return yCord - yOffset;
	}
	
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
