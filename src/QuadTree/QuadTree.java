package QuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * An implementation of the QuadTree data structure
 * Inserts points that are defined by a x-coordinate and a y-coordinate and has an ID-number.
 * Takes queries in the form of two points (x1, y1) and (x2, y2) which together defines a query box.
 * Returns the result of the query in the form of a list of points which are inside the query box.
 *
 */
public class QuadTree implements Parent {
	Element root;
	double xMax;
	double yMax;
	
	public QuadTree(int leafCap, double xMax, double yMax) {
		root = new Leaf(leafCap, this, 0, 0, xMax, yMax);
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	/**
	 * finds all the points that are held in the box defined by the two points (x1, y1), (x2, y2)
	 */
	public List<Point> query(double x1, double y1, double x2, double y2) {
		// If the query box is outside of the QuadTree area, an IllegalArgumentException is thrown
		if (x1 > xMax || x1 < 0 || x2 < 0 || x2 > xMax || y1 < 0 || y1 > yMax || y2 > yMax || y2 < 0) throw new IllegalArgumentException();
		BoundingBox queryBox = new BoundingBox(x1, y1, x2, y2);
		return root.query(queryBox, new ArrayList<Point>());
	}
	
	/**
	 * inserts a point in the QuadTree
	 */
	public void insert(double x, double y, int id) {
		// if the point that is to be inserted is outside the QuadTree area, an IllegalArgumentException is thrown
		if (x < 0 || x > xMax || y < 0 || y > yMax) throw new IllegalArgumentException();
		root.insert(new Point(x, y, id));
	}
	
	/**
	 * changes the root reference. Used when the root is converted to a Node
	 */
	public void changeChild(Element oldChild, Element newChild) {
		root = newChild;
	}
	
	/**
	 * shows the structure of the QuadTree
	 */
	public void showTree() {
		root.show();
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		double maxX = 10;
		double maxY = 10;
		QuadTree QT = new QuadTree(3, maxX, maxY);
		Random random = new Random();
		ArrayList<Point> coordList = new ArrayList<Point>();
		int N = 10;
		for (int i = 0; i < N; i++) {
			double x = random.nextDouble()*maxX;
			double y = random.nextDouble()*maxY;
			QT.insert(x, y, i);
			coordList.add(new Point(x, y, i));
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Time to insert " + N + " points: " + duration/1000.0 + "s");
		startTime = System.currentTimeMillis(); 
		List<Point> list = QT.query(0, 0, 10, 10);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time for query: " + duration/1000.0 + "s");
		for (Point c : list) {
			System.out.println(c.getID() + ": (" + c.getX() + ", " + c.getY() + ")");
		}
		//QT.showTree();
	}
	

}