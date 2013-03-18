package QuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuadTree implements Parent {
	Element root;
	
	public QuadTree(int leafCap, int xMax, int yMax) {
		root = new Leaf(leafCap, this, 0, 0, xMax, yMax);
		
	}
	
	/**
	 * finds all the points that are held in the box defined by the two points (x1, y1), (x2, y2)
	 */
	public List<Integer> query(int x1, int y1, int x2, int y2) {
		BoundingBox box = new BoundingBox(x1, y1, x2, y2);
		return root.query(box, new ArrayList<Integer>());
	}
	
	/**
	 * inserts a point in the QuadTree
	 * TODO: Check that the coordinate is inside the borders of the QuadTree
	 */
	public void insert(int x, int y, int id) {
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
		int maxX = 10;
		int maxY = 10;
		QuadTree QT = new QuadTree(3, maxX, maxY);
		Random random = new Random();
		ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
		int N = 10;
		for (int i = 0; i < N; i++) {
			int x = random.nextInt(maxX);
			int y = random.nextInt(maxY);
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
