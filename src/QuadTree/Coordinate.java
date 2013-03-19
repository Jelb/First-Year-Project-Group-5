package QuadTree;

/**
 * 
 * The Coordinate class defines the coordinates in the QuadTree
 *
 */
public class Coordinate {
	double x;
	double y;
	int id;
	
	public Coordinate(double x, double y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public boolean equals(Coordinate c) {
		return (x == c.getX() && y == c.getY());
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
