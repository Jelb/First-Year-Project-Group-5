package QuadTree;

import java.util.List;
 /**
  * 
  * The Node class holds references to the four quadrants: Northwest (NW), Northeast (NE), Southwest (SW) and Southeast (SE)
  * The four quadrants of the Node can be either Nodes or Leafs, it is not necessary for the Node to know this.
  * When a query or an insertion is called on the Node, it sends it on to its children.
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
	 * Sends the given point on to the next Node or Leaf according to where the point belongs
	 */
	public void insert(Point c) {
		if (NW.holds(c.getX(), c.getY())) NW.insert(c);
		else if (NE.holds(c.getX(), c.getY())) NE.insert(c);
		else if (SW.holds(c.getX(), c.getY())) SW.insert(c);
		else if (SE.holds(c.getX(), c.getY())) SE.insert(c);
	}
	
	/**
	 * Sends the query on to the next Node or Leaf according to which of the children of the Node intersects with the query box
	 */
	public List<Point> query(BoundingBox queryBox, List<Point> list) {
		if (NW.intersects(queryBox)) list = NW.query(queryBox, list);
		if (NE.intersects(queryBox)) list = NE.query(queryBox, list);
		if (SW.intersects(queryBox)) list = SW.query(queryBox, list);
		if (SE.intersects(queryBox)) list = SE.query(queryBox, list);
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
	
	/**
	 * Used when testing to show the structure of the QuadTree
	 */
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