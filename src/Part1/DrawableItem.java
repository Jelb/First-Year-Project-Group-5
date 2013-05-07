package Part1;

import java.awt.Graphics;

public abstract class DrawableItem {
	
	// The current minimum and maximum values of the area of the map we're looking at.
	public static double geoMaxX, geoMaxY, geoMinX, geoMinY;
	
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
	 * Used to convert the UTM coordinate to pixel.
	 */
	
	public int calcPixelX(double geoCord){
		double diffX = (geoMaxX - geoMinX);
		int width = Window.use().getMapWidth();		
		int x =(int)(((geoCord-geoMinX)/diffX)*width);
		return x;
	}
	
	public int calcPixelY(double geoCord){
		double diffY = (geoMaxY - geoMinY);
		int height = Window.use().getMapHeight();		
		int y =(int)(height-(((geoCord-geoMinY)/diffY)*height));
		return y;
	}
	
	public abstract void paintComponent(Graphics g);
	
	public abstract void updatePosition();
}
