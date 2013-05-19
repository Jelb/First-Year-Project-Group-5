package QuadTree;

import java.util.ArrayList;
import java.util.List;

import Part1.Node;

 /**
  * 
  * The QuadNode class holds references to the four quadrants: Northwest (NW), Northeast (NE), Southwest (SW) and Southeast (SE)
  * The four quadrants of the QuadNode can be either Nodes or Leafs, it is not necessary for the QuadNode to know this.
  * When a query or an insertion is called on the QuadNode, it sends it on to its children.
  *
  */
public class QuadNode extends BoundingBox implements Element, Parent {
	Element NW;
	Element NE;
	Element SW;
	Element SE;
	
	public QuadNode(double x1, double y1, double x2, double y2) {
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
	 * Sends the given point on to the next QuadNode or Leaf according to where the point belongs
	 * returns the height of the quadtree rooted in this quadnode
	 */
	public int insert(Node c) {
		int height = 0;
		if (NW.holds(c.getXCord(), c.getYCord())) height = NW.insert(c);
		else if (NE.holds(c.getXCord(), c.getYCord())) height = NE.insert(c);
		else if (SW.holds(c.getXCord(), c.getYCord())) height = SW.insert(c);
		else if (SE.holds(c.getXCord(), c.getYCord())) height = SE.insert(c);
		return ++height;
	}
	
	/**
	 * Sends the query on to the next QuadNode or Leaf according to which of the children of the QuadNode intersects with the query box
	 */
	public List<Node> query(BoundingBox queryBox, List<Node> list) {
		if (NW.intersects(queryBox)) list = NW.query(queryBox, list);
		if (NE.intersects(queryBox)) list = NE.query(queryBox, list);
		if (SW.intersects(queryBox)) list = SW.query(queryBox, list);
		if (SE.intersects(queryBox)) list = SE.query(queryBox, list);
		return list;
	}
	
	/**
	 * Changes the child of either one the quadrants to a new given Element. Used when Leafs converts to Nodes
	 * Throws IllegalStateException if the given former (current) child does not match any of the children of this QuadNode
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
		System.out.println("I'm a QuadNode");
		System.out.println("NW");
		NW.show();
		System.out.println("NE");
		NE.show();
		System.out.println("SW");
		SW.show();
		System.out.println("SE");
		SE.show();
	}
	
	public Element getNW() {
		return NW;
	}
	
	public Element getNE() {
		return NE;
	}
	
	public Element getSW() {
		return SW;
	}
	
	public Element getSE() {
		return SE;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof QuadNode)) return false;
		QuadNode q = (QuadNode) o;
		if (!NW.equals(q.getNW())) return false;
		if (!NE.equals(q.getNE())) return false;
		if (!SW.equals(q.getSW())) return false;
		if (!SE.equals(q.getSE())) return false;
		return true;
	}
	
	
}
