package QuadTree;

import java.util.List;

/**
 * 
 * An interface defining an element (Node or Leaf) of the QuadTree
 *
 */
public interface Element {
	public List<Point> query(BoundingBox queryBox, List<Point> list);
	
	public void insert(Point c);
	
	public boolean intersects(BoundingBox b);
	
	public boolean holds(double x, double y);
	
	public void show();
}
