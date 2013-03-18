package Part1;

public class Node {
	
	private double xCord;
	private double yCord;
	private int kdvID;
	//private static Node[] nodeArray = new Node[675901];
	
	public Node(double xCord, double yCord, int kdvID) {
		this.xCord = xCord;
		this.yCord = yCord;
		this.kdvID = kdvID;
	}
	
	public double getXCord(){
		return xCord;
	}
	
	public double getYCord(){
		return yCord;
	}
	
	public int getKdvID(){
		return kdvID;
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
