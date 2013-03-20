package Part1;

public class Node {
	
	private double xCord;
	private double yCord;
	private int kdvID;
	private static double xOffset;
	private static double yOffset;
	//private static Node[] nodeArray = new Node[675901];
	
	public Node(double xCord, double yCord, int kdvID) {
		this.xCord = xCord;
		this.yCord = yCord;
		this.kdvID = kdvID;
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
	
//	public static void insertNode(Node node){
//		int i = node.getKdvID();
//		nodeArray[i] = node;
//	}
//	
//	public static Node getNode(int index) {
//		return nodeArray[index];
//	}

}
