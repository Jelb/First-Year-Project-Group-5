package QuadTree;

import java.util.List;

public class Leaf extends BoundingBox implements Element {
	Coordinate[] points;
	int top;
	int cap;
	Parent parent;
	
	public Leaf(int cap, Parent parent, double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		points = new Coordinate[cap];
		this.cap = cap;
		top = 0;
		this.parent = parent;
	}
	
	/**
	 * inserts coordinate in leaf. If there is overflow the leaf is converted into a node
	 */
	public void insert(Coordinate c) {
		if (top == points.length) convertToNode().insert(c);
		else {
			// points must be unique
			for (Coordinate point : points) {
				if (point != null)
					if (c.equals(point)) return;
			}
			points[top++] = c;
		}
	}
	
	/**
	 * adds the points of the Leaf that are inside the Bounding Box of the query
	 */
	public List<Integer> query(BoundingBox box, List<Integer> list) {
		for (Coordinate c: points) {
			if (c == null) break;
			else if (box.holds(c.getX(), c.getY())) list.add(c.getID());
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
		for (Coordinate c : points) {
			node.insert(c);
		}
		parent.changeChild(this, node);
		return node;
	}
	
	/**
	 * 
	 */
	public void show() {
		System.out.println("I'm a Leaf");
		for (Coordinate c : points) {
			if (c == null) break;
			System.out.println("(" + c.getX() + ", " + c.getY() + ")");
		}
	}

}
