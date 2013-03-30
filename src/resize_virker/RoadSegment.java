package resize_virker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class RoadSegment extends JComponent {
	/**
	 * The objects of this class are the JComponents which make up the lines on the map
	 */
	private static double geoMaxX, geoMaxY, geoMinX, geoMinY;
	
	private double geoStartX, geoStartY, geoEndX, geoEndY;
	private int xStart, yStart, xEnd, yEnd;
	private Color color;
	private int roadWidth;
	
	private static final long serialVersionUID = 1L;

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
		calcPixel();
	}
	
	/**
	 * Used to convert the UTM coordinate to pixel.
	 */
	public void calcPixel(){
		double diffX = (geoMaxX - geoMinX);
		double diffY = (geoMaxY - geoMinY);
		int width = Window.use().getWidth();
		int height = Window.use().getHeight();
		int x1 =(int)(((geoStartX)/diffX)*width);
		int y1 =(int)(height-(((geoStartY)/diffY)*height));
		int x2 =(int)(((geoEndX)/diffX)*width);
		int y2 =(int)(height-(((geoEndY)/diffY)*height));
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
	 * Method used to receive the color used to paint the specific roadSegment.
	 *
	 * @param TYP An integer describing the road type.
	 * @return The Color used to paint the roadSegment.
	 */
	private static Color getRoadSegmentColor(int TYP){
		switch(TYP) {
		case 1 : return Color.red;		// Motor ways 	
		case 2 : return Color.red;		// Motor traffic road
		case 3 : return Color.blue; 	// Primary roads > 6 m 
		case 4 : return Color.blue;		// Secondary roads > 6 m
		case 5 : return Color.black;	// Roads between 3-6 meters
		case 8 : return Color.green;	// paths
		default : return Color.pink; 	// everything else
		}
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		// set road color
		g2.setColor(color);
	
		// setting stroke type
		g2.setStroke(new BasicStroke(roadWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		// draw the road segment
		g2.drawLine(xStart, yStart, xEnd, yEnd);
	}
}
