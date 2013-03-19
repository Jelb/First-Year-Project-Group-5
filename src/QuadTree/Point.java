package QuadTree;

/**
 * 
 * The Point class defines the points in the QuadTree
 *
 */
public class Point {
	double x;
	double y;
	int id;
	
	public Point(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public boolean equals(Point p) {
		return (x == p.getX() && y == p.getY());
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public int getID() {
		return id;
	}
}
