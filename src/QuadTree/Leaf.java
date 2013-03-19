package QuadTree;

import java.util.List;

/**
 * 
 * A Leaf of the QuadTree. Holds a number of coordinates specified when the QuadTree is created.
 * Is converted to a Node when a number of coordinates exceeding the capacity is added to the Leaf.
 *
 */
public class Leaf extends BoundingBox implements Element {
	Coordinate[] coordinates;
	int top;
	int cap;
	Parent parent;
	
	public Leaf(int cap, Parent parent, double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		coordinates = new Coordinate[cap];
		this.cap = cap;
		top = 0;
		this.parent = parent;
	}
	
	/**
	 * inserts coordinate in leaf. If there is overflow the leaf is converted into a node
	 */
	public void insert(Coordinate c) {
		if (top == coordinates.length) convertToNode().insert(c);
		else {
			// coordinates must be unique
			for (Coordinate coordinate : coordinates) {
				if (coordinate != null)
					if (c.equals(coordinate)) return;
			}
			coordinates[top++] = c;
		}
	}
	
	/**
	 * adds the coordinates of the Leaf that are inside the Bounding Box of the query
	 */
	public List<Coordinate> query(BoundingBox queryBox, List<Coordinate> list) {
		for (Coordinate c: coordinates) {
			if (c == null) break;
			else if (queryBox.holds(c.getX(), c.getY())) list.add(c);
		}
		return list;
	}
	
	/**
	 * 
	 * converts this leaf to a node and puts the coordinates that it holds into the corresponding quadrant of the new node
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
		for (Coordinate c : coordinates) {
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
		for (Coordinate c : coordinates) {
			if (c == null) break;
			System.out.println("(" + c.getX() + ", " + c.getY() + ")");
		}
	}

}
