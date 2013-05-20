package Part1;

import java.awt.Graphics;

/**
 * Abstract superclass for everything that needs to be drawn on the screen.
 */
public abstract class DrawableItem {
	
	// The current minimum and maximum values of the area of the map we're looking at.
	private static double geoMaxX, geoMaxY, geoMinX, geoMinY;
	
	/**
	 * Used to set maximum and minimum coordinate set of the map.
	 * 
	 * @param geoMaxX The max X-coordinate of the current displayed map.
	 * @param geoMaxY The max Y-coordinate of the current displayed map.
	 * @param geoMinX The min X-coordinate of the current displayed map.
	 * @param geoMinY The min Y-coordinate of the current displayed map.
	 */
	public static void setMapSize(double geoMaxX, double geoMaxY, double geoMinX, double geoMinY){
		DrawableItem.geoMaxX = geoMaxX;
		DrawableItem.geoMaxY = geoMaxY;
		DrawableItem.geoMinX = geoMinX;
		DrawableItem.geoMinY = geoMinY;
	}
	
	/**
	 * Shifts the map area known by the road segments
	 */
	public static void shiftMapSize(double deltaX, double deltaY) {
		setMapSize(geoMaxX+deltaX, geoMaxY+deltaY, geoMinX+deltaX, geoMinY+deltaY);
	}
	
	/**
	 * Converts geo (UTM) coordinate to on-screen pixel coordinate.
	 * @param geoCord	UTM X-axis value
	 * @return			Pixel X-axis value
	 */
	public int calcPixelX(double geoCord){
		return Equation.calcPixelX(geoCord);
	}
	
	/**
	 * Converts geo (UTM) coordinate to on-screen pixel coordinate.
	 * @param geoCord	UTM Y-axis value
	 * @return			Pixel Y-axis value
	 */
	public int calcPixelY(double geoCord){
		return Equation.calcPixelY(geoCord);
	}
	
	public abstract void paintComponent(Graphics g);
	
	public abstract void updatePosition();

	/**
	 * Getter method for the geoMaxX field.
	 * 
	 * @return the geoMaxX
	 */
	public static double getGeoMaxX() {
		return geoMaxX;
	}

	/**
	 * Getter method for the geoMaxY field.
	 * 
	 * @return the geoMaxY
	 */
	public static double getGeoMaxY() {
		return geoMaxY;
	}

	/**
	 * Getter method for the geoMinX field.
	 * 
	 * @return the geoMinX
	 */
	public static double getGeoMinX() {
		return geoMinX;
	}

	/**
	 * Getter method for the geoMinY field.
	 * 
	 * @return the geoMinY
	 */
	public static double getGeoMinY() {
		return geoMinY;
	}
}
