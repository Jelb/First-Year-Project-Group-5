package Part1;

public class WindowHandler {

	/**
	 * Calculates the pixel coordinate of a given geo coordinate, and returns a new coordinate set with int values
	 * 
	 * @param geoCoord  The geo coordinate
	 * @return 			A new Coordinate set of int values (a pixel coordinate set)
	 */
	private Coordinate geoToPixel(Coordinate geoCoord) {
		double x = geoCoord.getXcoordDouble();
		double y = geoCoord.getYcoordDouble();
		
		int intX = (int) Math.round(( (x - 450000 + Window.origoX) * (Window.windowSize / 200) * Window.zoomFactor) / 1000);
		int intY = (int) Math.round(((6200000 + y + Window.origoY) * (Window.windowSize / 200) * Window.zoomFactor) / 1000);
		
		return new Coordinate(intX, intY);
	}
	
	/**
	 * Calculates the UMT coordinate of a given on-screen pixel coordinate, and returns a new coordinate set with double values
	 * 
	 * @param pixelCoord 	The coordinate of the pixel
	 * @return 				The UMT coordinate of said pixel
	 */	
	private Coordinate pixelToGeo(Coordinate pixelCoord) {
		int x = pixelCoord.getXcoordInt();
		int y = pixelCoord.getYcoordInt();
		
		double doubleX = (double) (( (x + 450000 - Window.origoX) / (Window.windowSize / 200) / Window.zoomFactor) * 1000);
		double doubleY = (double) (((6200000 + y - Window.origoY) / (Window.windowSize / 200) / Window.zoomFactor) * 1000);
		
		return new Coordinate(doubleX, doubleY);
	}
	

	
	
}
