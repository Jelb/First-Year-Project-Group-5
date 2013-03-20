package QuadTree;

/**
 * 
 * The BoundingBox class defines methods for detecting whether elements of the QuadTree (Leafs, Nodes and query boxes) intersect with each other,
 * and whether a given point is inside a given BoundingBox (e.g. a QuadNode)
 *
 */

public class BoundingBox {
	double xMin;
	double yMin;
	double xMax;
	double yMax;
	
	/**
	 * 
	 * Sets the smallest x to xMin and the smallest y to yMin
	 * We use this guarantee in the implementation of the other methods of the class
	 */
	public BoundingBox(double x1, double y1, double x2, double y2) {
		if (x1 <= x2) {
			this.xMin = x1;
			this.xMax = x2;
		}
		else {
			this.xMin = x2;
			this.xMax = x1;
		}
		if (y1 <= y2) {
			this.yMin = y1;
			this.yMax = y2;
		}
		else {
			this.yMin = y2;
			this.yMax = y1;
		}
	}
	
	/**
	 * Returns true if the given BoundingBox b intersects this BoundingBox
	 */
	public boolean intersects(BoundingBox b) {
		return (axisOverlaps(xMin, xMax, b.getXMin(), b.getXMax()) && axisOverlaps(yMin, yMax, b.getYMin(), b.getYMax()));
		
	}
	
	/**
	 * 
	 * Returns true if the given point defined by x and y is inside the bounding box
	 */
	public boolean holds(double x, double y) {
		return (xMin <= x && x <= xMax && yMin <= y && y <= yMax);
	}
	
	/**
	 * returns true if the given two lines overlap
	 */
	private boolean axisOverlaps(double a1, double a2, double otherA1, double otherA2) {
		return (a1 <= otherA1 && otherA1 <= a2) || (otherA1 <= a1 && a1 <= otherA2);
	}

	public double getXMin() {
		return xMin;
	}

	public double getYMin() {
		return yMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMax() {
		return yMax;
	}

}
