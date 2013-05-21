package pytheas;

/**
 * Class describing a single point on the coast line. Points are used to describe
 * the polygons that are used to draw the land masses in the GUI.
 * 
 */
public class CoastPoint{
	
	private double geoX; 
	private double geoY;

	/**
	 * Constructor. Sets up the geo coordinates of the point.
	 * @param x		The X-axis UTM value
	 * @param y		The Y-axis UTM value
	 */
	public CoastPoint(double x, double y) {
		geoX = x;
		geoY = y;
	}
	
	public double getX() {
		return geoX;
	}
	
	public double getY() {
		return geoY;
	}
	
	@Override
	public int hashCode() {
		return Double.valueOf(geoX).hashCode() + Double.valueOf(geoY).hashCode();
	}
	
	/**
	 * Compares two CoastPoints to see if their position is identical.
	 * @param that	The point being compared to this one
	 * @return		Return true if points have same location
	 */
	public boolean equals(CoastPoint that) {
		if(this.getX() == that.geoX && this.getY() == that.getY()) return true;
		else return false;
	}
}
