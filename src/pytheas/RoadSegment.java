package pytheas;

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
	private static int zoomLevel;
	
	private boolean borderDrawn = false, isPath = false;
	
	
	//The colors used to draw RoadSegments.
	private static Color 
		lightYellow = new Color(254,252,138),
		lightOrange = new Color(255,197,72),
		darkOrange = new Color(250,144,57),
		white = new Color(255,255,255),
		lightGrey = new Color(239,235,226),
		darkGrey = new Color(190,190,190),
		green = new Color(202,223,170);
	
	/**
	 * Constructs a RoadSegment.
	 * 
	 * @param xStartCoord	The geo (UTM) X-axis coordinate of the start of the segment
	 * @param yStartCoord	The geo (UTM) Y-axis coordinate of the start of the segment
	 * @param xEndCoord		The geo (UTM) Y-axis coordinate of the end of the segment
	 * @param yEndCoord		The geo (UTM) Y-axis coordinate of the end of the segment
	 * @param Type			The road type
	 */
	public RoadSegment(double xStartCoord, double yStartCoord, double xEndCoord, double yEndCoord, int type){
		geoStartX = xStartCoord;
		geoStartY = yStartCoord;
		geoEndX = xEndCoord;
		geoEndY = yEndCoord;
		this.type = type;
		color = getRoadSegmentColor(type);
		setRoadWidth(type);
		if(type == 4242) roadWidth = 5;
		updatePosition();
	}
	
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
		case 6    : roadWidth = zoomLevelSix(type);
					break;
		case 7    : roadWidth = zoomLevelSeven(type);
					break;
		case 8    : roadWidth = zoomLevelEight(type);
					break;
		case 9    : roadWidth = zoomLevelNine(type);
					break;
		case 10   : roadWidth = zoomLevelTen(type);
				    break;
		default   : roadWidth = zoomLevelOne(type);
					break;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 1
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelOne(int type) {
		switch(type) {
		case 1    : return 2.6f;
		case 2    : return 2.2f;
		case 3    : return 2.0f;
		case 4    : return 1.8f;
		case 5    : return 1.0f;
		case 6    : return 1.0f;
		case 8    : return 0.8f;
		case 4242 : return 3.6f;
		default   : return 0.8f;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 2
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelTwo(int type) {
		switch(type) {
		case 1    : return 3.8f;
		case 2    : return 3.4f;
		case 3    : return 3.0f;
		case 4    : return 2.8f;
		case 5    : return 1.4f;
		case 6    : return 1.4f;
		case 8    : return 0.8f;
		case 4242 : return 4.0f;
		default   : return 1.0f;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 3
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelThree(int type) {
		switch(type) {
		case 1    : return 4.4f;
		case 2    : return 4.0f;
		case 3    : return 3.2f;
		case 4    : return 2.8f;
		case 5    : return 1.4f;
		case 6    : return 1.4f;
		case 8    : return 1.0f;
		case 4242 : return 4.4f;
		default   : return 1.3f;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 4
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelFour(int type) {
		switch(type) {
		case 1    : return 6.0f;
		case 2    : return 5.0f;
		case 3    : return 3.8f;
		case 4    : return 3.0f;
		case 5    : return 1.4f;
		case 6    : return 1.2f;
		case 8    : return 1.0f;
		case 4242 : return 5.0f;
		default   : return 1.5f;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 5
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelFive(int type) {
		switch(type) {
		case 1    : return 7.0f;
		case 2    : return 6.0f;
		case 3    : return 4.5f;
		case 4    : return 3.9f;
		case 5    : return 1.8f;
		case 6    : return 1.4f;
		case 8    : return 1.0f;
		case 4242 : return 7.0f;
		default   : return 2.0f;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 6
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelSix(int type) {
		switch(type) {
		case 1    : return 8.0f;
		case 2    : return 7.0f;
		case 3    : return 6.0f;
		case 4    : return 4.0f;
		case 5    : return 3.0f;
		case 6    : return 2.5f;
		case 8    : return 2.0f;
		case 4242 : return 10.0f;
		default   : return 2.0f;
		}
	}

	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 7
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelSeven(int type) {
		switch(type) {
		case 1    : return 9.0f;
		case 2    : return 8.0f;
		case 3    : return 7.0f;
		case 4    : return 6.5f;
		case 5    : return 4.0f;
		case 6    : return 3.4f;
		case 8    : return 3.0f;
		case 4242 : return 10.0f;
		default   : return 2.5f;
		}
	}
	
	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 8
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelEight(int type) {
		switch(type) {
		case 1    : return 12.0f;
		case 2    : return 11.0f;
		case 3    : return 9.0f;
		case 4    : return 9.0f;
		case 5    : return 7.0f;
		case 6    : return 6.0f;
		case 8    : return 3.0f;
		case 4242 : return 12.0f;
		default   : return 3.0f;
		}
	}
	
	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 9
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelNine(int type) {
		switch(type) {
		case 1    : return 14.0f;
		case 2    : return 12.0f;
		case 3    : return 12.0f;
		case 4    : return 11.0f;
		case 5    : return 9.5f;
		case 6    : return 8.5f;
		case 8    : return 4.0f;
		case 4242 : return 12.0f;
		default   : return 3.0f;
		}
	}
	
	/**
	 * Setting the width of a RoadSegment when drawn at zoom level 10
	 * @param type	The type of road
	 * @return		The width this RoadSegment will be drawn with
	 */
	public static float zoomLevelTen(int type) {
		switch(type) {
		case 1    : return 18.0f;
		case 2    : return 15.0f;
		case 3    : return 14.0f;
		case 4    : return 13.0f;
		case 5    : return 12.0f;
		case 6    : return 11.0f;
		case 8    : return 5.0f;
		case 4242 : return 20.0f;
		default   : return 3.0f;
		}
	}
	
	/**
	 * Checks if this RoadSegment is supposed to be drawn as a border,
	 * and if it is, increases the width and changes the colour to grey.
	 * @param isBorder
	 */
	public void adjustForBorders(boolean isBorder) {
		if(isBorder) {
			roadWidth += 2.0f;
			color = darkGrey;
		}
	}
	
	/**
	 * Changes the stored pixel coordinates of the start and end location
	 * of the road segment, based on the current geo (or UTM) coordinate.
	 */
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
		case 2    : return lightOrange;				// Motor traffic road
		case 3    : return lightYellow; 			// Primary roads > 6 m 
		case 4    : return lightYellow;				// Secondary roads > 6 m
		case 5    : return white;					// Roads between 3-6 meters
		case 6    : return white;					// All regular roads and streets
		case 8    : return green;					// paths
		case 4242 : return Color.RED;				// route
		default   : return lightGrey; 				// everything else
		}
	}
	
	/**
	 * Sets the zoom level field based on the currently displayed GeoWidth.
	 */
	public static void setZoomLevel() {
		if(WindowHandler.getGeoWidth() > 50000.0) {
			zoomLevel = 1; 
			}
		else if(WindowHandler.getGeoWidth() <= 50000.0 && WindowHandler.getGeoWidth() > 40000.0) {
			zoomLevel = 2; 
			}
		else if(WindowHandler.getGeoWidth() <= 40000.0 && WindowHandler.getGeoWidth() > 40000.0) {
			zoomLevel = 3; 
			}
		else if (WindowHandler.getGeoWidth() <= 30000.0 && WindowHandler.getGeoWidth() > 20000.0) {
			zoomLevel = 4; 
			}
		else if (WindowHandler.getGeoWidth() <= 20000.0 && WindowHandler.getGeoWidth() > 10000.0) {
			zoomLevel = 5; 
			}
		else if (WindowHandler.getGeoWidth() <= 10000.0 && WindowHandler.getGeoWidth() > 5000.0) {
			zoomLevel = 6; 
			}
		else if (WindowHandler.getGeoWidth() <= 5000.0 && WindowHandler.getGeoWidth() > 2500.0) {
			zoomLevel = 7; 
			}
		else if (WindowHandler.getGeoWidth() <= 2500.0 && WindowHandler.getGeoWidth() > 1250.0) {
			zoomLevel = 8;
			}
		else if (WindowHandler.getGeoWidth() <= 1250.0 && WindowHandler.getGeoWidth() > 600.0) {
			zoomLevel = 9;
			}
		else if (WindowHandler.getGeoWidth() <= 600.0) {
			zoomLevel = 10;
		}
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
	
		// setting stroke type
		if(type == 4242) {
			g2.setColor(color);
			g2.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		} else if(!borderDrawn) {
			g2.setColor(darkGrey);
			g2.setStroke(new BasicStroke(roadWidth+2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		} else {
			g2.setColor(color);
			g2.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}
		
		// draw the road segment
		g2.drawLine(xStart, yStart, xEnd, yEnd);
		borderDrawn = !borderDrawn;
	}

	/**
	 * Getter method for the zoomLevel field.
	 * 
	 * @return the zoomLevel
	 */
	public static int getZoomLevel() {
		return zoomLevel;
	}
}
