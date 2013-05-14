package QuadTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Part1.Node;

/**
 * 
 * A Leaf of the QuadTree. Holds a number of nodes specified when the QuadTree is created.
 * Is converted to a QuadNode when a number of nodes exceeding the capacity is added to the Leaf.
 *
 */
public class Leaf extends BoundingBox implements Element {
	Node[] nodes;
	int top;
	int cap;
	Parent parent;
	
	public Leaf(int cap, Parent parent, double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		nodes = new Node[cap];
		this.cap = cap;
		top = 0;
		this.parent = parent;
	}
	
	/**
	 * inserts Node in leaf. If there is overflow the leaf is converted into a Node
	 */
	public int insert(Node p) {
		if (top == nodes.length) return convertToNode().insert(p);
		else {
			// nodes must be unique
			for (Node Node : nodes) {
				if (Node != null)
					if (p.equals(Node)) return 1;
			}
			nodes[top++] = p;
			return 1;
		}
	}
	
	/**
	 * adds the nodes of the Leaf that are inside the Bounding Box of the query
	 */
	public List<Node> query(BoundingBox queryBox, List<Node> list) {
		for (Node p : nodes) {
			if (p == null) break;
			else if (queryBox.holds(p.getXCord(), p.getYCord())) list.add(p);
		}
		return list;
	}
	
	/**
	 * 
	 * converts this leaf to a Node and puts the nodes that it holds into the corresponding quadrant of the new Node
	 */
	public QuadNode convertToNode() {
		double xMid = (xMin + xMax) / 2.0;
		double yMid = (yMin + yMax) / 2.0;
		QuadNode quadNode = new QuadNode(xMin, yMin, xMax, yMax);
		Leaf NW = new Leaf(cap, quadNode, xMin, yMid, xMid, yMax);
		Leaf NE = new Leaf(cap, quadNode, xMid, yMid, xMax, yMax);
		Leaf SW = new Leaf(cap, quadNode, xMin, yMin, xMid, yMid);
		Leaf SE = new Leaf(cap, quadNode, xMid, yMin, xMax, yMid);
		quadNode.setNW(NW);
		quadNode.setNE(NE);
		quadNode.setSW(SW);
		quadNode.setSE(SE);
		for (Node p : nodes) {
			quadNode.insert(p);
		}
		parent.changeChild(this, quadNode);
		return quadNode;
	}
	
	/**
	 * used to show the structure of the QuadTree
	 */
	public void show() {
		System.out.println("I'm a Leaf");
		for (Node p : nodes) {
			if (p == null) break;
			System.out.println("(" + p.getXCord() + ", " + p.getYCord() + ")");
		}
	}
	
	public ArrayList<Element> structure(ArrayList<Element> tree) {
		tree.add(this);
		return tree;
	}
	
	public Node[] getNodes() {
		return nodes;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Leaf)) return false;
		Leaf l = (Leaf) o;
		return Arrays.deepEquals(nodes, l.getNodes());
	}

}
