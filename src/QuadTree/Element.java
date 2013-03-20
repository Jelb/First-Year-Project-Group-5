package QuadTree;

import java.util.List;

import Part1.Node;

/**
 * 
 * An interface defining an element (QuadNode or Leaf) of the QuadTree
 *
 */
public interface Element {
	public List<Node> query(BoundingBox queryBox, List<Node> list);
	
	public void insert(Node n);
	
	public boolean intersects(BoundingBox b);
	
	public boolean holds(double x, double y);
	
	public void show();
}
