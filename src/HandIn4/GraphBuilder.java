package HandIn4;

public abstract class GraphBuilder {

	private static void main(String[] args) {
		init();
	}
	
	private static void init() {
		createNodes();
		createEdges();
	}
	
	private static void createNodes() {
		// skriv en Node XML parser
		// byg en node
		// kald Node.inserNode() med den
		
		Node node = new Node(692067.66450, 6049914.43018, 675902);  // hardcodet et eksempel
		Node.insertNode(node);
	}
	
	// eller noget...
	private static void createEdges() {
		
	}
}