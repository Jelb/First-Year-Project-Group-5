package QuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Part1.Node;

/**
 * An implementation of the QuadTree data structure
 * Inserts nodes that are defined by a x-coordinate and a y-coordinate and has an ID-number.
 * Takes queries in the form of two nodes (x1, y1) and (x2, y2) which together defines a query box.
 * Returns the result of the query in the form of a list of nodes which are inside the query box.
 */
public class QuadTree implements Parent {
	Element root;
	double xMax;
	double yMax;
	int height; //Height of quad tree
	
	public QuadTree(int leafCap, double xMax, double yMax) {
		root = new Leaf(leafCap, this, 0, 0, xMax, yMax);
		height = 1;
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	/**
	 * Finds all the nodes that are held in the box defined 
	 * by the two nodes (x1, y1), (x2, y2)
	 */
	public List<Node> query(double x1, double y1, double x2, double y2) {
		// If the query box is outside of the QuadTree area, 
		// it is set to the ends of the quadtree area
		if (x1 > xMax) x1 = xMax;
		if (x1 < 0) x1 = 0;
		if (x2 < 0) x2 = 0;
		if (x2 > xMax) x2 = xMax;
		if (y1 < 0) y1 = 0;
		if (y1 > yMax) y1 = yMax;
		if (y2 > yMax) y2 = yMax; 
		if (y2 < 0) y2 = 0;
		BoundingBox queryBox = new BoundingBox(x1, y1, x2, y2);
		return root.query(queryBox, new ArrayList<Node>());
	}
	
	/**
	 * Inserts a point in the QuadTree
	 */
	public void insert(double x, double y, int id) {
		insert(new Node(x, y, id));
	}
	
	/**
	 * Inserts a node in the QuadTree
	 */
	public void insert(Node node) {
		double x = node.getXCord();
		double y = node.getYCord();
		// if the point that is to be inserted is outside the QuadTree area, an IllegalArgumentException is thrown
		if (x < 0 || x > xMax || y < 0 || y > yMax) throw new IllegalArgumentException();
		int newHeight = 0;
		newHeight = root.insert(node);
		if (newHeight > height) height = newHeight;
	}
	
	/**
	 * Changes the root reference. Used when the root is converted to a QuadNode
	 */
	public void changeChild(Element oldChild, Element newChild) {
		root = newChild;
	}
	
	/**
	 * Shows the structure of the QuadTree
	 */
	public void showTree() {
		root.show();
	}
	
	public double getXMax() {
		return xMax;
	}
	
	public double getYMax() {
		return yMax;
	}
	
	public Element getRoot() {
		return root;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Test of QuadTree
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		double maxX = 10;
		double maxY = 10;
		QuadTree QT = new QuadTree(3, maxX, maxY);
		Random random = new Random();
		ArrayList<Node> coordList = new ArrayList<Node>();
		int N = 10;
		for (int i = 0; i < N; i++) {
			double x = random.nextDouble()*maxX;
			double y = random.nextDouble()*maxY;
			QT.insert(x, y, i);
			coordList.add(new Node(x, y, i));
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Time to insert " + N + " nodes: " + duration/1000.0 + "s");
		startTime = System.currentTimeMillis(); 
		List<Node> list = QT.query(0, 0, 10, 10);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time for query: " + duration/1000.0 + "s");
		for (Node c : list) {
			System.out.println(c.getKdvID() + ": (" + c.getXCord() + ", " + c.getYCord() + ")");
		}
		System.out.println("Height of quadtree:" + QT.getHeight());
		QT.showTree();
	}
}
