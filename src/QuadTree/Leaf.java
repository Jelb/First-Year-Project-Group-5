package QuadTree;

import java.util.List;

/**
 * 
 * A Leaf of the QuadTree. Holds a number of points specified when the QuadTree is created.
 * Is converted to a Node when a number of points exceeding the capacity is added to the Leaf.
 *
 */
public class Leaf extends BoundingBox implements Element {
	Point[] points;
	int top;
	int cap;
	Parent parent;
	
	public Leaf(int cap, Parent parent, double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		points = new Point[cap];
		this.cap = cap;
		top = 0;
		this.parent = parent;
	}
	
	/**
	 * inserts point in leaf. If there is overflow the leaf is converted into a node
	 */
	public void insert(Point p) {
		if (top == points.length) convertToNode().insert(p);
		else {
			// points must be unique
			for (Point point : points) {
				if (point != null)
					if (p.equals(point)) return;
			}
			points[top++] = p;
		}
	}
	
	/**
	 * adds the points of the Leaf that are inside the Bounding Box of the query
	 */
	public List<Point> query(BoundingBox queryBox, List<Point> list) {
		for (Point p : points) {
			if (p == null) break;
			else if (queryBox.holds(p.getX(), p.getY())) list.add(p);
		}
		return list;
	}
	
	/**
	 * 
	 * converts this leaf to a node and puts the points that it holds into the corresponding quadrant of the new node
	 */
	public Node convertToNode() {
		double xMid = (xMin + xMax) / 2.0;
		double yMid = (yMin + yMax) / 2.0;
		Node node = new Node(xMin, yMin, xMax, yMax);
		Leaf NW = new Leaf(cap, node, xMin, yMid, xMid, yMax);
		Leaf NE = new Leaf(cap, node, xMid, yMid, xMax, yMax);
		Leaf SW = new Leaf(cap, node, xMin, yMin, xMid, yMid);
		Leaf SE = new Leaf(cap, node, xMid, yMin, xMax, yMid);
		node.setNW(NW);
		node.setNE(NE);
		node.setSW(SW);
		node.setSE(SE);
		for (Point p : points) {
			node.insert(p);
		}
		parent.changeChild(this, node);
		return node;
	}
	
	/**
	 * used to show the structure of the QuadTree
	 */
	public void show() {
		System.out.println("I'm a Leaf");
		for (Point p : points) {
			if (p == null) break;
			System.out.println("(" + p.getX() + ", " + p.getY() + ")");
		}
	}

}
