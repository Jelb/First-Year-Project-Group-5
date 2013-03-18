package QuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	public List<Integer> query(double x1, double y1, double x2, double y2) {
		if (x1 > xMax || x1 < 0 || x2 < 0 || x2 > xMax || y1 < 0 || y1 > yMax || y2 > yMax || y2 < 0) throw new IllegalArgumentException();
		BoundingBox box = new BoundingBox(x1, y1, x2, y2);
		return root.query(box, new ArrayList<Integer>());
	}
	
	/**
	 * inserts a point in the QuadTree
	 */
	public void insert(double x, double y, int id) {
		if (x < 0 || x > xMax || y < 0 || y > yMax) throw new IllegalArgumentException();
		root.insert(new Coordinate(x, y, id));
	}
	
	/**
	 * changes the root reference. Used when the root is converted to a Node
	 */
	public void changeChild(Element oldChild, Element newChild) {
		root = newChild;
	}
	
	/**
	 * 
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
		ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
		int N = 10;
		for (int i = 0; i < N; i++) {
			double x = random.nextDouble()*maxX;
			double y = random.nextDouble()*maxY;
			QT.insert(x, y, i);
			coordList.add(new Coordinate(x, y, i));
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Time to insert " + N + " points: " + duration/1000.0 + "s");
		startTime = System.currentTimeMillis(); 
		List<Integer> list = QT.query(0, 0, 10, 10);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Time for query: " + duration/1000.0 + "s");
		for (Integer i : list) {
			System.out.println(i + ": (" + coordList.get(i).getX() + ", " + coordList.get(i).getY() + ")");
		}
		QT.showTree();
	}
	

}
