package Part1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class RoadSegment extends JComponent {
	/**
	 * The objects of this class are the JComponents which make up the lines on the map
	 */
	
	// The current minimum and maximum values of the area of the map we're looking at.
	private static double geoMaxX, geoMaxY, geoMinX, geoMinY;
	
	
	// The start and end points of the road segment in UTM-values 
	private double geoStartX, geoStartY, geoEndX, geoEndY;
	
	// The start and end points of the road segment in pixels
	private int xStart, yStart, xEnd, yEnd;
	
	private Color color;
	private static float roadWidth;
	
	
	/**
	 * 
	 * 
	 * @param xStartCoord
	 * @param yStartCoord
	 * @param xEndCoord
	 * @param yEndCoord
	 * @param Type
	 */
	public RoadSegment(double xStartCoord, double yStartCoord, double xEndCoord, double yEndCoord, int Type){
		geoStartX = xStartCoord;
		geoStartY = yStartCoord;
		geoEndX = xEndCoord;
		geoEndY = yEndCoord;
		color = getRoadSegmentColor(Type);
		if(Type == 4242) roadWidth = 5;
		//else roadWidth = 1;
		else setRoadWidth();
		calcPixel();
	}
	
	/**
	 * Used to convert the UTM coordinate to pixel.
	 */
	public void calcPixel(){
		double diffX = (geoMaxX - geoMinX);
		double diffY = (geoMaxY - geoMinY);
		int width = Window.use().getMapWidth();
		int height = Window.use().getMapHeight();
		int x1 =(int)(((geoStartX-geoMinX)/diffX)*width);
		int y1 =(int)(height-(((geoStartY-geoMinY)/diffY)*height));
		int x2 =(int)(((geoEndX-geoMinX)/diffX)*width);
		int y2 =(int)(height-(((geoEndY-geoMinY)/diffY)*height));
		xStart = x1;
		yStart = y1;
		xEnd = x2;
		yEnd = y2;
	}
	
	/**
	 * Used to set maximum and minimum coordinate set of the map.
	 * 
	 * @param geoMaxX The max X-coordinate of the current displayed map.
	 * @param geoMaxY The max Y-coordinate of the current displayed map.
	 * @param geoMinX The min X-coordinate of the current displayed map.
	 * @param geoMinY The min Y-coordinate of the current displayed map.
	 */
	public static void setMapSize(double geoMaxX, double geoMaxY, double geoMinX, double geoMinY){
		RoadSegment.geoMaxX = geoMaxX;
		RoadSegment.geoMaxY = geoMaxY;
		RoadSegment.geoMinX = geoMinX;
		RoadSegment.geoMinY = geoMinY;
	}
	
	/**
	 * Shifts the map area known by the road segments
	 */
	public static void shiftMapSize(double deltaX, double deltaY) {
		setMapSize(geoMaxX+deltaX, geoMaxY+deltaY, geoMinX+deltaX, geoMinY+deltaY);
	}
	
	/**
	 * Method used to receive the color used to paint the specific roadSegment.
	 *
	 * @param TYP An integer describing the road type.
	 * @return The Color used to paint the roadSegment.
	 */
	private static Color getRoadSegmentColor(int TYP){
		switch(TYP) {
		case 1    : return Color.red;		// Motor ways 	
		case 2    : return Color.red;		// Motor traffic road
		case 3    : return Color.blue; 		// Primary roads > 6 m 
		case 4    : return Color.blue;		// Secondary roads > 6 m
		case 5    : return Color.gray.darker();		// Roads between 3-6 meters
		case 8    : return Color.green.darker();		// paths
		case 4242 : return Color.orange;		// route
		default   : return Color.gray; 		// everything else
		}
	}
	
	/**
	 * Changes the road width according to the zoom level
	 */
	private static void setRoadWidth() {
		//System.out.println("geoWidth = " + WindowHandler.geoWidth);
		roadWidth = 1;
		//if (WindowHandler.geoWidth > 100000) {
		//	roadWidth = 1; }
		if(WindowHandler.geoWidth < 20000 && WindowHandler.geoWidth > 5000) {
			roadWidth = 1.2f; }
			//System.out.println("geoWidth less than 20.000, width = 1.2"); }
		else if(WindowHandler.geoWidth < 5000 && WindowHandler.geoWidth > 4000) {
			roadWidth = 1.4f; }
			//System.out.println("geoWidth less than 5.000, width = 1.4"); }
		//else if(WindowHandler.geoWidth < 4000 && WindowHandler.geoWidth > 3000) {
		//	roadWidth = 1.6f;
		//	System.out.println("geoWidth less than 4.000, width = 1.6"); }
		else if (WindowHandler.geoWidth < 4000 && WindowHandler.geoWidth > 1500) {
			roadWidth = 1.8f; }
			//System.out.println("geoWidth less than 4.000, width = 1.8"); }
		else if (WindowHandler.geoWidth < 1500 && WindowHandler.geoWidth > 600) {
			roadWidth = 3; }
			//System.out.println("geoWidth less than 1500, width = 3"); }
		else if (WindowHandler.geoWidth < 600) {
			roadWidth = 5; }
			//System.out.println("geoWidth less than 1500, width = 4"); }
	}
	
	/**
	 * Method used to draw a line (roadSegment) on the Map.
	 * 
	 * @param g A Graphics object used for drawing
	 */
	protected void paintComponent(Graphics g) {
		
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
