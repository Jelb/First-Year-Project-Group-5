package QuadTree;

public class BoundingBox {
	int x1;
	int y1;
	int x2;
	int y2;
	
	/**
	 * 
	 * Sets the smallest x to x1 and the smallest y to y1
	 */
	public BoundingBox(int x1, int y1, int x2, int y2) {
		if (x1 <= x2) {
			this.x1 = x1;
			this.x2 = x2;
		}
		else {
			this.x1 = x2;
			this.x2 = x1;
		}
		if (y1 <= y2) {
			this.y1 = y1;
			this.y2 = y2;
		}
		else {
			this.y1 = y2;
			this.y2 = y1;
		}
	}
	
	/**
	 * Returns true if the given BoundingBox intersects this BoundingBox
	 */
	public boolean intersects(BoundingBox b) {
		return (axisOverlaps(x1, x2, b.getX1(), b.getX2()) && axisOverlaps(y1, y2, b.getY1(), b.getY2()));
		
	}
	
	/**
	 * 
	 * Returns true if the given point is inside the bounding box
	 */
	public boolean holds(int x, int y) {
		return (x1 <= x && x <= x2 && y1 <= y && y <= y2);
	}
	
	/**
	 * returns true if the given two lines overlap
	 */
	private boolean axisOverlaps(int x1, int x2, int otherX1, int otherX2) {
		return (x1 <= otherX1 && otherX1 <= x2) || (otherX1 <= x1 && x1 <= otherX2);
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

}
