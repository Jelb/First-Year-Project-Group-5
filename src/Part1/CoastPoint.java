package Part1;


public class CoastPoint {
	
	private double geoX; 
	private double geoY;

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
	
	public int hashCode() {
		return Double.valueOf(geoX).hashCode() + Double.valueOf(geoY).hashCode();
	}
}