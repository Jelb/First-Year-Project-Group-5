package QuadTree;

import java.util.List;
 /**
  * 
  * The Node class holds references to the four quadrants: Northwest (NW), Northeast (NE), Southwest (SW) and Southeast (SE)
  *
  */
public class Node extends BoundingBox implements Element, Parent {
	Element NW;
	Element NE;
	Element SW;
	Element SE;
	
	public Node(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}
	
	public void setNW(Element nW) {
		NW = nW;
	}

	public void setNE(Element nE) {
		NE = nE;
	}

	public void setSW(Element sW) {
		SW = sW;
	}

	public void setSE(Element sE) {
		SE = sE;
	}

	/**
	 * Sends the given coordinate on to the next Node or Leaf according to where the coordinate/point belongs
	 */
	public void insert(Coordinate c) {
		if (NW.holds(c.getX(), c.getY())) NW.insert(c);
		else if (NE.holds(c.getX(), c.getY())) NE.insert(c);
		else if (SW.holds(c.getX(), c.getY())) SW.insert(c);
		else if (SE.holds(c.getX(), c.getY())) SE.insert(c);
	}
	
	/**
	 * Sends the query on to the next Node or Leaf according to where the search box intersects with the quadrant
	 */
	public List<Coordinate> query(BoundingBox box, List<Coordinate> list) {
		if (NW.intersects(box)) list = NW.query(box, list);
		if (NE.intersects(box)) list = NE.query(box, list);
		if (SW.intersects(box)) list = SW.query(box, list);
		if (SE.intersects(box)) list = SE.query(box, list);
		return list;
	}
	
	/**
	 * Changes the child of either one the quadrants to a new given Element. Used when Leafs converts to Nodes
	 * Throws IllegalStateException if the given former (current) child does not match any of the children of this Node
	 */
	public void changeChild(Element oldChild, Element newChild) {
		if (NW == oldChild) NW = newChild;
		else if (NE == oldChild) NE = newChild;
		else if (SW == oldChild) SW = newChild;
		else if (SE == oldChild) SE = newChild;
		else {throw new IllegalStateException();}
	}
	
	public void show() {
		System.out.println("I'm a Node");
		System.out.println("NW");
		NW.show();
		System.out.println("NE");
		NE.show();
		System.out.println("SW");
		SW.show();
		System.out.println("SE");
		SE.show();
	}
	
	
}
