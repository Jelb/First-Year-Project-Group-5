package QuadTree;

public class Coordinate {
	int x;
	int y;
	int id;
	
	public Coordinate(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public boolean equals(Coordinate c) {
		return (x == c.getX() && y == c.getY());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getID() {
		return id;
	}
}
