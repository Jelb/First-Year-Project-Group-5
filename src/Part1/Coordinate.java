package Part1;

public class Coordinate {
	
	private double xCoordDouble;
	private double yCoordDouble;
	private int xCoordInt;
	private int yCoordInt;
	
	public Coordinate(double x, double y) {
		xCoordDouble = x;
		yCoordDouble = y;
	}
	
	public Coordinate(int x, int y) {
		xCoordInt = x;
		yCoordInt = y;
	}
	
	public double getXcoordDouble() {
		return xCoordDouble;
	}
	
	public double getYcoordDouble() {
		return yCoordDouble;
	}
	
	public int getXcoordInt() {
		return xCoordInt;
	}
	
	public int getYcoordInt() {
		return yCoordInt;
	}
}
