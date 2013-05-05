package Part1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoadSegment extends DrawableItem {
	/**
	 * The objects of this class are the JComponents which make up the lines on the map
	 */

	// The start and end points of the road segment in UTM-values 
	private double geoStartX, geoStartY, geoEndX, geoEndY;
	
	// The start and end points of the road segment in pixels
	private int xStart, yStart, xEnd, yEnd, type;
	
	private Color color;
	private float roadWidth;
	private static int roadType4242;
	public static int zoomLevel;
	
	private static Color lightYellow, lightOrange, darkOrange, white, lightGrey, darkGrey;
	
	/**
	 * 
	 * 
	 * @param xStartCoord
	 * @param yStartCoord
	 * @param xEndCoord
	 * @param yEndCoord
	 * @param Type
	 */
	public RoadSegment(double xStartCoord, double yStartCoord, double xEndCoord, double yEndCoord, int type, boolean border){
		geoStartX = xStartCoord;
		geoStartY = yStartCoord;
		geoEndX = xEndCoord;
		geoEndY = yEndCoord;
		this.type = type;
		color = getRoadSegmentColor(type);
		setRoadWidth(type);
		adjustRoadWidthForBorders(border);
		if(type == 4242) roadWidth = 5;
		updatePosition();
	}
	
//	public static int 
	
	/**
	 * Changes the road width according to the zoom level
	 */
	public void setRoadWidth(int type) {
		
		switch(zoomLevel) {
		case 1    : roadWidth = zoomLevelOne(type);	
					break;
		case 2    : roadWidth = zoomLevelTwo(type);
					break;
		case 3    : roadWidth = zoomLevelThree(type);
					break;
		case 4    : roadWidth = zoomLevelFour(type);
					break;
		case 5    : roadWidth = zoomLevelFive(type);
					break;
		default   : roadWidth = zoomLevelOne(type);
					break;
		}
	}
	
	public float zoomLevelOne(int type) {
		switch(type) {
		case 1    : return 3.6f;
		case 2    : return 3.0f;
		case 3    : return 2.5f;
		case 4    : return 2.0f;
		case 5    : return 1.0f;
		case 8    : return 0.8f;
		case 4242 : return 8.0f;
		default   : return 0.8f;
		}
	}
	
	public float zoomLevelTwo(int type) {
		switch(type) {
		case 1    : return 4.0f;
		case 2    : return 3.6f;
		case 3    : return 3.0f;
		case 4    : return 2.8f;
		case 5    : return 1.4f;
		case 8    : return 0.8f;
		case 4242 : return 8.0f;
		default   : return 0.8f;
		}
	}
	
	public float zoomLevelThree(int type) {
		switch(type) {
		case 1    : return 4.4f;
		case 2    : return 4.0f;
		case 3    : return 3.2f;
		case 4    : return 2.8f;
		case 5    : return 1.4f;
		case 8    : return 1.0f;
		case 4242 : return 8.0f;
		default   : return 0.8f;
		}
	}
	
	public float zoomLevelFour(int type) {
		switch(type) {
		case 1    : return 5.0f;
		case 2    : return 4.2f;
		case 3    : return 3.6f;
		case 4    : return 3.0f;
		case 5    : return 1.4f;
		case 8    : return 1.0f;
		case 4242 : return 8.0f;
		default   : return 0.8f;
		}
	}
	
	public float zoomLevelFive(int type) {
		switch(type) {
		case 1    : return 7.0f;
		case 2    : return 5.0f;
		case 3    : return 4.4f;
		case 4    : return 3.9f;
		case 5    : return 1.8f;
		case 8    : return 1.0f;
		case 4242 : return 8.0f;
		default   : return 0.8f;
		}
	}
	
	public void adjustRoadWidthForBorders(boolean border) {
		if(border) {
			roadWidth += 0.8f;
		}
	}
	
	/**
	 * Creates color objects like the ones used by Google Maps.
	 */
	public static void setColors() {
		lightYellow = new Color(254,252,138);
		lightOrange = new Color(255,197,72);
		darkOrange = new Color(250,144,57);
		white = new Color(255,255,255);
		lightGrey = new Color(239,235,226);
		darkGrey = new Color(190,190,190);
	}
	
	public void updatePosition(){
		xStart = calcPixelX(geoStartX);
		yStart = calcPixelY(geoStartY);
		xEnd = calcPixelX(geoEndX);
		yEnd = calcPixelY(geoEndY);
		setRoadWidth(type);
	}
	
	/**
	 * Method used to receive the color used to paint the specific roadSegment.
	 *
	 * @param TYP An integer describing the road type.
	 * @return The Color used to paint the roadSegment.
	 */
	private static Color getRoadSegmentColor(int TYP){
		switch(TYP) {
		case 1    : return lightOrange;				// Motor ways 	
		case 2    : return darkOrange;				// Motor traffic road
		case 3    : return lightYellow; 			// Primary roads > 6 m 
		case 4    : return lightYellow;				// Secondary roads > 6 m
		case 5    : return darkGrey;				// Roads between 3-6 meters
		case 8    : return Color.green.darker();	// paths
		case 4242 : return Color.red;				// route
		default   : return white; 					// everything else
		}
	}
	
	public static void setZoomLevel() {
//		zoomLevel = 1;
		
		if(WindowHandler.geoWidth < 200000.0 && WindowHandler.geoWidth > 50000.0) {
			zoomLevel = 1; 
			}
		else if(WindowHandler.geoWidth < 50000.0 && WindowHandler.geoWidth > 40000.0) {
			zoomLevel = 2; 
			}
		else if (WindowHandler.geoWidth < 40000.0 && WindowHandler.geoWidth > 15000.0) {
			zoomLevel = 3; 
			}
		else if (WindowHandler.geoWidth < 15000.0 && WindowHandler.geoWidth > 6000.0) {
			zoomLevel = 4; 
			}
		else if (WindowHandler.geoWidth < 6000.0) {
			zoomLevel = 5; 
			}
		
		System.out.println("Zoomlevel: " + zoomLevel);
	}
		
	/**
	 * Method used to draw a line (roadSegment) on the Map.
	 * 
	 * @param g A Graphics object used for drawing
	 */
	public void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		// enable anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		// set road color
		g2.setColor(color);
	
		// setting stroke type
		g2.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		// draw the road segment
		g2.drawLine(xStart, yStart, xEnd, yEnd);

	}
	
	
}
