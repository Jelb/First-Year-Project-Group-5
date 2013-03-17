package QuadTree;

import java.util.List;

/**
 * 
 * An interface defining an element (Node or Leaf) of the QuadTree
 *
 */
public interface Element {
	public List<Integer> query(BoundingBox box, List<Integer> list);
	
	public void insert(Coordinate c);
	
	public boolean intersects(BoundingBox b);
	
	public boolean holds(int x, int y);
}
